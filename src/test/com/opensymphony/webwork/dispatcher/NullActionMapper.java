/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.dispatcher;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action2.dispatcher.mapper.ActionMapper;
import org.apache.struts.action2.dispatcher.mapper.ActionMapping;

/**
 * ActionMapper for testing FilterDispatcher (used in FilterDispaatcherTest)
 * @author tm_jee
 * @version $Date: 2005/12/09 00:31:49 $ $Id: NullActionMapper.java,v 1.1 2005/12/09 00:31:49 tmjee Exp $
 */
public class NullActionMapper implements ActionMapper {

		private static ActionMapping _actionMapping;
	
		public NullActionMapper() {}
		
		public static void setActionMapping(ActionMapping actionMappingToBeRetrned) {
			_actionMapping = actionMappingToBeRetrned;
		}
		
		public ActionMapping getMapping(HttpServletRequest request) {
			return _actionMapping;
		}

		public String getUriFromActionMapping(ActionMapping mapping) {
			throw new UnsupportedOperationException("operation not supported");
		}
}
