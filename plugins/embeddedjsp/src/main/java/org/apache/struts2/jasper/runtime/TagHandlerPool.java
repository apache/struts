/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.struts2.jasper.runtime;

import javax.servlet.ServletConfig;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.apache.AnnotationProcessor;
import org.apache.struts2.jasper.Constants;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/**
 * Pool of tag handlers that can be reused.
 *
 * @author Jan Luehe
 */
public class TagHandlerPool {

    private Tag[] handlers;

    public static String OPTION_TAGPOOL="tagpoolClassName";
    public static String OPTION_MAXSIZE="tagpoolMaxSize";

    private Log log = LogFactory.getLog(TagHandlerPool.class);
    
    // index of next available tag handler
    private int current;
    protected AnnotationProcessor annotationProcessor = null;

    public static TagHandlerPool getTagHandlerPool( ServletConfig config) {
        TagHandlerPool result=null;

        String tpClassName=getOption( config, OPTION_TAGPOOL, null);
        if( tpClassName != null ) {
            try {
                Class c=Class.forName( tpClassName );
                result=(TagHandlerPool)c.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                result=null;
            }
        }
        if( result==null ) result=new TagHandlerPool();
        result.init(config);

        return result;
    }

    protected void init( ServletConfig config ) {
        int maxSize=-1;
        String maxSizeS=getOption(config, OPTION_MAXSIZE, null);
        if( maxSizeS != null ) {
            try {
                maxSize=Integer.parseInt(maxSizeS);
            } catch( Exception ex) {
                maxSize=-1;
            }
        }
        if( maxSize <0  ) {
            maxSize=Constants.MAX_POOL_SIZE;
        }
        this.handlers = new Tag[maxSize];
        this.current = -1;
        this.annotationProcessor = 
            (AnnotationProcessor) config.getServletContext().getAttribute(AnnotationProcessor.class.getName());
    }

    /**
     * Constructs a tag handler pool with the default capacity.
     */
    public TagHandlerPool() {
	// Nothing - jasper generated servlets call the other constructor,
        // this should be used in future + init .
    }

    /**
     * Constructs a tag handler pool with the given capacity.
     *
     * @param capacity Tag handler pool capacity
     * @deprecated Use static getTagHandlerPool
     */
    public TagHandlerPool(int capacity) {
	this.handlers = new Tag[capacity];
	this.current = -1;
    }

    /**
     * Gets the next available tag handler from this tag handler pool,
     * instantiating one if this tag handler pool is empty.
     *
     * @param handlerClass Tag handler class
     *
     * @return Reused or newly instantiated tag handler
     *
     * @throws JspException if a tag handler cannot be instantiated
     */
    public Tag get(Class handlerClass) throws JspException {
	Tag handler = null;
        synchronized( this ) {
            if (current >= 0) {
                handler = handlers[current--];
                return handler;
            }
        }

        // Out of sync block - there is no need for other threads to
        // wait for us to construct a tag for this thread.
        try {
            Tag instance = (Tag) handlerClass.newInstance();
            AnnotationHelper.postConstruct(annotationProcessor, instance);
            return instance;
        } catch (Exception e) {
            throw new JspException(e.getMessage(), e);
        }
    }

    /**
     * Adds the given tag handler to this tag handler pool, unless this tag
     * handler pool has already reached its capacity, in which case the tag
     * handler's release() method is called.
     *
     * @param handler Tag handler to add to this tag handler pool
     */
    public void reuse(Tag handler) {
        synchronized( this ) {
            if (current < (handlers.length - 1)) {
                handlers[++current] = handler;
                return;
            }
        }
        // There is no need for other threads to wait for us to release
        handler.release();
        if (annotationProcessor != null) {
            try {
                AnnotationHelper.preDestroy(annotationProcessor, handler);
            } catch (Exception e) {
                log.warn("Error processing preDestroy on tag instance of " 
                        + handler.getClass().getName(), e);
            }
        }
    }

    /**
     * Calls the release() method of all available tag handlers in this tag
     * handler pool.
     */
    public synchronized void release() {
        for (int i = current; i >= 0; i--) {
            handlers[i].release();
            if (annotationProcessor != null) {
                try {
                    AnnotationHelper.preDestroy(annotationProcessor, handlers[i]);
                } catch (Exception e) {
                    log.warn("Error processing preDestroy on tag instance of " 
                            + handlers[i].getClass().getName(), e);
                }
            }
        }
    }

    protected static String getOption( ServletConfig config, String name, String defaultV) {
        if( config == null ) return defaultV;

        String value=config.getInitParameter(name);
        if( value != null ) return value;
        if( config.getServletContext() ==null )
            return defaultV;
        value=config.getServletContext().getInitParameter(name);
        if( value!=null ) return value;
        return defaultV;
    }

}

