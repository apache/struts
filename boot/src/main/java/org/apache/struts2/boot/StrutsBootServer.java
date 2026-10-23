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
import org.apache.catalina.connector.Connector;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.valves.ErrorReportValve;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public final class StrutsBootServer implements AutoCloseable {

    private static final String FILTER_NAME = "struts";

    private final Tomcat tomcat;
    private final Path baseDir;
    private final BootSettings settings;
    private final AtomicBoolean closed = new AtomicBoolean();
    private final Thread shutdownHook = new Thread(this::close, "struts-boot-shutdown");
    private Connector connector;

    private StrutsBootServer(Tomcat tomcat, Path baseDir, BootSettings settings) {
        this.tomcat = tomcat;
        this.baseDir = baseDir;
        this.settings = settings;
    }

    static StrutsBootServer start(ActionRegistry registry, BootSettings settings) {
        Path baseDir = createBaseDir();
        Tomcat tomcat = new Tomcat();
        StrutsBootServer server = new StrutsBootServer(tomcat, baseDir, settings);
        CapturingStrutsFilter filter = new CapturingStrutsFilter();
        try {
            tomcat.setBaseDir(baseDir.toString());
            hideErrorReportDetails(tomcat);
            Path docBase = Files.createDirectory(baseDir.resolve("webapp"));
            Context context = tomcat.addContext(settings.contextPath(), docBase.toString());
            context.addServletContainerInitializer(
                    (classes, servletContext) -> servletContext.setAttribute(ActionRegistry.SERVLET_CONTEXT_ATTRIBUTE, registry),
                    null);
            Tomcat.addServlet(context, "default", new DefaultServlet());
            context.addServletMappingDecoded("/", "default");
            addStrutsFilter(context, filter);

            tomcat.start();
            assertContextStarted(context, filter);
            server.openConnector();
            Runtime.getRuntime().addShutdownHook(server.shutdownHook);
        } catch (Exception e) {
            ConfigurationException failure = e instanceof ConfigurationException configurationException
                    ? configurationException
                    : new ConfigurationException("Struts Boot failed to start: " + e.getMessage(), e);
            server.closeAfterFailure(failure);
            throw failure;
        } catch (Error e) {
            server.closeAfterFailure(e);
            throw e;
        }
        return server;
    }

    public int port() {
        return connector.getLocalPort();
    }

    public String baseUrl() {
        return "http://" + urlHost() + ":" + port() + settings.contextPath();
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

    private void closeAfterFailure(Throwable failure) {
        try {
            close();
        } catch (RuntimeException closeFailure) {
            failure.addSuppressed(closeFailure);
        }
    }

    private void openConnector() {
        Connector created = new Connector();
        created.setPort(settings.port());
        settings.address().ifPresent(address -> {
            if (!created.setProperty("address", address)) {
                throw new ConfigurationException("Invalid struts.boot.address: '" + address + "'");
            }
        });
        connector = created;
        tomcat.setConnector(created);
        if (created.getState() != LifecycleState.STARTED || created.getLocalPort() < 0) {
            throw new ConfigurationException("Struts Boot failed to start: connector state "
                    + created.getState() + " on port " + settings.port());
        }
    }

    private String urlHost() {
        return settings.address()
                .filter(address -> !address.equals("0.0.0.0") && !address.equals("::"))
                .map(address -> address.contains(":") ? "[" + address + "]" : address)
                .orElse("127.0.0.1");
    }

    private void removeShutdownHook() {
        if (Thread.currentThread() == shutdownHook) {
            return;
        }
        try {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        } catch (IllegalStateException ignored) {
            // the JVM is already shutting down
        }
    }

    private static void assertContextStarted(Context context, CapturingStrutsFilter filter) {
        Throwable initFailure = filter.failure.get();
        if (initFailure != null || context.getState() != LifecycleState.STARTED) {
            String reason = initFailure == null ? "context state " + context.getState() : rootMessage(initFailure);
            throw new ConfigurationException("Struts Boot failed to start: " + reason, initFailure);
        }
    }

    private static void hideErrorReportDetails(Tomcat tomcat) {
        ErrorReportValve valve = new ErrorReportValve();
        valve.setShowReport(false);
        valve.setShowServerInfo(false);
        tomcat.getHost().getPipeline().addValve(valve);
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
            String prefix = "struts-boot-" + UUID.randomUUID();
            if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
                return Files.createTempDirectory(prefix, PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------")));
            }
            return Files.createTempDirectory(prefix);
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
            } catch (Throwable e) {
                failure.set(e);
                throw e;
            }
        }
    }
}
