/* ====================================================================
 * The OpenSymphony Software License, Version 1.1
 *
 * (this license is derived and fully compatible with the Apache Software
 * License - see http://www.apache.org/LICENSE.txt)
 *
 * Copyright (c) 2001-2005 The OpenSymphony Group. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        OpenSymphony Group (http://www.opensymphony.com/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "OpenSymphony" and "The OpenSymphony Group"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact license@opensymphony.com .
 *
 * 5. Products derived from this software may not be called "OpenSymphony"
 *    or "WebWork", nor may "OpenSymphony" or "WebWork" appear in their
 *    name, without prior written permission of the OpenSymphony Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package com.opensymphony.webwork.showcase.action;

import org.apache.log4j.Logger;
import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.webwork.showcase.dao.Dao;
import com.opensymphony.webwork.showcase.model.IdEntity;

import java.util.Collection;
import java.io.Serializable;

/**
 * AbstractCRUDAction.
 *
 * @author <a href="mailto:gielen@it-neering.net">Rene Gielen</a>
 */

public abstract class AbstractCRUDAction extends ActionSupport {

    private static final Logger log = Logger.getLogger(AbstractCRUDAction.class);

    private Collection availableItems;
    private String[] toDelete;

    protected abstract Dao getDao();


    public Collection getAvailableItems() {
        return availableItems;
    }

    public String[] getToDelete() {
        return toDelete;
    }

    public void setToDelete(String[] toDelete) {
        this.toDelete = toDelete;
    }

    public String list() throws Exception {
        this.availableItems = getDao().findAll();
        if (log.isDebugEnabled()) {
            log.debug("AbstractCRUDAction - [list]: " + (availableItems !=null?""+availableItems.size():"no") + " items found");
        }
        return execute();
    }

    public String delete() throws Exception {
        if (toDelete != null) {
            int count=0;
            for (int i = 0, j=toDelete.length; i < j; i++) {
                count = count + getDao().delete(toDelete[i]);
            }
            if (log.isDebugEnabled()) {
                log.debug("AbstractCRUDAction - [delete]: " + count + " items deleted.");
            }
        }
        return SUCCESS;
    }

    /**
     * Utility method for fetching already persistent object from storage for usage in params-prepare-params cycle.
     *
     * @param tryId     The id to try to get persistent object for
     * @param tryObject The object, induced by first params invocation, possibly containing id to try to get persistent
     *                  object for
     * @return The persistent object, if found. <tt>null</tt> otherwise.
     */
    protected IdEntity fetch(Serializable tryId, IdEntity tryObject) {
        IdEntity result = null;
        if (tryId != null) {
            result = getDao().get(tryId);
        } else if (tryObject != null) {
            result = getDao().get(tryObject.getId());
        }
        return result;
    }
}
