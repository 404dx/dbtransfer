package com.bird.dbtransfer.core;

import com.bird.dbtransfer.TransferException;
import com.bird.dbtransfer.config.FieldRelation;
import com.bird.dbtransfer.config.TransferRelation;
import com.bird.dbtransfer.consts.TransferType;
import com.bird.dbtransfer.type.TypeHandler;
import com.bird.dbtransfer.util.MateDataUtils;
import com.bird.dbtransfer.util.PlaceholderUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class SimpleDataBaseTransfer extends AbstractDataBaseTransfer{

    private static final Logger logger = Logger.getLogger(SimpleDataBaseTransfer.class);

    private final String  COUNT_SQL_TEMPLATE = "select count(1) from ({sql}) temp";
    // 每次插入数据数量
    private final int BATCH_SQL_COUNT = 5000;
    private final String from;
    private final String to;

    private String lineSeparator = java.security.AccessController.doPrivileged(
            new sun.security.action.GetPropertyAction("line.separator"));

    public SimpleDataBaseTransfer(String path) throws TransferException {
        super(path);
        this.from = this.getTransferConfig().getFrom();
        this.to = this.getTransferConfig().getTo();
    }

    @Override
    public void transfer() throws TransferException {
        Connection connection = this.getConnection(from);
        try {
            // 解析Sql template
            Map<String,SqlTemplate> sqlTemplateMap = this.parseConfigToSqlTemplate(connection);
            String type = this.getTransferConfig().getType();
            if(TransferType.SQL.getValue().equalsIgnoreCase(type)){
                this.transferSql(sqlTemplateMap);
            }else if (TransferType.TRASFER.getValue().equalsIgnoreCase(type)){
                this.transferDB(sqlTemplateMap);
            }
            logger.info("---------------------------------> transfer success");
        } catch (Exception e) {
            logger.error(e);
            throw new TransferException(e);
        } finally {
            this.closeConnection(connection);
        }
    }

    /**
     * 将数据转换为sql
     * @param sqlTemplateMap
     */
    private void transferSql(Map<String,SqlTemplate> sqlTemplateMap) throws SQLException, IOException {
        Connection connection = this.getConnection(this.from);
        Statement statement = connection.createStatement();
        StringBuilder stringBuilder = new StringBuilder();
        int sqlCount = 100;
        int index = 0;
        Writer writer = new FileWriter(this.getTransferConfig().getPath());
        BufferedWriter bw = new BufferedWriter(writer,1024 * 8);
        try{
            for(Map.Entry<String,SqlTemplate> entry : sqlTemplateMap.entrySet()){
                logger.info("----------------> 开始解析 [ "+ entry.getKey() + " ] <----------------");
                logger.info("----------------> Execute [ "+ entry.getValue().getSourceSelectSql() + " ] <----------------");
                bw.append("-- ").append(entry.getKey()).append(lineSeparator);
                try {
                    ResultSet rs = statement.executeQuery(entry.getValue().getSourceSelectSql());
                    String insertSql = entry.getValue().getTargetInsertSql();
                    while (rs.next()){
                        String sql = insertSql;
                        ResultSetMetaData resultRs = rs.getMetaData();
                        int count = resultRs.getColumnCount();
                        for(int i = 1;i<=count;i++){
                            String colName = resultRs.getColumnName(i);
                            String typeName = resultRs.getColumnTypeName(i);
                            int typeLength = resultRs.getColumnType(i);
                            TypeHandler<?> typeHandler = this.getTypeHandlerRegister().getTypeHandler(typeName);
                            Object obj = typeHandler.getResult(rs,colName);
                            String value = sqlResultObjToString(obj);
                            sql = sql.replace("{"+colName+"}",value);
                        }
                        stringBuilder.append(sql).append(lineSeparator);
                        index++;
                        if(index == sqlCount){
                            bw.append(stringBuilder.toString());
                            bw.flush();
                            stringBuilder = new StringBuilder();
                            index = 0;
                        }
                    }
                    if(index > 0){
                        bw.append(stringBuilder.toString());
                        bw.flush();
                        stringBuilder = new StringBuilder();
                    }
                    logger.info("----------------> 解析成功 [ "+ entry.getKey() + "] <----------------");
                } catch (SQLException e) {
                    logger.error(e);
                    logger.info("----------------> 解析失败 [ "+ entry.getKey() + "] <----------------");
                }
            }
        } finally {
            writer.close();
        }

    }

    private String sqlResultObjToString(Object obj){
        if(obj == null){
            return "\"\"";
        }
        if(obj instanceof String){
            return "\""+obj.toString()+"\"";
        }else if(obj instanceof Date){
            return this.getDataBaseDateFormat().getDateFunc((Date)obj,this.getDataSourceType(to));
        }else if(byte[].class.isAssignableFrom(obj.getClass())){
            return new String((byte[]) obj);
        }else {
            return obj.toString();
        }
    }

    private void closeResource(Statement statement,ResultSet rs){
        if (null != statement) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger.error(e);
            }
        }
        if (null != rs) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error(e);
            }
        }
    };

    /**
     * 将数据库直接转换至对应的数据库表中
     * @param sqlTemplateMap
     */
    private void transferDB(Map<String,SqlTemplate> sqlTemplateMap){
        Connection fromConn = this.getConnection(this.from);
        try{
            for(Map.Entry<String,SqlTemplate> entry : sqlTemplateMap.entrySet()){
                logger.info("begin parsing "+entry.getKey());
                Statement statement = null;
                ResultSet rs = null;
                String selectSql = entry.getValue().getSourceSelectSql();
                String countSql = COUNT_SQL_TEMPLATE.replace("{sql}",selectSql);
                try{
                    statement = fromConn.createStatement();
                    rs = statement.executeQuery(countSql);
                    rs.next();
                    int count = rs.getInt(1);
                    rs.close();
                    // 如果数据量大于每次批量插入的上限。则做分页查询多次批量插入
                    if(count > BATCH_SQL_COUNT){
                        // 计算页数
                        int pageSize = (int)Math.ceil((count * 1.0)/ BATCH_SQL_COUNT);
                        String type = this.getDataSourceType(this.from);
                        for(int i = 1;i<=pageSize; i++){
                            // 获取分页sql
                            String partPageSql = this.getPartPageFormat().formatSql(type,selectSql,i,BATCH_SQL_COUNT);
                            ResultSet queryRs = statement.executeQuery(partPageSql);
                            saveDataTo(queryRs,entry.getValue());
                            this.closeResource(null,queryRs);
                        }
                    } else {
                        ResultSet queryRs = statement.executeQuery(selectSql);
                        saveDataTo(queryRs,entry.getValue());
                        this.closeResource(null,queryRs);
                    }
                } catch (Exception e) {
                    logger.error("------------------------------------> Table ["+entry.getKey()+"] 数据同步失败");
                } finally {
                    this.closeResource(statement,rs);
                }
                logger.info("end parsing "+entry.getKey());
            }
        } catch (Exception e){
            logger.error(e);
        } finally {
           this.closeConnection(fromConn);
        }
    }


    private void saveDataTo(ResultSet dataRs,SqlTemplate sqlTemplate){
        Connection toConn = this.getConnection(this.to);
        PreparedStatement statement = null;
        try {
            if(!dataRs.next()){
                logger.warn(sqlTemplate.getSourceTableName()+" is an empty table.");
            }else{
                String targetTableName = sqlTemplate.getTargetTableName();
                // 获取目标表数据类型
                Map<String, SqlTemplate.SqlField> targetSqlFieldMap = this.getTableFieldDetails(targetTableName,toConn);
                String insertSql = sqlTemplate.getTargetInsertSql();
                String [] placeholders = PlaceholderUtil.parsePlaceholder(insertSql,PlaceholderUtil.DEFAULT_PREFIX,PlaceholderUtil.DEFAULT_SUFFIX);
                for(String str : placeholders){
                    insertSql = insertSql.replace("{"+str+"}","?");
                }
                statement = toConn.prepareStatement(insertSql);
                do{
                    for(int i=1;i<=placeholders.length; i++){
                        String fieldStr = placeholders[i-1];
                        Optional<SqlTemplate.SqlField> sfOptional = sqlTemplate.getSqlFields().stream()
                                .filter(sf -> sf.getName().equals(fieldStr))
                                .findFirst();
                        String type = sfOptional.get().getType();
                        String targetName = sfOptional.get().getTargetName();
                        SqlTemplate.SqlField targetSqlField = targetSqlFieldMap.get(targetName.toUpperCase());
                        if(targetSqlField == null){
                            throw new TransferException(to+":"+targetTableName+"."+targetName+" 不存在");
                        }
                        TypeHandler typeHandler = this.getTypeHandlerRegister().getTypeHandler(type);
                        Object value = typeHandler.getResult(dataRs,fieldStr);
                        TypeHandler targetTypeHandler = this.getTypeHandlerRegister().getTypeHandler(targetSqlField.getType());
                        try{
                            targetTypeHandler.setParameter(statement,i,value,new TypeHandler.JdbcType(targetSqlField.getType()));
                        }catch (Exception e){
                            logger.error("set parameter failure; prompt: ["+ fieldStr + ":"+type+"] match [" + targetSqlField.getName() + ":" + targetSqlField.getType() + "] ");
                            throw new Exception(e);
                        }
                    }
                    statement.addBatch();
                } while (dataRs.next());
                statement.executeBatch();
                statement.clearParameters();
                statement.clearBatch();
            }
        } catch (Exception e){
            this.closeResource(null,dataRs);
            logger.error(e);
        } finally {
            this.closeResource(statement,null);
            this.closeConnection(toConn);
        }
    }

    /**
     * 获取表字段信息
     * @param table 表名称
     * @param connection 数据库连接
     * @return
     * @throws TransferException
     */
    private Map<String, SqlTemplate.SqlField> getTableFieldDetails(String table,Connection connection) throws TransferException{
        ResultSet colRs = null;
        try {
            DatabaseMetaData dbMetaData  = connection.getMetaData();
            colRs = dbMetaData.getColumns(connection.getCatalog(),this.getSchema(this.to),table,"%");
            if(!colRs.next()){
                throw new TransferException("Table: [ "+table+" ] 不存在于"+this.to+"数据库当中");
            }
            String dbType = this.getDataSourceType(this.to);
            Map<String,SqlTemplate.SqlField> sqlFieldMap = new HashMap<>();
            do {
                String name = colRs.getString(MateDataUtils.getColumnByDBType(dbType,MateDataUtils.COL_NAME));
                if(sqlFieldMap.containsKey(name)){
                    break;
                }
                String type = colRs.getString(MateDataUtils.getColumnByDBType(dbType,MateDataUtils.COL_TYPE));
                int typeLength = colRs.getInt(MateDataUtils.getColumnByDBType(dbType,MateDataUtils.COL_TYPE_LENGTH));
                SqlTemplate.SqlField sqlField = new SqlTemplate.SqlField(name,type,typeLength,null);
                sqlFieldMap.put(name.toUpperCase(),sqlField);
            }while(colRs.next());
            return sqlFieldMap;
        } catch (Exception e) {
            logger.error(e);
            throw new TransferException(e);
        }finally {
            this.closeResource(null,colRs);
        }
    }

    private Map<String,SqlTemplate> parseConfigToSqlTemplate(Connection connection) throws SQLException, TransferException {
        // 获取数据库元数据
        DatabaseMetaData dbMetaData  = connection.getMetaData();
        // sql模板
        Map<String,SqlTemplate> sqlTemplateMap = new HashMap<>();
        // 是否包含全部表
        if(this.getTransferConfig().isAll()){
            if (!getTransferConfig().getRelations().isEmpty()){
                this.configToSqlTemplate(sqlTemplateMap,connection);
            }
            String fromDbType = this.getDataSourceType(this.from);
            ResultSet tableRs = dbMetaData.getTables(connection.getCatalog(),this.getSchema(from),"%",new String[]{"TABLE"});
            while(tableRs.next()){
                String tableName = tableRs.getString("TABLE_NAME");
                if(!sqlTemplateMap.containsKey(tableName.toUpperCase())){
                    ResultSet columnRs = dbMetaData.getColumns(connection.getCatalog(),this.getSchema(from),tableName,"%");
                    DefaultSqlTemplate template = new DefaultSqlTemplate(tableName,tableName);
                    while (columnRs.next()){
                        String name = columnRs.getString(MateDataUtils.getColumnByDBType(fromDbType,MateDataUtils.COL_NAME));
                        long count = template.getSqlFields().stream().filter(f->f.getName().equalsIgnoreCase(name)).count();
                        if(count > 0){
                            break;
                        }
                        String type = columnRs.getString(MateDataUtils.getColumnByDBType(fromDbType,MateDataUtils.COL_TYPE));
                        int typeLength = columnRs.getInt(MateDataUtils.getColumnByDBType(fromDbType,MateDataUtils.COL_TYPE_LENGTH));
                        DefaultSqlTemplate.SqlField sqlField = new DefaultSqlTemplate.SqlField(name,type,typeLength,name);
                        template.addSqlField(sqlField);
                    }
                    columnRs.close();
                    sqlTemplateMap.put(tableName.toUpperCase(),template);
                }
            }
            tableRs.close();
        }else{
            this.configToSqlTemplate(sqlTemplateMap,connection);
        }
        return sqlTemplateMap;
    }

    /**
     *  根据配置内容获取对应关系
     * @param sqlTemplateMap
     * @param connection
     */
    private void configToSqlTemplate(Map<String,SqlTemplate> sqlTemplateMap, Connection connection) throws TransferException, SQLException {
        // 获取表关系
        List<TransferRelation> relations = this.getTransferConfig().getRelations();
        // 获取数据库元数据
        DatabaseMetaData dbMetaData  = connection.getMetaData();
        for(TransferRelation relation : relations){
            ResultSet columnRs = dbMetaData.getColumns(connection.getCatalog(),this.getSchema(from),relation.getSource(),"%");
            if(!columnRs.next()){
                throw new TransferException("Table: [ "+relation.getSource()+" ] 不存在于"+this.from+"数据库当中");
            }
            SqlTemplate template = this.getConfigSqlTemplate(relation,columnRs);
            sqlTemplateMap.put(relation.getSource().toUpperCase(),template);
            columnRs.close();
        }
    }

    /**
     * 根据配置获取sql template
     * @param relation 表关系配置
     * @param columnRs 查询的字段结果集
     */
    private SqlTemplate getConfigSqlTemplate(TransferRelation relation, ResultSet columnRs) throws TransferException, SQLException {
        if(StringUtils.isBlank(relation.getSource()) || StringUtils.isBlank(relation.getTarget())){
             throw new TransferException("source or target 为空，无法解析");
        }
        // 添加表与表对应关系
        DefaultSqlTemplate template = new DefaultSqlTemplate(relation.getSource(),relation.getTarget());
        // 获取from指向的数据库类型
        String fromDbType = this.getDataSourceType(this.from);
        do {
            List<FieldRelation> fieldRelations = relation.getFields();
            // 获取字段名称
            String name = columnRs.getString(MateDataUtils.getColumnByDBType(fromDbType,MateDataUtils.COL_NAME));
            long count = template.getSqlFields().stream().filter(f->f.getName().equalsIgnoreCase(name)).count();
            if(count > 0){
                break;
            }
            // 获取字段类型
            String type = columnRs.getString(MateDataUtils.getColumnByDBType(fromDbType,MateDataUtils.COL_TYPE));
            // 获取字段类型长度
            int typeLength = columnRs.getInt(MateDataUtils.getColumnByDBType(fromDbType,MateDataUtils.COL_TYPE_LENGTH));
            // 表示是否已配置
            boolean isConfig = false;
            // 是否存在配置
            boolean isExistsConfig = true;
            if(null != fieldRelations && !fieldRelations.isEmpty()){
                // 根据配置添加对应关系
                for (FieldRelation fieldRelation : fieldRelations) {
                    String sourceName = fieldRelation.getSource();
                    String targetName = fieldRelation.getTarget();
                    if (sourceName.equalsIgnoreCase(name)) {
                        DefaultSqlTemplate.SqlField sqlField = new DefaultSqlTemplate.SqlField();
                        sqlField.setName(sourceName);
                        sqlField.setTargetName(targetName);
                        sqlField.setType(type);
                        sqlField.setLength(typeLength);
                        template.addSqlField(sqlField);
                        isConfig = true;
                        break;
                    }
                }
            }else{
                isExistsConfig = false;
            }
            // 添加默认字段关系
            if((!isConfig && !relation.isOnlyConfig()) | !isExistsConfig){
                DefaultSqlTemplate.SqlField sqlField = new DefaultSqlTemplate.SqlField(name,type,typeLength,name);
                template.addSqlField(sqlField);
            }
        } while (columnRs.next());
        return template;
    }


}
