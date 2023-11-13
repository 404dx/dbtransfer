package co.qingyu.main;


import co.qingyu.dbtransfer.core.DataBaseTransfer;
import co.qingyu.dbtransfer.core.SimpleDataBaseTransfer;
import co.qingyu.dbtransfer.log.TransferLogger;

public class Application {

    public static void main(String[] args) throws Exception {
        //String path = "D:\\system\\Desktop\\transfer.xml";
        // String path = "D:\\system\\Desktop\\transfer\\TransferConfig.xml";
        String path = "C:\\Users\\Administrator\\Downloads\\dbtransfer-root\\dbtransfer-run\\src\\main\\resources\\config\\TransferConfig.xml";
        try {
            DataBaseTransfer dataBaseTransfer = new SimpleDataBaseTransfer(path);
            dataBaseTransfer.transfer();
        } catch (Exception e) {
            TransferLogger.error(e);
        }
    }

}
