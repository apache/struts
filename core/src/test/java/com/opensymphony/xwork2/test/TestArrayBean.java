package com.opensymphony.xwork2.test;

import java.util.ArrayList;
import java.util.List;

public class TestArrayBean {

    private List<String> persons = new ArrayList<>();

    public void setPersons(List<String> persons) {
        this.persons = persons;
    }

    public List<String> getPersons() {
        return persons;
    }
}
