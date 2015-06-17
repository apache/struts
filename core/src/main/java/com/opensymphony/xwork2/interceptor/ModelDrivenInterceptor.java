/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;


/**
 * <!-- START SNIPPET: description -->
 *
 * Watches for {@link ModelDriven} actions and adds the action's model on to the value stack.
 *
 * <p/> <b>Note:</b>  The ModelDrivenInterceptor must come before the both {@link StaticParametersInterceptor} and
 * {@link ParametersInterceptor} if you want the parameters to be applied to the model.
 * 
 * <p/> <b>Note:</b>  The ModelDrivenInterceptor will only push the model into the stack when the
 * model is not null, else it will be ignored.
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>refreshModelBeforeResult - set to true if you want the model to be refreshed on the value stack after action
 * execution and before result execution.  The setting is useful if you want to change the model instance during the
 * action execution phase, like when loading it from the data layer.  This will result in getModel() being called at
 * least twice.</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Extending the interceptor:</u>
 *
 * <p/>
 *
 * <!-- START SNIPPET: extending -->
 *
 * There are no known extension points to this interceptor.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="modelDriven"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class ModelDrivenInterceptor extends AbstractInterceptor {

    protected boolean refreshModelBeforeResult = false;

    public void setRefreshModelBeforeResult(boolean val) {
        this.refreshModelBeforeResult = val;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof ModelDriven) {
            ModelDriven modelDriven = (ModelDriven) action;
            ValueStack stack = invocation.getStack();
            Object model = modelDriven.getModel();
            if (model !=  null) {
            	stack.push(model);
            }
            if (refreshModelBeforeResult) {
                invocation.addPreResultListener(new RefreshModelBeforeResult(modelDriven, model));
            }
        }
        return invocation.invoke();
    }

    /**
     * Refreshes the model instance on the value stack, if it has changed
     */
    protected static class RefreshModelBeforeResult implements PreResultListener {
        private Object originalModel = null;
        protected ModelDriven action;


        public RefreshModelBeforeResult(ModelDriven action, Object model) {
            this.originalModel = model;
            this.action = action;
        }

        public void beforeResult(ActionInvocation invocation, String resultCode) {
            ValueStack stack = invocation.getStack();
            CompoundRoot root = stack.getRoot();

            boolean needsRefresh = true;
            Object newModel = action.getModel();

            // Check to see if the new model instance is already on the stack
            for (Object item : root) {
                if (item.equals(newModel)) {
                    needsRefresh = false;
                    break;
                }
            }

            // Add the new model on the stack
            if (needsRefresh) {

                // Clear off the old model instance
                if (originalModel != null) {
                    root.remove(originalModel);
                }
                if (newModel != null) {
                    stack.push(newModel);
                }
            }
        }
    }
}
