package com.bird.dbtransfer.type;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.*;

public class BlobTypeHandler implements TypeHandler<byte []> {

    @Override
    public void setParameter(PreparedStatement statement, int index, byte[] value, JdbcType jdbcType) throws SQLException {
        if(value == null){
            statement.setNull(index,JDBCType.BLOB.getVendorTypeNumber());
        }else{
            ByteArrayInputStream bis = new ByteArrayInputStream(value);
            statement.setBinaryStream(index, bis, value.length);
        }

    }

    @Override
    public byte[] getResult(ResultSet resultSet, String colName) throws SQLException {
        Blob blob = resultSet.getBlob(colName);
        byte[] returnValue = null;
        if (null != blob) {
            returnValue = blob.getBytes(1L, (int)blob.length());
        }
        return returnValue;
    }

    @Override
    public byte[] getResult(ResultSet resultSet, int colIndex) throws SQLException {
        Blob blob = resultSet.getBlob(colIndex);
        byte[] returnValue = null;
        if (null != blob) {
            returnValue = blob.getBytes(1L, (int)blob.length());
        }
        return returnValue;
    }

}
