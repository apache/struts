/**
 * Copyright (C) 2006 Google Inc.
 *
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * </p>
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.opensymphony.xwork2.inject;

import java.lang.reflect.Member;
import java.util.LinkedHashMap;

/**
 * An immutable snapshot of the current context which is safe to
 * expose to client code.
 *
 * @author crazybob@google.com (Bob Lee)
 */
class ExternalContext<T> implements Context {

    final Member member;
    final Key<T> key;
    final ContainerImpl container;

    public ExternalContext(Member member, Key<T> key, ContainerImpl container) {
        this.member = member;
        this.key = key;
        this.container = container;
    }

    public Class<T> getType() {
        return key.getType();
    }

    public Scope.Strategy getScopeStrategy() {
        return (Scope.Strategy) container.localScopeStrategy.get();
    }

    public Container getContainer() {
        return container;
    }

    public Member getMember() {
        return member;
    }

    public String getName() {
        return key.getName();
    }

    @Override
    public String toString() {
        return "Context" + new LinkedHashMap<String, Object>() {{
            put("member", member);
            put("type", getType());
            put("name", getName());
            put("container", container);
        }}.toString();
    }

    static <T> ExternalContext<T> newInstance(Member member, Key<T> key, ContainerImpl container) {
        return new ExternalContext<T>(member, key, container);
    }
}
