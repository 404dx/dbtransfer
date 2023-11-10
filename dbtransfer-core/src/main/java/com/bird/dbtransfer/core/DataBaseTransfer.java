package com.bird.dbtransfer.core;

import com.bird.dbtransfer.TransferException;

/**
 * 数据库数据转换
 */
public interface DataBaseTransfer {

    /**
     * 数据数传接口
     * @throws TransferException e
     */
    void transfer() throws TransferException;

}
