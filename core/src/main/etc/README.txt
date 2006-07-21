Thanks for trying out Struts:
Java's most cutting edge web development platform.
Struts is brought to you by the Apache Struts team. You can
find out more about Struts and the Apache Struts project at
http://struts.apache.org.

=== Getting started ===
We think the best way to get started with any technology is to try it
out right away. That's why we've made Struts the easiest web
development platform to get started with. You can use the QuickStart
feature (read the docs on QuickStart to learn how you can use this
important tool with your own projects):

 * Open a command prompt to the root directory of this distribution
 * Using a Java 5 JDK, execute:
           java -jar struts-2.0.jar quickstart:showcase
 * Open your browser to the URL:
           http://localhost:8080/showcase
 * Feel free to edit any of the sources (.java, .jsp, etc) in:
           apps/showcase
 * Reload your browser to see the changes right away

=== Getting help ===
To get help from the Struts developers and community,
please read the instructions at the following URL:

http://struts.apache.org/mail.html

=== Documentation ===
The documentation can be found in HTML and PDF format in the docs
directory:

 * HTML format: docs/Documentation.html
 * PDF format: docs/docs.pdf
 * Javadocs: docs/api/index.html

This documentation has been generated from the Struts wiki at the time
of this release. If you are looking for the absolute latest
documentation, please visit the wiki at:

http://wiki.opensymphony.com/display/WW/Documentation

Note that these docs could include information about features not
included in this release, so take care when referencing the wiki.

=== Building ===
If you'd like to build your own version of Struts, we've included
everything you need in this distribution. The ant script, build.xml,
contains a "jar" task that you can launch to create your own struts
jar. The only thing you must do before hand is set up the proper jars
in the ANT_HOME/lib directory. These jars are:

 * lib/bootstrap/clover-license.jar
 * lib/build/clover.jar
 * lib/build/junit.jar

Once you've copied these three jars in to the ANT_HOME/lib directory,
simply run "ant jar" to create your own version of Struts.


=== Distribution jars ===

The distribution zip file contains in the main dir the Struts jar, named struts-core-<version>.jar.

You can also find in the dist subdirectory a different Struts packaging:
- struts-core-nostatic-<version>.jar: containing only Struts without the static content
- struts-core-static-<version>.zip: containing the required Struts static dependencies

The struts-core-static-<version>.zip content must be included in the war distribution under struts directory,
or a directory mapped to the URL <context_path>/struts.
You must also set in your webapp struts.properties the struts.serve.static property to false.

If these conditions are not fullfilled, Struts may report strange errors.