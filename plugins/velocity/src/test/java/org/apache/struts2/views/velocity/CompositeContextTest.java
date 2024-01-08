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
package org.apache.struts2.views.velocity;

import org.apache.velocity.context.Context;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompositeContextTest {

    @Test
    public void compositeLooksUpInForwardOrder_factory() {
        Context context1 = mock(Context.class);
        Context context2 = mock(Context.class);

        Context compositeContext = CompositeContext.composite(asList(context1, context2));

        when(context1.get("x")).thenReturn("x1");
        when(context2.get("x")).thenReturn("x2");

        assertEquals("x1", compositeContext.get("x"));
    }

    @Test
    public void compositeLooksUpInForwardOrder_altFactory() {
        Context context1 = mock(Context.class);
        Context context2 = mock(Context.class);

        Context compositeContext = CompositeContext.composite(context1, context2);

        when(context1.get("x")).thenReturn("x1");
        when(context2.get("x")).thenReturn("x2");

        assertEquals("x1", compositeContext.get("x"));
    }

    @Test
    public void compositeGeneratesSupersetOfKeys() {
        final Context context1 = mock(Context.class);
        final Context context2 = mock(Context.class);

        when(context1.getKeys()).thenReturn(new String[]{"a", "b"});
        when(context2.getKeys()).thenReturn(new String[]{"b", "c"});

        Context compositeContext = CompositeContext.composite(asList(context1, context2));

        assertThat(compositeContext.getKeys()).containsExactlyInAnyOrder("a", "b", "c");
    }
}
