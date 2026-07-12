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
package org.apache.struts2.json;

/**
 * Marker reader proving that a user-configured {@code struts.json.reader} override
 * wins over the default {@link StrutsJSONReader}.
 */
public class CustomTestJSONReader implements JSONReader {

    public static final String SENTINEL = "__customReader__";

    @Override
    public Object read(String string) throws JSONException {
        return SENTINEL;
    }

    // Limit setters are intentionally ignored: this marker always returns SENTINEL.
    @Override public void setMaxElements(int maxElements) { /* no-op marker */ }
    @Override public void setMaxDepth(int maxDepth) { /* no-op marker */ }
    @Override public void setMaxStringLength(int maxStringLength) { /* no-op marker */ }
    @Override public void setMaxKeyLength(int maxKeyLength) { /* no-op marker */ }
}
