INFORMATION
===========
- This is Struts 2's "Portlet" Maven Archetype
- To be used to create a simple portlet that can be deployed as JSR286 portlet.
- There is a maven profile in order to test this portlet on Pluto Portal.
 

USAGE
=====

1- Generate your archetype
mvn archetype:generate -B -DgroupId=com.mycompany.mysystem \
							-DartifactId=myWebApp \
							-DarchetypeGroupId=org.apache.struts \
							-DarchetypeArtifactId=struts2-archetype-portlet \
							-DarchetypeVersion=<CURRENT_STRUTS_VERSION> \
							-DremoteRepositories=http://struts.apache.org

2- Build your portlet project for Pluto Portal
- cd struts2-portlet-helloworld
- mvn clean package -Ppluto-embedded

3- Download and install Pluto Portal
- cd ..
- curl -v -H "Accept-Encoding: gzip" "http://apache.opensourcemirror.com/portals/pluto/pluto-2.0.3-bundle.zip" > pluto-2.0.3.zip
- unzip pluto-2.0.3.zip -d .

4- Deploy your portlet app
- cp struts2-portlet-helloworld/target/struts2-portlet-helloworld-1.0.0-SNAPSHOT.war pluto-2.0.3/webapps

5- Start Pluto Portal and create a page for your portlet
- ./pluto-2.0.3/bin/startup.sh
- Go to http://localhost:8080/pluto/portal (login: pluto / pwd: pluto)
- Click on "Pluto Admin" (http://localhost:8080/pluto/portal/Pluto%20Admin)
- On "Portal Pages" section : Add page "Struts2"
- On "Portlet Applications" section : select "/struts2-portlet-helloworld" and "HelloPortlet" then click "Add Portlet"

6- Test your portlet app
- Go to your Struts2 page http://localhost:8080/pluto/portal/Struts2 and play with your portlet !!


For more details about the Struts2 archetypes, please take a look at our Wiki page.

-> https://cwiki.apache.org/confluence/display/WW/Struts+2+Maven+Archetypes

I18N:
=====
Please note that this project was created with the assumption that it will be run
in an environment where the default locale is set to English. This means that
the default messages defined in package.properties are in English. If the default
locale for your server is different, then rename package.properties to package_en.properties
and create a new package.properties with proper values for your default locale.
