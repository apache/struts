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
package org.apache.struts2.tiles;

import org.apache.tiles.request.ApplicationResource;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;

public class StrutsApplicationResourceTest {

    @Test
    public void testWW5011workaround() throws Exception {
        URL resource = getClass().getClassLoader().getResource("emptyTiles.xml");
        ApplicationResource ar = new StrutsApplicationResource(resource);
        Assert.assertNotEquals(0, ar.getLastModified());

        resource = getClass().getClassLoader().getResource("emptyTiles###2.xml");
        ar = new StrutsApplicationResource(resource);
        Assert.assertNotEquals(0, ar.getLastModified());

        resource = getClass().getClassLoader().getResource("emptyTiles.xml");
        ar = new StrutsApplicationResource(new URL(resource + "#ref1"));
        Assert.assertNotEquals(0, ar.getLastModified());
    }
}
