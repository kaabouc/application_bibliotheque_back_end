pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                // Clone the repository from GitHub
                git branch: 'main', url: 'https://github.com/kaabouc/application_bibliotheque_back_end.git'
            }
        }

        stage('Build with Maven') {
            steps {
                // Run Maven build
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Run Application') {
            steps {
                // Run the application
                sh 'java -jar target/*.jar &'
            }
        }
    }

    post {
        always {
            echo 'Pipeline completed.'
        }
        success {
            echo 'Application is running.'
        }
        failure {
            echo 'Pipeline failed. Check logs for errors.'
        }
    }
}
