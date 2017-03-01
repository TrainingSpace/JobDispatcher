/**
 * Created by Fernanda.Menks on 2/26/2017.
 *
 * Sample Gradle execution string: rest  -Dpattern=jobs/JobDispatcher.groovy -DbaseUrl=http://fefezinha.com:8080/jenkins/ -Dusername=juliano -Dpassword=msdje123
 */

// **** SETUP JENKINS STRUCTURE ****
//Create list view and folder structure
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







// **** LOAD JOB LIBRARY ****
//1. Make sure that each recommended script has a job in the library
// 1.1. Read all recommended jobs
    List<String> IDs = new ArrayList<String>()
    ManipulateCSV Recommended_Jobs = new ManipulateCSV()
    IDs = Recommended_Jobs.ReadScriptIDs()

// 1.2. Create pending jobs
// For each script ID in the recommendation list...
//     If there is no job... (2nd column in CSV is empty)
//          1.2.1. Create job based on the location (4th column in CSV either ALM or GitHub)
//          case ALM:
//          case GitHub:
//          1.2.2. Add job name into job library (2nd column in CSV)

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






//**** JOB DISPATCHER ****
// 1.3. Create pipeline in execution folder
// 1.4. Execute pipeline (job dispatcher)
// 1.5. Generate result in CSV file
    IDs = Recommended_Jobs.Export_Execution_Results()