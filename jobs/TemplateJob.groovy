/**
 * Created by Fernanda.Menks on 2/26/2017.
 *
 * Sample Gradle execution string: rest  -Dpattern=jobs/JobDispatcher.groovy -DbaseUrl=http://fefezinha.com:8080/jenkins/ -Dusername=juliano -Dpassword=msdje123
 */



List<String> IDs = new ArrayList<String>()
ManipulateCSV Recommended_Jobs = new ManipulateCSV()
IDs = Recommended_Jobs.ReadScriptIDs()




String baseView = 'TCoE Job Dispatcher'

String basePath_A = 'Application_A'
String basePath_B = 'Application_B'
String basePath_C = 'Application_C'

String folderLibrary = 'Job Library'
String folderExecution = 'Executions'




//Create folders per application
folder(basePath_A) {
    description 'Scripts to validate Application A'
}
folder("$basePath_A/$folderLibrary"){}
folder("$basePath_A/$folderExecution"){}

folder(basePath_B) {
    description 'Scripts to validate Application B'
}
folder("$basePath_B/$folderLibrary"){}
folder("$basePath_B/$folderExecution"){}

folder(basePath_C) {
    description 'Scripts to validate Application C'
}
folder("$basePath_C/$folderLibrary"){}
folder("$basePath_C/$folderExecution"){}


//For each script ID in the array, create a job that matches the ID number
for(String line : IDs){

    // Create jobs under folders
    mavenJob("$basePath_A/$folderLibrary/App_A_GitHub_Selenium_Script_" + line) {
        scm {
            github('TrainingSpace/Training_BDD', 'master')
        }
        triggers {
            githubPush()
        }
        rootPOM('pom.xml')
        goals('clean verify')
        publishers {
            cucumberReports {
                jsonReportPath('target')
            }
        }
    }
} // end of loop


job("$basePath_A/$folderLibrary/App_A_ALM_UFT_Script_11"){

}
job("$basePath_A/$folderLibrary/App_A_ALM_Worksoft_Script_13"){

}







//Setup view including all folders per application
listView(baseView) {
    description('Job Dispatcher view for all applications in TCoE.'
            +'\n\nEach application structure Includes:'
            +"\n        App > $folderLibrary -- includes ALL jobs for each single script available in this app"
            +"\n        App > $folderExecution -- host executions for each recommendation execution request"
            +'\n\n')

    //Config view
    jobs{
        name("$basePath_A")
        name("$basePath_B")
        name("$basePath_C")
    }
    columns{
        status()
        name()
    }
}