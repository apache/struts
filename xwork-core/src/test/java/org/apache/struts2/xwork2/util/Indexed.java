package com.opensymphony.xwork2.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mimo
 *
 */
public class Indexed {

    public Object[] values = new Object[3];
    public Map<String, Object> map = new HashMap<String, Object>();

    public void setSimple(int i, Object v) {
        values[i] = v;
    }

    public Object getSimple(int i) {
        return values[i];
    }



    public void setIntegerMap(String key, Integer value) {
        map.put(key, value);
    }

    public Integer getIntegerMap(String key) {
        return (Integer) map.get(key);
    }

    public void setStringMap(String key, String value) {
        map.put(key, value);
    }

    public String getStringMap(String key) {
        return (String) map.get(key);
    }

}
