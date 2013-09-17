INFORMATION
===========
- This is Struts 2's "Database Portlet" Maven Archetype
- To be used to create a simple portlet that displays information in a database and can be deployed as a webapp or JSR168 portlet.
 

USAGE
=====

- change directory to where the portlet template is to be created,
  for example, a 'projects' directory (which you may need to create):
  
     cd /home/tmjee/projects
  
- run the following command to generate the sample portlet template, assuming :-
    - root java package -> com.myCompany.myPortlet

    - the portlet war name   ->  myWebApp


mvn archetype:generate -B -DgroupId=com.mycompany.mysystem \
							-DartifactId=myWebApp \
							-DarchetypeGroupId=org.apache.struts \
							-DarchetypeArtifactId=struts2-archetype-dbportlet \
							-DarchetypeVersion=<CURRENT_STRUTS_VERSION> \
							-DremoteRepositories=http://struts.apache.org

For more details about the Struts2 archetypes, please take a look at our Wiki page.

-> https://cwiki.apache.org/confluence/display/WW/Struts+2+Maven+Archetypes


- to compile, execute
  mvn compile

- to run test cases execute
  mvn test

- to clean execute
  mvn clean

- to package execute 
  mvn package

- to start it with jetty execute as a servlet
  mvn jetty:run

