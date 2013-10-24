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
package org.apache.struts2.convention;

import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.inject.Container;
import junit.framework.TestCase;
import org.apache.struts2.convention.actions.NoAnnotationAction;
import org.apache.struts2.convention.actions.result.ActionLevelResultAction;
import org.apache.struts2.convention.actions.result.ActionLevelResultsAction;
import org.apache.struts2.convention.actions.result.ClassLevelResultAction;
import org.apache.struts2.convention.actions.result.ClassLevelResultsAction;
import org.apache.struts2.convention.actions.result.GlobalResultAction;
import org.apache.struts2.convention.actions.result.GlobalResultOverrideAction;
import org.apache.struts2.convention.actions.result.InheritedResultExtends;
import org.apache.struts2.convention.actions.result.InheritedResultsExtends;
import org.apache.struts2.convention.actions.result.OverrideInheritedResultExtends;
import org.apache.struts2.convention.actions.result.OverrideResultAction;
import org.apache.struts2.convention.actions.resultpath.ClassLevelResultPathAction;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.dispatcher.ServletDispatcherResult;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

import javax.servlet.ServletContext;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.apache.struts2.convention.ReflectionTools.getAnnotation;

/**
 * <p>
 * This class tests the simple result map builder.
 * </p>
 */
public class DefaultResultMapBuilderTest extends TestCase {
    private Container container;
    private ConventionsService conventionsService;

    public void testBuild() throws Exception {
        ServletContext context = mockServletContext("/WEB-INF/location");

        // Test with a slash
        PackageConfig packageConfig = createPackageConfigBuilder("/namespace");
        this.conventionsService = new ConventionsServiceImpl("/WEB-INF/location");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        Map<String, ResultConfig> results = builder.build(NoAnnotationAction.class, null, "action", packageConfig);
        verify(context, "/WEB-INF/location", results, false);

        // Test without a slash
        context = mockServletContext("/WEB-INF/location");
        packageConfig = createPackageConfigBuilder("namespace");
        this.conventionsService = new ConventionsServiceImpl("/WEB-INF/location");
        builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        results = builder.build(NoAnnotationAction.class, null, "action", packageConfig);
        verify(context, "/WEB-INF/location", results, false);
    }

    public void testResultOverrride() throws Exception {
        ServletContext context = mockServletContext("/WEB-INF/location");

        // Test with a slash
        PackageConfig packageConfig = createPackageConfigBuilder("/namespace");
        this.conventionsService = new ConventionsServiceImpl("/WEB-INF/location");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        Action actionAnn = OverrideResultAction.class.getMethod("execute").getAnnotation(Action.class);
        Map<String, ResultConfig> results = builder.build(OverrideResultAction.class, actionAnn, "action100", packageConfig);
        ResultConfig result = results.get("error");
        assertNotNull(result);
        assertEquals("/WEB-INF/location/namespace/error-overriden.jsp", result.getParams().get("location"));
    }

    public void testGlobalResult() throws Exception {

        ServletContext context = mockServletContext("/WEB-INF/location");
        this.conventionsService = new ConventionsServiceImpl("/WEB-INF/location");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");

        ResultTypeConfig resultType = new ResultTypeConfig.Builder("dispatcher", ServletDispatcherResult.class.getName()).
                addParam("key", "value").addParam("key1", "value1").defaultResultParam("location").build();
        ResultConfig globalError = new ResultConfig.Builder("error", ServletDispatcherResult.class.getName()).
                addParam("location", "/globalError.jsp").
                build();
        PackageConfig packageConfig = new PackageConfig.Builder("package").
                namespace("/namespace").
                defaultResultType("dispatcher").
                addResultTypeConfig(resultType).
                addGlobalResultConfig(globalError).
                build();


        Map<String, ResultConfig> results = builder.build(GlobalResultAction.class, null, "action", packageConfig);
        ResultConfig result = results.get("error");
        assertNotNull(result);
        assertEquals("/globalError.jsp", result.getParams().get("location"));
    }

    public void testGlobalResultOverride() throws Exception {

        ServletContext context = EasyMock.createStrictMock(ServletContext.class);
        String resultPath = "/WEB-INF/location";
        // Setup some mock jsps
        Set<String> resources = new HashSet<String>();
        resources.add(resultPath + "/namespace/action.jsp");
        resources.add(resultPath + "/namespace/action-success.jsp");
        resources.add(resultPath + "/namespace/action-error.jsp");
        EasyMock.expect(context.getResourcePaths(resultPath + "/namespace/")).andReturn(resources);
        EasyMock.replay(context);

        this.conventionsService = new ConventionsServiceImpl("/WEB-INF/location");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");

        ResultTypeConfig resultType = new ResultTypeConfig.Builder("dispatcher", ServletDispatcherResult.class.getName()).
                addParam("key", "value").addParam("key1", "value1").defaultResultParam("location").build();
        ResultConfig globalError = new ResultConfig.Builder("error", ServletDispatcherResult.class.getName()).
                addParam("location", "/globalError.jsp").
                build();
        PackageConfig packageConfig = new PackageConfig.Builder("package").
                namespace("/namespace").
                defaultResultType("dispatcher").
                addResultTypeConfig(resultType).
                addGlobalResultConfig(globalError).
                build();


        Map<String, ResultConfig> results = builder.build(GlobalResultOverrideAction.class, null, "action", packageConfig);
        ResultConfig result = results.get("error");
        assertNotNull(result);
        assertEquals(resultPath + "/namespace/action-error.jsp", result.getParams().get("location"));
    }

    public void testNull() throws Exception {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(context.getResourcePaths("/WEB-INF/location/namespace/")).andReturn(null);
        EasyMock.replay(context);

        // Test with a slash
        PackageConfig packageConfig = createPackageConfigBuilder("/namespace");
        this.conventionsService = new ConventionsServiceImpl("/WEB-INF/location");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        Map<String, ResultConfig> results = builder.build(NoAnnotationAction.class, null, "action", packageConfig);
        assertEquals(0, results.size());
        EasyMock.verify(context);
    }

    public void testResultPath() throws Exception {
        ServletContext context = mockServletContext("/class-level");

        // Test with a result path
        PackageConfig packageConfig = createPackageConfigBuilder("/namespace");
        this.conventionsService = new ConventionsServiceImpl("/not-used");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        Map<String, ResultConfig> results = builder.build(ClassLevelResultPathAction.class, null, "action", packageConfig);
        verify(context, "/class-level", results, false);
    }

    public void testFromServletContextWithBadNames() throws Exception {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);

        // Setup some mock jsps
        Set<String> resources = new HashSet<String>();
        resources.add("/WEB-INF/location/namespace/.something");
        resources.add("/WEB-INF/location/namespace/.somethingelse/");
        EasyMock.expect(context.getResourcePaths("/WEB-INF/location/namespace/")).andReturn(resources);
        EasyMock.replay(context);

        PackageConfig packageConfig = createPackageConfigBuilder("/namespace");

        this.conventionsService = new ConventionsServiceImpl("/WEB-INF/location");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        Map<String, ResultConfig> results = builder.build(NoAnnotationAction.class, null, "no-annotation", packageConfig);
        assertEquals(0, results.size());
        EasyMock.verify(context);

    }

    public void testFromServletContext() throws Exception {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);

        // Setup some mock jsps
        Set<String> resources = new HashSet<String>();
        resources.add("/WEB-INF/location/namespace/no-annotation.ftl");
        resources.add("/WEB-INF/location/namespace/no-annotation-success.jsp");
        resources.add("/WEB-INF/location/namespace/no-annotation-failure.jsp");
        EasyMock.expect(context.getResourcePaths("/WEB-INF/location/namespace/")).andReturn(resources);
        EasyMock.replay(context);

        PackageConfig packageConfig = createPackageConfigBuilder("/namespace");

        this.conventionsService = new ConventionsServiceImpl("/WEB-INF/location");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        Map<String, ResultConfig> results = builder.build(NoAnnotationAction.class, null, "no-annotation", packageConfig);
        assertEquals(4, results.size());
        assertEquals("success", results.get("success").getName());
        assertEquals(3, results.get("success").getParams().size());
        assertEquals("org.apache.struts2.dispatcher.ServletDispatcherResult", results.get("success").getClassName());
        assertEquals("/WEB-INF/location/namespace/no-annotation-success.jsp", results.get("success").getParams().get("location"));
        assertEquals(1, results.get("input").getParams().size());
        assertEquals("org.apache.struts2.views.freemarker.FreemarkerResult", results.get("input").getClassName());
        assertEquals("/WEB-INF/location/namespace/no-annotation.ftl", results.get("input").getParams().get("location"));
        assertEquals(1, results.get("error").getParams().size());
        assertEquals("org.apache.struts2.views.freemarker.FreemarkerResult", results.get("error").getClassName());
        assertEquals("/WEB-INF/location/namespace/no-annotation.ftl", results.get("error").getParams().get("location"));
        assertEquals(3, results.get("failure").getParams().size());
        assertEquals("org.apache.struts2.dispatcher.ServletDispatcherResult", results.get("success").getClassName());
        assertEquals("/WEB-INF/location/namespace/no-annotation-failure.jsp", results.get("failure").getParams().get("location"));
        EasyMock.verify(context);

    }

    public void testFromServletContextNotFlat() throws Exception {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);

        // Setup some mock jsps
        Set<String> resources = new HashSet<String>();
        resources.add("/WEB-INF/location/namespace/no-annotation/index.ftl");
        resources.add("/WEB-INF/location/namespace/no-annotation/success.jsp");
        resources.add("/WEB-INF/location/namespace/no-annotation/failure.jsp");
        EasyMock.expect(context.getResourcePaths("/WEB-INF/location/namespace/no-annotation")).andReturn(resources);
        EasyMock.replay(context);

        PackageConfig packageConfig = createPackageConfigBuilder("/namespace");

        this.conventionsService = new ConventionsServiceImpl("/WEB-INF/location");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        builder.setFlatResultLayout("false");

        Map<String, ResultConfig> results = builder.build(NoAnnotationAction.class, null, "no-annotation", packageConfig);

        assertEquals(3, results.size());

        assertEquals("success", results.get("success").getName());
        assertEquals(3, results.get("success").getParams().size());
        assertEquals("org.apache.struts2.dispatcher.ServletDispatcherResult", results.get("success").getClassName());
        assertEquals("/WEB-INF/location/namespace/no-annotation/success.jsp", results.get("success").getParams().get("location"));

        assertEquals(1, results.get("index").getParams().size());
        assertEquals("org.apache.struts2.views.freemarker.FreemarkerResult", results.get("index").getClassName());
        assertEquals("/WEB-INF/location/namespace/no-annotation/index.ftl", results.get("index").getParams().get("location"));

        assertEquals(3, results.get("failure").getParams().size());
        assertEquals("org.apache.struts2.dispatcher.ServletDispatcherResult", results.get("success").getClassName());
        assertEquals("/WEB-INF/location/namespace/no-annotation/failure.jsp", results.get("failure").getParams().get("location"));
        EasyMock.verify(context);
    }

    public void testIgnoreFilesWithoutName() throws Exception {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);

        // Setup some mock jsps
        Set<String> resources = new HashSet<String>();
        resources.add("/WEB-INF/location/namespace/no-annotation/.svn");
        resources.add("/WEB-INF/location/namespace/no-annotation-success.jsp");
        EasyMock.expect(context.getResourcePaths("/WEB-INF/location/namespace/")).andReturn(resources);
        EasyMock.replay(context);

        PackageConfig packageConfig = createPackageConfigBuilder("/namespace");

        this.conventionsService = new ConventionsServiceImpl("/WEB-INF/location");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        Map<String, ResultConfig> results = builder.build(NoAnnotationAction.class, null, "no-annotation", packageConfig);
        assertEquals(1, results.size());
        assertEquals("success", results.get("success").getName());
        assertEquals(3, results.get("success").getParams().size());
        assertEquals("org.apache.struts2.dispatcher.ServletDispatcherResult", results.get("success").getClassName());
        assertEquals("/WEB-INF/location/namespace/no-annotation-success.jsp", results.get("success").getParams().get("location"));
        EasyMock.verify(context);

    }

    public void testClassLevelSingleResultAnnotation() throws Exception {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);

        // Setup some mock jsps
        Set<String> resources = new HashSet<String>();
        EasyMock.expect(context.getResourcePaths("/WEB-INF/location/namespace/")).andReturn(resources);
        EasyMock.replay(context);

        PackageConfig packageConfig = createPackageConfigBuilder("/namespace");

        this.conventionsService = new ConventionsServiceImpl("/WEB-INF/location");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        Map<String, ResultConfig> results = builder.build(ClassLevelResultAction.class, null, "class-level-result", packageConfig);
        assertEquals(1, results.size());
        assertEquals("error", results.get("error").getName());
        assertEquals(3, results.get("error").getParams().size());
        assertEquals("org.apache.struts2.dispatcher.ServletDispatcherResult", results.get("error").getClassName());
        assertEquals("/WEB-INF/location/namespace/error.jsp", results.get("error").getParams().get("location"));
        assertEquals("value", results.get("error").getParams().get("key"));
        assertEquals("value1", results.get("error").getParams().get("key1"));
        EasyMock.verify(context);
    }

    public void testClassLevelInheritedSingleResultAnnotation() throws Exception {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);

        // Setup some mock jsps
        Set<String> resources = new HashSet<String>();
        EasyMock.expect(context.getResourcePaths("/WEB-INF/location/namespace/")).andReturn(resources);
        EasyMock.replay(context);

        PackageConfig packageConfig = createPackageConfigBuilder("/namespace");

        this.conventionsService = new ConventionsServiceImpl("/WEB-INF/location");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        Map<String, ResultConfig> results = builder.build(InheritedResultExtends.class, null, "result-inheritance-extends", packageConfig);
        assertEquals(1, results.size());
        assertEquals("error", results.get("error").getName());
        assertEquals(3, results.get("error").getParams().size());
        assertEquals("org.apache.struts2.dispatcher.ServletDispatcherResult", results.get("error").getClassName());
        assertEquals("/WEB-INF/location/namespace/error.jsp", results.get("error").getParams().get("location"));
        assertEquals("value", results.get("error").getParams().get("key"));
        assertEquals("value1", results.get("error").getParams().get("key1"));
        EasyMock.verify(context);
    }

     public void testClassLevelOverwriteInheritedSingleResultAnnotation() throws Exception {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);

        // Setup some mock jsps
        Set<String> resources = new HashSet<String>();
        EasyMock.expect(context.getResourcePaths("/WEB-INF/location/namespace/")).andReturn(resources);
        EasyMock.replay(context);

        PackageConfig packageConfig = createPackageConfigBuilder("/namespace");

        this.conventionsService = new ConventionsServiceImpl("/WEB-INF/location");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        Map<String, ResultConfig> results = builder.build(OverrideInheritedResultExtends.class, null, "result-inheritance-extends", packageConfig);
        assertEquals(1, results.size());
        assertEquals("error", results.get("error").getName());
        assertEquals(5, results.get("error").getParams().size());
        assertEquals("org.apache.struts2.dispatcher.ServletDispatcherResult", results.get("error").getClassName());
        assertEquals("/WEB-INF/location/namespace/error.jsp", results.get("error").getParams().get("location"));
        assertEquals("value", results.get("error").getParams().get("key"));
        assertEquals("value1", results.get("error").getParams().get("key1"));
        assertEquals("value-overwritten", results.get("error").getParams().get("key-overwritten"));
        assertEquals("value1-overwritten", results.get("error").getParams().get("key1-overwritten"));
        EasyMock.verify(context);
    }

    public void testClassLevelMultipleResultAnnotation() throws Exception {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);

        // Setup some mock jsps
        Set<String> resources = new HashSet<String>();
        EasyMock.expect(context.getResourcePaths("/WEB-INF/location/namespace/")).andReturn(resources);
        EasyMock.replay(context);

        PackageConfig packageConfig = createPackageConfigBuilder("/namespace");

        this.conventionsService = new ConventionsServiceImpl("/WEB-INF/location");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        Map<String, ResultConfig> results = builder.build(ClassLevelResultsAction.class, null, "class-level-results", packageConfig);
        assertEquals(4, results.size());
        assertEquals("error", results.get("error").getName());
        assertEquals("input", results.get("input").getName());
        assertEquals("success", results.get("success").getName());
        assertEquals("failure", results.get("failure").getName());
        assertEquals(3, results.get("error").getParams().size());
        assertEquals("org.apache.struts2.dispatcher.ServletDispatcherResult", results.get("error").getClassName());
        assertEquals("/WEB-INF/location/namespace/error.jsp", results.get("error").getParams().get("location"));
        assertEquals("ann-value", results.get("error").getParams().get("key"));
        assertEquals("ann-value1", results.get("error").getParams().get("key1"));
        assertEquals(1, results.get("input").getParams().size());
        assertEquals("foo.action", results.get("input").getParams().get("actionName"));
        assertEquals("org.apache.struts2.dispatcher.ServletActionRedirectResult", results.get("input").getClassName());
        assertEquals(3, results.get("failure").getParams().size());
        assertEquals("/WEB-INF/location/namespace/action-failure.jsp", results.get("failure").getParams().get("location"));
        assertEquals("org.apache.struts2.dispatcher.ServletDispatcherResult", results.get("failure").getClassName());
        assertEquals("value", results.get("failure").getParams().get("key"));
        assertEquals("value1", results.get("failure").getParams().get("key1"));
        assertEquals(3, results.get("success").getParams().size());
        assertEquals("/WEB-INF/location/namespace/action-success.jsp", results.get("success").getParams().get("location"));
        assertEquals("org.apache.struts2.dispatcher.ServletDispatcherResult", results.get("success").getClassName());
        assertEquals("value", results.get("success").getParams().get("key"));
        assertEquals("value1", results.get("success").getParams().get("key1"));
        EasyMock.verify(context);
    }

    public void testClassLevelInheritanceMultipleResultAnnotation() throws Exception {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);

        // Setup some mock jsps
        Set<String> resources = new HashSet<String>();
        EasyMock.expect(context.getResourcePaths("/WEB-INF/location/namespace/")).andReturn(resources);
        EasyMock.replay(context);

        PackageConfig packageConfig = createPackageConfigBuilder("/namespace");

        this.conventionsService = new ConventionsServiceImpl("/WEB-INF/location");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        Map<String, ResultConfig> results = builder.build(InheritedResultsExtends.class, null, "class-level-results", packageConfig);
        assertEquals(4, results.size());
        assertEquals("error", results.get("error").getName());
        assertEquals("input", results.get("input").getName());
        assertEquals("success", results.get("success").getName());
        assertEquals("failure", results.get("failure").getName());
        assertEquals(3, results.get("error").getParams().size());
        assertEquals("org.apache.struts2.dispatcher.ServletDispatcherResult", results.get("error").getClassName());
        assertEquals("/WEB-INF/location/namespace/error.jsp", results.get("error").getParams().get("location"));
        assertEquals("ann-value", results.get("error").getParams().get("key"));
        assertEquals("ann-value1", results.get("error").getParams().get("key1"));
        assertEquals(1, results.get("input").getParams().size());
        assertEquals("foo.action", results.get("input").getParams().get("actionName"));
        assertEquals("org.apache.struts2.dispatcher.ServletActionRedirectResult", results.get("input").getClassName());
        assertEquals(3, results.get("failure").getParams().size());
        assertEquals("/WEB-INF/location/namespace/action-failure.jsp", results.get("failure").getParams().get("location"));
        assertEquals("org.apache.struts2.dispatcher.ServletDispatcherResult", results.get("failure").getClassName());
        assertEquals("value", results.get("failure").getParams().get("key"));
        assertEquals("value1", results.get("failure").getParams().get("key1"));
        assertEquals(3, results.get("success").getParams().size());
        assertEquals("/WEB-INF/location/namespace/action-success.jsp", results.get("success").getParams().get("location"));
        assertEquals("org.apache.struts2.dispatcher.ServletDispatcherResult", results.get("success").getClassName());
        assertEquals("value", results.get("success").getParams().get("key"));
        assertEquals("value1", results.get("success").getParams().get("key1"));
        EasyMock.verify(context);
    }

    public void testActionLevelSingleResultAnnotation() throws Exception {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);

        // Setup some mock jsps
        Set<String> resources = new HashSet<String>();
        EasyMock.expect(context.getResourcePaths("/WEB-INF/location/namespace/")).andReturn(resources);
        EasyMock.replay(context);

        PackageConfig packageConfig = createPackageConfigBuilder("/namespace");

        this.conventionsService = new ConventionsServiceImpl("/WEB-INF/location");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        Map<String, ResultConfig> results = builder.build(ActionLevelResultAction.class, getAnnotation(ActionLevelResultAction.class, "execute", Action.class), "action-level-result", packageConfig);
        assertEquals(1, results.size());
        assertEquals("success", results.get("success").getName());
        assertEquals(3, results.get("success").getParams().size());
        assertEquals("org.apache.struts2.dispatcher.ServletDispatcherResult", results.get("success").getClassName());
        assertEquals("/WEB-INF/location/namespace/action-success.jsp", results.get("success").getParams().get("location"));
        assertEquals("value", results.get("success").getParams().get("key"));
        assertEquals("value1", results.get("success").getParams().get("key1"));
        EasyMock.verify(context);
    }

    public void testActionLevelMultipleResultAnnotation() throws Exception {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);

        // Setup some mock jsps
        Set<String> resources = new HashSet<String>();
        EasyMock.expect(context.getResourcePaths("/WEB-INF/location/namespace/")).andReturn(resources);
        EasyMock.replay(context);

        PackageConfig packageConfig = createPackageConfigBuilder("/namespace");

        this.conventionsService = new ConventionsServiceImpl("/WEB-INF/location");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        Map<String, ResultConfig> results = builder.build(ActionLevelResultsAction.class, getAnnotation(ActionLevelResultsAction.class, "execute", Action.class), "action-level-results", packageConfig);
        assertEquals(4, results.size());
        assertEquals("error", results.get("error").getName());
        assertEquals("input", results.get("input").getName());
        assertEquals("success", results.get("success").getName());
        assertEquals("failure", results.get("failure").getName());
        assertEquals(3, results.get("error").getParams().size());
        assertEquals("org.apache.struts2.dispatcher.ServletDispatcherResult", results.get("error").getClassName());
        assertEquals("/WEB-INF/location/namespace/error.jsp", results.get("error").getParams().get("location"));
        assertEquals("value", results.get("success").getParams().get("key"));
        assertEquals("value1", results.get("success").getParams().get("key1"));
        assertEquals(1, results.get("input").getParams().size());
        assertEquals("foo.action", results.get("input").getParams().get("actionName"));
        assertEquals("org.apache.struts2.dispatcher.ServletActionRedirectResult", results.get("input").getClassName());
        assertEquals(3, results.get("failure").getParams().size());
        assertEquals("/WEB-INF/location/namespace/action-failure.jsp", results.get("failure").getParams().get("location"));
        assertEquals("org.apache.struts2.dispatcher.ServletDispatcherResult", results.get("failure").getClassName());
        assertEquals(3, results.get("success").getParams().size());
        assertEquals("/WEB-INF/location/namespace/action-success.jsp", results.get("success").getParams().get("location"));
        assertEquals("org.apache.struts2.dispatcher.ServletDispatcherResult", results.get("success").getClassName());
        EasyMock.verify(context);
    }

    public void testClassPath() throws Exception {
        ServletContext context = EasyMock.createNiceMock(ServletContext.class);

        ResultTypeConfig resultType = new ResultTypeConfig.Builder("freemarker", "org.apache.struts2.dispatcher.ServletDispatcherResult").
                defaultResultParam("location").build();
        PackageConfig packageConfig = new PackageConfig.Builder("package").
                defaultResultType("dispatcher").addResultTypeConfig(resultType).build();

        this.conventionsService = new ConventionsServiceImpl("/WEB-INF/component");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        Map<String, ResultConfig> results = builder.build(NoAnnotationAction.class, null, "no-annotation", packageConfig);
        assertEquals(4, results.size());
        assertEquals("input", results.get("input").getName());
        assertEquals("error", results.get("error").getName());
        assertEquals("success", results.get("success").getName());
        assertEquals("foo", results.get("foo").getName());
        assertEquals(1, results.get("success").getParams().size());
        assertEquals("/WEB-INF/component/no-annotation.ftl", results.get("success").getParams().get("location"));
        assertEquals(1, results.get("input").getParams().size());
        assertEquals("/WEB-INF/component/no-annotation.ftl", results.get("input").getParams().get("location"));
        assertEquals(1, results.get("error").getParams().size());
        assertEquals("/WEB-INF/component/no-annotation.ftl", results.get("error").getParams().get("location"));
        assertEquals(1, results.get("foo").getParams().size());
        assertEquals("/WEB-INF/component/no-annotation-foo.ftl", results.get("foo").getParams().get("location"));
    }

    private PackageConfig createPackageConfigBuilder(String namespace) {
        ResultTypeConfig resultType = new ResultTypeConfig.Builder("dispatcher", "org.apache.struts2.dispatcher.ServletDispatcherResult").
                addParam("key", "value").addParam("key1", "value1").defaultResultParam("location").build();

        ResultTypeConfig redirect = new ResultTypeConfig.Builder("redirectAction",
                "org.apache.struts2.dispatcher.ServletActionRedirectResult").defaultResultParam("actionName").build();

        ResultTypeConfig ftlResultType = new ResultTypeConfig.Builder("freemarker",
                "org.apache.struts2.views.freemarker.FreemarkerResult").defaultResultParam("location").build();

        return new PackageConfig.Builder("package").
                namespace(namespace).
                defaultResultType("dispatcher").
                addResultTypeConfig(resultType).
                addResultTypeConfig(redirect).
                addResultTypeConfig(ftlResultType).build();
    }

    private ServletContext mockServletContext(String resultPath) {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);

        // Setup some mock jsps
        Set<String> resources = new HashSet<String>();
        resources.add(resultPath + "/namespace/action.jsp");
        resources.add(resultPath + "/namespace/action-success.jsp");
        resources.add(resultPath + "/namespace/action-failure.jsp");
        EasyMock.expect(context.getResourcePaths(resultPath + "/namespace/")).andReturn(resources);
        EasyMock.replay(context);
        return context;
    }

    private void verify(ServletContext context, String resultPath, Map<String, ResultConfig> results,
                        boolean redirect) {
        assertEquals(4, results.size());
        assertEquals("success", results.get("success").getName());
        assertEquals("input", results.get("input").getName());
        assertEquals("error", results.get("error").getName());
        assertEquals("failure", results.get("failure").getName());
        assertEquals(3, results.get("success").getParams().size());
        assertEquals(resultPath + "/namespace/action-success.jsp", results.get("success").getParams().get("location"));
        assertEquals("value", results.get("success").getParams().get("key"));
        assertEquals("value1", results.get("success").getParams().get("key1"));
        assertEquals(3, results.get("failure").getParams().size());
        assertEquals(resultPath + "/namespace/action-failure.jsp", results.get("failure").getParams().get("location"));
        assertEquals("value", results.get("failure").getParams().get("key"));
        assertEquals("value1", results.get("failure").getParams().get("key1"));

        if (redirect) {
            assertEquals(1, results.get("input").getParams().size());
            assertEquals("foo.action", results.get("input").getParams().get("actionName"));
        } else {
            assertEquals(3, results.get("input").getParams().size());
            assertEquals(resultPath + "/namespace/action.jsp", results.get("input").getParams().get("location"));
            assertEquals("value", results.get("input").getParams().get("key"));
            assertEquals("value1", results.get("input").getParams().get("key1"));
        }

        assertEquals(3, results.get("error").getParams().size());
        assertEquals(resultPath + "/namespace/action.jsp", results.get("error").getParams().get("location"));
        assertEquals("value", results.get("error").getParams().get("key"));
        assertEquals("value1", results.get("error").getParams().get("key1"));
        EasyMock.verify(context);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.container = EasyMock.createNiceMock(Container.class);
        EasyMock.expect(container.getInstance(String.class, ConventionConstants.CONVENTION_CONVENTIONS_SERVICE)).andReturn("convention").anyTimes();
        EasyMock.expect(container.getInstance(ConventionsService.class, "convention")).andAnswer(new IAnswer<ConventionsService>() {
            public ConventionsService answer() throws Throwable {
                return DefaultResultMapBuilderTest.this.conventionsService;
            }
        }).anyTimes();
        EasyMock.replay(this.container);
    }
}
