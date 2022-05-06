#!groovy

pipeline {
  agent none
  options {
    buildDiscarder logRotator(daysToKeepStr: '14', numToKeepStr: '10')
    timeout(80)
    disableConcurrentBuilds()
    skipStagesAfterUnstable()
    quietPeriod(30)
  }
  triggers {
    pollSCM 'H/15 * * * *'
  }
  stages {
    stage('Prepare') {
      agent {
        label 'ubuntu'
      }
      stages {
        stage('Clean up') {
          steps {
            cleanWs deleteDirs: true, patterns: [[pattern: '**/target/**', type: 'INCLUDE']]
          }
        }
      }
    }
    stage('JDK 17') {
      agent {
        label 'ubuntu'
      }
      tools {
        jdk 'jdk_17_latest'
        maven 'maven_3_latest'
      }
      environment {
        MAVEN_OPTS = "-Xmx1024m"
      }
      stages {
        stage('Build') {
          steps {
            sh './mvnw -B clean install -DskipTests -DskipAssembly'
          }
        }
        stage('Test') {
          steps {
            sh './mvnw -B test'
          }
          post {
            always {
              junit(testResults: '**/surefire-reports/*.xml', allowEmptyResults: true)
              junit(testResults: '**/failsafe-reports/*.xml', allowEmptyResults: true)
            }
          }
        }
      }
      post {
        always {
          cleanWs deleteDirs: true, patterns: [[pattern: '**/target/**', type: 'INCLUDE']]
        }
      }
    }
    stage('JDK 11') {
      agent {
        label 'ubuntu'
      }
      tools {
        jdk 'jdk_11_latest'
        maven 'maven_3_latest'
      }
      environment {
        MAVEN_OPTS = "-Xmx1024m"
      }
      stages {
        stage('Build') {
          steps {
            sh './mvnw -B clean install -DskipTests -DskipAssembly'
          }
        }
        stage('Test') {
          steps {
            sh './mvnw -B test'
          }
          post {
            always {
              junit(testResults: '**/surefire-reports/*.xml', allowEmptyResults: true)
              junit(testResults: '**/failsafe-reports/*.xml', allowEmptyResults: true)
            }
          }
        }
        stage('Code Quality') {
          when {
            branch 'master'
          }
          steps {
            withCredentials([string(credentialsId: 'asf-struts-sonarcloud', variable: 'SONARCLOUD_TOKEN')]) {
              sh './mvnw sonar:sonar -DskipAssembly -Dsonar.login=${SONARCLOUD_TOKEN}'
            }
          }
        }
      }
      post {
        always {
          cleanWs deleteDirs: true, patterns: [[pattern: '**/target/**', type: 'INCLUDE']]
        }
      }
    }
    stage('JDK 8') {
      agent {
        label 'ubuntu'
      }
      tools {
        jdk 'jdk_1.8_latest'
        maven 'maven_3_latest'
      }
      environment {
        MAVEN_OPTS = "-Xmx1024m"
      }
      stages {
        stage('Build') {
          steps {
            sh './mvnw -B clean install -DskipTests -DskipAssembly'
          }
        }
        stage('Test') {
          steps {
            sh './mvnw -B test'
            // step([$class: 'JiraIssueUpdater', issueSelector: [$class: 'DefaultIssueSelector'], scm: scm])
          }
          post {
            always {
              junit(testResults: '**/surefire-reports/*.xml', allowEmptyResults: true)
              junit(testResults: '**/failsafe-reports/*.xml', allowEmptyResults: true)
            }
          }
        }
        stage('Build Source & JavaDoc') {
          when {
            branch 'master'
          }
          steps {
            dir("local-snapshots-dir/") {
              deleteDir()
            }
            sh './mvnw -B source:jar javadoc:jar -DskipTests -DskipAssembly'
          }
        }
        stage('Deploy Snapshot') {
          when {
            branch 'master'
          }
          steps {
            withCredentials([file(credentialsId: 'lukaszlenart-repository-access-token', variable: 'CUSTOM_SETTINGS')]) {
              sh './mvnw -s \${CUSTOM_SETTINGS} deploy -DskipTests -DskipAssembly'
            }
          }
        }
        stage('Upload nightlies') {
          when {
            branch 'master'
          }
          steps {
            sshPublisher(publishers: [
                sshPublisherDesc(
                    configName: 'Nightlies',
                    transfers: [
                        sshTransfer(
                            remoteDirectory: '/struts/snapshot',
                            removePrefix: 'assembly/target/assembly/out',
                            sourceFiles: 'assembly/target/assembly/out/struts-*.zip'
                        )
                    ],
                    verbose: true
                )
            ])
          }
        }
      }
      post {
        always {
          cleanWs deleteDirs: true, patterns: [[pattern: '**/target/**', type: 'INCLUDE']]
        }
      }
    }
  }
  post {
    // If this build failed, send an email to the list.
    failure {
      script {
        emailext(
            to: "dev@struts.apache.org",
            recipientProviders: [[$class: 'DevelopersRecipientProvider']],
            from: "Mr. Jenkins <jenkins@builds.apache.org>",
            subject: "Jenkins job ${env.JOB_NAME}#${env.BUILD_NUMBER} failed",
            body: """
There is a build failure in ${env.JOB_NAME}.

Build: ${env.BUILD_URL}
Logs: ${env.BUILD_URL}console
Changes: ${env.BUILD_URL}changes

--
Mr. Jenkins
Director of Continuous Integration
"""
        )
      }
    }

    // If this build didn't fail, but there were failing tests, send an email to the list.
    unstable {
      script {
        emailext(
            to: "dev@struts.apache.org",
            recipientProviders: [[$class: 'DevelopersRecipientProvider']],
            from: "Mr. Jenkins <jenkins@builds.apache.org>",
            subject: "Jenkins job ${env.JOB_NAME}#${env.BUILD_NUMBER} unstable",
            body: """
Some tests have failed in ${env.JOB_NAME}.

Build: ${env.BUILD_URL}
Logs: ${env.BUILD_URL}console
Changes: ${env.BUILD_URL}changes

--
Mr. Jenkins
Director of Continuous Integration
"""
        )
      }
    }

    // Send an email, if the last build was not successful and this one is.
    fixed {
      script {
        emailext(
            to: "dev@struts.apache.org",
            recipientProviders: [[$class: 'DevelopersRecipientProvider']],
            from: 'Mr. Jenkins <jenkins@builds.apache.org>',
            subject: "Jenkins job ${env.JOB_NAME}#${env.BUILD_NUMBER} back to normal",
            body: """
The build for ${env.JOB_NAME} completed successfully and is back to normal.

Build: ${env.BUILD_URL}
Logs: ${env.BUILD_URL}console
Changes: ${env.BUILD_URL}changes

--
Mr. Jenkins
Director of Continuous Integration
"""
        )
      }
    }
  }
}
