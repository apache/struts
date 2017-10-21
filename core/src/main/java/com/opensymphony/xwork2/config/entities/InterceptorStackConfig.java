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
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Configuration for InterceptorStack.
 *
 * <p>
 * In the xml configuration file this is defined as the <code>interceptor-stack</code> tag.
 * </p>
 *
 * @author Mike
 * @author Rainer Hermanns
 */
public class InterceptorStackConfig extends Located implements Serializable {

    private static final long serialVersionUID = 2897260918170270343L;

    /**
     * A list of InterceptorMapping object
     */
    protected List<InterceptorMapping> interceptors;
    protected String name;

    /**
     * Creates an InterceptorStackConfig object.
     */
    protected InterceptorStackConfig() {
        this.interceptors = new ArrayList<>();
    }

    /**
     * Creates an InterceptorStackConfig object with a particular <code>name</code>.
     *
     * @param orig interceptor stack configuration
     */
    protected InterceptorStackConfig(InterceptorStackConfig orig) {
        this.name = orig.name;
        this.interceptors = new ArrayList<>(orig.interceptors);
        this.location = orig.location;
    }


    /**
     * Returns a <code>Collection</code> of InterceptorMapping objects.
     *
     * @return collection of interceptor mappings
     */
    public Collection<InterceptorMapping> getInterceptors() {
        return interceptors;
    }

    /**
     * Get the name of this interceptor stack configuration.
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * An InterceptorStackConfig object is equals with <code>o</code> only if
     * <ul>
     * <li>o is an InterceptorStackConfig object</li>
     * <li>both names are equals</li>
     * <li>all of their <code>InterceptorMapping</code>s are equals</li>
     * </ul>
     *
     * @param o the given object to check
     * @return true if object match the rules
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof InterceptorStackConfig)) {
            return false;
        }

        final InterceptorStackConfig interceptorStackConfig = (InterceptorStackConfig) o;

        if ((interceptors != null) ? (!interceptors.equals(interceptorStackConfig.interceptors)) : (interceptorStackConfig.interceptors != null)) {
            return false;
        }

        if ((name != null) ? (!name.equals(interceptorStackConfig.name)) : (interceptorStackConfig.name != null)) {
            return false;
        }

        return true;
    }

    /**
     * @return generated hashcode based on <code>InterceptorStackConfig</code>'s name and its
     * <code>InterceptorMapping</code>s.
     */
    @Override
    public int hashCode() {
        int result;
        result = ((name != null) ? name.hashCode() : 0);
        result = (29 * result) + ((interceptors != null) ? interceptors.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return "InterceptorStackConfig: [" + name + "] contains " + interceptors;
    }

    /**
     * The builder for this object.  An instance of this object is the only way to construct a new instance.  The
     * purpose is to enforce the immutability of the object.  The methods are structured in a way to support chaining.
     * After setting any values you need, call the {@link #build()} method to create the object.
     */
    public static class Builder implements InterceptorListHolder {
        protected InterceptorStackConfig target;

        public Builder(String name) {
            target = new InterceptorStackConfig();
            target.name = name;
        }

        public Builder name(String name) {
            target.name = name;
            return this;
        }

        /**
         * Add an <code>InterceptorMapping</code> object.
         *
         * @param interceptor interceptor mapping
         *
         * @return this builder
         */
        public Builder addInterceptor(InterceptorMapping interceptor) {
            target.interceptors.add(interceptor);
            return this;
        }

        /**
         * Add a List of <code>InterceptorMapping</code> objects.
         *
         * @param interceptors list of interceptor mappings
         *
         * @return this builder
         */
        public Builder addInterceptors(List<InterceptorMapping> interceptors) {
            target.interceptors.addAll(interceptors);
            return this;
        }

        public Builder location(Location loc) {
            target.location = loc;
            return this;
        }

        public InterceptorStackConfig build() {
            embalmTarget();
            InterceptorStackConfig result = target;
            target = new InterceptorStackConfig(target);
            return result;
        }

        protected void embalmTarget() {
            target.interceptors = Collections.unmodifiableList(target.interceptors);
        }
    }
}
