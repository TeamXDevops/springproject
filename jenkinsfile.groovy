pipeline {
    agent any
    environment {
        NEXUS_URL = 'http://192.168.33.10:8081/repository/maven-releases/tn/esprit/kaddem/0.0.2/kaddem-0.0.2.jar' // The Nexus URL for the JAR file
        JAR_FILE = 'kaddem-0.0.2.jar' // The name of the JAR file to download
    }

    stages {
        stage('Checkout Code') {
            steps {
                script {
                    def repoUrl = 'https://github.com/TeamXDevops/springproject.git'
                    def branch = 'zrig'  // Ensure this branch exists in your repository
                    withCredentials([usernamePassword(credentialsId: 'github-credentials', usernameVariable: 'GITHUB_USERNAME', passwordVariable: 'GITHUB_TOKEN')]) {
                        checkout([$class: 'GitSCM',
                            branches: [[name: "*/${branch}"]],
                            userRemoteConfigs: [[url: "${repoUrl}", credentialsId: 'github-credentials']]
                        ])
                    }
                }
            }
        }

        stage('Maven Clean and Package') { // Consider renaming to 'Package' if applicable
            steps {
                script {
                    sh 'mvn clean package' // Use 'package' if you want to create a deployable artifact
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
                    sh 'mvn clean deploy -DskipTests' // Review if skipping tests is intended
                }
            }
        }

        stage('Download JAR from Nexus') {
            steps {
                script {
                    echo "Downloading JAR from Nexus"
                    withCredentials([usernamePassword(credentialsId: 'nexus-credential', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASSWORD')]) {
                        sh "wget --user=$NEXUS_USER --password=$NEXUS_PASSWORD -O ${JAR_FILE} ${NEXUS_URL}"
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Build the Docker image using the Dockerfile
                    sh "docker build -t medzrig/kaddem:latest ."
                }
            }
        }
        stage('Push Docker Image to DockerHub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                        // Login to DockerHub
                        sh "echo $DOCKERHUB_PASSWORD | docker login -u $DOCKERHUB_USERNAME --password-stdin"

                        // Push the image to DockerHub
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
                    sh 'docker compose down'
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the logs for details.'
            // You can add additional notifications here
        }
    }
}
