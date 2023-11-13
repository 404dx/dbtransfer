package co.qingyu.dbtransfer;

import co.qingyu.dbtransfer.log.TransferLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author jackie
 * @since 2.0
 */
public class DbTest {

//    public static void main(String[] args) {
//        getTableInfoBySql();
//    }


    private static Connection getConnection() {
        String username = "root", password = "jackie1234";
        String url = "jdbc:mysql://192.168.1.57:53306/mysql?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&nullNamePatternMatchesAll=true";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static void closeConnection(Connection connection) {
        if (Objects.nonNull(connection)) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private static void test1() {

    }

    @Test
    @DisplayName("通过sql获取数据库信息")
    public void getTableInfoBySql() {
        Connection connection = getConnection();
        String tableSql = "show create table %s";
        String tableView = "show create view user_info";
        ArrayList<String> tables = new ArrayList<>();
        ArrayList<String> views = new ArrayList<>();
        String[] types = {"TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"};
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(connection.getCatalog(), connection.getSchema(), "%", types);
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                String typeName = resultSet.getString("TABLE_TYPE");
                if (Objects.equals(typeName, "VIEW")) {
                    views.add(tableName);
                } else {
                    tables.add(tableName);
                }
            }
            for (String table : tables) {
                PreparedStatement statement = connection.prepareStatement(String.format(tableSql, table));
                ResultSet tableResult = statement.executeQuery();
                while (tableResult.next()) {
                    String tableName = tableResult.getString("Table");
                    String createSql = tableResult.getString("Create Table");
                    TransferLogger.info("表名：" + tableName);
                    TransferLogger.info("建表语句：\n" + createSql);
                }
                tableResult.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    @Test
    @DisplayName("通过表结构获取信息")
    public void getTableInfo() {
        Connection connection = getConnection();
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String[] types = {"TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"};
            //获取所有表信息
            ResultSet tableResult = databaseMetaData.getTables(connection.getCatalog(), connection.getSchema(), "%", types);
            while (tableResult.next()) {
                String tableName = tableResult.getString("TABLE_NAME");
                String remarks = tableResult.getString("REMARKS");
                String tableCat = tableResult.getString("TABLE_CAT");
                String tableType = tableResult.getString("TABLE_TYPE");
                String generation = tableResult.getString("REF_GENERATION");
                TransferLogger.info("=======================" + (Objects.equals(tableType, "TABLE") ? "表信息：" : " 视图信息：") + tableName + " ===========================");
                TransferLogger.info("表名称： " + tableName);
                TransferLogger.info("表注释： " + remarks);
                TransferLogger.info("表目录： " + tableCat);
                TransferLogger.info("表类型 ： " + tableType);
                TransferLogger.info("generation ： " + generation);
                //获取表字段信息
                ResultSet columns = databaseMetaData.getColumns(connection.getCatalog(), connection.getSchema(), tableName, "%");
                while (columns.next()) {
                    String cat = columns.getString("TABLE_CAT");
                    String schem = columns.getString("TABLE_SCHEM");
                    String colName = columns.getString("COLUMN_NAME");
                    String dataType = columns.getString("DATA_TYPE");
                    String typeName = columns.getString("TYPE_NAME");
                    int columnSize = columns.getInt("COLUMN_SIZE");
                    String columnsRemark = columns.getString("REMARKS");
                    String columnDef = columns.getString("COLUMN_DEF");
                    String nullable = columns.getString("NULLABLE");
                    String isNullable = columns.getString("IS_NULLABLE");
                    String autoincrement = columns.getString("IS_AUTOINCREMENT");
                    String generatedcolumn = columns.getString("IS_GENERATEDCOLUMN");
                    TransferLogger.info("===>  列名称：{},数据类型：{},列大小：{},备注信息：{},默认值：{},可为空：{},ISO规则用于确定列的可为空性：{},是否自动递增：{},是否是生成的列：{}",
                            colName, dataType, typeName, columnSize, columnsRemark, columnDef, isNullable, nullable, autoincrement, generatedcolumn);
                }
                //获取主键信息
                ResultSet primaryKeys = databaseMetaData.getPrimaryKeys(connection.getCatalog(), connection.getSchema(), tableName);
                while (primaryKeys.next()) {
                    String keyColumnName = primaryKeys.getString("COLUMN_NAME");
                    short keySeq = primaryKeys.getShort("KEY_SEQ");
                    String pkName = primaryKeys.getString("PK_NAME");
                    TransferLogger.info("------->主键列：{}", keyColumnName);
                    TransferLogger.info("------->主键名称：{}", pkName);
                    TransferLogger.info("------->序列号内的主键：{}", keySeq);
                }
            }
            tableResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }
}
