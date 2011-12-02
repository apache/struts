package com.opensymphony.xwork2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <code>GenericsBean</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 */
public class GenericsBean {
    private List<Double> blubb;
    private List<Double> getterList;
    private Map<Double, Integer> genericMap = new HashMap<Double, Integer>();
    private Map<Double, List<Integer>> extendedMap = new HashMap<Double, List<Integer>>();

    /**
     * @return Returns the doubles.
     */
    public List<Double> getDoubles() {
        return blubb;
    }

    /**
     * @param doubles The doubles to set.
     */
    public void setDoubles(List<Double> doubles) {
        this.blubb = doubles;
    }

    public Map<Double, Integer> getGenericMap() {
        return genericMap;
    }

    public void setGenericMap(Map<Double, Integer> genericMap) {
        this.genericMap = genericMap;
    }

    public List<Double> getGetterList() {
        if ( getterList == null ) {
            getterList = new ArrayList<Double>(1);
            getterList.add(42.42);
        }
        return getterList;
    }

    public Map<Double, List<Integer>> getExtendedMap() {
        return extendedMap;
    }

    public void setExtendedMap(Map<Double, List<Integer>> extendedMap) {
        this.extendedMap = extendedMap;
    }
}
