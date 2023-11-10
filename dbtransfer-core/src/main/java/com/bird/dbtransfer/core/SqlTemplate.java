package com.bird.dbtransfer.core;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public interface SqlTemplate {

    public String getSourceSelectSql();

    public String getTargetInsertSql();

    public List<SqlField>  getSqlFields();

    public String getSourceTableName();

    public String getTargetTableName();

    class SqlField{

        @Getter
        @Setter
        private String name;
        @Setter
        private String type;
        @Getter
        @Setter
        private int length;
        @Getter
        @Setter
        private String targetName;

        public SqlField() {
        }

        public SqlField(String name, String type, int length, String targetName) {
            this.name = name;
            this.type = type;
            this.length = length;
            this.targetName = targetName;
        }

        public String getType() {
            if(StringUtils.isNotBlank(this.type)){
                int i = this.type.indexOf("(");
                if(i != -1){
                    return this.type.substring(0,i);
                }
            }
            return type;
        }

    }

}
