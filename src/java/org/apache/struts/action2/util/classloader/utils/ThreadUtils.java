package org.apache.struts.action2.util.classloader.utils;


public final class ThreadUtils {

    public static void sleep(final long pDelay) {
        try {
            Thread.sleep(pDelay);
        } catch (final InterruptedException e) {
        }
    }

}
