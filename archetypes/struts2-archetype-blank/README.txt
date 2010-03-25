INFORMATION
===========
- This is Struts 2's "Blank" Maven Archetype

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
                       -DarchetypeArtifactId=struts2-archetype-blank \
                       -DarchetypeVersion=2.0.9-SNAPSHOT \
                       -DremoteRepositories=http://people.apache.org/repo/m2-snapshot-repository

IMPORTANT:
==========
- The generated template uses the 'example' package, regardless of what you
specify during mvn archetype:create.

Unfortunately, the Maven Archetype plugin does not currently provide a way to
move resource files into a package structure.

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

