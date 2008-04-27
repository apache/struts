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

package org.apache.struts2.jsf;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.PhaseId;

/**
 * Restores the view or component tree
 */
public class RestoreViewInterceptor extends FacesInterceptor {

    private static final long serialVersionUID = -1500785113037140668L;

    /**
     * Restore View (JSF.2.2.1)
     *
     * @param viewId
     *            The view id
     * @param facesContext
     *            The faces context
     * @return true, if immediate rendering should occur
     */
    protected boolean executePhase(String viewId, FacesContext facesContext) {
        boolean skipFurtherProcessing = false;
        if (log.isTraceEnabled())
            log.trace("entering restoreView");

        informPhaseListenersBefore(facesContext, PhaseId.RESTORE_VIEW);

        try {
            if (isResponseComplete(facesContext, "restoreView", true)) {
                // have to skips this phase
                return true;
            }
            if (shouldRenderResponse(facesContext, "restoreView", true)) {
                skipFurtherProcessing = true;
            }

            ExternalContext externalContext = facesContext.getExternalContext();
            String defaultSuffix = externalContext
                    .getInitParameter(ViewHandler.DEFAULT_SUFFIX_PARAM_NAME);
            String suffix = defaultSuffix != null ? defaultSuffix
                    : ViewHandler.DEFAULT_SUFFIX;
            if (viewId != null) {
                viewId += suffix;
            }

            if (viewId == null) {
                if (!externalContext.getRequestServletPath().endsWith("/")) {
                    try {
                        externalContext.redirect(externalContext
                                .getRequestServletPath()
                                + "/");
                        facesContext.responseComplete();
                        return true;
                    } catch (IOException e) {
                        throw new FacesException("redirect failed", e);
                    }
                }
            }

            Application application = facesContext.getApplication();
            ViewHandler viewHandler = application.getViewHandler();

            // boolean viewCreated = false;
            UIViewRoot viewRoot = viewHandler.restoreView(facesContext, viewId);
            if (viewRoot == null) {
                viewRoot = viewHandler.createView(facesContext, viewId);
                viewRoot.setViewId(viewId);
                facesContext.renderResponse();
                // viewCreated = true;
            }

            facesContext.setViewRoot(viewRoot);

            /*
             * This section has been disabled because it causes some bug. Be
             * careful if you need to re-enable it. Furthermore, for an unknown
             * reason, it seems that by default it is executed (i.e.
             * log.isTraceEnabled() is true). Bug example : This traceView
             * causes DebugUtil.printComponent to print all the attributes of
             * the view components. And if you have a data table within an
             * aliasBean, this causes the data table to initialize it's value
             * attribute while the alias isn't set. So, the value initializes
             * with an UIData.EMPTY_DATA_MODEL, and not with the aliased one.
             * But as it's initialized, it will not try to get the value from
             * the ValueBinding next time it needs to. I expect this to cause
             * more similar bugs. TODO : Completely remove or be SURE by default
             * it's not executed, and it has no more side-effects.
             *
             * if (log.isTraceEnabled()) { //Note: DebugUtils Logger must also
             * be in trace level DebugUtils.traceView(viewCreated ? "Newly
             * created view" : "Restored view"); }
             */

            if (facesContext.getExternalContext().getRequestParameterMap()
                    .isEmpty()) {
                // no POST or query parameters --> set render response flag
                facesContext.renderResponse();
            }

            recursivelyHandleComponentReferencesAndSetValid(facesContext,
                    viewRoot);
        } finally {
            informPhaseListenersAfter(facesContext, PhaseId.RESTORE_VIEW);
        }

        if (isResponseComplete(facesContext, "restoreView", false)
                || shouldRenderResponse(facesContext, "restoreView", false)) {
            // since this phase is completed we don't need to return right away
            // even if the response is completed
            skipFurtherProcessing = true;
        }

        if (!skipFurtherProcessing && log.isTraceEnabled())
            log.trace("exiting restoreView ");
        return skipFurtherProcessing;
    }

    /**
     * Walk the component tree, executing any component-bindings to reattach
     * components to their backing beans. Also, any UIInput component is marked
     * as Valid.
     * <p>
     * Note that this method effectively breaks encapsulation; instead of asking
     * each component to update itself and its children, this method just
     * reaches into each component. That makes it impossible for any component
     * to customise its behaviour at this point.
     * <p>
     * This has been filed as an issue against the spec. Until this issue is
     * resolved, we'll add a new marker-interface for components to allow them
     * to define their interest in handling children bindings themselves.
     */
    protected void recursivelyHandleComponentReferencesAndSetValid(
            FacesContext facesContext, UIComponent parent) {
        recursivelyHandleComponentReferencesAndSetValid(facesContext, parent,
                false);
    }

    protected void recursivelyHandleComponentReferencesAndSetValid(
            FacesContext facesContext, UIComponent parent, boolean forceHandle) {
        Method handleBindingsMethod = getBindingMethod(parent);

        if (handleBindingsMethod != null && !forceHandle) {
            try {
                handleBindingsMethod.invoke(parent, new Object[] {});
            } catch (Throwable th) {
                log.error(
                        "Exception while invoking handleBindings on component with client-id:"
                                + parent.getClientId(facesContext), th);
            }
        } else {
            for (Iterator it = parent.getFacetsAndChildren(); it.hasNext();) {
                UIComponent component = (UIComponent) it.next();

                ValueBinding binding = component.getValueBinding("binding"); // TODO:
                // constant
                if (binding != null && !binding.isReadOnly(facesContext)) {
                    binding.setValue(facesContext, component);
                }

                if (component instanceof UIInput) {
                    ((UIInput) component).setValid(true);
                }

                recursivelyHandleComponentReferencesAndSetValid(facesContext,
                        component);
            }
        }
    }

    /**
     * This is all a hack to work around a spec-bug which will be fixed in
     * JSF2.0
     *
     * @param parent
     * @return true if this component is bindingAware (e.g. aliasBean)
     */
    private static Method getBindingMethod(UIComponent parent) {
        Class[] clazzes = parent.getClass().getInterfaces();

        for (int i = 0; i < clazzes.length; i++) {
            Class clazz = clazzes[i];

            if (clazz.getName().indexOf("BindingAware") != -1) {
                try {
                    return parent.getClass().getMethod("handleBindings",
                            new Class[] {});
                } catch (NoSuchMethodException e) {
                    // return
                }
            }
        }

        return null;
    }
}
