package co.qingyu.dbtransfer.log;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransferLogger {

    private static final Logger logger = LoggerFactory.getLogger(TransferLogger.class);

    public static void error(Throwable e) {
        error(e.getMessage());
    }

    public static void error(String message) {
        logger.error(message);
    }

    public static void info(Throwable e) {
        info(e.getMessage());
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void info(String var1, Object... var2) {
        logger.info(var1, var2);
    }

    public static void warn(Throwable e) {
        warn(e.getMessage());
    }

    public static void warn(String message) {
        logger.warn(message);
    }


    public static void debug(Throwable e) {
        debug(e.getMessage());
    }

    public static void debug(String message) {
        logger.debug(message);
    }


}
