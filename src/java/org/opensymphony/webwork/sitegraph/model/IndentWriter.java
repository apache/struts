package com.opensymphony.webwork.sitegraph.model;

import java.io.IOException;
import java.io.Writer;

/**
 * User: plightbo
 * Date: Jun 26, 2005
 * Time: 5:02:14 PM
 */
public class IndentWriter extends Writer {
    Writer writer;

    public IndentWriter(Writer writer) {
        this.writer = writer;
    }

    public void close() throws IOException {
        writer.close();
    }

    public void flush() throws IOException {
        writer.flush();
    }

    public void write(String str) throws IOException {
        write(str, false);
    }

    public void write(String str, boolean noIndent) throws IOException {
        if (!noIndent) {
            str = "    " + str;
        }

        if (writer instanceof IndentWriter) {
            ((IndentWriter) writer).write(str, false);
        } else {
            writer.write(str + "\n");
        }
    }

    public void write(char cbuf[], int off, int len) throws IOException {
        writer.write(cbuf, off, len);
    }
}
