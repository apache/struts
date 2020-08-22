package org.apache.struts2.action;

import com.opensymphony.xwork2.XWorkTestCase;
import org.apache.struts2.interceptor.csp.CspSettings;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class CspReportActionTest extends XWorkTestCase {
  public void testWhenMethodNotPost_thenReportNotProcessed() {
    for (HttpMethod method : HttpMethod.values()) {
      TestCspReportAction cspReportAction = new TestCspReportAction();

      // only expect a report if the method is post
      int expectedReports = method == HttpMethod.POST ? 1 : 0;

      MockHttpServletRequest request = new MockHttpServletRequest(method.toString(), "/requestUri");
      request.setContent("someSampleContent".getBytes());
      request.setContentType(CspSettings.CSP_REPORT_TYPE);

      cspReportAction.withServletRequest(request);

      assertEquals(
          "Unexpected behaviour with method " + method,
          expectedReports,
          cspReportAction.actualNumberOfReports
      );

      assertCorrectResponseStatusCode(cspReportAction);
    }
  }

  public void testWhenNoContentLength_thenReportNotProcessed() {
    TestCspReportAction cspReportAction = new TestCspReportAction();
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/requestUri");
    request.setContentType(CspSettings.CSP_REPORT_TYPE);

    cspReportAction.withServletRequest(request);

    assertEquals(
        "Report request with empty body should not be processed",
        0,
        cspReportAction.actualNumberOfReports
    );

    assertCorrectResponseStatusCode(cspReportAction);
  }

  public void testWhenContentTypeNotCsp_thenReportNotProcessed() {
    TestCspReportAction cspReportAction = new TestCspReportAction();
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/requestUri");
    request.setContent("someSampleContent".getBytes());
    request.setContentType("application/json");

    cspReportAction.withServletRequest(request);

    assertEquals(
        "Report request with wrong content type should not be processed",
        0,
        cspReportAction.actualNumberOfReports
    );

    assertCorrectResponseStatusCode(cspReportAction);
  }

  public void testWhenValidReportRequest_thenReportProcessed() {
    TestCspReportAction cspReportAction = new TestCspReportAction();
    String sampleReport = "someSampleContent";

    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/requestUri");
    request.setContent(sampleReport.getBytes());
    request.setContentType(CspSettings.CSP_REPORT_TYPE);

    cspReportAction.withServletRequest(request);

    assertEquals(
        "Valid report request was not processed",
        1,
        cspReportAction.actualNumberOfReports
    );

    assertEquals(
        "Processed report body did not match",
        sampleReport,
        cspReportAction.actualReport
    );

    assertCorrectResponseStatusCode(cspReportAction);
  }

  private void assertCorrectResponseStatusCode(TestCspReportAction cspReportAction) {
    MockHttpServletResponse response = new MockHttpServletResponse();
    cspReportAction.withServletResponse(response);

    assertEquals(
        "Unexpected response status code: " + response.getStatus(),
        204,
        response.getStatus()
    );
  }

  static class TestCspReportAction extends CspReportAction {
    int actualNumberOfReports;
    String actualReport;

    @Override
    void processReport(String jsonCspReport) {
      actualNumberOfReports++;
      actualReport = jsonCspReport;
    }
  }
}
