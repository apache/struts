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
 * <p>
 * Deserializes an object from a JSON string.
 * </p>
 */
public interface JSONReader {

    int DEFAULT_MAX_ELEMENTS = 10_000;
    int DEFAULT_MAX_DEPTH = 64;
    int DEFAULT_MAX_STRING_LENGTH = 262_144;    // 256KB
    int DEFAULT_MAX_KEY_LENGTH = 512;

    Object read(String string) throws JSONException;

    void setMaxElements(int maxElements);

    void setMaxDepth(int maxDepth);

    void setMaxStringLength(int maxStringLength);

    void setMaxKeyLength(int maxKeyLength);
}
