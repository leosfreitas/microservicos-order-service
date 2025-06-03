pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = "leosfreitas/gateway"
        DOCKER_TAG = "${BUILD_NUMBER}"
        AWS_REGION = "us-east-2"
        EKS_CLUSTER = "eks-store"
        KUBE_NAMESPACE = "default"
        SERVICE_NAME = "gateway"
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build Application') {
            steps {
                script {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    def image = docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}", ".")
                    docker.withRegistry('https://registry-1.docker.io/v2/', 'docker-hub-credentials') {
                        image.push()
                        image.push("latest")
                    }
                }
            }
        }
        
        stage('Deploy to EKS') {
            steps {
                withCredentials([
                    string(credentialsId: 'aws-access-key-id', variable: 'AWS_ACCESS_KEY_ID'),
                    string(credentialsId: 'aws-secret-access-key', variable: 'AWS_SECRET_ACCESS_KEY')
                ]) {
                    script {
                        sh """
                            aws configure set aws_access_key_id $AWS_ACCESS_KEY_ID
                            aws configure set aws_secret_access_key $AWS_SECRET_ACCESS_KEY
                            aws configure set region ${AWS_REGION}
                            aws eks update-kubeconfig --region ${AWS_REGION} --name ${EKS_CLUSTER}
                            kubectl set image deployment/${SERVICE_NAME} ${SERVICE_NAME}=${DOCKER_IMAGE}:${DOCKER_TAG} -n ${KUBE_NAMESPACE}
                            kubectl rollout status deployment/${SERVICE_NAME} -n ${KUBE_NAMESPACE}
                            kubectl get pods -n ${KUBE_NAMESPACE}
                        """
                    }
                }
            }
        }
        
        stage('Verify Deployment') {
            steps {
                script {
                    sh """
                        kubectl get services -n ${KUBE_NAMESPACE}
                        kubectl describe deployment ${SERVICE_NAME} -n ${KUBE_NAMESPACE}
                    """
                }
            }
        }
    }
    
    post {
        success {
            echo 'Deploy do ${SERVICE_NAME} realizado com sucesso!'
        }
        failure {
            echo 'Deploy do ${SERVICE_NAME} falhou!'
        }
        always {
            cleanWs()
        }
    }
}