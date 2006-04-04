/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.struts.action2.views.jsp;


/**
 * The iterator tag can export an IteratorStatus object so that
 * one can get information about the status of the iteration, such as
 * the size, current index, and whether any more items are available.
 *
 * @author Rickard �berg (rickard@dreambean.com)
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
        return ((state.index + 1) % 2) == 1;
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
