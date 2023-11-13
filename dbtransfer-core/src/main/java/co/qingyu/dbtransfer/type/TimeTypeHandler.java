package co.qingyu.dbtransfer.type;

import java.sql.*;

public class TimeTypeHandler implements TypeHandler<Time> {

    @Override
    public void setParameter(PreparedStatement statement, int index, Time value, JdbcType jdbcType) throws SQLException {
        if (value == null) {
            statement.setNull(index, JDBCType.TIME.getVendorTypeNumber());
        } else {
            statement.setTime(index, value);
        }
    }

    @Override
    public Time getResult(ResultSet resultSet, String colName) throws SQLException {
        return resultSet.getTime(colName);
    }

    @Override
    public Time getResult(ResultSet resultSet, int colIndex) throws SQLException {
        return resultSet.getTime(colIndex);
    }
}
