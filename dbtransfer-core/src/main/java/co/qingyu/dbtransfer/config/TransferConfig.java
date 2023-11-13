package co.qingyu.dbtransfer.config;

import lombok.Data;

import java.util.List;

@Data
public class TransferConfig {

    private String type;
    private String path;
    private String from;
    private String to;
    private boolean isAll;
    private List<DataSourceConfig> datasources;
    private List<TransferRelation> relations;

}
