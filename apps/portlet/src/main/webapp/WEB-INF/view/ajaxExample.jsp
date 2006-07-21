<%@ taglib prefix="s" uri="/tags" %>
<s:head theme="ajax"/>
<link rel="stylesheet" type="text/css" href="<s:url value="/struts/tabs.css"/>">
<b>This is a tabbed pane with two panels that fetches data from a remote action via ajax</b>

<s:tabbedPanel id="test2" theme="simple" >
      <s:panel id="left" tabName="left" theme="ajax" href="/">
          This is the left pane<br/>
          <s:form >
              <s:textfield name="tt" label="Test Text" />  <br/>
              <s:textfield name="tt2" label="Test Text2" />
          </s:form>
      </s:panel>
      <s:panel remote="true" href="/view/ajaxData.action" id="ryh1" theme="ajax" tabName="remote one" />
      <s:panel id="middle" tabName="middle" theme="ajax" href="/">
          middle tab<br/>
          <s:form >
              <s:textfield name="tt" label="Test Text44" />  <br/>
              <s:textfield name="tt2" label="Test Text442" />
          </s:form>
      </s:panel>
      <s:panel remote="true" href="/view/ajaxData.action"  id="ryh21" theme="ajax" tabName="remote right" />
  </s:tabbedPanel>
  
<p/>
A DIV that waits for 5 seconds before loading the contents
<s:div
        id="once"
        theme="ajax"
        cssStyle="border: 1px solid yellow;"
        href="/view/ajaxData.action"
        delay="5000"
        loadingText="loading...">
    Waiting for data</s:div>
<p/>
A DIV that is updated every 2 seconds
<s:div
            id="twoseconds"
            cssStyle="border: 1px solid yellow;"
            href="/view/ajaxData.action"
            theme="ajax"
            delay="2000"
            updateFreq="2000"
            errorText="There was an error"
            loadingText="loading...">Initial Content
    </s:div>
<p/>
<a href="<s:url action="index"/>">Back to front page</a>
