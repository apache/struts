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
package org.apache.struts2.showcase.application;

import org.apache.struts2.showcase.exception.CreateException;
import org.apache.struts2.showcase.exception.DuplicateKeyException;
import org.apache.struts2.showcase.exception.StorageException;
import org.apache.struts2.showcase.exception.UpdateException;
import org.apache.struts2.showcase.model.IdEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * MemoryStorage.
 * Very simple in-memory persistence emulation.
 */

@Repository
@Scope("singleton")
public class MemoryStorage implements Storage {

	private static final long serialVersionUID = 8611213748834904125L;


	private Map memory = new HashMap();

	private Map getEntityMap(Class entityClass) {
		if (entityClass != null) {
			Map tryMap = (Map) memory.get(entityClass);
			if (tryMap == null) {
				synchronized (memory) {
					tryMap = new HashMap();
					memory.put(entityClass, tryMap);
				}
			}
			return tryMap;
		} else {
			return null;
		}
	}

	private IdEntity intStore(Class entityClass, IdEntity object) {
		getEntityMap(entityClass).put(object.getId(), object);
		return object;
	}

	public IdEntity get(Class entityClass, Serializable id) {
		if (entityClass != null && id != null) {
			return (IdEntity) getEntityMap(entityClass).get(id);
		} else {
			return null;
		}
	}

	public Serializable create(IdEntity object) throws CreateException {
		if (object == null) {
			throw new CreateException("Either given class or object was null");
		}
		if (object.getId() == null) {
			throw new CreateException("Cannot store object with null id");
		}
		if (get(object.getClass(), object.getId()) != null) {
			throw new DuplicateKeyException("Object with this id already exists.");
		}
		return intStore(object.getClass(), object).getId();
	}

	public IdEntity update(IdEntity object) throws UpdateException {
		if (object == null) {
			throw new UpdateException("Cannot update null object.");
		}
		if (get(object.getClass(), object.getId()) == null) {
			throw new UpdateException("Object to update not found.");
		}
		return intStore(object.getClass(), object);
	}

	public Serializable merge(IdEntity object) throws StorageException {
		if (object == null) {
			throw new StorageException("Cannot merge null object");
		}
		if (object.getId() == null || get(object.getClass(), object.getId()) == null) {
			return create(object);
		} else {
			return update(object).getId();
		}
	}

	public int delete(Class entityClass, Serializable id) throws CreateException {
		try {
			if (get(entityClass, id) != null) {
				getEntityMap(entityClass).remove(id);
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			throw new CreateException(e);
		}
	}

	public int delete(IdEntity object) throws CreateException {
		if (object == null) {
			throw new CreateException("Cannot delete null object");
		}
		return delete(object.getClass(), object.getId());
	}

	public Collection findAll(Class entityClass) {
		if (entityClass != null) {
			return getEntityMap(entityClass).values();
		} else {
			return new ArrayList();
		}
	}

	public void reset() {
		this.memory = new HashMap();
	}

}
