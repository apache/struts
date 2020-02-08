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
        step([$class: 'JiraIssueUpdater', issueSelector: [$class: 'DefaultIssueSelector'], scm: scm])
      }
      post {
        always {
          junit '**/target/surefire-reports/*.xml'
        }
      }
    }
    stage('Code Quality') {
      when {
        branch 'master'
      }
      steps {
        withSonarQubeEnv('ASF Sonar Analysis') {
          sh 'mvn -P${JENKINS_PROFILE} sonar:sonar'
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
        sh 'mvn -B source:jar javadoc:jar -DskipAssembbly'
      }
    }
    // see https://cwiki.apache.org/confluence/display/INFRA/Multibranch+Pipeline+recipes#MultibranchPipelinerecipes-DeployingArtifacts
    stage('Deploy Snapshot') {
      when {
        branch 'master'
      }
      steps {
        sh 'mvn -DaltDeploymentRepository=snapshot-repo::default::file:./local-snapshots-dir deploy'
        stash name: 'struts2-build-snapshots', includes: 'local-snapshots-dir/**'
      }
    }
    stage('Upload Snapshot') {
      when {
        branch 'master'
      }
      steps {
        dir("local-snapshots-dir/") {
          deleteDir()
        }
        unstash name: 'struts2-build-snapshots'
        sh 'mvn -f jenkins.pom -X -P deploy-snapshots wagon:upload'
      }
    }
  }
}
