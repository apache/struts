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
package org.apache.struts2.components;

import org.apache.struts2.ActionSupport;
import org.apache.struts2.interceptor.parameter.StrutsParameter;

public class ConstraintAction extends ActionSupport {

    private String username;
    private String comment;
    private String bio;
    private String nickname;
    private ConstraintUser user;

    public String getUsername() {
        return username;
    }

    @StrutsParameter
    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    @StrutsParameter
    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getBio() {
        return bio;
    }

    @StrutsParameter
    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getNickname() {
        return nickname;
    }

    @StrutsParameter
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @StrutsParameter(depth = 1)
    public ConstraintUser getUser() {
        return user;
    }

    public void setUser(ConstraintUser user) {
        this.user = user;
    }
}
