#!groovy
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
    jdk 'jdk_1.7_latest'
    maven 'maven_3_latest'
  }
  triggers {
    pollSCM 'H/15 * * * *'
  }
  environment {
    MAVEN_OPTS = '-Xmx1024m -XX:MaxPermSize=256m'
  }
  stages {
    stage('Build') {
      steps {
        sh 'mvn --version'
        sh 'mvn clean source:jar javadoc:jar install deploy -DskipWiki -Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2'
      }
    }
  }
}
