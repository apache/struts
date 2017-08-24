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

package org.apache.struts2.rest.handler;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.ArrayTypePermission;
import com.thoughtworks.xstream.security.ExplicitTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import com.thoughtworks.xstream.security.TypePermission;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Handles XML content
 */
public class XStreamHandler extends AbstractContentTypeHandler {

    private static final Logger LOG = LoggerFactory.getLogger(XStreamHandler.class);

    public String fromObject(ActionInvocation invocation, Object obj, String resultCode, Writer out) throws IOException {
        if (obj != null) {
            XStream xstream = createXStream(invocation);
            xstream.toXML(obj, out);
        }
        return null;
    }

    public void toObject(ActionInvocation invocation, Reader in, Object target) {
        XStream xstream = createXStream(invocation);
        xstream.fromXML(in, target);
    }

    /**
     * @deprecated use version with {@link ActionInvocation}
     */
    @Deprecated
    protected XStream createXStream() {
        LOG.warn("You are using a deprecated API!");
        return new XStream();
    }

    protected XStream createXStream(ActionInvocation invocation) {
        XStream stream = new XStream();
        LOG.debug("Clears existing permissions");
        stream.addPermission(NoTypePermission.NONE);

        LOG.debug("Adds per action permissions");
        addPerActionPermission(invocation, stream);

        LOG.debug("Adds default permissions");
        addDefaultPermissions(invocation, stream);
        return stream;
    }

    private void addPerActionPermission(ActionInvocation invocation, XStream stream) {
        Object action = invocation.getAction();
        if (action instanceof AllowedClasses) {
            Set<Class<?>> allowedClasses = ((AllowedClasses) action).allowedClasses();
            stream.addPermission(new ExplicitTypePermission(allowedClasses.toArray(new Class[allowedClasses.size()])));
        }
        if (action instanceof AllowedClassNames) {
            Set<String> allowedClassNames = ((AllowedClassNames) action).allowedClassNames();
            stream.addPermission(new ExplicitTypePermission(allowedClassNames.toArray(new String[allowedClassNames.size()])));
        }
        if (action instanceof XStreamPermissionProvider) {
            Collection<TypePermission> permissions = ((XStreamPermissionProvider) action).getTypePermissions();
            for (TypePermission permission : permissions) {
                stream.addPermission(permission);
            }
        }
    }

    protected void addDefaultPermissions(ActionInvocation invocation, XStream stream) {
        stream.addPermission(new ExplicitTypePermission(new Class[]{invocation.getAction().getClass()}));
        if (invocation.getAction() instanceof ModelDriven) {
            stream.addPermission(new ExplicitTypePermission(new Class[]{((ModelDriven) invocation.getAction()).getModel().getClass()}));
        }
        stream.addPermission(NullPermission.NULL);
        stream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        stream.addPermission(ArrayTypePermission.ARRAYS);
        stream.addPermission(CollectionTypePermission.COLLECTIONS);
        stream.addPermission(new ExplicitTypePermission(new Class[]{Date.class}));
    }

    public String getContentType() {
        return "application/xml";
    }

    public String getExtension() {
        return "xml";
    }

    private static class CollectionTypePermission implements TypePermission {

        private static final TypePermission COLLECTIONS = new CollectionTypePermission();

        public boolean allows(Class type) {
            return type != null && type.isInterface() &&
                    (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type));
        }

    }
}
