pipeline {
    agent any
    environment {
        NEXUS_URL = 'http://192.168.33.10:8081/repository/maven-releases/tn/esprit/spring/kaddem/0.0.2/kaddem-0.0.2.jar'
        JAR_FILE = 'kaddem-0.0.2.jar'
    }

    stages {
        stage('Checkout Code') {
            steps {
                script {
                    def repoUrl = 'https://github.com/TeamXDevops/springproject.git'
                    def branch = 'zrig'
                    withCredentials([usernamePassword(credentialsId: 'github-credentials', usernameVariable: 'GITHUB_USERNAME', passwordVariable: 'GITHUB_TOKEN')]) {
                        checkout([$class: 'GitSCM',
                                  branches: [[name: "*/${branch}"]],
                                  userRemoteConfigs: [[url: "${repoUrl}", credentialsId: 'github-credentials']]
                        ])
                    }
                }
            }
        }

      /*   stage('Maven Clean and Package') {
            steps {
                script {
                    sh 'mvn clean test package'
                }
            }
        }
*/
        stage('Unit Tests') {
            steps {
                script {
                    echo "Running unit tests"
                    sh 'mvn test'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'Sonarqube-Credential', usernameVariable: 'SONAR_USER', passwordVariable: 'SONAR_PASSWORD')]) {
                        sh '''
                        mvn sonar:sonar \
                          -Dsonar.projectKey=DevOps_Tp_Foyer \
                          -Dsonar.host.url=http://192.168.33.10:9000 \
                          -Dsonar.login=$SONAR_USER \
                          -Dsonar.password=$SONAR_PASSWORD
                        '''
                    }
                }
            }
        }

        stage('Maven Deploy') {
            steps {
                script {
                    sh 'mvn clean deploy -DskipTests'
                }
            }
        }

        stage('Mail notification') {
            steps {
                script {
                    emailext(
                            subject: "Build Successful: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                            body: """<p>Good news!</p>
                    <p>The build <strong>${env.JOB_NAME} #${env.BUILD_NUMBER}</strong> completed successfully.</p>
                    <p>Check it out at: <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>""",
                            to: 'mohamed.zrig@esprit.tn'
                    )
                }
            }
        }


        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t medzrig/kaddem:latest ."
                }
            }
        }

        stage("Docker run") {
            steps {
                sh 'docker run -d -p 8089:8089 medzrig/kaddem:latest'
            }
        }

        stage('Push Docker Image to DockerHub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                        sh "echo $DOCKERHUB_PASSWORD | docker login -u $DOCKERHUB_USERNAME --password-stdin"
                        sh "docker push medzrig/kaddem:latest"
                    }
                }
            }
        }

        stage('Docker Compose') {
            steps {
                script {
                    echo "Running Docker Compose"
                    sh 'docker compose up -d'
                }
            }
        }
    }
    post {
        success {
            emailext (
                    to: 'mohamed.zrig@esprit.tn',
                    subject: "Jenkins Pipeline Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: "The build ${env.JOB_NAME} #${env.BUILD_NUMBER} has succeeded."
            )
        }

        failure {
            emailext (
                    to: 'mohamed.zrig@esprit.tn',
                    subject: "Jenkins Pipeline Failure: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: "The build ${env.JOB_NAME} #${env.BUILD_NUMBER} has failed. Please check the console output for details."
            )
        }
    }
}
