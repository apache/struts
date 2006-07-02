INFORMATION
===========
- This is SAF2's maven archetype plugin. 
- To be used to create a simple webapp template that one could alter to meet his/her needs.
- The created template contains
  - sitemesh integeration
  - a simple SAF2 action
  - a simple SAF2 validation
  - a simple SAF2 conversion
  - a simple SAF2 global resource bundle
  - a simple SAF2 action level resource bundle
  

USAGE
=====

- change directory to where the web app template is to be created,
  for example, a 'projects' directory (which you may need to create):
  
     cd /home/tmjee/projects
  
- run the following command to generate the sample web app templatem, assuming :-
    - root java package -> com.myCompany.mySystem

    - the webapp war name   ->  myWebApp


  mvn archetype:create -DgroupId=com.myCompany.mySystem \
                       -DartifactId=myWebApp \
                       -DarchetypeGroupId=org.apache.struts \
                       -DarchetypeArtifactId=struts2-archetype-quickstart \
                       -DarchetypeVersion=1.0-SNAPSHOT \
                       -DremoteRepositories=http://people.apache.org/maven-snapshot-repository

THis will create the sample Web app template

- to compile, execute
  mvn compile

- to run test cases execute
  mvn test

- to clean execute
  mvn clean

- to package execute 
  mvn package

- to start it with jetty6 execute
  mvn jetty6:run




