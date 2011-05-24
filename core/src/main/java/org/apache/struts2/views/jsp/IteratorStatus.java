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

package org.apache.struts2.views.jsp;


/**
 * The iterator tag can export an IteratorStatus object so that
 * one can get information about the status of the iteration, such as:
 * <ul>
 * <li>index: current iteration index, starts on 0 and increments in one on every iteration</li>
 * <li>count: iterations so far, starts on 1. count is always index + 1</li>
 * <li>first: true if index == 0</li>
 * <li>even: true if (index + 1) % 2 == 0</li>
 * <li>last: true if current iteration is the last iteration</li> 
 * <li>odd: true if (index + 1) % 2 == 1</li>
 * </ul>
 * <p>Example</p>
 * <pre>
 *   &lt;s:iterator status="status" value='{0, 1}'&gt;
 *      Index: &lt;s:property value="%{#status.index}" /&gt; &lt;br /&gt;
 *      Count: &lt;s:property value="%{#status.count}" /&gt; &lt;br /&gt;  
 *   &lt;/s:iterator>
 * </pre>
 * 
 * <p>will print</p>
 * <pre>
 *      Index: 0
 *      Count: 1
 *      Index: 1
 *      Count: 2
 * </pre>
 */
public class IteratorStatus {
    protected StatusState state;

    public IteratorStatus(StatusState aState) {
        state = aState;
    }

    public int getCount() {
        return state.index + 1;
    }

    public boolean isEven() {
        return ((state.index + 1) % 2) == 0;
    }

    public boolean isFirst() {
        return state.index == 0;
    }

    public int getIndex() {
        return state.index;
    }

    public boolean isLast() {
        return state.last;
    }

    public boolean isOdd() {
        return ((state.index + 1) % 2) != 0;
    }

    public int modulus(int operand) {
        return (state.index + 1) % operand;
    }

    public static class StatusState {
        boolean last = false;
        int index = 0;

        public void setLast(boolean isLast) {
            last = isLast;
        }

        public void next() {
            index++;
        }
    }
}
