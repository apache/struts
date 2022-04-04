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
