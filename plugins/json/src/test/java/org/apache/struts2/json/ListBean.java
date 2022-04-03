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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListBean {

    private List<List<String>> listOfLists;

    public ListBean() {
        listOfLists = new ArrayList<List<String>>();

        listOfLists.add(Arrays.asList("1", "2"));
        listOfLists.add(Arrays.asList("3", "4"));
        listOfLists.add(Arrays.asList("5", "6"));
        listOfLists.add(Arrays.asList("7", "8"));
        listOfLists.add(Arrays.asList("9", "0"));
    }

    public List<List<String>> getListOfLists() {
        return listOfLists;
    }
}
