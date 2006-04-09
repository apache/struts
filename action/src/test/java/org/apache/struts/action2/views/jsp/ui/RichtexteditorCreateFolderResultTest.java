/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.components.AbstractRichtexteditorConnector;


/**
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class RichtexteditorCreateFolderResultTest extends AbstractRichtexteditorTest {

	public void testExecuteResult() throws Exception {
		invocation.getStack().getContext().put("__richtexteditorCreateFolder", 
				AbstractRichtexteditorConnector.CreateFolderResult.noErrors());
		invocation.getStack().getContext().put("__richtexteditorCommand", "CreateFolder");
		invocation.getStack().getContext().put("__richtexteditorType", "Image");
		invocation.getStack().getContext().put("__richtexteditorFolderPath", "/folder/path/");
		invocation.getStack().getContext().put("__richtexteditorServerPath", "/server/path/");
		
		RichtexteditorCreateFolderResult result = new RichtexteditorCreateFolderResult();
		result.execute(invocation);
		
		verify(RichtexteditorCreateFolderResultTest.class.getResourceAsStream("RichtexteditorCreateFolderResultTest1.txt"));
	}
}
