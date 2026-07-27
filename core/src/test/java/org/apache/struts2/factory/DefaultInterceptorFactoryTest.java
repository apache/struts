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
package org.apache.struts2.factory;

import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.entities.InterceptorConfig;
import org.apache.struts2.interceptor.ActionFileUploadInterceptor;
import org.apache.struts2.interceptor.Interceptor;
import org.apache.struts2.interceptor.UploadPolicy;
import org.apache.struts2.mock.MockInterceptor;
import org.apache.struts2.mock.MockLazyInterceptor;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Covers the configuration-time validation of {@code WithLazyParams} interceptor params (WW-5659).
 * <p>
 * Params of a lazy interceptor-ref are resolved onto a per-invocation params holder, so a name the
 * holder cannot accept can never be applied. Before this check that was only noticed per request,
 * where it costs a WARN and - for {@link UploadPolicy} - the rejection of every upload. The names
 * are known when the configuration is parsed, so it fails there instead.
 */
public class DefaultInterceptorFactoryTest extends StrutsInternalTestCase {

    private InterceptorFactory factory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        factory = container.getInstance(InterceptorFactory.class);
    }

    private static Map<String, String> params(String... keysAndValues) {
        Map<String, String> params = new LinkedHashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            params.put(keysAndValues[i], keysAndValues[i + 1]);
        }
        return params;
    }

    private static InterceptorConfig config(String name, Class<?> clazz) {
        return new InterceptorConfig.Builder(name, clazz.getName()).build();
    }

    public void testRefParamsWritableOnTheHolderAreAccepted() {
        InterceptorConfig config = config("actionFileUpload", ActionFileUploadInterceptor.class);

        assertThatCode(() -> factory.buildInterceptor(config, params(
                "allowedTypes", "${uploadConfig.allowedMimeTypes}",
                "allowedExtensions", "${uploadConfig.allowedExtensions}",
                "maximumSize", "${uploadConfig.maxFileSize}",
                "disabled", "${uploadConfig.skip}"))).doesNotThrowAnyException();
    }

    public void testUnknownRefParamFailsConfiguration() {
        InterceptorConfig config = config("actionFileUpload", ActionFileUploadInterceptor.class);

        assertThatThrownBy(() -> factory.buildInterceptor(config, params("maximumSizes", "1024")))
                .isInstanceOf(ConfigurationException.class)
                .hasMessageContaining("maximumSizes")
                .hasMessageContaining("actionFileUpload")
                .hasMessageContaining(ActionFileUploadInterceptor.class.getName())
                .hasMessageContaining(UploadPolicy.class.getName());
    }

    /**
     * The failure must not be buried by the factory's generic {@code catch (Exception)} wrapper,
     * otherwise the operator reads "Caught Exception while registering Interceptor class" and has to
     * dig through the cause chain to find the param name.
     */
    public void testUnknownRefParamFailureIsNotRewrapped() {
        InterceptorConfig config = config("actionFileUpload", ActionFileUploadInterceptor.class);

        assertThatThrownBy(() -> factory.buildInterceptor(config, params("nope", "x")))
                .isInstanceOf(ConfigurationException.class)
                .hasNoCause();
    }

    /**
     * Params declared on the {@code <interceptor>} definition are applied to the interceptor and
     * never reach the {@code InterceptorMapping}, so they never reach the holder either. Validating
     * them would reject working configurations.
     */
    public void testInterceptorDefinitionParamsAreNotValidatedAgainstTheHolder() {
        InterceptorConfig config = new InterceptorConfig.Builder("lazy", MockLazyInterceptor.class.getName())
                .addParam("interceptorOnly", "applied at build time")
                .build();

        Interceptor interceptor = factory.buildInterceptor(config, params());

        assertThat(interceptor).isInstanceOf(MockLazyInterceptor.class);
        assertThat(((MockLazyInterceptor) interceptor).getInterceptorOnly()).isEqualTo("applied at build time");
    }

    /**
     * Same property, this time on the interceptor-ref: now it does reach the holder, and the holder
     * has no such property.
     */
    public void testSameParamOnTheRefIsValidated() {
        InterceptorConfig config = config("lazy", MockLazyInterceptor.class);

        assertThatThrownBy(() -> factory.buildInterceptor(config, params("interceptorOnly", "x")))
                .isInstanceOf(ConfigurationException.class)
                .hasMessageContaining("interceptorOnly")
                .hasMessageContaining(MockLazyInterceptor.MockLazyParams.class.getName());
    }

    /**
     * A compound name is an OGNL navigation expression, not a property of the holder, so this simple
     * check cannot decide it and leaves it to the runtime path.
     */
    public void testCompoundParamNameIsNotValidated() {
        InterceptorConfig config = config("lazy", MockLazyInterceptor.class);

        assertThatCode(() -> factory.buildInterceptor(config, params("nested.foo", "x"))).doesNotThrowAnyException();
    }

    /**
     * Non-lazy interceptors keep the lenient behaviour: their params go straight onto the instance
     * and an unknown one is ignored, which is long-standing and out of scope here.
     */
    public void testNonLazyInterceptorRefParamsAreNotValidated() {
        InterceptorConfig config = config("test", MockInterceptor.class);

        assertThatCode(() -> factory.buildInterceptor(config, params("noSuchProperty", "x"))).doesNotThrowAnyException();
    }
}
