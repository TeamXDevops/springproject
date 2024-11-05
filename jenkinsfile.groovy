pipeline {
    agent any

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
