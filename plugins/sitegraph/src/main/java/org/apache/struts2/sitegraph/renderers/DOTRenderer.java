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

import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import org.apache.struts2.sitegraph.StrutsConfigRetriever;
import org.apache.struts2.sitegraph.entities.Target;
import org.apache.struts2.sitegraph.entities.View;
import org.apache.struts2.sitegraph.model.*;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Renders flow diagram to the console at info level
 */
public class DOTRenderer {

    private Writer writer;
    private List<TempLink> links = new ArrayList<TempLink>();

    public DOTRenderer(Writer writer) {
        this.writer = writer;
    }

    public void render(String ns) {
        Graph graph = new Graph();

        TreeMap<ViewNode, View> viewMap = new TreeMap<ViewNode, View>(new Comparator<ViewNode>() {
            public int compare(ViewNode v1, ViewNode v2) {

                return v1.getFullName().compareTo(v2.getFullName());
            }
        });

        Set<String> namespaces = StrutsConfigRetriever.getNamespaces();
        for (String namespace : namespaces) {
            if (!namespace.startsWith(ns)) {
                continue;
            }

            SubGraph subGraph = graph.create(namespace);

            Set<String> actionNames = StrutsConfigRetriever.getActionNames(namespace);
            for (String actionName : actionNames) {
                ActionConfig actionConfig = StrutsConfigRetriever.getActionConfig(namespace,
                        actionName);

                ActionNode action = new ActionNode(actionName);
                subGraph.addNode(action);

                Set<String> resultNames = actionConfig.getResults().keySet();
                for (String resultName : resultNames) {
                    ResultConfig resultConfig = actionConfig.getResults().get(resultName);
                    String resultClassName = resultConfig.getClassName();

                    if (resultClassName.equals(ActionChainResult.class.getName())) {

                    } else if (resultClassName.contains("Dispatcher")
                            || resultClassName.contains("Velocity")
                            || resultClassName.contains("Freemarker")) {
                        if (resultConfig.getParams().get("location") == null) {
                            continue;
                        }

                        String location = getViewLocation(resultConfig.getParams().get("location"), namespace);
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
                    } else if (resultClassName.contains("Jasper")) {

                    } else if (resultClassName.contains("XSLT")) {

                    } else if (resultClassName.contains("Redirect")) {
                        // check if the redirect is to an action -- if so, link it
                        String locationConfig = resultConfig.getParams().get("location");
                        if (locationConfig == null) {
                            locationConfig = resultConfig.getParams().get("actionName");
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
        for (Map.Entry<ViewNode, View> viewNodeViewEntry : viewMap.entrySet()) {
            ViewNode view = viewNodeViewEntry.getKey();
            View viewFile = viewNodeViewEntry.getValue();
            Set<Target> targets = viewFile.getTargets();
            for (Target target : targets) {
                String viewTarget = target.getTarget();
                addTempLink(view, viewTarget, target.getType(), "");
            }
        }

        // finally, let's match up these links as real Link objects
        for (TempLink temp : links) {
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
        String view;
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
