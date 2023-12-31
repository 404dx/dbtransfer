package co.qingyu.dbtransfer;

public class TransferException extends Exception {

    public TransferException() {
        super();
    }

    public TransferException(String message) {
        super(message);
    }

    public TransferException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransferException(Throwable cause) {
        super(cause);
    }

    protected TransferException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
