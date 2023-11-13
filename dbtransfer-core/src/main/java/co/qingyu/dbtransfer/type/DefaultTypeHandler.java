package co.qingyu.dbtransfer.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DefaultTypeHandler implements TypeHandler<String> {

    @Override
    public void setParameter(PreparedStatement statement, int index, String value, JdbcType jdbcType) throws SQLException {
        if (value == null) {
            statement.setString(index, null);
        } else {
            statement.setString(index, value);
        }
    }

    @Override
    public String getResult(ResultSet resultSet, String colName) throws SQLException {
        return resultSet.getString(colName);
    }

    @Override
    public String getResult(ResultSet resultSet, int colIndex) throws SQLException {
        return resultSet.getString(colIndex);
    }
}
