The Apache Struts web framework
-------------------------------

[![Build Status](https://travis-ci.org/apache/struts.svg?branch=master)](https://travis-ci.org/apache/struts)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.struts/struts2-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.apache.struts/struts2-core/)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

The Apache Struts web framework is a free open-source solution for creating Java web applications.

Web applications differ from conventional websites in that web applications can create a dynamic response. Many websites deliver only static pages. A web application can interact with databases and business logic engines to customize a response.

Web applications based on JavaServer Pages sometimes commingle database code, page design code, and control flow code. In practice, we find that unless these concerns are separated, larger applications become difficult to maintain.

One way to separate concerns in a software application is to use a Model-View-Controller (MVC) architecture. The Model represents the business or database code, the View represents the page design code, and the Controller represents the navigational code. The Struts framework is designed to help developers create web applications that utilize a MVC architecture.

The framework provides three key components:

A “request” handler provided by the application developer that is mapped to a standard URI. A “response” handler that transfers control to another resource which completes the response. A tag library that helps developers create interactive form-based applications with server pages. The framework’s architecture and tags are buzzword compliant. Struts works well with conventional REST applications and with technologies like SOAP and AJAX.

The Apache Struts Project
-------------------------

The Apache Struts Project is the open source community that creates and maintains the Apache Struts framework. The project consists of a diverse group of volunteers who share common values regarding collaborative, community-based open source development. The Apache Struts Project is proud to share these values with our parent organization: The Apache Software Foundation.

The project is called “Struts” because the framework is meant to furnish the “invisible underpinnings” that support professional application development. Struts provides the glue that joins the various elements of the standard Java platform into a coherent whole. Our goal is to leverage existing standards by producing the missing pieces we need to create enterprise-grade applications that are easy to maintain over time.

The Apache Struts Project offered two major versions of the Struts framework. Currently we are only maintaining the Struts 2 version. It is recommended to upgrade all Struts 1.x application to Struts 2. Please do not start new application development using Struts 1.x, as we are no longer issuing security patches.

Struts 2 was originally known as WebWork 2. After working independently for several years, the WebWork and Struts communities joined forces to create Struts 2. The 2.x framework is the best choice for teams who value elegant solutions to difficult problems.

Why should you use Apache Struts?
---------------------------------

Apache Struts is a modern, maintained and full-featured web framework. It has been there for years and give the huge user base it is unlikely it will go away anytime soon in the future. Not only that we have dedicated users and developers on the project. Apache Struts is licensed to the Apache License 2.0 and this will not change. We maintain a clean IP and you are “safe” to use the project. Sometimes you are not “safe” to use a project when a company controls the SCM. Access to Source Code doesn’t mean it is free. With Apache Struts, you are not only free to “do what you want with it”, you can even contribute (which is not always the case). And best of it: you can become a part of the core team too.

It is usually very easy to integrate other technologies with Apache Struts. If you are using an ORM like Apache Cayenne, Hibernate or JDBC you will not have any restrictions. Apache Struts is not even tied too much to a frontend technology. In old days it was JSP, then came Velocity and Freemarker. Nowadays you might build your web application with just static HTML and AngularJS. Or you want to use Sitemesh or Tiles. This all is no problem due to Struts elegant and easy to use extension mechanisms.

Unlike other, component oriented frameworks, we do not aim to hide the stateless nature of the web. We think it is perfectly acceptable to build upon a Request/Response cycle. We also think the MVC pattern is not so bad, just because it is old. In fact, we believe the Apache Struts architecture is clean and easy to understand.

Of course, if you wish to build components on the server side which render on the front end side, you are most likely wrong with Struts. This is a different approach which promises to reduce the amount of HTML/JavaScript knowledge needed and to create reusable components for the view layer. Projects like Wicket and Tapestry serve this purpose very well. As with every framework you need to decide if it makes sense for you to build components or if you are better with the Struts-approach.
