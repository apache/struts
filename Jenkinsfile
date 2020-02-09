#!groovy
var statusMessage = 'Build passed normally'

def stageF(name, block) {
  statusMessage = "Build failed in $name stage"
  return stage(name, block)
}

pipeline {
  agent {
    docker {
      label 'ubuntu'
      image 'maven:3-jdk-8'
      args '-v $HOME/.m2:/root/.m2 -e MAVEN_OPTS="-Xmx1024m" -e USER=$USER'
      reuseNode true
    }
  }
  options {
    buildDiscarder logRotator(daysToKeepStr: '14', numToKeepStr: '10')
    timeout(80)
    // disableConcurrentBuilds()
    skipStagesAfterUnstable()
  }
  triggers {
    pollSCM 'H/15 * * * *'
  }
  stages {
    stageF('Build') {
      steps {
        sh 'mvn -B clean package -DskipTests -DskipAssembly'
      }
    }
    stageF('Test') {
      steps {
        sh 'mvn -B test'
        step([$class: 'JiraIssueUpdater', issueSelector: [$class: 'DefaultIssueSelector'], scm: scm])
      }
      post {
        always {
          junit(testResults: '**/surefire-reports/*.xml', allowEmptyResults: true)
          junit(testResults: '**/failsafe-reports/*.xml', allowEmptyResults: true)
        }
      }
    }
    stageF('Code Quality') {
      when {
        branch 'master'
      }
      steps {
        withCredentials([string(credentialsId: 'asf-struts-sonarcloud', variable: 'SONARCLOUD_TOKEN')]) {
          sh 'mvn sonar:sonar -DskipAssembly -Dsonar.projectKey=apache_struts -Dsonar.organization=apache -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=${SONARCLOUD_TOKEN}'
        }
      }
    }
    stageF('Build Source & JavaDoc') {
      when {
        branch 'master'
      }
      steps {
        dir("local-snapshots-dir/") {
          deleteDir()
        }
        sh 'mvn -B source:jar javadoc:jar -DskipAssembbly'
      }
    }
    stageF('Deploy Snapshot') {
      when {
        branch 'master'
      }
      steps {
        withCredentials([file(credentialsId: 'struts-custom-settings_xml', variable: 'CUSTOM_SETTINGS')]) {
          sh 'mvn -s \${CUSTOM_SETTINGS} deploy -skipAssembly'
        }
      }
    }
  }
  post {
    // If this build failed, send an email to the list.
    failure {
      script {
        emailext(
            subject: "[BUILD-FAILURE]: Job '${env.JOB_NAME} [${env.BRANCH_NAME}] [${env.BUILD_NUMBER}]'",
            body: """
              BUILD-FAILURE: Job '${env.JOB_NAME} [${env.BRANCH_NAME}] [${env.BUILD_NUMBER}]':
               
              Check console output at "<a href="${env.BUILD_URL}">${env.JOB_NAME} [${env.BRANCH_NAME}] [${env.BUILD_NUMBER}]</a>"

              Status: ${statusMessage}
            """.stripMargin(),
            to: "dev@struts.apache.org",
            recipientProviders: [[$class: 'DevelopersRecipientProvider']]
        )
      }
    }

    // If this build didn't fail, but there were failing tests, send an email to the list.
    unstable {
      script {
        emailext(
            subject: "[BUILD-UNSTABLE]: Job '${env.JOB_NAME} [${env.BRANCH_NAME}] [${env.BUILD_NUMBER}]'",
            body: """
              BUILD-UNSTABLE: Job '${env.JOB_NAME} [${env.BRANCH_NAME}] [${env.BUILD_NUMBER}]':
               
              Check console output at "<a href="${env.BUILD_URL}">${env.JOB_NAME} [${env.BRANCH_NAME}] [${env.BUILD_NUMBER}]</a>"

              Status: ${statusMessage}
            """.stripMargin(),
            to: "dev@struts.apache.org",
            recipientProviders: [[$class: 'DevelopersRecipientProvider']]
        )
      }
    }

    // Send an email, if the last build was not successful and this one is.
    fixed {
      script {
        emailext(
            subject: "[BUILD-STABLE]: Job '${env.JOB_NAME} [${env.BRANCH_NAME}] [${env.BUILD_NUMBER}]'",
            body: """
              BUILD-STABLE: Job '${env.JOB_NAME} [${env.BRANCH_NAME}] [${env.BUILD_NUMBER}]':
               
              Is back to normal.

              Status: ${statusMessage}
            """.stripMargin(),
            to: "dev@struts.apache.org",
            recipientProviders: [[$class: 'DevelopersRecipientProvider']]
        )
      }
    }
  }
}
