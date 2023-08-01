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
package org.apache.tiles.autotag.jsp;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;

import org.apache.tiles.autotag.generate.TemplateGenerator;
import org.apache.tiles.autotag.generate.TemplateGeneratorBuilder;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;

/**
 * Tests JspTemplateGeneratorFactory.
 */
public class JspTemplateGeneratorFactoryTest {

    /**
     * Test method JspTemplateGeneratorFactory#createTemplateGenerator().
     */
    @Test
    public void testCreateTemplateGenerator() {
        File classesOutputDirectory = createMock(File.class);
        File resourcesOutputDirectory = createMock(File.class);
        VelocityEngine velocityEngine = createMock(VelocityEngine.class);
        TemplateGeneratorBuilder builder = createMock(TemplateGeneratorBuilder.class);
        TemplateGenerator generator = createMock(TemplateGenerator.class);

        expect(builder.setClassesOutputDirectory(classesOutputDirectory)).andReturn(builder);
        expect(builder.setResourcesOutputDirectory(resourcesOutputDirectory)).andReturn(builder);
        expect(builder.addResourcesTemplateSuiteGenerator(isA(TLDGenerator.class))).andReturn(builder);
        expect(builder.addClassesTemplateClassGenerator(isA(TagClassGenerator.class))).andReturn(builder);
        expect(builder.build()).andReturn(generator);

        replay(classesOutputDirectory, resourcesOutputDirectory, velocityEngine, builder, generator);
        JspTemplateGeneratorFactory factory = new JspTemplateGeneratorFactory(
                classesOutputDirectory, resourcesOutputDirectory,
                velocityEngine, builder);
        assertSame(generator, factory.createTemplateGenerator());
        verify(classesOutputDirectory, resourcesOutputDirectory, velocityEngine, builder, generator);
    }

}
