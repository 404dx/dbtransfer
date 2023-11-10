package com.bird.dbtransfer.core;

import java.util.ArrayList;
import java.util.List;

public class DefaultSqlTemplate implements SqlTemplate {

    private String tableName;
    private String targetTableName;

    private List<SqlField> sqlFields = new ArrayList<>();

    public DefaultSqlTemplate(String tableName, String targetTableName) {
        this.tableName = tableName;
        this.targetTableName = targetTableName;
    }

    public DefaultSqlTemplate(String tableName, String targetTableName,List<SqlField> sqlFields) {
        this.tableName = tableName;
        this.targetTableName = targetTableName;
        this.sqlFields = sqlFields;
    }

    public DefaultSqlTemplate addSqlField(SqlField sqlField){
        this.sqlFields.add(sqlField);
        return this;
    }

    @Override
    public String getSourceTableName() {
        return this.tableName;
    }

    @Override
    public String getTargetTableName() {
        return this.targetTableName;
    }

    public void setSqlFields(List<SqlField> sqlFields){
        this.sqlFields = sqlFields;
    }

    public List<SqlField> getSqlFields() {
        return sqlFields;
    }

    @Override
    public String getSourceSelectSql() {
        return "select "+this.getTableSourceFields()+" from " + this.tableName;
    }

    @Override
    public String getTargetInsertSql() {
        return "insert into " + targetTableName + "("+this.getTableTargetFields()+")"+" values("+this.getParamSourceFields()+")";
    }

    private String getTableSourceFields(){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<this.sqlFields.size();i++){
            SqlField sqlField = sqlFields.get(i);
            if(i == this.sqlFields.size() -1){
                sb.append(sqlField.getName());
            }else{
                sb.append(sqlField.getName()).append(",");
            }
        }
        return sb.toString();
    }

    private String getParamSourceFields(){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<this.sqlFields.size();i++){
            SqlField sqlField = sqlFields.get(i);
            if(i == this.sqlFields.size() -1){
                sb.append("{").append(sqlField.getName()).append("}");
            }else{
                sb.append("{").append(sqlField.getName()).append("}").append(",");
            }
        }
        return sb.toString();
    }

    private String getTableTargetFields(){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<this.sqlFields.size();i++){
            SqlField sqlField = sqlFields.get(i);
            if(i == this.sqlFields.size() -1){
                sb.append(sqlField.getTargetName());
            }else{
                sb.append(sqlField.getTargetName()).append(",");
            }
        }
        return sb.toString();
    }


}
