/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.Location;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Configuration for Result.
 * <p/>
 * In the xml configuration file this is defined as the <code>result</code> tag.
 *
 * @author Mike
 */
public class ResultConfig extends Located implements Serializable {

    protected Map<String,String> params;
    protected String className;
    protected String name;

    protected ResultConfig(String name, String className) {
        this.name = name;
        this.className = className;
        params = new LinkedHashMap<String, String>();
    }

    protected ResultConfig(ResultConfig orig) {
        this.params = orig.params;
        this.name = orig.name;
        this.className = orig.className;
        this.location = orig.location;
    }

    public String getClassName() {
        return className;
    }

    public String getName() {
        return name;
    }

    public Map<String,String> getParams() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ResultConfig)) {
            return false;
        }

        final ResultConfig resultConfig = (ResultConfig) o;

        if ((className != null) ? (!className.equals(resultConfig.className)) : (resultConfig.className != null)) {
            return false;
        }

        if ((name != null) ? (!name.equals(resultConfig.name)) : (resultConfig.name != null)) {
            return false;
        }

        if ((params != null) ? (!params.equals(resultConfig.params)) : (resultConfig.params != null)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = ((name != null) ? name.hashCode() : 0);
        result = (29 * result) + ((className != null) ? className.hashCode() : 0);
        result = (29 * result) + ((params != null) ? params.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return "ResultConfig: [" + name + "] => [" + className + "] with params " + params;
    }

    /**
     * The builder for this object.  An instance of this object is the only way to construct a new instance.  The
     * purpose is to enforce the immutability of the object.  The methods are structured in a way to support chaining.
     * After setting any values you need, call the {@link #build()} method to create the object.
     */
    public static final class Builder {
        protected ResultConfig target;

        public Builder(String name, String className) {
            target = new ResultConfig(name, className);
        }

        public Builder(ResultConfig orig) {
            target = new ResultConfig(orig);
        }

        public Builder name(String name) {
            target.name = name;
            return this;
        }

        public Builder className(String name) {
            target.className = name;
            return this;
        }

         public Builder addParam(String name, String value) {
            target.params.put(name, value);
            return this;
        }

        public Builder addParams(Map<String,String> params) {
            target.params.putAll(params);
            return this;
        }

        public Builder location(Location loc) {
            target.location = loc;
            return this;
        }

        public ResultConfig build() {
            embalmTarget();
            ResultConfig result = target;
            target = new ResultConfig(target);
            return result;
        }

        protected void embalmTarget() {
            target.params = Collections.unmodifiableMap(target.params);
        }
    }
}
