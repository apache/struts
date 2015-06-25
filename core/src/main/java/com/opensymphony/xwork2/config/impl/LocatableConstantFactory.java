/**
 * 
 */
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.LocationUtils;

/**
 * Factory that remembers where a constant came from
 */
public class LocatableConstantFactory<T> extends Located implements Factory {
    T constant;
    public LocatableConstantFactory(T constant, Object location) {
        this.constant = constant;
        setLocation(LocationUtils.getLocation(location));
    }
    
    public T create(Context ignored) {
        return constant;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" defined at ");
        sb.append(getLocation().toString());
        return sb.toString();
    }

}