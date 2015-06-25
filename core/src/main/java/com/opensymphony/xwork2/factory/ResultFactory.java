package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.config.entities.ResultConfig;

import java.util.Map;

/**
 * Used by {@link com.opensymphony.xwork2.ObjectFactory} to build {@link com.opensymphony.xwork2.Result}
 */
public interface ResultFactory {

    Result buildResult(ResultConfig resultConfig, Map<String, Object> extraContext) throws Exception;

}
