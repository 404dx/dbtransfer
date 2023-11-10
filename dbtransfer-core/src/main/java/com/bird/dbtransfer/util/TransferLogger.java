package com.bird.dbtransfer.util;

import org.apache.log4j.Logger;

public class TransferLogger {

    private static final Logger logger = Logger.getLogger(TransferLogger.class);

    public static void error(Throwable e){
        logger.error(e);
    }

    public static void error(String message){
        logger.error(message);
    }

    public static void info(Throwable e){
        logger.info(e);
    }

    public static void info(String message){
        logger.info(message);
    }

    public static void warn(Throwable e){
        logger.warn(e);
    }

    public static void warn(String message){
        logger.warn(message);
    }


    public static void debug(Throwable e){
        logger.debug(e);
    }

    public static void debug(String message){
        logger.debug(message);
    }


}
