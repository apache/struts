Thanks for trying out WebWork: Java's most cutting edge web development
platform. WebWork is brought to you by the OpenSymphony team. You can
find out more about WebWork and OpenSymphony at
http://www.opensymphony.com.

=== Getting started ===
We think the best way to get started with any technology is to try it
out right away. That's why we've made WebWork the easiest web
development platform to get started with. You can use the QuickStart
feature (read the docs on QuickStart to learn how you can use this
important tool with your own projects):

 * Open a command prompt to the root directory of this distribution
 * Using a Java 5 JDK, execute:
           java -jar webwork-2.2.jar quickstart:showcase
 * Open your browser to the URL:
           http://localhost:8080/showcase
 * Feel free to edit any of the sources (.java, .jsp, etc) in:
           webapps/showcase
 * Reload your browser to see the changes right away

=== Getting help ===
To get help from the WebWork developers and community, please read the
instructions at the following URL:

http://www.opensymphony.com/webwork/userForums.action

=== Documentation ===
The documentation can be found in HTML and PDF format in the docs
directory:

 * HTML format: docs/Documentation.html
 * PDF format: docs/docs.pdf
 * Javadocs: docs/api/index.html

This documentation has been generated from the WebWork wiki at the time
of this release. If you are looking for the absolute latest
documentation, please visit the wiki at:

http://wiki.opensymphony.com/display/WW/Documentation

Note that these docs could include information about features not
included in this release, so take care when referencing the wiki.

=== Building ===
If you'd like to build your own version of WebWork, we've included
everything you need in this distribution. The ant script, build.xml,
contains a "jar" task that you can launch to create your own webwork
jar. The only thing you must do before hand is set up the proper jars
in the ANT_HOME/lib directory. These jars are:

 * lib/bootstrap/clover-license.jar
 * lib/build/clover.jar
 * lib/build/junit.jar

Once you've copied these three jars in to the ANT_HOME/lib directory,
simply run "ant jar" to create your own version of WebWork.


=== Distribution jars ===

The distribution zip file contains in the main dir the WebWork jar, named webwork-<version>.jar.

You can also find in the dist subdirectory a different WebWork packaging:
- webwork-nostatic-<version>.jar: containing only WebWork without the static content
- webwork-static-<version>.zip: containing the required WebWork static dependencies

The webwork-static-<version>.zip content must be included in the war distribution under webwork directory,
or a directory mapped to the URL <context_path>/webwork. 
You must also set in your webapp webwork.properties the webwork.serve.static property to false.

If these conditions are not fullfilled, WebWork may report strange errors. 