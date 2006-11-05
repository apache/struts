/*
 * $Id: ActionMapper.java 449367 2006-09-24 06:49:04Z mrdon $
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
package org.apache.struts2.dispatcher.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.config.Settings;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.util.FileManager;

/**
 * <!-- START SNIPPET: description -->
 * 
 * A composite action mapper that is capable of delegating to a series of {@link ActionMapper} if the former 
 * failed to obtained a valid {@link ActionMapping} or uri.
 * <p/>
 * It is configured through struts.properties. 
 * <p/>
 * For example, with the following entries in struts.properties
 * <p/>
 * <pre>
 * struts.mapper.class=org.apache.struts2.dispatcher.mapper.CompositeActionMapper
 * struts.mapper.composite.1=org.apache.struts2.dispatcher.mapper.DefaultActionMapper
 * struts.mapper.composite.2=org.apache.struts2.dispatcher.mapper.RestfulActionMapper
 * struts.mapper.composite.3=org.apache.struts2.dispatcher.mapper.Restful2ActionMapper
 * </pre>
 * <p/>
 * When {@link CompositeActionMapper#getMapping(HttpServletRequest, ConfigurationManager)} or 
 * {@link CompositeActionMapper#getUriFromActionMapping(ActionMapping)} is invoked, 
 * {@link CompositeActionMapper} would go through these {@link ActionMapper}s in sequence 
 * starting from {@link ActionMapper} identified by 'struts.mapper.composite.1', followed by 
 * 'struts.mapper.composite.2' and finally 'struts.mapper.composite.3' (in this case) until either
 * one of the {@link ActionMapper} return a valid result (not null) or it runs out of {@link ActionMapper}
 * in which case it will just return null for both 
 * {@link CompositeActionMapper#getMapping(HttpServletRequest, ConfigurationManager)} and 
 * {@link CompositeActionMapper#getUriFromActionMapping(ActionMapping)} methods.
 * <p/>
 * 
 * For example with the following in struts.properties :-
 * <pre>
 * struts.mapper.class=org.apache.struts2.dispatcher.mapper.CompositeActionMapper
 * struts.mapper.composite.1=org.apache.struts2.dispatcher.mapper.DefaultActionMapper
 * struts.mapper.composite.2=foo.bar.MyActionMapper
 * struts.mapper.composite.3=foo.bar.MyAnotherActionMapper
 * </pre>
 * <p/>
 * <code>CompositeActionMapper</code> will be configured with 3 ActionMapper, namely
 * "DefaultActionMapper", "MyActionMapper" and "MyAnotherActionMapper".  
 * <code>CompositeActionMapper</code> would consult each of them in order described above.
 * 
 * <!-- END SNIPPET: description -->
 * 
 * @see ActionMapper
 * @see ActionMapperFactory
 * @see ActionMapping
 * @see IndividualActionMapperEntry
 * 
 * @version $Date$ $Id$
 */
public class CompositeActionMapper implements ActionMapper {

	private static final Log LOG = LogFactory.getLog(CompositeActionMapper.class);
	
	protected List<IndividualActionMapperEntry> orderedActionMappers;
	
	
	public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
		
		for (IndividualActionMapperEntry actionMapperEntry: getOrderedActionMapperEntries()) {
			ActionMapping actionMapping = actionMapperEntry.actionMapper.getMapping(request, configManager);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Using ActionMapper from entry ["+actionMapperEntry.propertyName+"="+actionMapperEntry.propertyValue+"]");
			}
			if (actionMapping == null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("ActionMapper from entry ["+actionMapperEntry.propertyName+"="+actionMapperEntry.propertyValue+"] failed to return an ActionMapping (null)");
				}
			}
			else {
				return actionMapping;
			}
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("exhausted from ActionMapper that could return an ActionMapping");
		}
		return null;
	}

	public String getUriFromActionMapping(ActionMapping mapping) {
		
		for (IndividualActionMapperEntry actionMapperEntry: getOrderedActionMapperEntries()) {
			String uri = actionMapperEntry.actionMapper.getUriFromActionMapping(mapping);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Using ActionMapper from entry ["+actionMapperEntry.propertyName+"="+actionMapperEntry.propertyValue+"]");
			}
			if (uri == null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("ActionMapper from entry ["+actionMapperEntry.propertyName+"="+actionMapperEntry.propertyValue+"] failed to return a uri (null)");
				}
			}
			else {
				return uri;
			}
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("exhausted from ActionMapper that could return a uri");
		}
		return null;
	}
	
	
	protected List<IndividualActionMapperEntry> getOrderedActionMapperEntries() {
		if (this.orderedActionMappers == null || FileManager.isReloadingConfigs()) {
			
			List<IndividualActionMapperEntry> actionMapperEntriesContainer = new ArrayList<IndividualActionMapperEntry>();
			Iterator settings = Settings.list();
			while(settings.hasNext()) {
				String setting = settings.next().toString();
				if (setting.startsWith(StrutsConstants.STRUTS_MAPPER_COMPOSITE)) {
					try {
						int order = Integer.valueOf(setting.substring(StrutsConstants.STRUTS_MAPPER_COMPOSITE.length(), setting.length()));
						String propertyValue = Settings.get(setting);
						if (propertyValue != null && propertyValue.trim().length() > 0) {
							actionMapperEntriesContainer.add(
									new IndividualActionMapperEntry(order, setting, propertyValue));
						}
						else {
							LOG.warn("Ignoring property "+setting+" that contains no value");
						}
					}
					catch(NumberFormatException e) {
						LOG.warn("Ignoring malformed property "+setting);
					}
				}
			}
		
			Collections.sort(actionMapperEntriesContainer, new Comparator<IndividualActionMapperEntry>() {
				public int compare(IndividualActionMapperEntry o1, IndividualActionMapperEntry o2) {
					return o1.compareTo(o2);
				}
			});
		
		
			ObjectFactory objectFactory = ObjectFactory.getObjectFactory();
			List<IndividualActionMapperEntry> result = new ArrayList<IndividualActionMapperEntry>();
			for (IndividualActionMapperEntry entry: actionMapperEntriesContainer) {
				String actionMapperClassName = entry.propertyValue;
				try {
					// Let us get ClassCastException if it does not implement ActionMapper
					ActionMapper actionMapper = (ActionMapper) objectFactory.buildBean(actionMapperClassName, null);
					result.add(new IndividualActionMapperEntry(entry.order, entry.propertyName, entry.propertyValue, actionMapper));
				}
				catch(Exception e) {
					LOG.warn("failed to create action mapper "+actionMapperClassName+", ignoring it", e);
				}
			}
		
			this.orderedActionMappers = result;
		}
		
		return this.orderedActionMappers;
	}
	
	
	/**
	 * A value object (holder) that holds information regarding {@link ActionMapper} this {@link CompositeActionMapper}
	 * is capable of delegating to.
	 * <p/>
	 * The information stored are :-
	 * <ul>
	 * 	<li> order</li>
	 * 	<li> propertyValue</li>
	 * 	<li> propertyName</li>
	 * 	<li> actionMapper</li>
	 * </ul>
	 * 
	 * eg. if we have the following entry in struts.properties
	 * <pre>
	 * struts.mapper.composite.1=foo.bar.ActionMapper1
	 * struts.mapper.composite.2=foo.bar.ActionMapper2
	 * struts.mapper.composite.3=foo.bar.ActionMapper3
	 * </pre>
	 * 
	 * <table border="1">
	 * 	<tr>
	 * 		<td>order</td>
	 *    	<td>propertyName</td>
	 *      <td>propertyValue</td>
	 *      <td>actionMapper</td>
	 *  </tr>
	 *  <tr>
	 *  	<td>1</td>
	 *      <td>struts.mapper.composite.1</td>
	 *      <td>foo.bar.ActionMapper1</td>
	 *      <td>instance of foo.bar.ActionMapper1</td>
	 *  </tr>
	 *  <tr>
	 *  	<td>2</td>
	 *      <td>struts.mapper.composite.2</td>
	 *      <td>foo.bar.ActionMapper2</td>
	 *      <td>instance of foo.bar.ActionMapper2</td>
	 *  </tr>
	 *  <tr>
	 *  	<td>3</td>
	 *      <td>struts.mapper.composite.3</td>
	 *      <td>foo.bar.ActionMapper3</td>
	 *      <td>instance of foo.bar.ActionMapper3</td>
	 *  </tr>
	 * </table>
	 * 
	 * @version $Date$ $Id$
	 */
	public class IndividualActionMapperEntry implements Comparable<IndividualActionMapperEntry> {
		
		public Integer order;
		public String propertyValue;
		public String propertyName;
		public ActionMapper actionMapper;
		
		
		private IndividualActionMapperEntry(Integer order, String propertyName, String propertyValue) {
			assert(order != null);
			assert(propertyValue != null);
			assert(propertyName != null);
			this.order = order;
			this.propertyValue = propertyValue;
			this.propertyName = propertyName;
		}
		
		public IndividualActionMapperEntry(Integer order, String propertyName, String propertyValue, ActionMapper actionMapper) {
			assert(order != null);
			assert(propertyValue != null);
			assert(propertyName != null);
			assert(actionMapper != null);
			this.order = order;
			this.propertyValue = propertyValue;
			this.propertyName = propertyName;
			this.actionMapper = actionMapper;
		}

		public int compareTo(IndividualActionMapperEntry o) {
			return order - o.order;
		}
	}
}
