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
- change directory to SAF2's maven archetype directory eg. from SAF2's root directory
  cd ./archetype   <press enter>

- run the following command to install SAF2 maven archetype into the local repository
  mvn install 

- change directory to where the web app template is to be created eg. assuming the directory to be myWebApp.
  mkdir /home/tmjee/myWebApp
  cd /home/tmjee/myWebApp

- run the following command to generate the sample web app templatem, assuming :-
    - root java package -> com.myCompany.mySystem

    - the webapp war name   ->  myWebApp


  mvn archetype:create -DgroupId=com.myCompany.mySystem -DartifactId=myWebApp -DarchetypeGroupId=org.apache.struts2 -DarchetypeArtifactId=struts2-archetype-quickstart

THis will create the sample Web app template

- to complile execute
  mvn compile

- to run test cases execute
  mvn test

- to clean execute
  mvn clean

- to package execute 
  mvn package

- to start it with jetty6 execute
  mvn jetty6:run




