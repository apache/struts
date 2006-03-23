package com.opensymphony.webwork.showcase.person;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * User: plightbo
 * Date: Sep 20, 2005
 * Time: 6:27:07 PM
 */
public class PersonManager {
    private static Set people = new HashSet(5);
    private static long COUNT = 5;

    static {
        // create some imaginary persons
        Person p1 = new Person(new Long(1), "Patrick", "Lightbuddie");
        Person p2 = new Person(new Long(2), "Jason", "Carrora");
        Person p3 = new Person(new Long(3), "Alexandru", "Papesco");
        Person p4 = new Person(new Long(4), "Jay", "Boss");
        Person p5 = new Person(new Long(5), "Rainer", "Hermanos");
        people.add(p1);
        people.add(p2);
        people.add(p3);
        people.add(p4);
        people.add(p5);
    }

    public void createPerson(Person person) {
        person.setId(new Long(++COUNT));
        people.add(person);
    }

    public void updatePerson(Person person) {
        people.add(person);
    }

    public Set getPeople() {
        return people;
    }
}
