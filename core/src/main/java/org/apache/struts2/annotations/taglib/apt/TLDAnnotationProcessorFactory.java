/*
 * $Id:  $
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
 
package org.apache.struts2.annotations.taglib.apt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

public class TLDAnnotationProcessorFactory implements AnnotationProcessorFactory {

  /**
   * Returns a TLD annotation processor.
   *
   * @return An annotation processor for note annotations if requested, otherwise, returns the NO_OP
   *         annotation processor.
   */
  public AnnotationProcessor getProcessorFor(Set<AnnotationTypeDeclaration> declarations,
      AnnotationProcessorEnvironment env) {
    AnnotationProcessor result;
    if (declarations.isEmpty()) {
      result = AnnotationProcessors.NO_OP;
    } else {
      result = new TagAnnotationProcessor(env);
    }
    return result;
  }

  /**
   * This factory builds a processor for Tag and TagAttribute
   *
   * @return a collection containing StutsTag and StrutsTagAttribute
   */
  public Collection<String> supportedAnnotationTypes() {
    return Arrays.asList(TagAnnotationProcessor.TAG, TagAnnotationProcessor.TAG_ATTRIBUTE);
  }

  /**
   * Options used to generate the TLD
   *
   * @return an empty list.
   */
  public Collection<String> supportedOptions() {
    return Arrays.asList("-AoutFile",
        "-AtlibVersion",
        "-AjspVersion",
        "-AshortName",
        "-Auri",
        "-Adescription",
        "-AdisplayName");
  }
}
