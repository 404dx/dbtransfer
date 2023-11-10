package com.bird.dbtransfer.type;

import java.sql.*;
import java.util.Date;

public class DateTimeTypeHandler implements TypeHandler<Date> {

    @Override
    public void setParameter(PreparedStatement statement, int index, Date value, JdbcType jdbcType) throws SQLException {
        if(value == null){
            statement.setNull(index, JDBCType.DATE.getVendorTypeNumber());
        }else{
            statement.setTimestamp(index,new Timestamp(value.getTime()));
        }
    }

    @Override
    public Date getResult(ResultSet resultSet, String colName) throws SQLException {
        return resultSet.getDate(colName);
    }

    @Override
    public Date getResult(ResultSet resultSet, int colIndex) throws SQLException {
        return resultSet.getDate(colIndex);
    }
}
