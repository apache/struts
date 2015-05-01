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
package ${package}.actions.data;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.convention.annotation.Result;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>List Apache projects.</code>
 */
@Result(type = "json")
public class ProjectsAction extends ActionSupport {

    private static final long serialVersionUID = 9037336532369476225L;
    private static final Logger log = LogManager.getLogger(ProjectsAction.class);

    private List<String> projectNames;

    public String execute() throws Exception {

        projectNames = new ArrayList<String>();
        projectNames.add("Apache Struts");
        projectNames.add("Apache Log4j");
        projectNames.add("Apache Tomcat");
        projectNames.add("Apache Maven");
        projectNames.add("Apache Ant");
        projectNames.add("Apache Log4Net");
        projectNames.add("Apache Log4Cxx");
        projectNames.add("Apache Chainsaw");
        projectNames.add("Apache Incubator");
        projectNames.add("Apache Hadoop");
        projectNames.add("Apache OpenOffice");
        projectNames.add("Apache Mahout");
        projectNames.add("Apache Tapestry");
        projectNames.add("Apache Jena");
        projectNames.add("Apache Solr");
        projectNames.add("Apache Cayenne");
        projectNames.add("Apache OpenEJB");
        projectNames.add("Apache Deltaspike");
        projectNames.add("Apache Cordova");

        log.debug("Return {} Apache projects", projectNames.size());

        return SUCCESS;
    }

    public List<String> getProjectNames() {
        return projectNames;
    }
}
