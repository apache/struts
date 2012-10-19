/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.showcase.chat;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.util.StrutsTypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DateConverter extends StrutsTypeConverter {

	private static final Logger LOG = LoggerFactory.getLogger(DateConverter.class);

	public Object convertFromString(Map context, String[] values, Class toClass) {

		if (values.length > 0 && values[0] != null && values[0].trim().length() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat();
			try {
				return sdf.parse(values[0]);
			} catch (ParseException e) {
				LOG.error("error converting value [" + values[0] + "] to Date ", e);
			}
		}
		return null;
	}

	public String convertToString(Map context, Object o) {

		if (o instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			return sdf.format((Date) o);
		}
		return "";
	}
}

