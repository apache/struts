package com.opensymphony.webwork.interceptor;

import com.opensymphony.xwork.ActionInvocation;

import java.io.Serializable;

/**
 * Background thread to be executed by the ExecuteAndWaitInterceptor.
 *
 * @author <a href="plightbo@gmail.com">Pat Lightbody</a>
 * @author <a href="jim@jimvanfleet.com">Jim Van Fleet</a>
 */
public class BackgroundProcess implements Serializable {
    protected Object action;
    protected ActionInvocation invocation;
    protected String result;
    protected Exception exception;
    protected boolean done;

    public BackgroundProcess(String threadName, final ActionInvocation invocation, int threadPriority) {
        this.invocation = invocation;
        this.action = invocation.getAction();
        try {
            final Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        beforeInvocation();
                        result = invocation.invokeActionOnly();
                        afterInvocation();
                    } catch (Exception e) {
                        exception = e;
                    }

                    done = true;
                }
            });
            t.setName(threadName);
            t.setPriority(threadPriority);
            t.start();
        } catch (Exception e) {
            exception = e;
        }
    }

    /**
     * called before the background thread determines the result code
     * from the ActionInvocation.
     *
     * @throws Exception any exception thrown will be thrown, in turn, by the ExecuteAndWaitInterceptor
     */
    protected void beforeInvocation() throws Exception {
    }

    /**
     * called after the background thread determines the result code
     * from the ActionInvocation, but before the background thread is
     * marked as done.
     *
     * @throws Exception any exception thrown will be thrown, in turn, by the ExecuteAndWaitInterceptor
     */
    protected void afterInvocation() throws Exception {
    }

    /**
     * Retrieves the action.
     *
     * @return  the action.
     */
    public Object getAction() {
        return action;
    }

    /**
     * Retrieves the action invocation.
     * 
     * @return the action invocation
     */
    public ActionInvocation getInvocation() {
        return invocation;
    }

    /**
     * Gets the result of the background process.
     * 
     * @return  the result; <tt>null</tt> if not done.
     */
    public String getResult() {
        return result;
    }

    /**
     * Gets the exception if any was thrown during the execution of the background process.
     * 
     * @return the exception or <tt>null</tt> if no exception was thrown.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Returns the status of the background process.
     * 
     * @return <tt>true</tt> if finished, <tt>false</tt> otherwise
     */
    public boolean isDone() {
        return done;
    }
}
