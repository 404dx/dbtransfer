package co.qingyu.dbtransfer.core;

import co.qingyu.dbtransfer.TransferException;

/**
 * 数据库数据转换
 */
public interface DataBaseTransfer {

    /**
     * 数据数传接口
     *
     * @throws TransferException e
     */
    void transfer() throws TransferException;

}
