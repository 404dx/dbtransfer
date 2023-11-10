package com.bird.dbtransfer.type;

import lombok.Data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface TypeHandler<T> {

    void setParameter(PreparedStatement statement,int index,T value,JdbcType jdbcType) throws SQLException;

    T getResult(ResultSet resultSet,String colName) throws SQLException;

    T getResult(ResultSet resultSet,int colIndex) throws SQLException;


    class JdbcType{
        private String name;
        private String code;

        public JdbcType() {
        }

        public JdbcType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

}
