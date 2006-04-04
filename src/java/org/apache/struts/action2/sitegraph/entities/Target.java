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
package org.apache.struts.action2.sitegraph.entities;


/**
 * User: plightbo
 * Date: Jun 25, 2005
 * Time: 2:11:30 PM
 */
public class Target {
    private String target;
    private int type;

    public Target(String target, int type) {
        this.target = target;
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public int getType() {
        return type;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Target)) return false;

        final Target target1 = (Target) o;

        if (type != target1.type) return false;
        if (target != null ? !target.equals(target1.target) : target1.target != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (target != null ? target.hashCode() : 0);
        result = 29 * result + type;
        return result;
    }
}
