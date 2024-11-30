package org.apache.struts2.views.jasperreports7;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperReport;

public interface JasperReport7Aware {

    void beforeReportGeneration(HttpServletRequest request, HttpServletResponse response) throws Exception;
    void afterReportGeneration(HttpServletRequest request, HttpServletResponse response, JasperReport jasperReport) throws Exception;

}
