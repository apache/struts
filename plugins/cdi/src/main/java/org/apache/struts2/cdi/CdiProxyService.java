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
package org.apache.struts2.cdi;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.ognl.ProxyCacheFactory;
import org.apache.struts2.util.StrutsProxyService;
import org.jboss.weld.proxy.WeldClientProxy;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * CDI-aware {@link org.apache.struts2.util.ProxyService}. Extends the default
 * {@link StrutsProxyService} (Spring + Hibernate detection) with recognition of
 * Weld client proxies, so {@code SecurityMemberAccess} can resolve the real
 * target class of a normal-scoped CDI bean before evaluating the OGNL allowlist.
 *
 * @see WW-5604
 */
public class CdiProxyService extends StrutsProxyService {

    @Inject
    public CdiProxyService(ProxyCacheFactory<?, ?> proxyCacheFactory) {
        super(proxyCacheFactory);
    }

    @Override
    public boolean isProxy(Object object) {
        return super.isProxy(object) || isWeldProxy(object);
    }

    @Override
    public boolean isProxyMember(Member member, Object object) {
        return super.isProxyMember(member, object) || isWeldProxyMember(member);
    }

    @Override
    public Class<?> ultimateTargetClass(Object candidate) {
        if (isWeldProxy(candidate)) {
            return weldUltimateTargetClass(candidate);
        }
        return super.ultimateTargetClass(candidate);
    }

    private boolean isWeldProxy(Object object) {
        if (object == null) {
            return false;
        }
        try {
            return object instanceof WeldClientProxy;
        } catch (LinkageError ignored) {
            return false;
        }
    }

    private Class<?> weldUltimateTargetClass(Object candidate) {
        try {
            Object instance = ((WeldClientProxy) candidate).getMetadata().getContextualInstance();
            return instance != null ? instance.getClass() : candidate.getClass();
        } catch (LinkageError ignored) {
            return candidate.getClass();
        }
    }

    private boolean isWeldProxyMember(Member member) {
        try {
            if (member instanceof Method method) {
                return MethodUtils.getMatchingMethod(WeldClientProxy.class, member.getName(), method.getParameterTypes()) != null;
            }
            return false;
        } catch (LinkageError ignored) {
            return false;
        }
    }
}
