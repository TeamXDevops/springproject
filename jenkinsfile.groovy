pipeline {
    agent any
 
    stages {
        stage('Checkout Code') {
            steps {
                script {
                    def repoUrl = 'https://github.com/TeamXDevops/springproject.git'
                    def branch = 'mohamedzrig'
                    withCredentials([usernamePassword(credentialsId: 'github-credentials', usernameVariable: 'GITHUB_USERNAME', passwordVariable: 'GITHUB_TOKEN')]) {
                        checkout([$class: 'GitSCM',
                            branches: [[name: "*/${branch}"]],
                            userRemoteConfigs: [[url: "${repoUrl}", credentialsId: 'github-credentials']]
                        ])
                    }
                }
            }
        }
 
        stage('Maven Clean and Compile') {
            steps {
                script {
                    sh 'mvn clean compile'
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
    }
}