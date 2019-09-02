#!groovy
pipeline {
  agent {
    docker {
      label 'ubuntu'
      image 'maven:3-jdk-8'
      args '-v $HOME/.m2:/root/.m2 -e MAVEN_OPTS="-Xmx1024m" -e USER=$USER'
    }
  }
  options {
    buildDiscarder logRotator(daysToKeepStr: '14', numToKeepStr: '10')
    timeout(80)
    disableConcurrentBuilds()
  }
  triggers {
    pollSCM 'H/15 * * * *'
  }
  stages {
    stage('Maven version') {
      steps {
        sh 'mvn -v'
      }
    }

    stage('Build') {
      steps {
        sh 'mvn -B -DskipTests -DskipAssembly clean package'
      }
    }

    stage('Test') {
      steps {
        sh 'mvn test'
      }
      post {
        always {
          junit '**/target/surefire-reports/*.xml'
        }
      }
    }
  }
}
