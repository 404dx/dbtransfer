package com.bird.dbtransfer.db;

import java.sql.Connection;

public interface DataSource {

    Connection getConnection();

    String getType();


}
