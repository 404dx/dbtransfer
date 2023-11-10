package com.bird.dbtransfer.type;

import com.sun.org.apache.regexp.internal.RE;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author jackie
 * @since 2.0
 */
public class BitTypeHandler implements TypeHandler<Integer>{
    @Override
    public void setParameter(PreparedStatement statement, int index, Integer value, JdbcType jdbcType) throws SQLException {
        if (Objects.isNull(value)){
            statement.setInt(index, JDBCType.BIT.getVendorTypeNumber());
            return;
        }
        statement.setBoolean(index, value==0?Boolean.FALSE:Boolean.TRUE);
    }

    @Override
    public Integer getResult(ResultSet resultSet, String colName) throws SQLException {
        return resultSet.getInt(colName);
    }

    @Override
    public Integer getResult(ResultSet resultSet, int colIndex) throws SQLException {
        return resultSet.getInt(colIndex);
    }
}
