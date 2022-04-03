<!---
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
The Apache Struts web framework
-------------------------------

[![Build Status @ Jenkins](https://builds.apache.org/buildStatus/icon?job=Struts%2FStruts+Core%2Fmaster)](https://ci-builds.apache.org/job/Struts/job/Struts%20Core/job/master/)
[![Build Status @ Travis](https://travis-ci.com/apache/struts.svg?branch=master)](https://app.travis-ci.com/apache/struts)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.struts/struts2-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.apache.struts/struts2-core/)
[![Javadocs](https://javadoc.io/badge/org.apache.struts/struts2-core.svg)](https://javadoc.io/doc/org.apache.struts/struts2-core)
[![Coverage Status](https://coveralls.io/repos/github/apache/struts/badge.svg)](https://coveralls.io/github/apache/struts)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

The Apache Struts web framework is a free open-source solution for creating Java web applications.

## Documentation

More information can be found on the [homepage](https://struts.apache.org/). Please read the [Security Guide](https://struts.apache.org/security/),
and the [JavaDocs](https://struts.apache.org/maven/struts2-core/apidocs/index.html) can be browsed.
Questions related to the usage of Apache Struts should be posted to the [user mailing list](https://struts.apache.org/mail.html).

## Description

Web applications differ from conventional websites in that web applications can create a dynamic response. Many websites 
deliver only static pages. A web application can interact with databases and business logic engines to customize a response.

Web applications based on JavaServer Pages sometimes commingle database code, page design code, and control flow code. 
In practice, we find that unless these concerns are separated, larger applications become difficult to maintain.

One way to separate concerns in a software application is to use a Model-View-Controller (MVC) architecture. The Model 
represents the business or database code, the View represents the page design code, and the Controller represents 
the navigational code. The Struts framework is designed to help developers create web applications that utilize 
an MVC architecture.

The framework provides three key components:

- A “request” handler provided by the application developer that is mapped to a standard URI.
- A “response” handler that transfers control to another resource which completes the response.
- A tag library that helps developers create interactive form-based applications with server pages.

The framework’s architecture and tags are buzzword compliant. Struts works well
with conventional REST applications and with technologies like SOAP and AJAX.

## The Apache Struts Project

The Apache Struts Project is the open source community that creates and maintains the Apache Struts framework. 
The project consists of a diverse group of volunteers who share common values regarding collaborative, community-based
open source development. The Apache Struts Project is proud to share these values with our parent organization: 
The Apache Software Foundation.

The project is called “Struts” because the framework is meant to furnish the “invisible underpinnings” that support 
professional application development. Struts provides the glue that joins the various elements of the standard Java 
platform into a coherent whole. Our goal is to leverage existing standards by producing the missing pieces we need to create 
enterprise-grade applications that are easy to maintain over time.

The Apache Struts Project offered two major versions of the Struts framework. Currently we are only maintaining the Struts 2 
version. It is recommended to upgrade all Struts 1.x applications to Struts 2. Please do not start new application development 
using Struts 1.x, as we are no longer issuing security patches.

Struts 2 was originally known as WebWork 2. After working independently for several years, the WebWork and Struts 
communities joined forces to create Struts 2. The 2.x framework is the best choice for teams who value elegant solutions 
to difficult problems.

## Why should you use Apache Struts?

Apache Struts is a modern, maintained and full-featured web framework. As it has been around for years and grown a huge user 
base it is unlikely it will go away anytime soon. Not only that, we have dedicated users and developers 
on the project. Apache Struts is licensed under the Apache License 2.0 and this will not change. We maintain a clean IP 
and you are “safe” to use the project. Sometimes you are not “safe” to use a project when a company controls the SCM. 
Access to Source Code doesn’t mean it is free. With Apache Struts, you are not only free to “do what you want with it”, 
you can even contribute (which is not always the case). And best of all: you can become a part of the core team too.

It is usually very easy to integrate other technologies with Apache Struts. If you are using an ORM like Apache Cayenne, 
Hibernate or JDBC, you will not have any restrictions. Apache Struts is not even tied too much to a frontend technology. 
In old days it was JSP, then came Velocity and Freemarker. Nowadays you might build your web application with just static 
HTML and AngularJS. Or you might want to use Sitemesh or Tiles. This all is no problem due to Struts' elegant and easy-to-use 
extension mechanisms.

Unlike other, component-oriented frameworks, we do not aim to hide the stateless nature of the web. We think it is 
perfectly acceptable to build upon a Request/Response cycle. We also think the MVC pattern is not so bad, just because 
it is old. In fact, we believe the Apache Struts architecture is clean and easy to understand.

Of course, if you wish to build components on the server side which render on the front end side, you will most likely 
not want Struts. This is a different approach which promises to reduce the amount of HTML/JavaScript knowledge needed 
and to create reusable components for the view layer. Projects like Wicket and Tapestry serve this purpose very well. 
As with every framework, you need to decide if it makes sense for you to build components or if you prefer 
the Struts approach.

## Commercial Support

The Apache Struts community does not offer commercial support by itself.

Some Apache Struts maintainers are working with [Tidelift](https://tidelift.com/) to provide commercial support and
invest paid working time in the improvement of the Apache Struts framework. For more information, visit 
the [Tidelift resources regarding Apache Struts](https://tidelift.com/subscription/pkg/maven-org-apache-struts-struts2-core?utm_source=maven-org-apache-struts-struts2-core&utm_medium=referral&utm_campaign=readme)
