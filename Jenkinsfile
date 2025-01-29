pipeline {
    agent {
        label 'docker'
    }
    tools {
      jdk "OpenJDK 21"
    }
    environment {
        PROJECT_NAME = sh(returnStdout: true, script: 'grep "rootProject.name" settings.gradle.kts | sed "s/rootProject.name = //g"').trim()
        REGISTRY = 'ghcr.io'
        GitHubPackages = credentials('1ef3f9de-5145-4141-acf6-1fca4564b441')
        ORG_GRADLE_PROJECT_GitHubPackagesUsername = "${GitHubPackages_USR}"
        ORG_GRADLE_PROJECT_GitHubPackagesPassword = "${GitHubPackages_PSW}"
        SonarToken = credentials("sonar.token")
        ORG_GRADLE_PROJECT_SONAR_TOKEN = "${SonarToken}"
    }
    stages {
        stage('Name version') {
          steps {
            script {
              env.TAG_BRANCH =  sh(returnStdout: true, script: 'echo ${BRANCH_NAME}|sed "s/[^a-zA-Z0-9]/-/g"').trim()
              env.TAG_ID = "${env.TAG_BRANCH}-${BUILD_ID}"

              env.VERSION_NAME = "${env.TAG_ID}"
              currentBuild.displayName = "$VERSION_NAME"
            }
          }
        }

        stage('Build') {
            steps {
                sh 'make buildCI'
            }
            post {
                always {
                    junit testResults: '**/TEST-*.xml', allowEmptyResults: true
                    archiveArtifacts artifacts: 'build/docker*.txt', fingerprint: true, allowEmptyArchive: true
                    archiveArtifacts artifacts: 'build/reports/**', fingerprint: true, allowEmptyArchive: true
                    publishHTML (target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: false,
                        keepAll: true,
                        reportDir: 'build/reports/jacoco/test/html',
                        reportFiles: 'index.html',
                        reportName: "JaCoCo Report"
                    ])
                    publishHTML (target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: false,
                        keepAll: true,
                        reportDir: 'build/reports/tests/test',
                        reportFiles: 'index.html',
                        reportName: "Junit Report"
                    ])
                    publishHTML (target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: false,
                        keepAll: true,
                        reportDir: 'build/reports/pmd',
                        reportFiles: 'main.html',
                        reportName: "PMD main Report"
                    ])
                    publishHTML (target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: false,
                        keepAll: true,
                        reportDir: 'build/reports/pmd',
                        reportFiles: 'test.html',
                        reportName: "PMD test Report"
                    ])
                    publishHTML (target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: false,
                        keepAll: true,
                        reportDir: 'build',
                        reportFiles: 'dockerImageName.txt',
                        reportName: "Docker image build"
                    ])
                }
            }
        }

        stage('Docker-compose tests') {
            environment {
              VERSION = "${TAG_ID}"
            }
            steps {
                sh 'make docker-compose-test'
            }
        }

        stage('Docker image push') {
//             environment {
//                 LOGIN = credentials('push.credentials.ghcr')
//             }
            steps {
                 sh 'docker login -u ${GitHubPackages_USR} -p ${GitHubPackages_PSW} ${REGISTRY}'
                 sh 'make push'
            }
        }
    }
}
