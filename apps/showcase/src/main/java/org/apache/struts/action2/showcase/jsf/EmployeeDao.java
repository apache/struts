package org.apache.struts.action2.showcase.jsf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EmployeeDao {
	
	private static String[] skills;
	
	private static Map<Integer,Employee> employees;
    
    private static int counter = 4;
    
	static {
		employees = new TreeMap<Integer,Employee>();
		employees.put(1, new Employee(1, "Tom Jones", new String[]{"Java", "PHP"}));
		employees.put(2, new Employee(2, "Bart Simpson", new String[]{"PHP"}));
		employees.put(3, new Employee(3, "Sofia Jones", new String[]{"Java"}));
		
		skills = new String[]{"Java", "PHP", "C#"};
	}
	
	public static Map<Integer,Employee> getEmployees() {
		return employees;
	}
	
	public static Employee getEmployee(int id) {
		return employees.get(id);
	}
	
	public static void save(Employee e) {
        if (e.getId() > 0) {
            employees.put(e.getId(), e);
        } else {
            e.setId(counter);
            employees.put(counter, e);
            counter++;
        }
	}
	
	public static String[] getSkills() {
		return skills;
	}
}
