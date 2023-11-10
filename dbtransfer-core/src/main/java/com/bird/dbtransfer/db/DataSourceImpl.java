package com.bird.dbtransfer.db;

import com.bird.dbtransfer.config.DataSourceConfig;
import com.bird.dbtransfer.consts.DataBaseType;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSourceImpl implements DataSource {

    private String name;
    private String type;
    private String url;
    private String driverName;
    private String username;
    private String password;
    private String schema;

    public DataSourceImpl(DataSourceConfig dataSourceConfig) {
        this.url = dataSourceConfig.getUrl();
        this.driverName = dataSourceConfig.getDriver();
        this.username = dataSourceConfig.getUsername();
        this.password = dataSourceConfig.getPassword();
        this.name = dataSourceConfig.getName();
        this.type = dataSourceConfig.getType();
        initDataSource();
    }

    private void initDataSource(){
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        try{
            return DriverManager.getConnection(url,username,password);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public String getSchema() {
        if(StringUtils.isBlank(this.schema)){
            if(DataBaseType.ORACLE.getValue().equalsIgnoreCase(this.getType())){
                schema = this.getUsername().toUpperCase();
            }else{
                try {
                    schema = this.getConnection().getSchema();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return schema;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
