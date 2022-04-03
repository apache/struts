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
package org.apache.struts2.showcase.async;

import com.opensymphony.xwork2.ActionSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Example to illustrate the <code>async</code> plugin.
 */
public class ChatRoomAction extends ActionSupport {
    private String message;
    private Integer lastIndex;
    private List<String> newMessages;

    private static final List<String> messages = new ArrayList<>();

    public void setMessage(String message) {
        this.message = message;
    }

    public void setLastIndex(Integer lastIndex) {
        this.lastIndex = lastIndex;
    }

    public List<String> getNewMessages() {
        return newMessages;
    }

    public Callable<String> receiveNewMessages() throws Exception {
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                while (lastIndex >= messages.size()) {
                    Thread.sleep(3000);
                }
                newMessages = messages.subList(lastIndex, messages.size());
                return SUCCESS;
            }
        };
    }

    public String sendMessage() {
        synchronized (messages) {
            messages.add(message);
        }
        return SUCCESS;
    }
}
