pipeline {

  agent {
    label 'ubuntu'
  }

  options {
    buildDiscarder logRotator(daysToKeepStr: '14', numToKeepStr: '10')
    timeout(80)
    disableConcurrentBuilds()
  }

  tools {
    jdk 'JDK 1.7 (latest)'
    maven 'Maven 3 (latest)'
  }

  triggers {
    pollSCM 'H/15 * * * *'
  }

  stages {
    stage('Build') {
      steps {
        environment {
          MAVEN_OPTS = "-Xmx1024m -XX:MaxPermSize=256m"
        }
        sh 'mvn --version'
        sh 'mvn clean source:jar javadoc:jar install deploy -DskipWiki -Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2'
       }
    }
  }
}
