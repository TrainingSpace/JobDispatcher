/**
 * Created by Fernanda.Menks on 2/26/2017.
 *
 * Sample Gradle execution string: rest  -Dpattern=jobs/TemplateJob.groovy -DbaseUrl=http://fefezinha.com:8080/jenkins/ -Dusername=juliano -Dpassword=msdje123
 * Documentation for job DSL plugin: https://jenkinsci.github.io/job-dsl-plugin/#path/job
 */

// **** SETUP JENKINS STRUCTURE ****
//Setup global parameter for ALM
ALM_Server_Name = 'PRD'

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
// 1.1. Merge new jobs into library
    JobDispatcherClass Recommended_Jobs = new JobDispatcherClass()
    Recommended_Jobs.Merge_New_Auto_Scope_into_Library()

// 1.2. Read all recommended jobs
    IDs = Recommended_Jobs.ReadScriptIDs()
    JobLibrary = Recommended_Jobs.ReadJobLibrary(basePath_A) //create jobs for 1st app only. Update this in the future

// 1.3. Create pending jobs
    // For each job in the job library...
    for(int i=0; i< JobLibrary.size(); i++){

        tempJob = JobLibrary.get(i)

        //     If there is no job yet...
        if (tempJob.Job_Name.isEmpty()) {
            //a) Create job name
            tempJob.Job_Name = "$tempJob.Application/$folderLibrary/$tempJob.Application" + "_$tempJob.Location" + "_$tempJob.Auto_Tool" + "_Script_" + tempJob.Script_ID

            //b) Add job name into job library (2nd column in CSV)

            //c) Create job based on the location (TODAY it's either ALM or GitHub)
            switch (tempJob.Location){
            //   case ALM:
                case "ALM":
                    Create_ALM_Job(tempJob)
                    //job(tempJob.Job_Name) {
                    //}
                    break

            //   case GitHub:
                case "GitHub":
                    mavenJob(tempJob.Job_Name) {
                        scm {
                            //github('TrainingSpace/Training_BDD', 'master')
                            github("$tempJob.GitHub_Repository_URL", 'master')
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
                    break
            }// end of switch

        }// end of condition if job name is empty
    }// end of loop for all jobs in library

/*
    // For each script ID in the recommendation list...
    for(String line : IDs){
        job("$basePath_A/$folderLibrary/script " + line){

        }
    }

*/








//**** JOB DISPATCHER ****
// 2. Create pipeline to dispatch recommended jobs
// 2.1. Create pipeline in execution folder
// 2.2. Execute pipeline (job dispatcher)

// 3. Export execution result to be analyzed by Recommendation engine
//    Generate result in CSV file
    IDs = Recommended_Jobs.Export_Execution_Results()