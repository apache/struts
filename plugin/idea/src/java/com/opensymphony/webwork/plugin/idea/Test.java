/*
 * Copyright 2000-2005 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.webwork.plugin.idea;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.RuntimeConfiguration;
import com.opensymphony.xwork.config.entities.ActionConfig;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.Iterator;

public class Test implements ProjectComponent {
    private Project myProject;

    private ToolWindow myToolWindow;
    private JPanel myContentPanel;

    public static final String TOOL_WINDOW_ID = "SimpleToolWindow";

    public Test(final Project project) {
        System.out.println("HEY");
        myProject = project;
    }

    public void projectOpened() {
        initToolWindow();
    }

    public void projectClosed() {
        unregisterToolWindow();
    }

    public void initComponent() {
        // empty
    }

    public void disposeComponent() {
        // empty
    }

    public String getComponentName() {
        return "SimpleToolWindow.SimpleToolWindowPlugin";
    }

    private void initToolWindow() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(myProject);

        myContentPanel = new JPanel(new GridBagLayout());

        myContentPanel.setBackground(UIManager.getColor("Tree.textBackground"));
        JButton button = new JButton("Hi");
        myContentPanel.add(button, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        JTabbedPane tabs = new JTabbedPane();
        JPanel panel = new JPanel(new GridBagLayout());
        tabs.add("Namespaces", panel);

        final DefaultMutableTreeNode top = new DefaultMutableTreeNode("WebWork");
        final JTree tree = new JTree(top);
        tree.setShowsRootHandles(true);

        panel.add(new JScrollPane(tree), new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        myContentPanel.add(tabs, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        myToolWindow = toolWindowManager.registerToolWindow(TOOL_WINDOW_ID, myContentPanel, ToolWindowAnchor.LEFT);
        myToolWindow.setTitle("SimpleWindow");


        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                top.removeAllChildren();
                ConfigurationManager.destroyConfiguration();
                ConfigurationManager.clearConfigurationProviders();
                ConfigurationManager.addConfigurationProvider(new PluginXmlConfigurationProvider(myProject));
                Configuration c = ConfigurationManager.getConfiguration();
                RuntimeConfiguration rc = c.getRuntimeConfiguration();

                Map ac = rc.getActionConfigs();
                for (Iterator iterator = ac.entrySet().iterator(); iterator.hasNext();) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    String namespace = (String) entry.getKey();
                    DefaultMutableTreeNode nsNode = new DefaultMutableTreeNode(namespace);
                    top.add(nsNode);

                    Map actions = (Map) entry.getValue();
                    for (Iterator iterator1 = actions.entrySet().iterator(); iterator1.hasNext();) {
                        Map.Entry entry1 = (Map.Entry) iterator1.next();
                        String action = (String) entry1.getKey();
                        ActionConfig config = (ActionConfig) entry1.getValue();

                        nsNode.add(new DefaultMutableTreeNode(action));
                    }
                }
                DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
                model.nodeStructureChanged(top);
            }
        });
    }

    private void unregisterToolWindow() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(myProject);
        toolWindowManager.unregisterToolWindow(TOOL_WINDOW_ID);
    }
}
