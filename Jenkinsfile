
pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '30'))
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout / Source Validation') {
            steps {
                echo 'Using local Java project'
                dir('D:\\jenkins-java-production-starter') {
                    bat 'java -version'
                    bat 'mvn -version'
                    bat 'git --version'
                }
            }
        }

        stage('Build and Unit Tests') {
            steps {
                dir('D:\\jenkins-java-production-starter') {
                    bat 'mvn -B clean verify'
                }
            }
            post {
                always {
                    script {
                        def reportPath =
                            'D:/jenkins-java-production-starter/target/surefire-reports/*.xml'
                        try {
                            junit testResults: reportPath,
                                  allowEmptyResults: true
                        } catch (Exception e) {
                            echo "Could not publish test reports: ${e.message}"
                        }
                    }
                }
            }
        }

        stage('Package Application') {
            steps {
                dir('D:\\jenkins-java-production-starter') {
                    bat 'if not exist "%WORKSPACE%\\artifacts" mkdir "%WORKSPACE%\\artifacts"'
                    bat 'copy /Y target\\task-api.jar "%WORKSPACE%\\artifacts\\task-api.jar"'
                }
                archiveArtifacts(
                    artifacts: 'artifacts/task-api.jar',
                    fingerprint: true
                )
            }
        }
    }

    post {
        success {
            echo 'CI pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check Console Output for the failing stage.'
        }
    }
}
