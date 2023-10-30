/*
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
package org.apache.struts2.jasper.el;

import java.util.Iterator;

import jakarta.el.ArrayELResolver;
import jakarta.el.BeanELResolver;
import jakarta.el.CompositeELResolver;
import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ELResolver;
import jakarta.el.ListELResolver;
import jakarta.el.MapELResolver;
import jakarta.el.PropertyNotFoundException;
import jakarta.el.PropertyNotWritableException;
import jakarta.el.ResourceBundleELResolver;
import jakarta.servlet.jsp.el.VariableResolver;

public final class ELResolverImpl extends ELResolver {
	
	public final static ELResolver DefaultResolver = new CompositeELResolver();

	static {
		((CompositeELResolver) DefaultResolver).add(new MapELResolver());
		((CompositeELResolver) DefaultResolver).add(new ResourceBundleELResolver());
		((CompositeELResolver) DefaultResolver).add(new ListELResolver());
		((CompositeELResolver) DefaultResolver).add(new ArrayELResolver());
		((CompositeELResolver) DefaultResolver).add(new BeanELResolver());
	}

	private final VariableResolver variableResolver;

	public ELResolverImpl(VariableResolver variableResolver) {
		this.variableResolver = variableResolver;
	}

	public Object getValue(ELContext context, Object base, Object property)
			throws NullPointerException, PropertyNotFoundException, ELException {
		if (context == null) {
			throw new NullPointerException();
		}

		if (base == null) {
			context.setPropertyResolved(true);
			if (property != null) {
				try {
					return this.variableResolver.resolveVariable(property
							.toString());
				} catch (jakarta.servlet.jsp.el.ELException e) {
					throw new ELException(e.getMessage(), e.getCause());
				}
			}
		}

		if (!context.isPropertyResolved()) {
			return DefaultResolver.getValue(context, base, property);
		}
		return null;
	}

	public Class<?> getType(ELContext context, Object base, Object property)
			throws NullPointerException, PropertyNotFoundException, ELException {
		if (context == null) {
			throw new NullPointerException();
		}

		if (base == null) {
			context.setPropertyResolved(true);
			if (property != null) {
				try {
					Object obj = this.variableResolver.resolveVariable(property
							.toString());
					return (obj != null) ? obj.getClass() : null;
				} catch (jakarta.servlet.jsp.el.ELException e) {
					throw new ELException(e.getMessage(), e.getCause());
				}
			}
		}

		if (!context.isPropertyResolved()) {
			return DefaultResolver.getType(context, base, property);
		}
		return null;
	}

	public void setValue(ELContext context, Object base, Object property,
			Object value) throws NullPointerException,
			PropertyNotFoundException, PropertyNotWritableException,
			ELException {
		if (context == null) {
			throw new NullPointerException();
		}

		if (base == null) {
			context.setPropertyResolved(true);
			throw new PropertyNotWritableException(
					"Legacy VariableResolver wrapped, not writable");
		}

		if (!context.isPropertyResolved()) {
			DefaultResolver.setValue(context, base, property, value);
		}
	}

	public boolean isReadOnly(ELContext context, Object base, Object property)
			throws NullPointerException, PropertyNotFoundException, ELException {
		if (context == null) {
			throw new NullPointerException();
		}

		if (base == null) {
			context.setPropertyResolved(true);
			return true;
		}

		return DefaultResolver.isReadOnly(context, base, property);
	}

	public Iterator<java.beans.FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
		return DefaultResolver.getFeatureDescriptors(context, base);
	}

	public Class<?> getCommonPropertyType(ELContext context, Object base) {
		if (base == null) {
			return String.class;
		}
		return DefaultResolver.getCommonPropertyType(context, base);
	}

}
