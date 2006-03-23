package com.opensymphony.webwork.views.freemarker.tags;

import com.opensymphony.webwork.components.Component;
import freemarker.template.TemplateModelException;
import freemarker.template.TransformControl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * User: plightbo
 * Date: Jul 18, 2005
 * Time: 8:00:24 PM
 */
public class CallbackWriter extends Writer implements TransformControl {
    private Component bean;
    private Writer writer;
    private StringWriter body;
    private boolean afterBody = false;

    public CallbackWriter(Component bean, Writer writer) {
        this.bean = bean;
        this.writer = writer;

        if (bean.usesBody()) {
            this.body = new StringWriter();
        }
    }

    public void close() throws IOException {
        if (bean.usesBody()) {
            body.close();
        }
    }

    public void flush() throws IOException {
        writer.flush();

        if (bean.usesBody()) {
            body.flush();
        }
    }

    public void write(char cbuf[], int off, int len) throws IOException {
        if (bean.usesBody() && !afterBody) {
            body.write(cbuf, off, len);
        } else {
            writer.write(cbuf, off, len);
        }
    }

    public int onStart() throws TemplateModelException, IOException {
        boolean result = bean.start(this);

        if (result) {
            return EVALUATE_BODY;
        } else {
            return SKIP_BODY;
        }
    }

    public int afterBody() throws TemplateModelException, IOException {
        afterBody = true;
        boolean result = bean.end(this, bean.usesBody() ? body.toString() : "");

        if (result) {
            return REPEAT_EVALUATION;
        } else {
            return END_EVALUATION;
        }
    }

    public void onError(Throwable throwable) throws Throwable {
        throw throwable;
    }

    public Component getBean() {
        return bean;
    }
}
