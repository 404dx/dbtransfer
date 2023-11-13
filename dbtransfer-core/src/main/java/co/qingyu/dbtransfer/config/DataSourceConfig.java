package co.qingyu.dbtransfer.config;

import lombok.Data;

@Data
public class DataSourceConfig {

    private String name;
    private String dbType;
    private String url;
    private String driver;
    private String username;
    private String password;

}
