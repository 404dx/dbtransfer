package com.bird.dbtransfer.type;

import java.sql.*;

public class TimestampTypeHandler implements TypeHandler<Timestamp> {


    @Override
    public void setParameter(PreparedStatement statement, int index, Timestamp value, JdbcType jdbcType) throws SQLException{
        if(value == null){
            statement.setNull(index, JDBCType.TIMESTAMP.getVendorTypeNumber());
        }else {
            statement.setTimestamp(index,value);
        }
    }

    @Override
    public Timestamp getResult(ResultSet resultSet, String colName) throws SQLException {
        return resultSet.getTimestamp(colName);
    }

    @Override
    public Timestamp getResult(ResultSet resultSet, int colIndex) throws SQLException {
        return resultSet.getTimestamp(colIndex);
    }
}
