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
package org.apache.struts2.sitegraph.renderers;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.sitegraph.StrutsConfigRetriever;
import org.apache.struts2.sitegraph.entities.Target;
import org.apache.struts2.sitegraph.entities.View;
import org.apache.struts2.sitegraph.model.ActionNode;
import org.apache.struts2.sitegraph.model.Graph;
import org.apache.struts2.sitegraph.model.IndentWriter;
import org.apache.struts2.sitegraph.model.Link;
import org.apache.struts2.sitegraph.model.SiteGraphNode;
import org.apache.struts2.sitegraph.model.SubGraph;
import org.apache.struts2.sitegraph.model.ViewNode;

import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;

/**
 * Renders flow diagram to the console at info level
 */
public class DOTRenderer {

    private Writer writer;
    private List links = new ArrayList();

    public DOTRenderer(Writer writer) {
        this.writer = writer;
    }

    public void render(String ns) {
        Graph graph = new Graph();

        TreeMap viewMap = new TreeMap(new Comparator() {
            public int compare(Object o1, Object o2) {
                ViewNode v1 = (ViewNode) o1;
                ViewNode v2 = (ViewNode) o2;

                return v1.getFullName().compareTo(v2.getFullName());
            }
        });

        Set namespaces = StrutsConfigRetriever.getNamespaces();
        for (Iterator iter = namespaces.iterator(); iter.hasNext();) {
            String namespace = (String) iter.next();

            if (!namespace.startsWith(ns)) {
                continue;
            }

            SubGraph subGraph = graph.create(namespace);

            Set actionNames = StrutsConfigRetriever.getActionNames(namespace);
            for (Iterator iterator = actionNames.iterator(); iterator.hasNext();) {
                String actionName = (String) iterator.next();
                ActionConfig actionConfig = StrutsConfigRetriever.getActionConfig(namespace,
                        actionName);

                ActionNode action = new ActionNode(actionName);
                subGraph.addNode(action);

                Set resultNames = actionConfig.getResults().keySet();
                for (Iterator iterator2 = resultNames.iterator(); iterator2.hasNext();) {
                    String resultName = (String) iterator2.next();
                    ResultConfig resultConfig = ((ResultConfig) actionConfig.getResults().get(resultName));
                    String resultClassName = resultConfig.getClassName();

                    if (resultClassName.equals(ActionChainResult.class.getName())) {

                    } else if (resultClassName.indexOf("Dispatcher") != -1
                            || resultClassName.indexOf("Velocity") != -1
                            || resultClassName.indexOf("Freemarker") != -1) {
                        if (resultConfig.getParams().get("location") == null) {
                            continue;
                        }

                        String location = getViewLocation((String) resultConfig.getParams().get("location"), namespace);
                        //  FIXME: work with new configuration style                        
                        if (location.endsWith("action")) {
                            addTempLink(action, location, Link.TYPE_RESULT, resultConfig.getName());
                        } else {
                            ViewNode view = new ViewNode(stripLocation(location));
                            subGraph.addNode(view);

                            addTempLink(action, location, Link.TYPE_RESULT, resultConfig.getName());

                            View viewFile = getView(namespace, actionName, resultName, location);
                            if (viewFile != null) {
                                viewMap.put(view, viewFile);
                            }
                        }
                    } else if (resultClassName.indexOf("Jasper") != -1) {

                    } else if (resultClassName.indexOf("XSLT") != -1) {

                    } else if (resultClassName.indexOf("Redirect") != -1) {
                        // check if the redirect is to an action -- if so, link it
                        String locationConfig = (String) resultConfig.getParams().get("location");
                        if (locationConfig == null) {
                            locationConfig = (String) resultConfig.getParams().get("actionName");
                        }
                        String location = getViewLocation(locationConfig, namespace);
                        //  FIXME: work with new configuration style
                        if (location.endsWith("action")) {
                            addTempLink(action, location, Link.TYPE_REDIRECT, resultConfig.getName());
                        } else {
                            ViewNode view = new ViewNode(stripLocation(location));
                            subGraph.addNode(view);

                            addTempLink(action, location, Link.TYPE_REDIRECT, resultConfig.getName());

                            View viewFile = getView(namespace, actionName, resultName, location);
                            if (viewFile != null) {
                                viewMap.put(view, viewFile);
                            }
                        }
                    }
                }
            }
        }

        // now look for links in the view
        for (Iterator iterator = viewMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            ViewNode view = (ViewNode) entry.getKey();
            View viewFile = (View) entry.getValue();
            Set targets = viewFile.getTargets();
            for (Iterator iterator1 = targets.iterator(); iterator1.hasNext();) {
                Target target = (Target) iterator1.next();
                String viewTarget = target.getTarget();
                addTempLink(view, viewTarget, target.getType(), "");
            }
        }

        // finally, let's match up these links as real Link objects
        for (Iterator iterator = links.iterator(); iterator.hasNext();) {
            TempLink temp = (TempLink) iterator.next();
            String location = temp.location;
            
            // FIXME: work with new configuration style
            if (location.endsWith("action")) {
                location = location.substring(0, location.indexOf("action") - 1);

                if (location.indexOf('!') != -1) {
                    temp.label = temp.label + "\\n(" + location.substring(location.indexOf('!')) + ")";
                    location = location.substring(0, location.indexOf('!'));
                }
            }
            SiteGraphNode to = graph.findNode(location, temp.node);
            if (to != null) {
                graph.addLink(new Link(temp.node, to, temp.typeResult, temp.label));
            }
        }

        try {
            //writer.write(graph.to_s(true));
            graph.render(new IndentWriter(writer));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addTempLink(SiteGraphNode node, String location, int type, String label) {
        links.add(new TempLink(node, location, type, label));
    }

    private String stripLocation(String location) {
        return location.substring(location.lastIndexOf('/') + 1);
    }

    private View getView(String namespace, String actionName, String resultName, String location) {
        int type = View.TYPE_JSP;
        if (location.endsWith(".fm") || location.endsWith(".ftl")) {
            type = View.TYPE_FTL;
        } else if (location.endsWith(".vm")) {
            type = View.TYPE_VM;
        }
        return StrutsConfigRetriever.getView(namespace, actionName, resultName, type);
    }

    private String getViewLocation(String location, String namespace) {
        String view = null;
        if (!location.startsWith("/")) {
            view = namespace + "/" + location;
        } else {
            view = location;
        }

        if (view.indexOf('?') != -1) {
            view = view.substring(0, view.indexOf('?'));
        }

        return view;
    }

    class TempLink {
        SiteGraphNode node;
        String location;
        int typeResult;
        String label;

        public TempLink(SiteGraphNode node, String location, int typeResult, String label) {
            this.node = node;
            this.location = location;
            this.typeResult = typeResult;
            this.label = label;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TempLink)) return false;

            final TempLink tempLink = (TempLink) o;

            if (typeResult != tempLink.typeResult) return false;
            if (label != null ? !label.equals(tempLink.label) : tempLink.label != null) return false;
            if (location != null ? !location.equals(tempLink.location) : tempLink.location != null) return false;
            if (node != null ? !node.equals(tempLink.node) : tempLink.node != null) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (node != null ? node.hashCode() : 0);
            result = 29 * result + (location != null ? location.hashCode() : 0);
            result = 29 * result + typeResult;
            result = 29 * result + (label != null ? label.hashCode() : 0);
            return result;
        }
    }
}
