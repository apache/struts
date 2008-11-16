/*
 * $Id: Person.java 440597 2006-09-06 03:34:39Z tmjee $
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
package org.apache.struts2.showcase.conversion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

/**
 * @version $Date$ $Id$
 */
public class EnumTypeConverter extends StrutsTypeConverter {

	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		List<Enum> result = new ArrayList<Enum>();
		for (int a=0; a< values.length; a++) {
			Enum e = Enum.valueOf(OperationsEnum.class, values[a]);
			if (e != null)
				result.add(e);
		}
		return result;
	}

	@Override
	public String convertToString(Map context, Object o) {
		List l = (List) o;
		String result ="<";
		for (Iterator i = l.iterator(); i.hasNext(); ) {
			result = result + "["+ i.next() +"]";
		}
		result = result+">";
		return result;
	}

	
}
