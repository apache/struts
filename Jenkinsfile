#!groovy
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
    stage('JDK 21') {
      agent {
        label 'ubuntu'
      }
      tools {
        jdk 'jdk_21_latest'
        maven 'maven_3_latest'
      }
      environment {
        MAVEN_OPTS = "-Xmx1024m"
      }
      stages {
        stage('Test') {
          steps {
            sh './mvnw -B -DskipAssembly verify --no-transfer-progress'
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
        stage('Test & Coverage') {
          steps {
            sh './mvnw -B verify -Pcoverage -DskipAssembly'
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
            anyOf {
              branch 'release/struts-6-7-x'
            }
          }
          steps {
            withCredentials([string(credentialsId: 'asf-struts-sonarcloud', variable: 'SONARCLOUD_TOKEN')]) {
              sh './mvnw -B -Pcoverage -DskipAssembly -Dsonar.login=${SONARCLOUD_TOKEN} verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar'
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
            sh './mvnw -B verify -DskipAssembly'
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
            branch 'release/struts-6-7-x'
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
            branch 'release/struts-6-7-x'
          }
          steps {
            withCredentials([file(credentialsId: 'lukaszlenart-repository-access-token', variable: 'CUSTOM_SETTINGS')]) {
              sh './mvnw -s \${CUSTOM_SETTINGS} deploy -DskipTests -DskipAssembly'
            }
          }
        }
        stage('Upload nightlies') {
          when {
            branch 'release/struts-6-7-x'
          }
          steps {
            sh './mvnw -B package -DskipTests'
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
            to: "notifications@struts.apache.org",
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
            to: "notifications@struts.apache.org",
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
            to: "notifications@struts.apache.org",
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
