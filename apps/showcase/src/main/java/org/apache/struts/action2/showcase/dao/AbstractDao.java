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
package org.apache.struts.action2.showcase.dao;

import org.apache.struts.action2.showcase.model.IdEntity;
import org.apache.struts.action2.showcase.exception.CreateException;
import org.apache.struts.action2.showcase.exception.UpdateException;
import org.apache.struts.action2.showcase.exception.StorageException;
import org.apache.struts.action2.showcase.application.Storage;

import java.io.Serializable;
import java.util.Collection;

/**
 * AbstractDao.
 *
 */

public abstract class AbstractDao implements Serializable, Dao {

    private Storage storage;

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public IdEntity get(Serializable id) {
        return getStorage().get(getFeaturedClass(), id);
    }

    public Serializable create(IdEntity object) throws CreateException {
        return getStorage().create(object);
    }

    public IdEntity update(IdEntity object) throws UpdateException {
        return getStorage().update(object);
    }

    public Serializable merge(IdEntity object) throws StorageException {
        return getStorage().merge(object);
    }

    public int delete(Serializable id) throws CreateException {
        return getStorage().delete(getFeaturedClass(), id);
    }

    public int delete(IdEntity object) throws CreateException {
        return getStorage().delete(object);
    }

    public Collection findAll() {
        return getStorage().findAll(getFeaturedClass());
    }

}
