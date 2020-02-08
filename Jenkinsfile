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
    jiraIssueSelector(issueSelector: [$class: 'DefaultIssueSelector'])
    stage('Build') {
      steps {
        sh './mvnw -B -DskipTests -DskipAssembly clean package'
      }
    }
    stage('Test') {
      steps {
        sh './mvnw -B test'
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
        sh './mvnw -B source:jar javadoc:jar -DskipWiki'
      }
    }
    stage('Deploy Snapshot') {
      when {
        branch 'master'
      }
      steps {
        withCredentials([usernamePassword(credentialsId: 'lukaszlenart-access-token-repository', passwordVariable: 'REPO_PASSWORD', usernameVariable: 'REPO_USERNAME')]) {
          sh './mvnw -B deploy -Dusername=\${REPO_USERNAME} -Dpassword=\${REPO_PASSWORD}'
        }
      }
    }
  }
}
