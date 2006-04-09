<%@ taglib prefix="saf" uri="/struts-action" %>
<saf:head theme="ajax"/>
<link rel="stylesheet" type="text/css" href="<saf:url value="/webwork/tabs.css"/>">
<b>This is a tabbed pane with two panels that fetches data from a remote action via ajax</b>

<saf:tabbedPanel id="test2" theme="simple" >
      <saf:panel id="left" tabName="left" theme="ajax" href="/">
          This is the left pane<br/>
          <saf:form >
              <saf:textfield name="tt" label="Test Text" />  <br/>
              <saf:textfield name="tt2" label="Test Text2" />
          </saf:form>
      </saf:panel>
      <saf:panel remote="true" href="/view/ajaxData.action" id="ryh1" theme="ajax" tabName="remote one" />
      <saf:panel id="middle" tabName="middle" theme="ajax" href="/">
          middle tab<br/>
          <saf:form >
              <saf:textfield name="tt" label="Test Text44" />  <br/>
              <saf:textfield name="tt2" label="Test Text442" />
          </saf:form>
      </saf:panel>
      <saf:panel remote="true" href="/view/ajaxData.action"  id="ryh21" theme="ajax" tabName="remote right" />
  </saf:tabbedPanel>
  
<p/>
A DIV that waits for 5 seconds before loading the contents
<saf:div
        id="once"
        theme="ajax"
        cssStyle="border: 1px solid yellow;"
        href="/view/ajaxData.action"
        delay="5000"
        loadingText="loading...">
    Waiting for data</saf:div>
<p/>
A DIV that is updated every 2 seconds
<saf:div
            id="twoseconds"
            cssStyle="border: 1px solid yellow;"
            href="/view/ajaxData.action"
            theme="ajax"
            delay="2000"
            updateFreq="2000"
            errorText="There was an error"
            loadingText="loading...">Initial Content
    </saf:div>
<p/>
<a href="<saf:url action="index"/>">Back to front page</a>
