package com.infinity.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoggerHelper {
    private final static int kTraceLevel = 4;
    private static Logger logger_ = null;

    public static void init(final String loggerName) {
        logger_ = LoggerFactory.getLogger(loggerName);
    }

    public static void fini() {
        if (logger_ == null)
            return;

        logger_ = null;
    }

    public static void debug(String format, Object... args) {
        if (logger_ == null || !logger_.isDebugEnabled()) return;
        LoggerHelper.d(String.format(format, args), kTraceLevel);
    }

    public static void info(String format, Object... args) {
        if (logger_ == null || !logger_.isInfoEnabled()) return;
        LoggerHelper.i(String.format(format, args), kTraceLevel);
    }

    public static void error(String format, Object... args) {
        if (logger_ == null || !logger_.isErrorEnabled()) return;
        LoggerHelper.e(String.format(format, args), kTraceLevel);
    }

    public static void debug(String msgInfo) {
        if (logger_ == null || !logger_.isDebugEnabled()) return;
        LoggerHelper.d(msgInfo, kTraceLevel);
    }

    public static void info(String msgInfo) {
        if (logger_ == null || !logger_.isInfoEnabled()) return;
        LoggerHelper.i(msgInfo, kTraceLevel);
    }

    public static void error(String msgError) {
        if (logger_ == null || !logger_.isErrorEnabled()) return;
        LoggerHelper.e(msgError, kTraceLevel);
    }

    private static void i(String msgInfo, int traceLevel) {
        if (logger_ == null)
            return;
        logger_.info(String.format("%s%s", whoCalledMe(traceLevel), msgInfo));
    }

    private static void d(String msgDebug, int traceLevel) {
        if (logger_ == null)
            return;

        logger_.debug(String.format("%s%s", whoCalledMe(traceLevel), msgDebug));
    }

    private static void e(String msgError, int traceLevel) {
        if (logger_ == null)
            return;
        logger_.error(String.format("%s%s", whoCalledMe(traceLevel), msgError));
    }

    private static String whoCalledMe(int traceLevel) {
        if (traceLevel == -1) return "";
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTraceElements[traceLevel];
        String filename = caller.getFileName();
        int lineNumber = caller.getLineNumber();
        return "[" + filename + ":" + lineNumber + "]";
    }

    public static void cost(int maxMs, String tagName, Runnable func) {
        long tm = System.currentTimeMillis();
        func.run();
        tm = System.currentTimeMillis() - tm;
        if (tm > maxMs) {
            LoggerHelper.costlog("[%s] cost %s ms beyond %s ms", tagName, tm, maxMs);
        }
    }

    public static void costlog(String format, Object... args) {
        if (!isRelease())
            LoggerHelper.error(format, args);
    }

    public static boolean isRelease() {
        return logger_ == null || !logger_.isDebugEnabled();
    }
}

