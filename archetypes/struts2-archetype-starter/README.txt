INFORMATION
===========
- This is Struts 2's "Starter" Maven Archetype
- To be used to create a simple webapp template that one could alter to meet his/her needs.
- The created template contains
  - sitemesh integration
  - a simple Struts action
  - a simple Struts action level validation
  - a simple Struts action alias level validation
  - a simple Struts global conversion
  - a simple Struts action level conversion
  - a simple Struts global resource bundle
  - a simple Struts action level resource bundle
  

USAGE
=====

- change directory to where the web app template is to be created,
  for example, a 'projects' directory (which you may need to create):
  
     cd /home/tmjee/projects
  
- run the following command to generate the sample web app template, assuming :-
    - root java package -> com.myCompany.mySystem

    - the webapp war name   ->  myWebApp


  mvn archetype:create -DgroupId=com.myCompany.mySystem \
                       -DartifactId=myWebApp \
                       -DarchetypeGroupId=org.apache.struts \
                       -DarchetypeArtifactId=struts2-archetype-starter \
                       -DarchetypeVersion=2.0.11


COMMON TASKS
==========

- to compile, execute
  mvn compile

- to run test cases execute
  mvn test

- to clean execute
  mvn clean

- to package execute 
  mvn package

- to start it with jetty6 execute
  mvn jetty:run
