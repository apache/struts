package org.apache.struts.action2.showcase.jsf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmployeeDao {
	
	private static String[] skills;
	
	private static List<Employee> employees;
	
	static {
		employees = new ArrayList<Employee>();
		employees.add(new Employee(0, "Tom Jones", new String[]{"Java", "PHP"}));
		employees.add(new Employee(1, "Bart Simpson", new String[]{"PHP"}));
		employees.add(new Employee(2, "Sofia Jones", new String[]{"Java"}));
		
		skills = new String[]{"Java", "PHP", "C#"};
	}
	
	public static List<Employee> getEmployees() {
		return employees;
	}
	
	public static Employee getEmployee(int id) {
		return employees.get(id);
	}
	
	public static void save(Employee e) {
		employees.set(e.getId(), e);
	}
	
	public static String[] getSkills() {
		return skills;
	}
}
