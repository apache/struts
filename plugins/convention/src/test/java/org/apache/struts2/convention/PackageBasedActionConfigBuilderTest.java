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

import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.factory.DefaultInterceptorFactory;
import com.opensymphony.xwork2.factory.DefaultResultFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Scope.Strategy;
import com.opensymphony.xwork2.ognl.OgnlReflectionProvider;
import com.opensymphony.xwork2.util.fs.DefaultFileManager;
import com.opensymphony.xwork2.util.fs.DefaultFileManagerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import junit.framework.TestCase;
import org.apache.struts2.convention.actions.DefaultResultPathAction;
import org.apache.struts2.convention.actions.NoAnnotationAction;
import org.apache.struts2.convention.actions.Skip;
import org.apache.struts2.convention.actions.action.ActionNameAction;
import org.apache.struts2.convention.actions.action.ActionNamesAction;
import org.apache.struts2.convention.actions.action.ClassLevelAnnotationAction;
import org.apache.struts2.convention.actions.action.ClassLevelAnnotationDefaultMethodAction;
import org.apache.struts2.convention.actions.action.ClassLevelAnnotationsAction;
import org.apache.struts2.convention.actions.action.ClassLevelAnnotationsDefaultMethodAction;
import org.apache.struts2.convention.actions.action.ClassNameAction;
import org.apache.struts2.convention.actions.action.SingleActionNameAction;
import org.apache.struts2.convention.actions.action.TestAction;
import org.apache.struts2.convention.actions.action.TestExtends;
import org.apache.struts2.convention.actions.chain.ChainedAction;
import org.apache.struts2.convention.actions.defaultinterceptor.SingleActionNameAction2;
import org.apache.struts2.convention.actions.exception.ExceptionsActionLevelAction;
import org.apache.struts2.convention.actions.exception.ExceptionsMethodLevelAction;
import org.apache.struts2.convention.actions.exclude.ExcludeAction;
import org.apache.struts2.convention.actions.interceptor.ActionLevelInterceptor2Action;
import org.apache.struts2.convention.actions.interceptor.ActionLevelInterceptor3Action;
import org.apache.struts2.convention.actions.interceptor.ActionLevelInterceptorAction;
import org.apache.struts2.convention.actions.interceptor.InterceptorsAction;
import org.apache.struts2.convention.actions.namespace.ActionLevelNamespaceAction;
import org.apache.struts2.convention.actions.namespace.ClassLevelNamespaceAction;
import org.apache.struts2.convention.actions.namespace.PackageLevelNamespaceAction;
import org.apache.struts2.convention.actions.namespace2.DefaultNamespaceAction;
import org.apache.struts2.convention.actions.namespace3.ActionLevelNamespacesAction;
import org.apache.struts2.convention.actions.namespace4.ActionAndPackageLevelNamespacesAction;
import org.apache.struts2.convention.actions.params.ActionParamsMethodLevelAction;
import org.apache.struts2.convention.actions.parentpackage.ClassLevelParentPackageAction;
import org.apache.struts2.convention.actions.parentpackage.PackageLevelParentPackageAction;
import org.apache.struts2.convention.actions.parentpackage.sub.ClassLevelParentPackageChildAction;
import org.apache.struts2.convention.actions.parentpackage.sub.PackageLevelParentPackageChildAction;
import org.apache.struts2.convention.actions.result.ActionLevelResultAction;
import org.apache.struts2.convention.actions.result.ActionLevelResultsAction;
import org.apache.struts2.convention.actions.result.ClassLevelResultAction;
import org.apache.struts2.convention.actions.result.ClassLevelResultsAction;
import org.apache.struts2.convention.actions.result.GlobalResultAction;
import org.apache.struts2.convention.actions.result.GlobalResultOverrideAction;
import org.apache.struts2.convention.actions.result.InheritedResultExtends;
import org.apache.struts2.convention.actions.result.OverrideResultAction;
import org.apache.struts2.convention.actions.resultpath.ClassLevelResultPathAction;
import org.apache.struts2.convention.actions.resultpath.PackageLevelResultPathAction;
import org.apache.struts2.convention.actions.skip.Index;
import org.apache.struts2.convention.actions.transactions.TransNameAction;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.dontfind.DontFindMeAction;
import org.apache.struts2.dispatcher.ServletDispatcherResult;
import org.easymock.EasyMock;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.struts2.convention.ReflectionTools.getAnnotation;
import static org.easymock.EasyMock.checkOrder;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.verify;

/**
 * <p>
 * This is a test for the package based name builder.
 * </p>
 */
public class PackageBasedActionConfigBuilderTest extends TestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ActionContext context = new ActionContext(new HashMap<String, Object>());
        context.setContainer(new DummyContainer());
        ActionContext.setContext(context);
    }

    public void testActionPackages() throws MalformedURLException {
        run("org.apache.struts2.convention.actions", null, null);
    }

    public void testPackageLocators() throws MalformedURLException {
        run(null, "actions,dontfind", null);
    }

    public void testExcludedPackages() throws MalformedURLException {
        run(null, "actions", "dontfind");
    }

    public void testJustExcludedPackages() throws MalformedURLException {
        run("org.apache.struts2.convention.actions", null, "org.apache.struts2.convention.actions.exclude");
    }

    private void run(String actionPackages, String packageLocators, String excludePackages) throws MalformedURLException {
        //setup interceptors
        List<InterceptorConfig> defaultInterceptors = new ArrayList<InterceptorConfig>();
        defaultInterceptors.add(makeInterceptorConfig("interceptor-1"));
        defaultInterceptors.add(makeInterceptorConfig("interceptor-2"));
        defaultInterceptors.add(makeInterceptorConfig("interceptor-3"));

        //setup interceptor stacks
        List<InterceptorStackConfig> defaultInterceptorStacks = new ArrayList<InterceptorStackConfig>();
        InterceptorMapping interceptor1 = new InterceptorMapping("interceptor-1", new TestInterceptor());
        InterceptorMapping interceptor2 = new InterceptorMapping("interceptor-2", new TestInterceptor());
        defaultInterceptorStacks.add(makeInterceptorStackConfig("stack-1", interceptor1, interceptor2));

        //setup results
        ResultTypeConfig[] defaultResults = new ResultTypeConfig[]{new ResultTypeConfig.Builder("dispatcher",
                ServletDispatcherResult.class.getName()).defaultResultParam("location").build(),
                new ResultTypeConfig.Builder("chain",
                        ActionChainResult.class.getName()).defaultResultParam("actionName").build()};

        PackageConfig strutsDefault = makePackageConfig("struts-default", null, null, "dispatcher",
                defaultResults, defaultInterceptors, defaultInterceptorStacks);

        PackageConfig packageLevelParentPkg = makePackageConfig("package-level", null, null, null);
        PackageConfig classLevelParentPkg = makePackageConfig("class-level", null, null, null);

        PackageConfig rootPkg = makePackageConfig("org.apache.struts2.convention.actions#struts-default#",
            "", strutsDefault, null);
        PackageConfig paramsPkg = makePackageConfig("org.apache.struts2.convention.actions.params#struts-default#/params",
                "/params", strutsDefault, null);
        PackageConfig defaultInterceptorPkg = makePackageConfig("org.apache.struts2.convention.actions.defaultinterceptor#struts-default#/defaultinterceptor",
                "/defaultinterceptor", strutsDefault, null);
        PackageConfig exceptionPkg = makePackageConfig("org.apache.struts2.convention.actions.exception#struts-default#/exception",
                "/exception", strutsDefault, null);
        PackageConfig actionPkg = makePackageConfig("org.apache.struts2.convention.actions.action#struts-default#/action",
            "/action", strutsDefault, null);
        PackageConfig idxPkg = makePackageConfig("org.apache.struts2.convention.actions.idx#struts-default#/idx",
            "/idx", strutsDefault, null);
        PackageConfig idx2Pkg = makePackageConfig("org.apache.struts2.convention.actions.idx.idx2#struts-default#/idx/idx2",
            "/idx/idx2", strutsDefault, null);
        PackageConfig interceptorRefsPkg = makePackageConfig("org.apache.struts2.convention.actions.interceptor#struts-default#/interceptor",
                "/interceptor", strutsDefault, null);
        PackageConfig packageLevelPkg = makePackageConfig("org.apache.struts2.convention.actions.parentpackage#package-level#/parentpackage",
            "/parentpackage", packageLevelParentPkg, null);
        PackageConfig packageLevelSubPkg = makePackageConfig("org.apache.struts2.convention.actions.parentpackage.sub#package-level#/parentpackage/sub",
            "/parentpackage/sub", packageLevelParentPkg, null);
        PackageConfig differentPkg = makePackageConfig("org.apache.struts2.convention.actions.parentpackage#class-level#/parentpackage",
            "/parentpackage", classLevelParentPkg, null);
        PackageConfig differentSubPkg = makePackageConfig("org.apache.struts2.convention.actions.parentpackage.sub#class-level#/parentpackage/sub",
            "/parentpackage/sub", classLevelParentPkg, null);
        PackageConfig pkgLevelNamespacePkg = makePackageConfig("org.apache.struts2.convention.actions.namespace#struts-default#/package-level",
            "/package-level", strutsDefault, null);
        PackageConfig classLevelNamespacePkg = makePackageConfig("org.apache.struts2.convention.actions.namespace#struts-default#/class-level",
            "/class-level", strutsDefault, null);
        PackageConfig actionLevelNamespacePkg = makePackageConfig("org.apache.struts2.convention.actions.namespace#struts-default#/action-level",
            "/action-level", strutsDefault, null);
        PackageConfig defaultNamespacePkg = makePackageConfig("org.apache.struts2.convention.actions.namespace2#struts-default#/namespace2",
            "/namespace2", strutsDefault, null);
        PackageConfig namespaces1Pkg = makePackageConfig("org.apache.struts2.convention.actions.namespace3#struts-default#/namespaces1",
                "/namespaces1", strutsDefault, null);
        PackageConfig namespaces2Pkg = makePackageConfig("org.apache.struts2.convention.actions.namespace3#struts-default#/namespaces2",
                "/namespaces2", strutsDefault, null);
        PackageConfig namespaces3Pkg = makePackageConfig("org.apache.struts2.convention.actions.namespace4#struts-default#/namespaces3",
                "/namespaces3", strutsDefault, null);
        PackageConfig namespaces4Pkg = makePackageConfig("org.apache.struts2.convention.actions.namespace4#struts-default#/namespaces4",
                "/namespaces4", strutsDefault, null);
        PackageConfig resultPkg = makePackageConfig("org.apache.struts2.convention.actions.result#struts-default#/result",
            "/result", strutsDefault, null);
        PackageConfig globalResultPkg = makePackageConfig("org.apache.struts2.convention.actions.result#class-level#/result",
                "/result", classLevelParentPkg, null);
        PackageConfig resultPathPkg = makePackageConfig("org.apache.struts2.convention.actions.resultpath#struts-default#/resultpath",
            "/resultpath", strutsDefault, null);
        PackageConfig skipPkg = makePackageConfig("org.apache.struts2.convention.actions.skip#struts-default#/skip",
            "/skip", strutsDefault, null);
        PackageConfig chainPkg = makePackageConfig("org.apache.struts2.convention.actions.chain#struts-default#/chain",
            "/chain", strutsDefault, null);
        PackageConfig transPkg = makePackageConfig("org.apache.struts2.convention.actions.transactions#struts-default#/transactions",
            "/transactions", strutsDefault, null);
        PackageConfig excludePkg = makePackageConfig("org.apache.struts2.convention.actions.exclude#struts-default#/exclude",
            "/exclude", strutsDefault, null);

        ResultMapBuilder resultMapBuilder = createStrictMock(ResultMapBuilder.class);
        checkOrder(resultMapBuilder, false);
        Map<String, ResultConfig> results = new HashMap<String, ResultConfig>();

        /* org.apache.struts2.convention.actions.action */
        expect(resultMapBuilder.build(ActionNameAction.class, getAnnotation(ActionNameAction.class, "run1", Action.class), "action1", actionPkg)).andReturn(results);
        expect(resultMapBuilder.build(ClassNameAction.class, getAnnotation(ClassNameAction.class, "run1", Action.class), "action3", actionPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionNameAction.class, getAnnotation(ActionNameAction.class, "run2", Action.class), "action2", actionPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionNamesAction.class, getAnnotation(ActionNamesAction.class, "run", Actions.class).value()[0], "actions1", actionPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionNamesAction.class, getAnnotation(ActionNamesAction.class, "run", Actions.class).value()[1], "actions2", actionPkg)).andReturn(results);
        expect(resultMapBuilder.build(SingleActionNameAction.class, getAnnotation(SingleActionNameAction.class, "run", Action.class), "action", actionPkg)).andReturn(results);
        expect(resultMapBuilder.build(TestAction.class, null, "test", actionPkg)).andReturn(results);
        expect(resultMapBuilder.build(TestExtends.class, null, "test-extends", actionPkg)).andReturn(results);

        Actions classLevelActions = ClassLevelAnnotationsAction.class.getAnnotation(Actions.class);
        expect(resultMapBuilder.build(ClassLevelAnnotationsAction.class, classLevelActions.value()[0], "class1", actionPkg)).andReturn(results);
        expect(resultMapBuilder.build(ClassLevelAnnotationsAction.class, classLevelActions.value()[1], "class2", actionPkg)).andReturn(results);

        Actions classLevelActionsDefaultMethod = ClassLevelAnnotationsDefaultMethodAction.class.getAnnotation(Actions.class);
        expect(resultMapBuilder.build(ClassLevelAnnotationsDefaultMethodAction.class, classLevelActionsDefaultMethod.value()[0], "class3", actionPkg)).andReturn(results);
        expect(resultMapBuilder.build(ClassLevelAnnotationsDefaultMethodAction.class, classLevelActionsDefaultMethod.value()[1], "class4", actionPkg)).andReturn(results);

        expect(resultMapBuilder.build(ClassLevelAnnotationAction.class, ClassLevelAnnotationAction.class.getAnnotation(Action.class), "class5", actionPkg)).andReturn(results);
        expect(resultMapBuilder.build(ClassLevelAnnotationDefaultMethodAction.class, ClassLevelAnnotationDefaultMethodAction.class.getAnnotation(Action.class), "class6", actionPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.idx */
        /* org.apache.struts2.convention.actions.idx.idx2 */
        expect(resultMapBuilder.build(org.apache.struts2.convention.actions.idx.Index.class, null, "index", idxPkg)).andReturn(results);
        expect(resultMapBuilder.build(org.apache.struts2.convention.actions.idx.idx2.Index.class, null, "index", idx2Pkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.params */
        expect(resultMapBuilder.build(ActionParamsMethodLevelAction.class, getAnnotation(ActionParamsMethodLevelAction.class, "run1", Action.class), "actionParam1", paramsPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.defaultinterceptor */
        expect(resultMapBuilder.build(SingleActionNameAction2.class, getAnnotation(SingleActionNameAction2.class, "execute", Action.class), "action345", defaultInterceptorPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.exception */
        expect(resultMapBuilder.build(ExceptionsMethodLevelAction.class, getAnnotation(ExceptionsMethodLevelAction.class, "run1", Action.class), "exception1", exceptionPkg)).andReturn(results);
        expect(resultMapBuilder.build(ExceptionsActionLevelAction.class, getAnnotation(ExceptionsActionLevelAction.class, "execute", Action.class), "exceptions-action-level", exceptionPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.interceptor */
        expect(resultMapBuilder.build(InterceptorsAction.class, getAnnotation(InterceptorsAction.class, "run1", Action.class), "action100", interceptorRefsPkg)).andReturn(results);
        expect(resultMapBuilder.build(InterceptorsAction.class, getAnnotation(InterceptorsAction.class, "run2", Action.class), "action200", interceptorRefsPkg)).andReturn(results);
        expect(resultMapBuilder.build(InterceptorsAction.class, getAnnotation(InterceptorsAction.class, "run3", Action.class), "action300", interceptorRefsPkg)).andReturn(results);
        expect(resultMapBuilder.build(InterceptorsAction.class, getAnnotation(InterceptorsAction.class, "run4", Action.class), "action400", interceptorRefsPkg)).andReturn(results);

        expect(resultMapBuilder.build(ActionLevelInterceptorAction.class, getAnnotation(ActionLevelInterceptorAction.class, "run1", Action.class), "action500", interceptorRefsPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionLevelInterceptorAction.class, getAnnotation(ActionLevelInterceptorAction.class, "run2", Action.class), "action600", interceptorRefsPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionLevelInterceptorAction.class, getAnnotation(ActionLevelInterceptorAction.class, "run3", Action.class), "action700", interceptorRefsPkg)).andReturn(results);

        expect(resultMapBuilder.build(ActionLevelInterceptor2Action.class, getAnnotation(ActionLevelInterceptor2Action.class, "run1", Action.class), "action800", interceptorRefsPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionLevelInterceptor3Action.class, getAnnotation(ActionLevelInterceptor3Action.class, "run1", Action.class), "action900", interceptorRefsPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.namespace */
        expect(resultMapBuilder.build(ActionLevelNamespaceAction.class, getAnnotation(ActionLevelNamespaceAction.class, "execute", Action.class), "action", actionLevelNamespacePkg)).andReturn(results);
        expect(resultMapBuilder.build(ClassLevelNamespaceAction.class, null, "class-level-namespace", classLevelNamespacePkg)).andReturn(results);
        expect(resultMapBuilder.build(PackageLevelNamespaceAction.class, null, "package-level-namespace", pkgLevelNamespacePkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.namespace2 */
        expect(resultMapBuilder.build(DefaultNamespaceAction.class, null, "default-namespace", defaultNamespacePkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.namespace3 */
        expect(resultMapBuilder.build(ActionLevelNamespacesAction.class, null, "action-level-namespaces", namespaces1Pkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionLevelNamespacesAction.class, null, "action-level-namespaces", namespaces2Pkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.namespace4 */
        expect(resultMapBuilder.build(ActionAndPackageLevelNamespacesAction.class, null, "action-and-package-level-namespaces", namespaces3Pkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionAndPackageLevelNamespacesAction.class, null, "action-and-package-level-namespaces", namespaces4Pkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.parentpackage */
        expect(resultMapBuilder.build(PackageLevelParentPackageAction.class, null, "package-level-parent-package", packageLevelPkg)).andReturn(results);
        expect(resultMapBuilder.build(PackageLevelParentPackageChildAction.class, null, "package-level-parent-package-child", packageLevelSubPkg)).andReturn(results);
        expect(resultMapBuilder.build(ClassLevelParentPackageAction.class, null, "class-level-parent-package", differentPkg)).andReturn(results);
        expect(resultMapBuilder.build(ClassLevelParentPackageChildAction.class, null, "class-level-parent-package-child", differentSubPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.result */
        expect(resultMapBuilder.build(ClassLevelResultAction.class, null, "class-level-result", resultPkg)).andReturn(results);
        expect(resultMapBuilder.build(ClassLevelResultsAction.class, null, "class-level-results", resultPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionLevelResultAction.class, getAnnotation(ActionLevelResultAction.class, "execute", Action.class), "action-level-result", resultPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionLevelResultsAction.class, getAnnotation(ActionLevelResultsAction.class, "execute", Action.class), "action-level-results", resultPkg)).andReturn(results);
        expect(resultMapBuilder.build(InheritedResultExtends.class, null, "inherited-result-extends", resultPkg)).andReturn(results);
        expect(resultMapBuilder.build(OverrideResultAction.class, getAnnotation(OverrideResultAction.class, "execute", Action.class), "override-result", resultPkg)).andReturn(results);
        expect(resultMapBuilder.build(GlobalResultAction.class, null, "global-result", globalResultPkg)).andReturn(results);
        expect(resultMapBuilder.build(GlobalResultOverrideAction.class, null, "global-result-override", globalResultPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.resultpath */
        expect(resultMapBuilder.build(ClassLevelResultPathAction.class, null, "class-level-result-path", resultPathPkg)).andReturn(results);
        expect(resultMapBuilder.build(PackageLevelResultPathAction.class, null, "package-level-result-path", resultPathPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions */
        expect(resultMapBuilder.build(NoAnnotationAction.class, null, "no-annotation", rootPkg)).andReturn(results);
        expect(resultMapBuilder.build(DefaultResultPathAction.class, null, "default-result-path", rootPkg)).andReturn(results);
        expect(resultMapBuilder.build(Skip.class, null, "skip", rootPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.skip */
        expect(resultMapBuilder.build(Index.class, null, "index", skipPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.chain */
        expect(resultMapBuilder.build(ChainedAction.class, getAnnotation(ChainedAction.class, "foo", Action.class), "foo", chainPkg)).andReturn(results);
        expect(resultMapBuilder.build(ChainedAction.class, getAnnotation(ChainedAction.class, "bar", Action.class), "foo-bar", chainPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.transactions */
        expect(resultMapBuilder.build(TransNameAction.class, getAnnotation(TransNameAction.class, "trans1", Action.class), "trans1", transPkg)).andReturn(results);
        expect(resultMapBuilder.build(TransNameAction.class, getAnnotation(TransNameAction.class, "trans2", Action.class), "trans2", transPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.exclude */
        // this is only expected when excludePackages was specified with org.apache.struts2.convention.actions.exclude package
        if (excludePackages == null || !excludePackages.contains("org.apache.struts2.convention.actions.exclude")) {
            expect(resultMapBuilder.build(ExcludeAction.class, getAnnotation(ExcludeAction.class, "run1", Action.class), "exclude1", excludePkg)).andReturn(results);
        }

        EasyMock.replay(resultMapBuilder);

        final DummyContainer mockContainer = new DummyContainer();

        Configuration configuration = new DefaultConfiguration() {

            @Override
            public Container getContainer() {
                return mockContainer;
            }

        };

        configuration.addPackageConfig("struts-default", strutsDefault);
        configuration.addPackageConfig("package-level", packageLevelParentPkg);
        configuration.addPackageConfig("class-level", classLevelParentPkg);

        ActionNameBuilder actionNameBuilder = new SEOActionNameBuilder("true", "-");
        ObjectFactory of = new ObjectFactory();
        DefaultInterceptorMapBuilder interceptorBuilder = new DefaultInterceptorMapBuilder();
        interceptorBuilder.setConfiguration(configuration);

        //set beans on mock container
        mockContainer.setActionNameBuilder(actionNameBuilder);
        mockContainer.setInterceptorMapBuilder(interceptorBuilder);
        mockContainer.setResultMapBuilder(resultMapBuilder);
        mockContainer.setConventionsService(new ConventionsServiceImpl(""));

        PackageBasedActionConfigBuilder builder = new PackageBasedActionConfigBuilder(configuration, mockContainer ,of, "false", "struts-default");
        builder.setFileProtocols("jar");
        if (actionPackages != null) {
            builder.setActionPackages(actionPackages);
        }
        if (packageLocators != null) {
            builder.setPackageLocators(packageLocators);
        }
        if (excludePackages != null) {
            builder.setExcludePackages(excludePackages);
        }
        DefaultFileManagerFactory fileManagerFactory = new DefaultFileManagerFactory();
        fileManagerFactory.setContainer(ActionContext.getContext().getContainer());
        fileManagerFactory.setFileManager(new DefaultFileManager());
        builder.setFileManagerFactory(fileManagerFactory);
        builder.setPackageLocatorsBase("org.apache.struts2.convention.actions");
        builder.buildActionConfigs();
        verify(resultMapBuilder);

        /* org.apache.struts2.convention.actions.action */
        PackageConfig pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.action#struts-default#/action");
        assertNotNull(pkgConfig);
        assertEquals(14, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "action1", ActionNameAction.class, "run1", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "action2", ActionNameAction.class, "run2", pkgConfig.getName());
        verifyMissingActionConfig(pkgConfig, "foo", DontFindMeAction.class, "foo", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "action3", "someClassName", "run1", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "actions1", ActionNamesAction.class, "run", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "actions2", ActionNamesAction.class, "run", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "action", SingleActionNameAction.class, "run", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "test", TestAction.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "test-extends", TestExtends.class, "execute", pkgConfig.getName());

        verifyActionConfig(pkgConfig, "class1", ClassLevelAnnotationsAction.class, null, pkgConfig.getName());
        verifyActionConfig(pkgConfig, "class2", ClassLevelAnnotationsAction.class, null, pkgConfig.getName());
        verifyActionConfig(pkgConfig, "class3", ClassLevelAnnotationsDefaultMethodAction.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "class4", ClassLevelAnnotationsDefaultMethodAction.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "class5", ClassLevelAnnotationAction.class, null, pkgConfig.getName());
        verifyActionConfig(pkgConfig, "class6", ClassLevelAnnotationDefaultMethodAction.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "class6", ClassLevelAnnotationDefaultMethodAction.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.namespace3 */
        //action on namespace1 (action level)
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.namespace3#struts-default#/namespaces1");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "action-level-namespaces", ActionLevelNamespacesAction.class, "execute", pkgConfig.getName());

        //action on namespace2 (action level)
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.namespace3#struts-default#/namespaces2");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "action-level-namespaces", ActionLevelNamespacesAction.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.namespace4 */
        //action on namespace3 (action level)
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.namespace4#struts-default#/namespaces3");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "action-and-package-level-namespaces", ActionAndPackageLevelNamespacesAction.class, "execute", pkgConfig.getName());

        //action on namespace4 (package level)
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.namespace4#struts-default#/namespaces4");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "action-and-package-level-namespaces", ActionAndPackageLevelNamespacesAction.class, "execute", pkgConfig.getName());



        /* org.apache.struts2.convention.actions.params */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.params#struts-default#/params");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        ActionConfig ac = pkgConfig.getAllActionConfigs().get("actionParam1");
        assertNotNull(ac);
        Map<String, String> params = ac.getParams();
        assertNotNull(params);
        assertEquals(2, params.size());
        assertEquals("val1", params.get("param1"));
        assertEquals("val2", params.get("param2"));

        /* org.apache.struts2.convention.actions.params */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.exception#struts-default#/exception");
        assertNotNull(pkgConfig);
        assertEquals(2, pkgConfig.getActionConfigs().size());

        ac = pkgConfig.getAllActionConfigs().get("exception1");
        assertNotNull(ac);
        List<ExceptionMappingConfig> exceptions = ac.getExceptionMappings();
        assertNotNull(exceptions);
        assertEquals(2, exceptions.size());
        ExceptionMappingConfig exception = exceptions.get(0);
        assertEquals("NPE1", exception.getExceptionClassName());
        assertEquals("success", exception.getResult());
        exception = exceptions.get(1);
        assertEquals("NPE2", exception.getExceptionClassName());
        assertEquals("success", exception.getResult());
        params = exception.getParams();
        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals("val1", params.get("param1"));

        ac = pkgConfig.getAllActionConfigs().get("exceptions-action-level");
        assertNotNull(ac);
        exceptions = ac.getExceptionMappings();
        assertNotNull(exceptions);
        assertEquals(2, exceptions.size());
        exception = exceptions.get(0);
        assertEquals("NPE1", exception.getExceptionClassName());
        assertEquals("success", exception.getResult());
        exception = exceptions.get(1);
        assertEquals("NPE2", exception.getExceptionClassName());
        assertEquals("success", exception.getResult());
        params = exception.getParams();
        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals("val1", params.get("param1"));

        /* org.apache.struts2.convention.actions.idx */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.idx#struts-default#/idx");
        assertNotNull(pkgConfig);
        assertEquals(3, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "", org.apache.struts2.convention.actions.idx.Index.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "index", org.apache.struts2.convention.actions.idx.Index.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "idx2", org.apache.struts2.convention.actions.idx.idx2.Index.class, "execute",
            "org.apache.struts2.convention.actions.idx.idx2#struts-default#/idx/idx2");

        /* org.apache.struts2.convention.actions.defaultinterceptor */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.defaultinterceptor#struts-default#/defaultinterceptor");
        assertNotNull(pkgConfig);
        assertEquals("validationWorkflowStack", pkgConfig.getDefaultInterceptorRef());

        /* org.apache.struts2.convention.actions.idx.idx2 */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.idx.idx2#struts-default#/idx/idx2");
        assertNotNull(pkgConfig);
        assertEquals(2, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "", org.apache.struts2.convention.actions.idx.idx2.Index.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "index", org.apache.struts2.convention.actions.idx.idx2.Index.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.namespace action level */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.namespace#struts-default#/action-level");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "action", ActionLevelNamespaceAction.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.namespace class level */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.namespace#struts-default#/class-level");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "class-level-namespace", ClassLevelNamespaceAction.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.namespace package level */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.namespace#struts-default#/package-level");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "package-level-namespace", PackageLevelNamespaceAction.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.namespace2 */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.namespace2#struts-default#/namespace2");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "default-namespace", DefaultNamespaceAction.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.parentpackage class level */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.parentpackage#class-level#/parentpackage");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "class-level-parent-package", ClassLevelParentPackageAction.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.parentpackage.sub class level */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.parentpackage.sub#class-level#/parentpackage/sub");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "class-level-parent-package-child", ClassLevelParentPackageChildAction.class, "execute", pkgConfig.getName());
        assertEquals("class-level", pkgConfig.getParents().get(0).getName());

        /* org.apache.struts2.convention.actions.parentpackage package level */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.parentpackage#package-level#/parentpackage");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "package-level-parent-package", PackageLevelParentPackageAction.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.parentpackage.sub package level */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.parentpackage.sub#package-level#/parentpackage/sub");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "package-level-parent-package-child", PackageLevelParentPackageChildAction.class, "execute", pkgConfig.getName());
        assertEquals("package-level", pkgConfig.getParents().get(0).getName());

        /* org.apache.struts2.convention.actions.result */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.result#struts-default#/result");
        assertNotNull(pkgConfig);
        assertEquals(6, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "class-level-result", ClassLevelResultAction.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "class-level-results", ClassLevelResultsAction.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "action-level-result", ActionLevelResultAction.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "action-level-results", ActionLevelResultsAction.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "inherited-result-extends", InheritedResultExtends.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.resultpath */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.resultpath#struts-default#/resultpath");
        assertNotNull(pkgConfig);
        assertEquals(2, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "class-level-result-path", ClassLevelResultPathAction.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "package-level-result-path", PackageLevelResultPathAction.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.interceptorRefs */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.interceptor#struts-default#/interceptor");
        assertNotNull(pkgConfig);
        assertEquals(9, pkgConfig.getActionConfigs().size());
        verifyActionConfigInterceptors(pkgConfig, "action100", "interceptor-1");
        verifyActionConfigInterceptors(pkgConfig, "action200", "interceptor-1", "interceptor-2");
        verifyActionConfigInterceptors(pkgConfig, "action300", "interceptor-1", "interceptor-2");
        verifyActionConfigInterceptors(pkgConfig, "action400", "interceptor-1", "interceptor-1", "interceptor-2");

        // Interceptors at class level
        verifyActionConfigInterceptors(pkgConfig, "action500", "interceptor-1");
        verifyActionConfigInterceptors(pkgConfig, "action600", "interceptor-1", "interceptor-2");
        verifyActionConfigInterceptors(pkgConfig, "action700", "interceptor-1", "interceptor-1", "interceptor-2");

        //multiple interceptor at class level
        verifyActionConfigInterceptors(pkgConfig, "action800", "interceptor-1", "interceptor-2");
        verifyActionConfigInterceptors(pkgConfig, "action900", "interceptor-1", "interceptor-1", "interceptor-2");

        /* org.apache.struts2.convention.actions */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions#struts-default#");
        assertNotNull(pkgConfig);

        assertEquals(4, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "no-annotation", NoAnnotationAction.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "default-result-path", DefaultResultPathAction.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "skip", Skip.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "idx", org.apache.struts2.convention.actions.idx.Index.class, "execute",
            "org.apache.struts2.convention.actions.idx#struts-default#/idx");

        /* org.apache.struts2.convention.actions.transactions */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.transactions#struts-default#/transactions");
        verifyActionConfig(pkgConfig, "trans1", TransNameAction.class, "trans1", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "trans2", TransNameAction.class, "trans2", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.exclude */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.exclude#struts-default#/exclude");
        if (excludePackages != null && excludePackages.contains("org.apache.struts2.convention.actions.exclude")) {
            verifyMissingActionConfig(configuration, "exclude1", ExcludeAction.class, "run1", "org.apache.struts2.convention.actions.exclude#struts-default#/exclude");
        } else {
            verifyActionConfig(pkgConfig, "exclude1", ExcludeAction.class, "run1", pkgConfig.getName());
        }

        //test unknown handler automatic chaining
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.chain#struts-default#/chain");
        ServletContext context = EasyMock.createNiceMock(ServletContext.class);
        EasyMock.replay(context);

        ObjectFactory workingFactory = configuration.getContainer().getInstance(ObjectFactory.class);
        ConventionUnknownHandler uh = new ConventionUnknownHandler(configuration, workingFactory, context, mockContainer, "struts-default", null, "-");
        ActionContext actionContext = new ActionContext(Collections.EMPTY_MAP);

        Result result = uh.handleUnknownResult(actionContext, "foo", pkgConfig.getActionConfigs().get("foo"), "bar");
        assertNotNull(result);
        assertTrue(result instanceof ActionChainResult);
        ActionChainResult chainResult = (ActionChainResult) result;
        ActionChainResult chainResultToCompare = new ActionChainResult(null, "foo-bar", null);
        assertEquals(chainResultToCompare, chainResult);
    }

    private void verifyActionConfig(PackageConfig pkgConfig, String actionName, Class<?> actionClass,
            String methodName, String packageName) {
        ActionConfig ac = pkgConfig.getAllActionConfigs().get(actionName);
        assertNotNull(ac);
        assertEquals(actionClass.getName(), ac.getClassName());
        assertEquals(methodName, ac.getMethodName());
        assertEquals(packageName, ac.getPackageName());
    }

    private void verifyMissingActionConfig(Configuration configuration, String actionName, Class<?> actionClass,
                                           String methodName, String packageName) {
        assertNull(configuration.getPackageConfig(packageName));
        assertFalse(configuration.getPackageConfigNames().contains(packageName));
    }

    private void verifyMissingActionConfig(PackageConfig pkgConfig, String actionName, Class<?> actionClass,
            String methodName, String packageName) {
        ActionConfig ac = pkgConfig.getAllActionConfigs().get(actionName);
        assertNull(ac);
    }

    private void verifyActionConfig(PackageConfig pkgConfig, String actionName, String actionClass, String methodName, String packageName) {
        ActionConfig ac = pkgConfig.getAllActionConfigs().get(actionName);
        assertNotNull(ac);
        assertEquals(actionClass, ac.getClassName());
        assertEquals(methodName, ac.getMethodName());
        assertEquals(packageName, ac.getPackageName());
    }

    private void verifyActionConfigInterceptors(PackageConfig pkgConfig, String actionName, String... refs) {
        ActionConfig ac = pkgConfig.getAllActionConfigs().get(actionName);
        assertNotNull(ac);
        List<InterceptorMapping> interceptorMappings = ac.getInterceptors();
        for (int i = 0; i < interceptorMappings.size(); i++) {
            InterceptorMapping interceptorMapping = interceptorMappings.get(i);
            assertEquals(refs[i], interceptorMapping.getName());
        }
    }

    private PackageConfig makePackageConfig(String name, String namespace, PackageConfig parent,
            String defaultResultType, ResultTypeConfig... results) {
        return makePackageConfig(name, namespace, parent, defaultResultType, results, null, null);
    }

    private PackageConfig makePackageConfig(String name, String namespace, PackageConfig parent,
            String defaultResultType, ResultTypeConfig[] results, List<InterceptorConfig> interceptors,
            List<InterceptorStackConfig> interceptorStacks) {
        PackageConfig.Builder builder = new PackageConfig.Builder(name);
        if (namespace != null) {
            builder.namespace(namespace);
        }
        if (parent != null) {
            builder.addParent(parent);
        }
        if (defaultResultType != null) {
            builder.defaultResultType(defaultResultType);
        }
        if (results != null) {
            for (ResultTypeConfig result : results) {
                builder.addResultTypeConfig(result);
            }
        }
        if (interceptors != null) {
            for (InterceptorConfig ref : interceptors) {
                builder.addInterceptorConfig(ref);
            }
        }
        if (interceptorStacks != null) {
            for (InterceptorStackConfig ref : interceptorStacks) {
                builder.addInterceptorStackConfig(ref);
            }
        }

        return new MyPackageConfig(builder.build());
    }

    private InterceptorConfig makeInterceptorConfig(String name) {
        InterceptorConfig.Builder builder = new InterceptorConfig.Builder(name, "org.apache.struts2.convention.TestInterceptor");
        return builder.build();
    }

    private InterceptorStackConfig makeInterceptorStackConfig(String name, InterceptorMapping... interceptors) {
        InterceptorStackConfig.Builder builder = new InterceptorStackConfig.Builder(name);
        for (InterceptorMapping interceptor : interceptors)
            builder.addInterceptor(interceptor);
        return builder.build();
    }

    public class MyPackageConfig extends PackageConfig {
        protected MyPackageConfig(PackageConfig packageConfig) {
            super(packageConfig);
        }

        public boolean equals(Object obj) {
            PackageConfig other = (PackageConfig) obj;
            return getName().equals(other.getName()) && getNamespace().equals(other.getNamespace()) &&
                getParents().get(0) == other.getParents().get(0) && getParents().size() == other.getParents().size();
        }
    }

    public class DummyContainer implements Container {
        private ActionNameBuilder actionNameBuilder;
        private ResultMapBuilder resultMapBuilder;
        private InterceptorMapBuilder interceptorMapBuilder;
        private ConventionsService conventionsService;
        private DefaultFileManagerFactory fileManagerFactory;

        public DummyContainer() {
            fileManagerFactory = new DefaultFileManagerFactory();
            fileManagerFactory.setContainer(this);
            fileManagerFactory.setFileManager(new DefaultFileManager());
        }

        public void setActionNameBuilder(ActionNameBuilder actionNameBuilder) {
            this.actionNameBuilder = actionNameBuilder;
        }

        public void setInterceptorMapBuilder(InterceptorMapBuilder interceptorMapBuilder) {
            this.interceptorMapBuilder = interceptorMapBuilder;
        }

        public void setResultMapBuilder(ResultMapBuilder resultMapBuilder) {
            this.resultMapBuilder = resultMapBuilder;
        }

        public void setConventionsService(ConventionsService conventionsService) {
            this.conventionsService = conventionsService;
        }

        public <T> T getInstance(Class<T> type) {
            try {
                if (type == FileManagerFactory.class) {
                    return (T) fileManagerFactory;
                }
                T obj = type.newInstance();
                if (obj instanceof ObjectFactory) {
                    OgnlReflectionProvider rp = new OgnlReflectionProvider() {

                        @Override
                        public void setProperties(Map<String, ?> properties, Object o) {
                        }

                        public void setProperties(Map<String, ?> properties, Object o, Map<String, Object> context, boolean throwPropertyExceptions) throws ReflectionException {
                            if (o instanceof ActionChainResult) {
                                ((ActionChainResult) o).setActionName(String.valueOf(properties.get("actionName")));
                            }
                        }

                        @Override
                        public void setProperty(String name, Object value, Object o, Map<String, Object> context, boolean throwPropertyExceptions) {
                            if (o instanceof ActionChainResult) {
                                ((ActionChainResult) o).setActionName((String) value);
                            }
                        }
                    };
                    DefaultInterceptorFactory dif = new DefaultInterceptorFactory();
                    dif.setObjectFactory((ObjectFactory) obj);
                    dif.setReflectionProvider(rp);

                    DefaultResultFactory drf = new DefaultResultFactory();
                    drf.setObjectFactory((ObjectFactory) obj);
                    drf.setReflectionProvider(rp);

                    ((ObjectFactory) obj).setInterceptorFactory(dif);
                    ((ObjectFactory) obj).setResultFactory(drf);
                }
                return obj;
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
        }

        public <T> T getInstance(Class<T> type, String name) {
            if (type == ResultMapBuilder.class)
                return (T) resultMapBuilder;
            else if (type == InterceptorMapBuilder.class)
                return (T) interceptorMapBuilder;
            else if (type == ActionNameBuilder.class)
                return (T) actionNameBuilder;
            else if (type == ConventionsService.class)
                return (T) conventionsService;
            else if (type == FileManagerFactory.class)
                return (T) fileManagerFactory;
            return null;
        }

        public Set<String> getInstanceNames(Class<?> type) {
            if (type == FileManager.class) {
                return Collections.emptySet();
            }
            return null;
        }

        public void inject(Object o) {

        }

        public <T> T inject(Class<T> implementation) {
            return null;
        }

        public void removeScopeStrategy() {

        }

        public void setScopeStrategy(Strategy scopeStrategy) {
        }

    }
}
