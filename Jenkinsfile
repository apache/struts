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
    stage('Build') {
      steps {
        sh 'mvn -B clean package -DskipTests -DskipAssembly'
      }
    }
    stage('Test') {
      steps {
        sh 'mvn -B test'
      }
      post {
        always {
          junit '**/target/surefire-reports/*.xml'
        }
      }
    }
    stage('Build Source & JavaDoc') {
      when {
        branch 'master'
      }
      steps {
        sh 'mvn -B source:jar javadoc:jar -DskipAssembbly'
      }
    }
    stage('Deploy Snapshot') {
      when {
        branch 'master'
      }
      steps {
        withCredentials([usernamePassword(credentialsId: 'lukaszlenart-access-token-repository', passwordVariable: 'REPO_PASSWORD', usernameVariable: 'REPO_USERNAME')]) {
          sh 'mvn -B deploy -Dusername=\${REPO_USERNAME} -Dpassword=\${REPO_PASSWORD}'
        }
      }
    }
  }
}
