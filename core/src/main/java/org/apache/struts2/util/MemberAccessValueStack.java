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
package org.apache.struts2.util;

import org.apache.struts2.ognl.ParameterMemberGuard;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * ValueStacks implementing this interface provide a way to remove block or allow access
 * to properties using regular expressions
 */
public interface MemberAccessValueStack {

    void useExcludeProperties(Set<Pattern> excludeProperties);

    void useAcceptProperties(Set<Pattern> acceptedProperties);

    /**
     * Applies a guard to every member OGNL resolves on this stack until it is replaced.
     * <p>
     * The default cannot apply a guard and returns {@code false}, so a stack that does not override this method is
     * reported instead of being silently left unguarded.
     *
     * @param guard the guard to apply, or {@code null} to remove the current one
     * @return {@code true} if this stack applies the guard
     * @since 7.6.0
     */
    default boolean useParameterMemberGuard(ParameterMemberGuard guard) {
        return false;
    }

}
