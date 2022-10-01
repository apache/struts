/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/**
 * The Tiles taglib and framework allows building web pages by assembling reusable
 pieces of pages, called Tiles. A Tiles is usually a simple JSP page.

 <div class="section">
 <h2>Introduction</h2>

 <p>The Tiles framework allows building pages by assembling reusable Tiles.
 As an example, the page in the next figure can be build by assembling a
 header, a footer, a menu and a body.</p>

 <p><img src="doc-files/image001.gif" height="169" width="145" alt="doc-files/image001"></p>

 <p>Each Tiles (header, menu, body, ...) is a JSP page and can itself be build
 by assembling other Tiles.</p>

 <p>Using Tiles can be compared as using Java methods: You need to define the Tiles (the method body), and then you
 can &quot;call&quot; this body anywhere you want, passing it some parameters. In Tiles, parameters are called
 &quot;attributes&quot; in order to avoid confusion with the request parameters.</p>

 <p>The Tiles body can be a simple JSP page, a Struts action or any URI pointing
 to a resource inside the current web site.</p>

 <p>Inserting the body, or calling it, is done with the tag &lt;tiles:insert
 ...&gt; anywhere in a JSP page. Insertion can also be done by specifying
 a <em>definition name </em>as the path of a Struts forward or as input,
 forward or include attributes of a Struts action.</p>

 <p>Tiles bodies are used to create layouts, reusable parts, ... Tiles insertions
 are used to insert Tiles. The same Tiles can be reused several times in
 the same site, or even in the same page.</p>

 <p>Insertion of a Tiles body can be associated to a logical name in what Tiles calls a &quot;definition&quot;. A
 definition contains a logical name, a page used as body and some attribute values. The definition declaration
 doesn't insert the associated Tiles body. It just associates it with the name. A definition name can be used
 anywhere insertion of a Tiles body can occur. The associated Tiles body is then inserted with associated
 attributes.</p>

 <p>The definition declarations can be done in JSP pages or in one or more
 centralized files. A definition can extend another one, overload some attributes,
 add new attributes ... This allows the declaration of a &quot;master&quot; definition
 declaring the common layout, header, menu and footer. All other definitions
 extend this master layout thereby making it possible to change the entire
 site look &amp; feel simply by changing the master definition. </p>
 </div>
 <div class="section">
 <h2>Simple Examples</h2>

 <div class="subsection1">
 <h3>Insert a JSP page</h3>
 <pre>&lt;tiles:insert <strong>page</strong>=&quot;/layouts/commonLayout.jsp&quot; flush=&quot;true&quot; /&gt;
 </pre>
 <p>This example inserts the specified page in place of the tag. The page attribute is any valid URL pointing to
 a resource inside the current site.</p>
 </div>
 <div class="subsection1">
 <a name="doc.InsertPageWithAttributes"></a>

 <h3>Insert a Tiles passing some attributes</h3>
 <pre>
 &lt;tiles:insert page=&quot;/layouts/classicLayout.jsp&quot; flush=&amp;quot;true&quot;&gt;
 &lt;tiles:put name=&quot;title&quot;  value=&quot;Page Title&quot; /&gt;
 &lt;tiles:put name=&quot;header&quot; value=&quot;/common/header.jsp&quot; /&gt;
 &lt;tiles:put name=&quot;footer&quot; value=&quot;/common/footer.jsp&quot; /&gt;
 &lt;tiles:put name=&quot;menu&quot;   value=&quot;/common/menu.jsp&quot; /&gt;
 &lt;tiles:put name=&quot;body&quot;   value=&quot;/tiles/mainBody.jsp&quot; /&gt;
 &lt;/tiles:insert&gt;
 </pre>
 <p>This example inserts the specified page, passing it the attributes. Attributes
 are stored in a Tiles context which is passed to the inserted pag and
 can then be accesssed by their names.</p>
 </div>
 <div class="subsection1">
 <h3>Retrieve an attribute value as String</h3>
 <pre>
 &lt;tiles:getAsString name=&quot;title&quot; /&gt;
 </pre>
 <p>This example retrieves the value of the attribute &quot;title&quot; and prints it as a String in the current
 output stream. The method toString() is applied on the attribute value, allowing to pass any kind of object
 as value.</p>
 </div>
 <div class="subsection1">
 <h3>Insert Tiles referenced by an attribute</h3>
 <pre>
 &lt;tiles:insert attribute='menu' /&gt;
 </pre>
 <p>This inserts the Tiles referenced by the attribute &quot;menu&quot; value. The
 specified attribute value is first retrieved from current Tiles's context,
 and then the value is used as a page target to insert.</p>
 </div>
 <div class="subsection1">
 <h3>Classic Layout </h3>

 <p>This example is a layout assembling a page in the classic header-footer-menu-body
 fashion.</p>
 <pre>
 &lt;%@ taglib uri=&quot;http://tiles.apache.org/tags-tiles&quot; prefix=&quot;tiles&quot; %&gt;
 &lt;HTML&gt;
 &lt;HEAD&gt;
 &lt;link rel=&quot;stylesheet&quot; href=&quot;&lt;%=request.getContextPath()%&gt;/layouts/stylesheet.css&quot;
 type=&quot;text/css&quot;/&gt;
 &lt;title&gt;&lt;tiles:getAsString name=&quot;title&quot;/&gt;&lt;/title&gt;
 &lt;/HEAD&gt;
 &lt;body&gt;
 &lt;table border=&quot;0&quot; width=&quot;100%&quot; cellspacing=&quot;5&quot;&gt;
 &lt;tr&gt;
 &lt;td colspan=&quot;2&quot;&gt;&lt;tiles:insert attribute=&quot;header&quot; /&gt;&lt;/td&gt;
 &lt;/tr&gt;
 &lt;tr&gt;
 &lt;td width=&quot;140&quot; valign=&quot;top&quot;&gt;
 &lt;tiles:insert attribute='menu' /&gt;
 &lt;/td&gt;
 &lt;td valign=&quot;top&quot;  align=&quot;left&quot;&gt;
 &lt;tiles:insert attribute='body' /&gt;
 &lt;/td&gt;
 &lt;/tr&gt;
 &lt;tr&gt;
 &lt;td colspan=&quot;2&quot;&gt;
 &lt;tiles:insert attribute=&quot;footer&quot; /&gt;
 &lt;/td&gt;
 &lt;/tr&gt;
 &lt;/table&gt;
 &lt;/body&gt;
 &lt;/html&gt;
 </pre>
 <p>The layout is declared in a JSP page (ex: /layouts/classicLayout.jsp).
 It can be used in conjunction with the tag described in &quot;<a href="#doc.InsertPageWithAttributes">Insert
 a page passing some attributes</a>&quot;. </p>
 </div>
 </div>
 <div class="section">
 <h2>Definitions</h2>

 <p>A definition associates a logical name with the URL of a Tiles to be inserted
 and some attribute values. A definition doesn't insert the Tiles. This is
 done later using the definition name. A definition name can be inserted
 as often as you want in your site, making it easy to reuse a Tiles. </p>

 <p>A definition can extend another definition and overload some attributes
 or add new ones. This makes easy factorization of definitions differing
 by some attributes. For example, you can define a master definition declaring
 the main header, menu, footer, and a default title. Then let each of your
 page definitions extend this master definition and overload the title and
 the body.</p>

 <p>Definitions can be declared in a JSP page, or in one or more centralized
 files. To enable the definitions from centralized files, you need to initialize
 the &quot;definitions factory&amp;&amp;quot; which will parse the definitions from the files
 and provide them to the Tiles framework.</p>

 <div class="subsection1">
 <h3>Enabling Definition Factory</h3>

 <p>To enable Tiles definitions described in one or more files, you need to write these files and to initialize the
 definition factory. </p>

 <p>Initialization is different depending on the Struts version you use,
 or if you do not use Struts at all.</p>

 <div class="subsection2">
 <h4>Struts1.1</h4>

 <p>Use the Tiles plug-in to enable Tiles definitions. This plug-in creates
 the definition factory and passese it a configuration object populated
 with parameters. Parameters can be specified in the web.xml file or
 as plug-in parameters. The plug-in first reads parameters from web.xml,
 and then overloads them with the ones found in the plug-in. All parameters
 are optional and can be omitted. The plug-in should be declared in each
 struts-config file:</p>
 <pre>
 &lt;plug-in className=&amp;&amp;quot;org.apache.struts.tiles.TilesPlugin&amp;&amp;quot; &gt;
 &lt;set-property property=&amp;&amp;quot;definitions-config&amp;&amp;quot;
 value=&amp;&amp;quot;/WEB-INF/tiles-defs.xml,
 /WEB-INF/tiles-tests-defs.xml,/WEB-INF/tiles-tutorial-defs.xml,
 /WEB-INF/tiles-examples-defs.xml&amp;&amp;quot; /&gt;
 &lt;set-property property=&amp;&amp;quot;moduleAware&amp;&amp;quot; value=&amp;&amp;quot;true&amp;&amp;quot; /&gt;
 &lt;set-property
 property=&amp;&amp;quot;org.apache.tiles.definition.digester.DigesterDefinitionsReader.PARSER_VALIDATE&amp;&amp;quot;
 value=&amp;&amp;quot;true&amp;&amp;quot; /&gt;
 &lt;/plug-in&gt;
 </pre>
 <ul>
 <li>definitions-config: (optional)
 <ul>
 <li>Specify configuration file names. There can be several comma separated file names (default: ?? )
 </li>
 </ul>
 </li>
 <li>org.apache.tiles.definition.digester.DigesterDefinitionsReader.PARSER_VALIDATE: (optional)
 <ul>
 <li>Specify if XML parser should validate the Tiles configuration
 file
 <ul>
 <li>true : validate. DTD should be specified in file header (default)</li>
 <li>false : no validation</li>

 </ul>
 </li>
 </ul>
 </li>

 <li>moduleAware: (optional)
 <ul>
 <li>Specify if the Tiles definition factory is module aware. If true (default),
 there will be one factory for each Struts module.
 If false, there will be one common factory for all module. In this later case,
 it is still needed to declare one plugin per module. The factory will be
 initialized with parameters found in the first initialized plugin (generally the
 one associated with the default module).
 <ul>
 <li>true : Tiles framework is module aware</li>
 <li>false :Tiles framework has one single factoy shared among modules (default)</li>
 </ul>
 </li>
 </ul>
 </li>

 <li>tilesUtilImplClassname: (optional - for advanced user)
 <ul>
 <li>Specify The classname of the TilesUtil implementation to use. The specified class should
 be a subclass of TilesUtilStrutsImpl. This option disable the moduleAware option.
 <br>Specifying &amp;&amp;&quot;TilesUtilStrutsImpl&amp;&amp;&quot; is equivalent to moduleAware =
 false.
 <br>Specifying &amp;&amp;&quot;TilesUtilStrutsModuleImpl&amp;&amp;&quot; is equivalent to moduleAware
 = true.
 This option is taken into account only once, when it is first encountered. To avoid problems,
 it is advice to specify the same values in all TilesPlugin declaration.
 </li>
 </ul>
 </li>

 </ul>
 <p>The TilesPlugin class creates one definition factory for each struts module.
 </p>

 <p>
 If the flag moduleAware is false, only one shared factory is created for all modules.
 In this later case, the factory is initialized with parameters found in the first plugin.
 The plugins should be declared in all modules, and the moduleAware flag should be
 the same for the entire application.</p>

 <p>
 Paths found in Tiles definitions are relative to the main context.</p>

 <p>You don't need to specify a TilesRequestProcessor, this is automatically
 done by the plug-in. If, however, you want to specify your own RequestProcessor,
 it should extend the TilesRequestProcessor. The plug-in checks this
 constraint.</p>
 </div>
 <div class="subsection2">
 <h4>Struts1.0.x</h4>

 <p>You need to use a special servlet extending the Struts servlet. This is specified in the web.xml file of your
 application:</p>
 <pre>
 &lt;servlet&gt;
 &lt;servlet-name&gt;action&lt;/servlet-name&gt;
 &lt;servlet-class&gt;org.apache.tiles.web.startup.TilesServlet&lt;/servlet-class&gt;
 &lt;!-- Tiles Servlet parameter
 Specify configuration file names. There can be several comma
 separated file names
 --&gt;
 &lt;init-param&gt;
 &lt;param-name&gt;definitions-config&lt;/param-name&gt;
 &lt;param-value&gt;/WEB-INF/tiles-defs.xml&lt;/param-value&gt;
 &lt;/init-param&gt;
 &lt;!-- Tiles Servlet parameter
 Specify if XML parser should validate the Tiles configuration file(s).
 true : validate. DTD should be specified in file header.
 false : no validation
 --&gt;
 &lt;init-param&gt;
 &lt;param-name&gt;org.apache.tiles.definition.digester.DigesterDefinitionsReader.PARSER_VALIDATE&lt;/param-name&gt;
 &lt;param-value&gt;true&lt;/param-value&gt;
 &lt;/init-param&gt;
 ...
 &lt;/servlet&gt;
 </pre>
 </div>
 <div class="subsection2">
 <h4>Without Struts</h4>

 <p>Tiles can be used without Struts. To initialize the definition factory, you can use the provided servlet. Declare
 it in the web.xml file of your application:</p>
 <pre>
 &lt;servlet&gt;
 &lt;servlet-name&gt;action&lt;/servlet-name&gt;
 &lt;servlet-class&gt;org.apache.struts.tiles.TilesServlet&lt;/servlet-class&gt;


 &lt;init-param&gt;
 &lt;param-name&gt;definitions-config&lt;/param-name&gt;
 &lt;param-value&gt;/WEB-INF/tiles-defs.xml&lt;/param-value&gt;
 &lt;/init-param&gt;
 &lt;init-param&gt;
 &lt;param-name&gt;org.apache.tiles.definition.digester.DigesterDefinitionsReader.PARSER_VALIDATE&lt;/param-name&gt;
 &lt;param-value&gt;true&lt;/param-value&gt;
 &lt;/init-param&gt;
 ...
 </pre>
 <p>The parameters are the same as for Struts1.1 or 1.0.</p>
 </div>
 </div>
 <div class="subsection1">
 <h3>Definition File Syntax</h3>

 <p>The definition file syntax can be found in the
 <a href="http://tiles.apache.org/dtds/tiles-config_2_0.dtd">tiles-config_2_0.dtd file</a>.
 </p>

 <p>Following is a simple example:</p>
 <pre>
 &lt;!DOCTYPE tiles-definitions PUBLIC
 &amp;&amp;quot;-//Apache Software Foundation//DTD Tiles Configuration//EN&amp;&amp;quot;
 &amp;&amp;quot;http://tiles.apache.org/dtds/tiles-config_2_0.dtd&amp;&amp;quot;&gt;

 &lt;!-- Definitions for Tiles documentation   --&gt;
 &lt;tiles-definitions&gt;

 &lt;!-- ========================================================== --&gt;
 &lt;!-- Master definition                                          --&gt;
 &lt;!-- ========================================================== --&gt;
 &lt;!-- Main page layout used as a root for other page definitions --&gt;

 &lt;definition name=&amp;&amp;quot;site.mainLayout&amp;&amp;quot;
   template=&amp;&amp;quot;/layouts/classicLayout.jsp&amp;&amp;quot;&gt;
 &lt;put name=&amp;&amp;quot;title&amp;&amp;quot;  value=&amp;&amp;quot;Tiles Blank Site&amp;&amp;quot; /&gt;
 &lt;put name=&amp;&amp;quot;header&amp;&amp;quot; value=&amp;&amp;quot;/tiles/common/header.jsp&amp;&amp;quot; /&gt;
 &lt;put name=&amp;&amp;quot;menu&amp;&amp;quot;   value=&amp;&amp;quot;site.menu.bar&amp;&amp;quot; /&gt;
 &lt;put name=&amp;&amp;quot;footer&amp;&amp;quot; value=&amp;&amp;quot;/tiles/common/footer.jsp&amp;&amp;quot; /&gt;
 &lt;put name=&amp;&amp;quot;body&amp;&amp;quot;   value=&amp;&amp;quot;/tiles/body.jsp&amp;&amp;quot; /&gt;
 &lt;/definition&gt;

 &lt;!-- ========================================================== --&gt;
 &lt;!-- Index page definition                                      --&gt;
 &lt;!-- ========================================================== --&gt;
 &lt;!-- This definition inherits from the main definition.
 It overloads the page title and the body used.
 Use the same mechanism to define new pages sharing common
 properties (here header, menu, footer, layout)
 --&gt;

 &lt;definition name=&amp;&amp;quot;site.index.page&amp;&amp;quot;
   extends=&amp;&amp;quot;site.mainLayout&amp;&amp;quot; &gt;
 &lt;put name=&amp;&amp;quot;title&amp;&amp;quot;  value=&amp;&amp;quot;Tiles Blank Site Index&amp;&amp;quot; /&gt;
 &lt;put name=&amp;&amp;quot;body&amp;&amp;quot;   value=&amp;&amp;quot;/tiles/body.jsp&amp;&amp;quot; /&gt;
 &lt;/definition&gt;

 &lt;/tiles-definition&gt;
 </pre>
 </div>
 <div class="subsection1">
 <h3>Debugging</h3>

 <p>To debug a page made of Tiles, you can use following advices:</p>
 <ul>
 <li>Check each Tiles separatly. Try to access nested Tiles directly to test
 if thes work properly.
 </li>
 <li>Enable Tiles logging. See the commons-logging package help.</li>
 </ul>
 </div>
 </div>

 */
package org.apache.tiles.api;

