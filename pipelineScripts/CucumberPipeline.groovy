import groovy.json.JsonSlurperClassic

@NonCPS
def jsonParse(def json) {
    new groovy.json.JsonSlurperClassic().parseText(json)
}

def optionsJson
def gitHubRepo
stage('Setup'){
    
    def options = "${options}"
    
    println "Options: ${options}"
    
    optionsJson = jsonParse(options)
    gitHubRepo = optionsJson.githubRepo
    featurePathList = optionsJson.featurePathList
}

node {
   def mvnHome
   stage('Preparation') { // for display purposes
      
      // Get some code from a GitHub repository
      git "${gitHubRepo}"
      // Get the Maven tool.
      // ** NOTE: This 'M3' Maven tool must be configured
      // **       in the global configuration.           
      mvnHome = tool 'M3'
   }
    stage('Test'){
        def buildExecutions = [:]
        def featurePath
        for (int i = 0; i < featurePathList.size(); i++) {
            featurePath = featurePathList[i]
            buildExecutions["${featurePath}"] = {
              // Run the maven build
              if (isUnix()) {
                 sh "'${mvnHome}/bin/mvn' --version"
                 sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore -Dcucumber.options='${featurePath}' clean test"
              } else {
                 bat(/"${mvnHome}\bin\mvn" --version/)
                 bat(/"${mvnHome}\bin\mvn" -Dmaven.test.failure.ignore -Dcucumber.options="${featurePath}" clean test/)
              }   
            }
        }
        
        parallel buildExecutions
   }
   
   stage('Publish to ALM'){
      build job: 'ALMPublisher', parameters: [[$class: 'StringParameterValue', name: 'buildWorkspace', value:
                                               "${WORKSPACE}"], [$class: 'StringParameterValue', name:'resultFilePattern', value: "**/target/surefire-reports/TEST-*.xml"]]
   }
   /* 
   stage('Results') {
      junit '**/target/surefire-reports/TEST-*.xml'
      archive 'target/*.jar'
   }*/
}
