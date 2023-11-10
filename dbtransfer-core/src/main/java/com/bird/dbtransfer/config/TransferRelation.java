package com.bird.dbtransfer.config;

import lombok.Data;

import java.util.List;

@Data
public class TransferRelation {

    // 是否只使用配置好的字段
    private boolean onlyConfig;
    private String source;
    private String target;
    private List<FieldRelation> fields;
}
