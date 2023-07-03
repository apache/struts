/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.freemarker.ext.jsp;

import freemarker.core.BugException;
import freemarker.core.Environment;
import freemarker.core._UnexpectedTypeErrorExplainerTemplateModel;
import freemarker.ext.beans.SimpleMethodModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template.utility.ClassUtil;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * Used when a custom JSP tag and an EL function uses the same name in a tag library, to create a single FTL value from
 * the two. As FTL as no separate namespace for "tags" and functions, both aspect has to be implemented by the same
 * value.
 * 
 * @since 2.3.25
 */
@SuppressWarnings("rawtypes")
class CustomTagAndELFunctionCombiner {

    /**
     * @param customTag
     *            Either a {@link TemplateDirectiveModel} or a {@link TemplateTransformModel}.
     */
    static TemplateModel combine(TemplateModel customTag, TemplateMethodModelEx elFunction) {
        if (customTag instanceof TemplateDirectiveModel) {
            return elFunction instanceof SimpleMethodModel //
                    ? new TemplateDirectiveModelAndSimpleMethodModel( //
                            (TemplateDirectiveModel) customTag, (SimpleMethodModel) elFunction) //
                    : new TemplateDirectiveModelAndTemplateMethodModelEx( //
                            (TemplateDirectiveModel) customTag, elFunction);
        } else if (customTag instanceof TemplateTransformModel) {
            return (elFunction instanceof SimpleMethodModel)
                    ? new TemplateTransformModelAndSimpleMethodModel( //
                            (TemplateTransformModel) customTag, (SimpleMethodModel) elFunction) //
                    : new TemplateTransformModelAndTemplateMethodModelEx( //
                            (TemplateTransformModel) customTag, elFunction);
        } else {
            throw new BugException(
                    "Unexpected custom JSP tag class: " + ClassUtil.getShortClassNameOfObject(customTag));
        }
    }

    /**
     * Tells if the value can be used as the "custom tag" parameter to
     * {@link #combine(TemplateModel, TemplateMethodModelEx)}.
     */
    static boolean canBeCombinedAsCustomTag(TemplateModel tm) {
        return (tm instanceof TemplateDirectiveModel || tm instanceof TemplateTransformModel)
                && !(tm instanceof CombinedTemplateModel);
    }

    /**
     * Tells if the value can be used as the "EL function" parameter to
     * {@link #combine(TemplateModel, TemplateMethodModelEx)}.
     */
    static boolean canBeCombinedAsELFunction(TemplateModel tm) {
        return tm instanceof TemplateMethodModelEx && !(tm instanceof CombinedTemplateModel);
    }

    private static class CombinedTemplateModel {
        // Marker only
    };

    private static class TemplateDirectiveModelAndSimpleMethodModel extends CombinedTemplateModel
            implements TemplateDirectiveModel, TemplateMethodModelEx, TemplateSequenceModel,
            _UnexpectedTypeErrorExplainerTemplateModel {

        private final TemplateDirectiveModel templateDirectiveModel;
        private final SimpleMethodModel simpleMethodModel;

        public TemplateDirectiveModelAndSimpleMethodModel( //
                TemplateDirectiveModel templateDirectiveModel, SimpleMethodModel simpleMethodModel) {
            this.templateDirectiveModel = templateDirectiveModel;
            this.simpleMethodModel = simpleMethodModel;
        }

        @Override
        public Object exec(List arguments) throws TemplateModelException {
            return simpleMethodModel.exec(arguments);
        }

        @Override
        public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
                throws TemplateException, IOException {
            templateDirectiveModel.execute(env, params, loopVars, body);
        }

        @Override
        public Object[] explainTypeError(Class[] expectedClasses) {
            return simpleMethodModel.explainTypeError(expectedClasses);
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return simpleMethodModel.get(index);
        }

        @Override
        public int size() throws TemplateModelException {
            return simpleMethodModel.size();
        }

    }

    private static class TemplateDirectiveModelAndTemplateMethodModelEx extends CombinedTemplateModel
            implements TemplateDirectiveModel, TemplateMethodModelEx {

        private final TemplateDirectiveModel templateDirectiveModel;
        private final TemplateMethodModelEx templateMethodModelEx;

        public TemplateDirectiveModelAndTemplateMethodModelEx( //
                TemplateDirectiveModel templateDirectiveModel, TemplateMethodModelEx templateMethodModelEx) {
            this.templateDirectiveModel = templateDirectiveModel;
            this.templateMethodModelEx = templateMethodModelEx;
        }

        @Override
        public Object exec(List arguments) throws TemplateModelException {
            return templateMethodModelEx.exec(arguments);
        }

        @Override
        public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
                throws TemplateException, IOException {
            templateDirectiveModel.execute(env, params, loopVars, body);
        }

    }

    private static class TemplateTransformModelAndTemplateMethodModelEx extends CombinedTemplateModel
            implements TemplateTransformModel, TemplateMethodModelEx {

        private final TemplateTransformModel templateTransformModel;
        private final TemplateMethodModelEx templateMethodModelEx;

        public TemplateTransformModelAndTemplateMethodModelEx( //
                TemplateTransformModel templateTransformModel, TemplateMethodModelEx templateMethodModelEx) {
            this.templateTransformModel = templateTransformModel;
            this.templateMethodModelEx = templateMethodModelEx;
        }

        @Override
        public Object exec(List arguments) throws TemplateModelException {
            return templateMethodModelEx.exec(arguments);
        }

        @Override
        public Writer getWriter(Writer out, Map args) throws TemplateModelException, IOException {
            return templateTransformModel.getWriter(out, args);
        }

    }

    private static class TemplateTransformModelAndSimpleMethodModel extends CombinedTemplateModel
            implements TemplateTransformModel, TemplateMethodModelEx, TemplateSequenceModel,
            _UnexpectedTypeErrorExplainerTemplateModel {

        private final TemplateTransformModel templateTransformModel;
        private final SimpleMethodModel simpleMethodModel;

        public TemplateTransformModelAndSimpleMethodModel( //
                TemplateTransformModel templateTransformModel, SimpleMethodModel simpleMethodModel) {
            this.templateTransformModel = templateTransformModel;
            this.simpleMethodModel = simpleMethodModel;
        }

        @Override
        public Object exec(List arguments) throws TemplateModelException {
            return simpleMethodModel.exec(arguments);
        }

        @Override
        public Object[] explainTypeError(Class[] expectedClasses) {
            return simpleMethodModel.explainTypeError(expectedClasses);
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return simpleMethodModel.get(index);
        }

        @Override
        public int size() throws TemplateModelException {
            return simpleMethodModel.size();
        }

        @Override
        public Writer getWriter(Writer out, Map args) throws TemplateModelException, IOException {
            return templateTransformModel.getWriter(out, args);
        }

    }

}
