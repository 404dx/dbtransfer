package com.bird.dbtransfer.core;

import com.bird.dbtransfer.TransferException;
import com.bird.dbtransfer.config.TransferConfig;
import com.bird.dbtransfer.consts.DataBaseType;
import com.bird.dbtransfer.consts.DefaultConfig;
import com.bird.dbtransfer.db.DataSource;
import com.bird.dbtransfer.db.DataSourceImpl;
import com.bird.dbtransfer.format.DataBaseDateFormat;
import com.bird.dbtransfer.format.PartPageFormat;
import com.bird.dbtransfer.util.TransferConfigParseUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDataBaseTransfer implements DataBaseTransfer {

    protected final FastDateFormat DEFAULT_DATEFORMAT = FastDateFormat.getInstance(DefaultConfig.DATEFORMAT_STRING);
    /**
     * 数据源map
     */
    private final Map<String, DataSource> dataSourceMap = new HashMap<>();
    /**
     * 数据库类型处理
     */
    private final TypeHandlerRegister typeHandlerRegister;
    /**
     * 配置信息
     */
    private final TransferConfig transferConfig;

    /**
     * 时间格式化匹配
     */
    private final DataBaseDateFormat dataBaseDateFormat = new DataBaseDateFormat();

    /**
     * 分页sql格式化
     */
    private final PartPageFormat partPageFormat = new PartPageFormat();

    public AbstractDataBaseTransfer(String path) throws TransferException {
        this(TransferConfigParseUtils.getTransferConfig(path));
    }

    public AbstractDataBaseTransfer(TransferConfig transferConfig) throws TransferException {
        this.transferConfig = transferConfig;
        this.typeHandlerRegister = new TypeHandlerRegister();
        this.initDataSource();
    }


    public AbstractDataBaseTransfer(String path, TypeHandlerRegister typeHandlerRegister) throws TransferException {
        this.transferConfig = TransferConfigParseUtils.getTransferConfig(path);
        this.typeHandlerRegister = typeHandlerRegister;
    }

    /**
     * 初始化数据源
     */
    protected void initDataSource() throws TransferException {
        transferConfig.getDatasources().forEach(dataSourceConfig -> {
            if (!DataBaseType.exists(dataSourceConfig.getDbType())) {
                throw new RuntimeException("数据源类型不匹配,数据源类型只能是:" + DataBaseType.text());
            }
            String name = dataSourceConfig.getName();
            DataSource dataSource = new DataSourceImpl(dataSourceConfig);
            dataSourceMap.put(name, dataSource);
        });
        if (!dataSourceMap.containsKey(transferConfig.getFrom())) {
            throw new TransferException("from :" + transferConfig.getFrom() + " 未找到对应的数据源,请在数据源当中配置name");
        }
        if (!dataSourceMap.containsKey(transferConfig.getTo())) {
            throw new TransferException("to :" + transferConfig.getFrom() + " 未找到对应的数据源,请在数据源当中配置name");
        }
    }

    /**
     * 获取数据源类型
     *
     * @param name
     * @return
     */
    protected String getDataSourceType(String name) {
        DataSourceImpl di = (DataSourceImpl) this.dataSourceMap.get(name);
        return di.getType();
    }

    /**
     * 获取数据库 schema
     *
     * @param name
     * @return
     */
    protected String getSchema(String name) {
        DataSourceImpl di = (DataSourceImpl) this.dataSourceMap.get(name);
        return di.getSchema();
    }

    /**
     * 根据配置当中的数据源name字段获取对应数据库连接
     *
     * @param name
     * @return
     */
    protected Connection getConnection(String name) {
        return this.dataSourceMap.get(name).getConnection();
    }

    /**
     * 获取配置信息
     *
     * @return
     */
    protected TransferConfig getTransferConfig() {
        return this.transferConfig;
    }

    public TypeHandlerRegister getTypeHandlerRegister() {
        return typeHandlerRegister;
    }

    /**
     * 获取时间格式化字符串
     */
    protected String getDateFormatString(String name) {
        String type = this.getDataSourceType(name);
        return this.dataBaseDateFormat.getFormatString(type);
    }

    protected DataBaseDateFormat getDataBaseDateFormat() {
        return this.dataBaseDateFormat;
    }

    protected PartPageFormat getPartPageFormat() {
        return this.partPageFormat;
    }

    /**
     * 关闭数据源
     *
     * @param connection
     */
    protected void closeConnection(Connection connection) {
        if (null != connection) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
