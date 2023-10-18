/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.jsp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.jsp.JspWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Adapts a {@link JspWriter} to a {@link PrintWriter}, swallowing {@link IOException}.
 */
public class JspPrintWriterAdapter extends PrintWriter {

    /**
     * The JSP writer.
     */
    private final JspWriter writer;

    /**
     * The logging object.
     */
    private static final Logger LOG = LogManager.getLogger(JspPrintWriterAdapter.class);

    /**
     * Constructor.
     *
     * @param writer The JSP writer.
     */
    public JspPrintWriterAdapter(JspWriter writer) {
        super(writer);
        this.writer = writer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrintWriter append(char c) {
        try {
            writer.append(c);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrintWriter append(CharSequence csq, int start, int end) {
        try {
            writer.append(csq, start, end);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrintWriter append(CharSequence csq) {
        try {
            writer.append(csq);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() {
        try {
            writer.flush();
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void print(boolean b) {
        try {
            writer.print(b);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void print(char c) {
        try {
            writer.print(c);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void print(char[] s) {
        try {
            writer.print(s);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void print(double d) {
        try {
            writer.print(d);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void print(float f) {
        try {
            writer.print(f);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void print(int i) {
        try {
            writer.print(i);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void print(long l) {
        try {
            writer.print(l);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void print(Object obj) {
        try {
            writer.print(obj);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void print(String s) {
        try {
            writer.print(s);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void println() {
        try {
            writer.println();
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void println(boolean x) {
        try {
            writer.println(x);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void println(char x) {
        try {
            writer.println(x);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void println(char[] x) {
        try {
            writer.println(x);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void println(double x) {
        try {
            writer.println(x);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void println(float x) {
        try {
            writer.println(x);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void println(int x) {
        try {
            writer.println(x);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void println(long x) {
        try {
            writer.println(x);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void println(Object x) {
        try {
            writer.println(x);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void println(String x) {
        try {
            writer.println(x);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(char[] buf, int off, int len) {
        try {
            writer.write(buf, off, len);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(char[] buf) {
        try {
            writer.write(buf);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(int c) {
        try {
            writer.write(c);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(String s, int off, int len) {
        try {
            writer.write(s, off, len);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(String s) {
        try {
            writer.write(s);
        } catch (IOException e) {
            LOG.error("Error when writing in JspWriter", e);
            setError();
        }
    }
}
