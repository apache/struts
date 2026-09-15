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
package org.apache.struts2.rest.handler.jackson;

import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Remembers which properties were authorized when read but assigned later: a forward reference by
 * {@code @JsonIdentityInfo} id is written by Jackson through the property's {@code set} once the
 * referenced object appears, wherever in the body that is. An entry is the id awaited and the
 * member name, and the deferred write finds it by the object that id resolved to — the one it
 * receives — so a referrer that is not constructed yet when the reference is read is covered too.
 * The match asks the id's {@code ObjectIdResolver}; a custom resolver that does not hand back the
 * object it was bound leaves the write on the check made where the object appeared.
 */
final class AuthorizedForwardReferences {

    private static final ThreadLocal<List<Entry>> ENTRIES = new ThreadLocal<>();

    private AuthorizedForwardReferences() {
        // utility
    }

    static void expect(ReadableObjectId awaited, String memberName) {
        List<Entry> entries = ENTRIES.get();
        if (entries == null) {
            entries = new ArrayList<>();
            ENTRIES.set(entries);
        }
        entries.add(new Entry(awaited, memberName));
    }

    static boolean consume(Object resolved, String memberName) {
        List<Entry> entries = ENTRIES.get();
        if (entries == null || resolved == null) {
            return false;
        }
        for (Iterator<Entry> it = entries.iterator(); it.hasNext(); ) {
            Entry entry = it.next();
            if (entry.memberName.equals(memberName) && entry.awaited.resolve() == resolved) {
                it.remove();
                if (entries.isEmpty()) {
                    ENTRIES.remove();
                }
                return true;
            }
        }
        return false;
    }

    static boolean isActive() {
        List<Entry> entries = ENTRIES.get();
        return entries != null && !entries.isEmpty();
    }

    static void clear() {
        ENTRIES.remove();
    }

    private record Entry(ReadableObjectId awaited, String memberName) {
    }
}
