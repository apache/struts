/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2;

import javax.servlet.Servlet;
import java.util.concurrent.*;

/**
 * Caches servlet instances by jsp location. If a requested jsp is not in the cache, "get"
 * will block and wait for the jsp to be loaded
 */
public class ServletCache {
    protected final ConcurrentMap<String, Future<Servlet>> cache
            = new ConcurrentHashMap<String, Future<Servlet>>();

    private final JSPLoader jspLoader = new JSPLoader();

    public void clear() {
        cache.clear();        
    }

    public Servlet get(final String location) throws InterruptedException {
        while (true) {
            Future<Servlet> future = cache.get(location);
            if (future == null) {
                Callable<Servlet> loadJSPCallable = new Callable<Servlet>() {
                    public Servlet call() throws Exception {
                        return jspLoader.load(location);
                    }
                };
                FutureTask<Servlet> futureTask = new FutureTask<Servlet>(loadJSPCallable);
                future = cache.putIfAbsent(location, futureTask);
                if (future == null) {
                    future = futureTask;
                    futureTask.run();
                }
            }
            try {
                return future.get();
            } catch (CancellationException e) {
                cache.remove(location, future);
            } catch (ExecutionException e) {
                throw launderThrowable(e.getCause());
            }
        }
    }

    public static RuntimeException launderThrowable(Throwable t) {
        if (t instanceof RuntimeException)
            return (RuntimeException) t;
        else if (t instanceof Error)
            throw (Error) t;
        else
            throw new IllegalStateException(t);
    }

}

