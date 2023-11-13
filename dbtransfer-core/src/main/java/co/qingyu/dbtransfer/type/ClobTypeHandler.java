package co.qingyu.dbtransfer.type;

import java.io.StringReader;
import java.sql.*;

public class ClobTypeHandler implements TypeHandler<String> {


    @Override
    public void setParameter(PreparedStatement statement, int index, String value, JdbcType jdbcType) throws SQLException {
        if (value == null) {
            statement.setNull(index, JDBCType.CLOB.getVendorTypeNumber());
        } else {
            StringReader reader = new StringReader(value);
            statement.setCharacterStream(index, reader, value.length());
        }

    }

    @Override
    public String getResult(ResultSet resultSet, String colName) throws SQLException {
        String value = "";
        Clob clob = resultSet.getClob(colName);
        if (clob != null) {
            int size = (int) clob.length();
            value = clob.getSubString(1L, size);
        }
        return value;
    }

    @Override
    public String getResult(ResultSet resultSet, int colIndex) throws SQLException {
        String value = "";
        Clob clob = resultSet.getClob(colIndex);
        if (clob != null) {
            int size = (int) clob.length();
            value = clob.getSubString(1L, size);
        }
        return value;
    }
}
