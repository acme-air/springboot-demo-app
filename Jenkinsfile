// node('maven') {
//     stage('Initialize') {
//         sh "git config --global http.sslVerify false"
//         sh "git clone https://github.com/acme-air/springboot-demo-app"
//     }
//     stage('Build') {
//         sh "mvn -v"
//         sh "mvn clean package -f springboot-demo-app/pom.xml"
         
//         def jarFile = sh(returnStdout: true, script: 'find springboot-demo-app/target -maxdepth 1 -regextype posix-extended -regex ".+\\.(jar|war)\$" | head -n 1').trim()
//         sh "cp ${jarFile} app.jar"
//     }
//     stage('Deploy') {
//         sh "oc new-build --name springboot-demo-app --binary -n apmt-project1 --image-stream=apmt-project1/openjdk-11-rhel7  || true"
//         sh "oc start-build springboot-demo-app --from-file=app.jar -n apmt-project1 --follow --wait"
//         sh "oc new-app apmt-project1/springboot-demo-app || true"
//         sh "oc expose svc/springboot-demo-app || true"
//     }
// }

def templateName = 'springboot-demo-app'
pipeline {
    agent {
      node {
        // spin up a Maven slave pod to run this build on
        label 'maven'
      }
    }
    options {
        // set a timeout of 20 minutes for this pipeline
        timeout(time: 20, unit: 'MINUTES')
        // parallelsAlwaysFailFast()
    }

    stages {
        stage('initialize') {
            steps {
                sh "mvn -v"
                sh "git config --global http.sslVerify false"
                sh "git clone https://github.com/acme-air/springboot-demo-app"

                script {
                    openshift.withCluster() {
                        openshift.withProject() {
                            echo "Using project: ${openshift.project()}"

                            // delete everything with this template label
                            openshift.selector("all", [ app : templateName ]).delete()
                            openshift.selector("all", [ deployment : templateName ]).delete()
                            // delete any secrets with this template label
                            if (openshift.selector("secrets", templateName).exists()) {
                                openshift.selector("secrets", templateName).delete()
                            }
                        }
                    }
                }
            }
        }
        stage('lint') {
            // tools { nodejs "nodejs-14.2.0" }
            steps {
                script {
                    try {
                        sh '''#!/bin/bash
                        # npm i -g eslint
                        echo Linting
                        '''
                    } finally {
                        // junit allowEmptyResults: true, testResults: '**/eslint.xml'
                    }
                }
            }
        }
        stage('unit test') {
            // tools { maven "maven-4.3.3" }
            steps {
                // sh 'mvn test'
                sh "echo Testing done"
            }
        }
        stage('build') {
            parallel {
                stage('local') {
                    steps {
                        script {
                            // sh 'podman build -t acmeair-web-app:latest .'
                            // sh 'echo complete '
                            sh "mvn clean package -f springboot-demo-app/pom.xml"
                            def jarFile = sh(returnStdout: true, script: 'find springboot-demo-app/target -maxdepth 1 -regextype posix-extended -regex ".+\\.(jar|war)\$" | head -n 1').trim()
                            sh "cp ${jarFile} app.jar"
                        } // script
                    } // steps
                } // stage
                stage('remote-check') {
                    steps {
                        script {
                            openshift.withCluster() {
                                openshift.withProject() {
                                    echo "Using project: ${openshift.project()}"
                                }
                            }
                        } // script
                    } // steps
                } // stage
            } // parallel
        } // stage                
        stage('test') {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject() {
                            echo "Using project: ${openshift.project()}"
                        }
                    }
                } // script
            } // steps
        } // stage                
        stage("scan prep") {
            steps {
                script {
                    sh '''#!/bin/bash
                    echo "" >> wicked-suppression.txt
                    '''
                }
            }
        }
        // stage('Quality Gate') {
        //     parallel {
        //         stage('SonarQube') {
        //             steps {
        //                 script {
        //                     withSonarQubeEnv('acme-sonarqube') {
        //                         def scannerHome = tool 'acme-sonarqube';
        //                         sh "${scannerHome}/bin/sonar-scanner"
        //                         sh "sleep 15"
        //                     }
        //                     def qg = waitForQualityGate()
        //                     if (qg.status != 'OK') {
        //                         error "Pipeline aborted due to quality gate failure: ${qg.status}"
        //                     }
        //                 }
        //             }
        //         } // stage
        //         stage("OWASP Check") {
        //             steps {        
        //                 // DEMO note: Uncommenting the below would make the dependency check work but will take +4 minutes for the builds to complete
        //                 //
        //                 // dependencyCheck additionalArguments: '', odcInstallation: 'acme-dependency-check' 
        //                 // dependencyCheckPublisher pattern: ''
        //                 sh 'echo done'
        //             } // steps
        //         } // stage
        //         stage('Twistlock') {
        //             steps {
        //                 script {
        //                     echo "Running Twistlock scan on image ${templateName}:latest"
        //                     prismaCloudScanImage ca: '', cert: '', dockerAddress: 'tcp://192.168.65.4:2376', ignoreImageBuildTime: true, image: 'acmeair-web-app:latest', key: '', logLevel: 'debug', podmanPath: '', project: '', resultsFile: 'prisma-cloud-scan-results.json'
        //                     echo "Completed Twistlock scan. Publishing the report..."
        //                     prismaCloudPublish resultsFilePattern: 'prisma-cloud-scan-results.json'
        //                     echo "Completed Twistlock publish"
        //                 } // script
        //             } // steps
        //         } // stage
        //     } // parallel
        // } // stage
        stage('deploy') {
            steps {
                sh "oc new-build --name springboot-demo-app --binary -n apmt-project1 --image-stream=apmt-project1/openjdk-11-rhel7  || true"
                sh "oc start-build springboot-demo-app --from-file=app.jar -n apmt-project1 --follow --wait"
                sh "oc new-app apmt-project1/springboot-demo-app || true"
                sh "oc expose svc/springboot-demo-app || true"

            //     script {
            //         openshift.withCluster() {
            //             openshift.withProject() {
            //                 // create a new application from the templatePath
            //                 openshift.newApp(templatePath)

            //                 def builds = openshift.selector("bc", templateName).related('builds')
            //                 builds.untilEach(1) {
            //                     return (it.object().status.phase == "Complete")
            //                 }

            //                 def rm = openshift.selector("dc", templateName).rollout()
            //                 openshift.selector("dc", templateName).related('pods').untilEach(1) {
            //                     return (it.object().status.phase == "Running")
            //                 }
            //             }
            //         }
            //     } // script
            } // steps
        } // stage
        stage('promote') {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject() {
                            // if everything else succeeded, tag the ${templateName}:latest image as ${templateName}-staging:latest
                            // a pipeline build config for the staging environment can watch for the ${templateName}-staging:latest
                            // image to change and then deploy it to the staging environment
                            openshift.tag("${templateName}:latest", "${templateName}-staging:latest")
                        }
                    }
                } // script
            } // steps
        } // stage
    } // stages
} // pipeline
