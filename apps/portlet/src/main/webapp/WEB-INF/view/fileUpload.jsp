<%@ taglib prefix="s" uri="/struts-tags" %>

    <h1>Fileupload sample</h1>

	<s:actionerror />
	<s:fielderror />
    <s:form action="fileUpload" method="POST" enctype="multipart/form-data">
        <s:file name="upload" label="File"/>
        <s:textfield name="caption" label="Caption"/>
        <s:submit />
    </s:form>


