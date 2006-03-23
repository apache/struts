<%@ taglib prefix="ww" uri="/webwork" %>
<ww:head theme="ajax"/>
<link rel="stylesheet" type="text/css" href="<ww:url value="/webwork/tabs.css"/>">
<b>This is a tabbed pane with two panels that fetches data from a remote action via ajax</b>

<ww:tabbedPanel id="test2" theme="simple" >
      <ww:panel id="left" tabName="left" theme="ajax" href="/">
          This is the left pane<br/>
          <ww:form >
              <ww:textfield name="tt" label="Test Text" />  <br/>
              <ww:textfield name="tt2" label="Test Text2" />
          </ww:form>
      </ww:panel>
      <ww:panel remote="true" href="/view/ajaxData.action" id="ryh1" theme="ajax" tabName="remote one" />
      <ww:panel id="middle" tabName="middle" theme="ajax" href="/">
          middle tab<br/>
          <ww:form >
              <ww:textfield name="tt" label="Test Text44" />  <br/>
              <ww:textfield name="tt2" label="Test Text442" />
          </ww:form>
      </ww:panel>
      <ww:panel remote="true" href="/view/ajaxData.action"  id="ryh21" theme="ajax" tabName="remote right" />
  </ww:tabbedPanel>
  
<p/>
A DIV that waits for 5 seconds before loading the contents
<ww:div
        id="once"
        theme="ajax"
        cssStyle="border: 1px solid yellow;"
        href="/view/ajaxData.action"
        delay="5000"
        loadingText="loading...">
    Waiting for data</ww:div>
<p/>
A DIV that is updated every 2 seconds
<ww:div
            id="twoseconds"
            cssStyle="border: 1px solid yellow;"
            href="/view/ajaxData.action"
            theme="ajax"
            delay="2000"
            updateFreq="2000"
            errorText="There was an error"
            loadingText="loading...">Initial Content
    </ww:div>
<p/>
<a href="<ww:url action="index"/>">Back to front page</a>
