/*
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
package org.apache.struts2.boot;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public final class StrutsBootServer implements AutoCloseable {

    private static final String FILTER_NAME = "struts";

    private final Tomcat tomcat;
    private final Path baseDir;
    private final String contextPath;
    private final AtomicBoolean closed = new AtomicBoolean();
    private final Thread shutdownHook = new Thread(this::close, "struts-boot-shutdown");

    private StrutsBootServer(Tomcat tomcat, Path baseDir, String contextPath) {
        this.tomcat = tomcat;
        this.baseDir = baseDir;
        this.contextPath = contextPath;
    }

    static StrutsBootServer start(ActionRegistry registry, BootSettings settings) {
        Path baseDir = createBaseDir();
        Tomcat tomcat = new Tomcat();
        StrutsBootServer server = new StrutsBootServer(tomcat, baseDir, settings.contextPath());
        CapturingStrutsFilter filter = new CapturingStrutsFilter();
        try {
            tomcat.setBaseDir(baseDir.toString());
            tomcat.setPort(settings.port());
            Context context = tomcat.addContext(settings.contextPath(), baseDir.toString());
            context.addServletContainerInitializer(
                    (classes, servletContext) -> servletContext.setAttribute(ActionRegistry.SERVLET_CONTEXT_ATTRIBUTE, registry),
                    null);
            Tomcat.addServlet(context, "default", new DefaultServlet());
            context.addServletMappingDecoded("/", "default");
            addStrutsFilter(context, filter);

            tomcat.start();
            server.assertStarted(context, filter);
        } catch (Exception e) {
            ConfigurationException failure = e instanceof ConfigurationException configurationException
                    ? configurationException
                    : new ConfigurationException("Struts Boot failed to start: " + e.getMessage(), e);
            try {
                server.close();
            } catch (RuntimeException closeFailure) {
                failure.addSuppressed(closeFailure);
            }
            throw failure;
        }
        Runtime.getRuntime().addShutdownHook(server.shutdownHook);
        return server;
    }

    public int port() {
        return tomcat.getConnector().getLocalPort();
    }

    public String baseUrl() {
        return "http://127.0.0.1:" + port() + contextPath;
    }

    public Path baseDir() {
        return baseDir;
    }

    public void await() {
        tomcat.getServer().await();
    }

    @Override
    public void close() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        try {
            tomcat.stop();
            tomcat.destroy();
        } catch (Exception e) {
            throw new ConfigurationException("Struts Boot failed to stop Tomcat", e);
        } finally {
            deleteRecursively(baseDir);
            removeShutdownHook();
        }
    }

    private void assertStarted(Context context, CapturingStrutsFilter filter) {
        Throwable initFailure = filter.failure.get();
        if (initFailure != null || context.getState() != LifecycleState.STARTED) {
            String reason = initFailure == null ? "context state " + context.getState() : rootMessage(initFailure);
            throw new ConfigurationException("Struts Boot failed to start: " + reason, initFailure);
        }
        if (tomcat.getConnector().getState() != LifecycleState.STARTED || port() < 0) {
            throw new ConfigurationException("Struts Boot failed to start: connector state "
                    + tomcat.getConnector().getState() + " on port " + tomcat.getConnector().getPort());
        }
    }

    private void removeShutdownHook() {
        if (Thread.currentThread() == shutdownHook) {
            return;
        }
        try {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        } catch (IllegalStateException | IllegalArgumentException ignored) {
            // JVM already shutting down, or the hook was never registered
        }
    }

    private static void addStrutsFilter(Context context, CapturingStrutsFilter filter) {
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName(FILTER_NAME);
        filterDef.setFilter(filter);
        filterDef.setFilterClass(filter.getClass().getName());
        filterDef.addInitParameter("configProviders", RegistryConfigurationProvider.class.getName());
        context.addFilterDef(filterDef);
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(FILTER_NAME);
        filterMap.addURLPattern("/*");
        context.addFilterMap(filterMap);
    }

    private static Path createBaseDir() {
        try {
            return Files.createTempDirectory("struts-boot-" + UUID.randomUUID());
        } catch (IOException e) {
            throw new ConfigurationException("Cannot create the Tomcat base directory", e);
        }
    }

    private static void deleteRecursively(Path dir) {
        if (!Files.exists(dir)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String rootMessage(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return root.getMessage();
    }

    private static final class CapturingStrutsFilter extends StrutsPrepareAndExecuteFilter {

        private final AtomicReference<Throwable> failure = new AtomicReference<>();

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            try {
                super.init(filterConfig);
            } catch (RuntimeException | ServletException e) {
                failure.set(e);
                throw e;
            }
        }
    }
}
