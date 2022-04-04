package it.org.apache.struts2.rest.example;

public class ParameterUtils {

    public static String getBaseUrl() {
        String port = System.getProperty("http.port");
        if (port == null) {
            port = "8080";
        }
        return "http://localhost:"+port+"/struts2-rest-showcase";
    }
}
