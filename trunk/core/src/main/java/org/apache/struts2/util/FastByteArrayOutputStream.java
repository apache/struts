/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * A speedy implementation of ByteArrayOutputStream. It's not synchronized, and it
 * does not copy buffers when it's expanded. There's also no copying of the internal buffer
 * if it's contents is extracted with the writeTo(stream) method.
 *
 */
public class FastByteArrayOutputStream extends OutputStream {

    // Static --------------------------------------------------------
    private static final int DEFAULT_BLOCK_SIZE = 8192;


    private LinkedList buffers;

    // Attributes ----------------------------------------------------
    // internal buffer
    private byte[] buffer;

    // is the stream closed?
    private boolean closed;
    private int blockSize;
    private int index;
    private int size;


    // Constructors --------------------------------------------------
    public FastByteArrayOutputStream() {
        this(DEFAULT_BLOCK_SIZE);
    }

    public FastByteArrayOutputStream(int aSize) {
        blockSize = aSize;
        buffer = new byte[blockSize];
    }


    public int getSize() {
        return size + index;
    }

    public void close() {
        closed = true;
    }

    public byte[] toByteArray() {
        byte[] data = new byte[getSize()];

        // Check if we have a list of buffers
        int pos = 0;

        if (buffers != null) {
            Iterator iter = buffers.iterator();

            while (iter.hasNext()) {
                byte[] bytes = (byte[]) iter.next();
                System.arraycopy(bytes, 0, data, pos, blockSize);
                pos += blockSize;
            }
        }

        // write the internal buffer directly
        System.arraycopy(buffer, 0, data, pos, index);

        return data;
    }

    public String toString() {
        return new String(toByteArray());
    }

    // OutputStream overrides ----------------------------------------
    public void write(int datum) throws IOException {
        if (closed) {
            throw new IOException("Stream closed");
        } else {
            if (index == blockSize) {
                addBuffer();
            }

            // store the byte
            buffer[index++] = (byte) datum;
        }
    }

    public void write(byte[] data, int offset, int length) throws IOException {
        if (data == null) {
            throw new NullPointerException();
        } else if ((offset < 0) || ((offset + length) > data.length) || (length < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (closed) {
            throw new IOException("Stream closed");
        } else {
            if ((index + length) > blockSize) {
                int copyLength;

                do {
                    if (index == blockSize) {
                        addBuffer();
                    }

                    copyLength = blockSize - index;

                    if (length < copyLength) {
                        copyLength = length;
                    }

                    System.arraycopy(data, offset, buffer, index, copyLength);
                    offset += copyLength;
                    index += copyLength;
                    length -= copyLength;
                } while (length > 0);
            } else {
                // Copy in the subarray
                System.arraycopy(data, offset, buffer, index, length);
                index += length;
            }
        }
    }

    // Public
    public void writeTo(OutputStream out) throws IOException {
        // Check if we have a list of buffers
        if (buffers != null) {
            Iterator iter = buffers.iterator();

            while (iter.hasNext()) {
                byte[] bytes = (byte[]) iter.next();
                out.write(bytes, 0, blockSize);
            }
        }

        // write the internal buffer directly
        out.write(buffer, 0, index);
    }

    public void writeTo(RandomAccessFile out) throws IOException {
        // Check if we have a list of buffers
        if (buffers != null) {
            Iterator iter = buffers.iterator();

            while (iter.hasNext()) {
                byte[] bytes = (byte[]) iter.next();
                out.write(bytes, 0, blockSize);
            }
        }

        // write the internal buffer directly
        out.write(buffer, 0, index);
    }

    public void writeTo(Writer out, String encoding) throws IOException {
        // Check if we have a list of buffers
        if (buffers != null) {
            Iterator iter = buffers.iterator();

            while (iter.hasNext()) {
                byte[] bytes = (byte[]) iter.next();

                if (encoding != null) {
                    out.write(new String(bytes, encoding));
                } else {
                    out.write(new String(bytes));
                }
            }
        }

        // write the internal buffer directly
        if (encoding != null) {
            out.write(new String(buffer, 0, index, encoding));
        } else {
            out.write(new String(buffer, 0, index));
        }
    }

    /**
     * Create a new buffer and store the
     * current one in linked list
     */
    protected void addBuffer() {
        if (buffers == null) {
            buffers = new LinkedList();
        }

        buffers.addLast(buffer);

        buffer = new byte[blockSize];
        size += index;
        index = 0;
    }
}
