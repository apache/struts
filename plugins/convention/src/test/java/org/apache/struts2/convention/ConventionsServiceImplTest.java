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
package org.apache.struts2.convention;

import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.entities.PackageConfig;
import org.apache.struts2.config.entities.ResultTypeConfig;
import org.apache.struts2.convention.actions.NoAnnotationAction;
import org.apache.struts2.convention.actions.resultpath.ClassLevelResultPathAction;
import org.apache.struts2.convention.actions.resultpath.PackageLevelResultPathAction;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class ConventionsServiceImplTest {

    private static final String GLOBAL = "/WEB-INF/content/";
    private static final String SCOPED = "/WEB-INF/admin/";

    private ConventionsServiceImpl service;
    private PackageConfig scopedPackage;
    private PackageConfig plainPackage;

    @Before
    public void setUp() {
        service = new ConventionsServiceImpl(GLOBAL);
        service.setScopedConstantProvider(name -> GLOBAL);
        scopedPackage = childOf(SCOPED);
        plainPackage = new PackageConfig.Builder("plain").build();
    }

    @Test
    public void scopedValueOfParentPackageWinsOverGlobal() {
        assertEquals(SCOPED, service.determineResultPath(NoAnnotationAction.class, scopedPackage));
    }

    @Test
    public void classLevelResultPathWinsOverScopedValue() {
        assertEquals("/class-level", service.determineResultPath(ClassLevelResultPathAction.class, scopedPackage));
    }

    @Test
    public void packageLevelResultPathWinsOverScopedValue() {
        assertEquals("/package-level", service.determineResultPath(PackageLevelResultPathAction.class, scopedPackage));
    }

    @Test
    public void packageWithoutScopedValueGivesGlobal() {
        assertEquals(GLOBAL, service.determineResultPath(NoAnnotationAction.class, plainPackage));
    }

    @Test
    public void emptyScopedValueIsUsedAsIs() {
        assertEquals("", service.determineResultPath(NoAnnotationAction.class, childOf("")));
    }

    @Test
    public void nullPackageGivesGlobalWithoutConsultingProvider() {
        ConventionsServiceImpl withoutProvider = new ConventionsServiceImpl(GLOBAL);

        assertEquals(GLOBAL, withoutProvider.determineResultPath(NoAnnotationAction.class, null));
        assertEquals(GLOBAL, withoutProvider.determineResultPath(NoAnnotationAction.class));
        assertEquals(GLOBAL, withoutProvider.determineResultPath((ActionConfig) null, null));
        assertEquals(GLOBAL, withoutProvider.determineResultPath((ActionConfig) null));
    }

    @Test
    public void actionlessRequestUsesScopedValueOfPackage() {
        assertEquals(SCOPED, service.determineResultPath((ActionConfig) null, scopedPackage));
    }

    @Test
    public void actionConfigResolvesThroughItsClass() {
        ActionConfig annotated = actionConfig(ClassLevelResultPathAction.class.getName());
        ActionConfig plain = actionConfig(NoAnnotationAction.class.getName());

        assertEquals("/class-level", service.determineResultPath(annotated, scopedPackage));
        assertEquals(SCOPED, service.determineResultPath(plain, scopedPackage));
    }

    @Test
    public void unknownActionClassStillFails() {
        ActionConfig missing = actionConfig("com.example.MissingAction");

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> service.determineResultPath(missing, scopedPackage));
        assertTrue(e.getMessage().contains("[com.example.MissingAction]"));
    }

    @Test
    public void customServiceWithOnlyOneArgumentMethodsKeepsItsBehaviour() {
        ConventionsService custom = new ConventionsService() {
            @Override
            public String determineResultPath(Class<?> actionClass) {
                return "/custom/";
            }

            @Override
            public String determineResultPath(ActionConfig actionConfig) {
                return "/custom/";
            }

            @Override
            public Map<String, ResultTypeConfig> getResultTypesByExtension(PackageConfig packageConfig) {
                return Collections.emptyMap();
            }
        };

        assertEquals("/custom/", custom.determineResultPath(NoAnnotationAction.class, scopedPackage));
        assertEquals("/custom/", custom.determineResultPath((ActionConfig) null, scopedPackage));
    }

    @Test
    public void resultPathIsScopable() {
        assertEquals(Set.of(ConventionConstants.CONVENTION_RESULT_PATH), new ConventionScopableConstants().getNames());
    }

    private static PackageConfig childOf(String scopedResultPath) {
        PackageConfig parent = new PackageConfig.Builder("admin-base")
                .addScopedConstant(ConventionConstants.CONVENTION_RESULT_PATH, scopedResultPath)
                .build();
        return new PackageConfig.Builder("admin-base#/admin").namespace("/admin").addParent(parent).build();
    }

    private static ActionConfig actionConfig(String className) {
        return new ActionConfig.Builder("admin-base#/admin", "foo", className).build();
    }
}
