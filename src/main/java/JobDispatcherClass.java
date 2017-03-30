import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;


public class JobDispatcherClass {

    //***** GLOBAL VARIABLES ****
    public static int iTotalJobIDs;    // Total job IDs in the recommendation file
    public static int iExecutionRunID; // ID of the execution run currently in place
    public static List<String> IDs = new ArrayList<String>(); // List of job IDs in the recommendation file
    public static List<JobContainer> JobLibrary = new ArrayList<>(); // List of jobs listed in the library for a particular application
    public static String Jenkins_URL = "http://jenkins-tcoe-qa.disney.com/"; //http://localhost:8080/";
    public static String Application_Name = "Application_A"; // initialized with sample application

    //ALM specific config info
    public static String ALM_Server_Name = "PRD"; // ALM server name listed in Jenkins server configuration
    public static String ALM_Username    = "coelf003";    // ALM user who can connect into ALM project where the job is located
    //public static String ALM_Password    = "{AQAAABAAAAAQ6zNBkrfEAu5SrK6+XEp/BbzVuWj5myeHT3J/b1NkoZg=}";    // ALM user password to connect into ALM project to execute auto scripts
    public static String ALM_Password    = "{AQAAABAAAAAQW/sm7kSd0hHws79tJMstZ7mV0gTpbW+1ViSNSLszIPo=}";    // ALM DEV



    /*
        Function to read CSV file with single column listing script IDs.
        The 1st row in the CSV file is IGNORED as it corresponds to the header.
        Sample:
                Script ID
                11
                12
                13
                15

        Author: Fernanda Menks - Feb 20, 2017
     */
    public List<String> ReadScriptIDs(){
        String csvFile = "./CSVs/"+Application_Name+"/JobIDs.csv";
        BufferedReader br = null;
        String line = "\n";
        String cvsSplitBy = ",";

        try {
            br = new BufferedReader(new FileReader(csvFile));
            // Ignore header line from CSV file
            br.readLine();

            // Add all script IDs into array 'ScriptIDs'
            while ((line = br.readLine()) != null) {

                String [] temp = line.split(cvsSplitBy);
                IDs.add(temp[0]);

                // In case need to display IDs while reading them...
                //System.out.println("Script ID = " + line);
            }
            // In case need to confirm that array was populated...
            //System.out.println("Total Scripts = " + IDs.size());

            //Populate global variable for total job IDs in the recommendation scope
            iTotalJobIDs = IDs.size();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return IDs;
    }



    /*
    Function to read all jobs from existent library in a particular application folder and populate array of Jobs
    Sample:   Job Library
       Script ID    Job Name                                    Application     Location    Auto Tool   etc.
       11           Application_A_Script_11_ALM_UFT             Application_A   ALM         UFT
       13           Application_A_Script_13_ALM_Worksoft        Application_A   ALM         Worksoft
       15           Application_A_Script_15_GitHub_Selenium     Application_A   GitHub      Selenium

    Author: Fernanda Menks - Mar 2, 2017
 */
    public List<JobContainer> ReadJobLibrary(String ApplicationFolder){
        String csvFile = "./CSVs/"+ ApplicationFolder +"/Job_Library.csv";
        BufferedReader br = null;
        String line = "\n";
        String cvsSplitBy = ",";
        JobContainer tempJob;
        int temp_Script_ID;
        String temp_Job_Name;
        String temp_Application;
        String temp_Location;
        String temp_Auto_Tool;
        String temp_ALM_Domain;
        String temp_ALM_Project;
        String temp_ALM_Execution_Path;
        String temp_GitHub_Feature;
        String temp_GitHub_Repository_URL;

        try {
            br = new BufferedReader(new FileReader(csvFile));
            // Ignore header line from CSV file
            br.readLine();

            // Add all script IDs into array 'ScriptIDs'
            while ((line = br.readLine()) != null) {

                String [] temp = line.split(cvsSplitBy);
                temp_Script_ID = Integer.parseInt(temp[0]);
                temp_Job_Name = temp[1];
                temp_Application = temp[2];
                temp_Location = temp[3];
                temp_Auto_Tool = temp[4];
                temp_ALM_Domain = temp[5];
                temp_ALM_Project = temp[6];
                temp_ALM_Execution_Path = temp[7];
                if (temp.length > 8){
                    temp_GitHub_Feature = temp[8];
                    temp_GitHub_Repository_URL = temp[9];
                }else{
                    temp_GitHub_Feature = "";
                    temp_GitHub_Repository_URL = "";
                }
                tempJob = new JobContainer(temp_Script_ID, temp_Job_Name, temp_Application, temp_Location, temp_Auto_Tool
                             , temp_ALM_Domain, temp_ALM_Project, temp_ALM_Execution_Path, temp_GitHub_Feature
                            , temp_GitHub_Repository_URL);

                JobLibrary.add(tempJob);

            }//end of loop

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return JobLibrary;
    }



    /*
        Function to return the Job object from JobLibrary list based on the script ID
        Author: Fernanda Menks, March 2, 2017
     */
/*public Job getJob(int pScript_ID){
        Job tempJob = new Job();
        for(int i=0; i<iTotalJobIDs; i++){
            tempJob = JobLibrary.get(i);
            if (tempJob.Script_ID = pScript_ID){
               break;
            }
        }
        return tempJob;
    }
*/

    /*
        Function to read CSV file from new auto scope and merge it into Job Library
        Sample:

            ****BEFORE****
            * New Auto Scope CSV:
               Script ID    Job Name                                    Application     Location    Auto Tool   etc.
               12                                                       Application_A   ALM         LeanFT
               14                                                       Application_A   GitHub      Selenium

            * Job Library CSV:
               Script ID    Job Name                                    Application     Location    Auto Tool   etc.
               11           Application_A_Script_11_ALM_UFT             Application_A   ALM         UFT
               13           Application_A_Script_13_ALM_Worksoft        Application_A   ALM         Worksoft
               15           Application_A_Script_15_GitHub_Selenium     Application_A   GitHub      Selenium




            ****AFTER****
            * New Auto Scope CSV: <empty>
               Script ID    Job Name                        Application     Location    Auto Tool   etc.

            * Job Library CSV:
               Script ID    Job Name                                    Application     Location    Auto Tool   etc.
               11           Application_A_Script_11_ALM_UFT             Application_A   ALM         UFT
               13           Application_A_Script_13_ALM_Worksoft        Application_A   ALM         Worksoft
               15           Application_A_Script_15_GitHub_Selenium     Application_A   GitHub      Selenium
               12                                                       Application_A   ALM         LeanFT
               14                                                       Application_A   GitHub      Selenium

        Author: Fernanda Menks - Mar 1, 2017
     */
    public List<String> Merge_New_Auto_Scope_into_Library(String ApplicationFolder) {
        List<Path> paths = Arrays.asList(Paths.get("./CSVs/"+ApplicationFolder+"/Job_Library.csv"), Paths.get("./CSVs/"+ApplicationFolder+"/New_Auto_Scope.csv"));
        List<String> mergedLines = null;

        try {
            // 1. If the new auto scope file doesn't exist or is empty, we can skip the entire merge activity ----------->>>> PENDING TO IMPLEMENT THIS!

            // 2. Merge
            mergedLines = getMergedLines(paths);
            Path target = Paths.get("./CSVs/"+ApplicationFolder+"/Job_Library.csv");
            Files.write(target, mergedLines, Charset.forName("UTF-8"));

            // 3. Clean new auto scope file
            FileWriter writer = new FileWriter("./CSVs/"+ApplicationFolder+"/New_Auto_Scope.csv", false);
            writer.write(""); // input empty data
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return mergedLines;
    }

    private static List<String> getMergedLines(List<Path> paths) throws IOException {
        List<String> mergedLines = null;
        try {
            mergedLines = new ArrayList<>();
            for (Path p : paths) {
                List<String> lines = Files.readAllLines(p, Charset.forName("UTF-8"));
                if (!lines.isEmpty()) {
                    if (mergedLines.isEmpty()) {
                        mergedLines.add(lines.get(0)); //add header only once
                    }
                    mergedLines.addAll(lines.subList(1, lines.size()));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return mergedLines;
    }





    /*
        Function to generate Execution Result CSV file.
        Sample:
            Script ID   Execution Status
            11          Passed
            12          Failed
            13          Passed

        Author: Fernanda Menks - Mar 1, 2017
        Update: Changed name of the CSV file to lower case as per Tharani Sivanandam  | Fernanda Menks - Mar 29, 2017
     */
    public void Export_Execution_Results() throws IOException {
        String csvFile = "./CSVs/"+Application_Name+"/execution_result.csv";
        FileWriter writer = new FileWriter(csvFile);
        String ID = "";
        String Exec_Status = "";

        //Include header
        writeLine(writer, Arrays.asList("Script ID", "Execution Status"));

        //Include job ID + status
        for(int i=0; i<iTotalJobIDs; i++){
            ID = IDs.get(i);
            Exec_Status = "Passed";      //<<<<<<<<<<<<<<<<<<<<<<<<<<<--------------- Call method to parse json file
            writeLine(writer, Arrays.asList(ID, Exec_Status));
        }

        writer.flush();
        writer.close();
    }


    public static void writeLine(Writer w, List<String> values) throws IOException {

        boolean first = true;
        char separators = ',';
        char customQuote = ' ';


        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());
    }
    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }


    /*
    Method to create ALM job based on template Jenkins config XML for ALM parameters.
    Steps:
        1. Define job name
        2. Save job name in the Job_Library CSV file
        3. Create XML file under .build/application_name folder
        4. Populate the XML with parameters from job
        5. Create ALM job in Jenkins
    Author: Fernanda Menks - March 16, 2017
 */
    public void Create_ALM_Job(JobContainer tempJob, int iRow_JobLibrary) throws IOException {

        if ( (tempJob.Job_Name.isEmpty()) && (tempJob.Location.contentEquals("ALM"))) {
            System.out.println("Creating ALM job for scrip #" + tempJob.Script_ID);

            //1. Define job name
            tempJob.Job_Name = tempJob.Application + "_Script_" + tempJob.Script_ID + "_" + tempJob.Location + "_" + tempJob.Auto_Tool;

            //2. Save job name in the Job_Library CSV file
            Update_CSV(tempJob.Application,tempJob.Job_Name,1,iRow_JobLibrary+1); // ----->>>>> Hidding this step now bc need to fix method that saves everything as string

            //3. Create XML file under for the application job
            String XML_Path = "./build/" + tempJob.Job_Name + ".xml";
            FileWriter writer = new FileWriter(XML_Path);

            //4. Populate the XML with parameters from job
            writer.write("<?xml version='1.0' encoding='UTF-8'?>\n" +
                    "<project>\n" +
                    "  <actions/>\n" +
                    "  <description>" + "Pre-set job item for ALM script ID " + tempJob.Script_ID + " | Auto tool = " + tempJob.Auto_Tool + "</description>\n" +
                    "  <keepDependencies>false</keepDependencies>\n" +
                    "<properties/>\n" +
                    "  <scm class=\"hudson.scm.NullSCM\"/>\n" +
                    "  <canRoam>true</canRoam>\n" +
                    "  <disabled>false</disabled>\n" +
                    "  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>\n" +
                    "  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>\n" +
                    "  <triggers/>\n" +
                    "  <concurrentBuild>false</concurrentBuild>\n" +
                    "  <builders>\n" +
                    "    <com.hp.application.automation.tools.run.RunFromAlmBuilder plugin=\"hp-application-automation-tools-plugin@5.1\">\n" +
                    "      <runFromAlmModel>\n" +
                    "        <almServerName>" + ALM_Server_Name + "</almServerName>\n" +
                    "        <almUserName>" + ALM_Username + "</almUserName>\n" +
                    "        <almPassword>" + ALM_Password + "</almPassword>\n" +
                    "        <almDomain>" + tempJob.ALM_Domain + "</almDomain>\n" +
                    "        <almProject>" + tempJob.ALM_Project + "</almProject>\n" +
                    "        <almTestSets>" + tempJob.ALM_Execution_Path + "</almTestSets>\n" +
                    "        <almTimeout></almTimeout>\n" +
                    "        <almRunMode>RUN_LOCAL</almRunMode>\n" +
                    "        <almRunHost></almRunHost>\n" +
                    "      </runFromAlmModel>\n" +
                    "      <ResultFilename>ApiResults.xml</ResultFilename>\n" +
                    "      <ParamFileName>ApiRun.txt</ParamFileName>\n" +
                    "    </com.hp.application.automation.tools.run.RunFromAlmBuilder>\n" +
                    "  </builders>\n" +
                    "  <publishers>\n" +
                    "    <com.hp.application.automation.tools.results.RunResultRecorder plugin=\"hp-application-automation-tools-plugin@5.1\">\n" +
                    "      <__resultsPublisherModel>\n" +
                    "        <archiveTestResultsMode>ALWAYS_ARCHIVE_TEST_REPORT</archiveTestResultsMode>\n" +
                    "      </__resultsPublisherModel>\n" +
                    "    </com.hp.application.automation.tools.results.RunResultRecorder>\n" +
                    "  </publishers>\n" +
                    "  <buildWrappers>\n" +
                    "    <hudson.plugins.build__timeout.BuildTimeoutWrapper plugin=\"build-timeout@1.18\">\n" +
                    "      <strategy class=\"hudson.plugins.build_timeout.impl.LikelyStuckTimeOutStrategy\"/>\n" +
                    "      <operationList>\n" +
                    "        <hudson.plugins.build__timeout.operations.AbortOperation/>\n" +
                    "      </operationList>\n" +
                    "    </hudson.plugins.build__timeout.BuildTimeoutWrapper>\n" +
                    "  </buildWrappers>\n" +
                    "</project>");

            writer.flush();
            writer.close();

            //5. Create ALM job in Jenkins
            JenkinsCLIWrapper jenks = new JenkinsCLIWrapper(Jenkins_URL);
            jenks.CreateJob(tempJob.Job_Name,XML_Path);
            jenks.BuildJob("MoveJobIntoFolder","APPLICATION_NAME",tempJob.Application, "JOB_NAME",tempJob.Job_Name);

        }// end condition if job doesn't exist and is ALM
    }




    /*
        Method to create GitHub job based on template Jenkins config XML for ALM parameters.
        Steps:
            1. Define job name
            2. Save job name in the Job_Library CSV file
            3. Create XML file under .build/application_name folder
            4. Populate the XML with parameters from job
            5. Create GitHub job in Jenkins
        Author: Fernanda Menks - March 16, 2017
     */
    public void Create_GitHub_Job(JobContainer tempJob, int iRow_JobLibrary) throws IOException {
        if ( (tempJob.Job_Name.isEmpty()) && (tempJob.Location.contentEquals("GitHub"))) {
            System.out.println("Creating GitHub job for scrip #" + tempJob.Script_ID);

            //1. Define job name
            tempJob.Job_Name = tempJob.Application + "_Script_" + tempJob.Script_ID + "_" + tempJob.Location + "_" + tempJob.Auto_Tool;

            //2. Save job name in the Job_Library CSV file
            Update_CSV(tempJob.Application,tempJob.Job_Name,1,iRow_JobLibrary+1);  //----->>>>> Hidding this step now bc need to fix method that saves everything as string

            //3. Create XML file under for the application job
            String XML_Path = "./build/" + tempJob.Job_Name + ".xml";
            FileWriter writer = new FileWriter(XML_Path);

            //4. Populate the XML with parameters from job
            writer.write("<?xml version='1.0' encoding='UTF-8'?>\n"+
                        "<maven2-moduleset plugin=\"maven-plugin@2.13\">\n"+
                        "  <actions/>\n"+
                        "  <description>"+ "Pre-set job item for GitHub script ID " + tempJob.Script_ID + " | Auto tool = " + tempJob.Auto_Tool +"</description>\n"+
                        "  <keepDependencies>false</keepDependencies>\n"+
                        "  <properties>\n"+
                        "    <com.coravy.hudson.plugins.github.GithubProjectProperty plugin=\"github@1.19.1\">\n"+
                        "      <projectUrl>"+tempJob.GitHub_Repository_URL+"</projectUrl>\n"+
                        "      <displayName></displayName>\n"+
                        "    </com.coravy.hudson.plugins.github.GithubProjectProperty>\n"+
                        "    <hudson.model.ParametersDefinitionProperty>\n"+
                        "      <parameterDefinitions>\n"+
                        "        <hudson.model.StringParameterDefinition>\n"+
                        "          <name>FEATURE_NAME</name>\n"+
                        "          <description></description>\n"+
                        "          <defaultValue>&quot;&apos;"+tempJob.GitHub_Feature+"&apos;&quot;</defaultValue>\n"+
                        "        </hudson.model.StringParameterDefinition>\n"+
                        "      </parameterDefinitions>\n"+
                        "    </hudson.model.ParametersDefinitionProperty>\n"+
                        "  </properties>\n"+
                        "  <scm class=\"hudson.plugins.git.GitSCM\" plugin=\"git@2.4.4\">\n"+
                        "    <configVersion>2</configVersion>\n"+
                        "    <userRemoteConfigs>\n"+
                        "      <hudson.plugins.git.UserRemoteConfig>\n"+
                        "        <url>"+tempJob.GitHub_Repository_URL+".git</url>\n"+
                        "      </hudson.plugins.git.UserRemoteConfig>\n"+
                        "    </userRemoteConfigs>\n"+
                        "    <branches>\n"+
                        "      <hudson.plugins.git.BranchSpec>\n"+
                        "        <name>*/master</name>\n"+
                        "      </hudson.plugins.git.BranchSpec>\n"+
                        "    </branches>\n"+
                        "    <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>\n"+
                        "    <submoduleCfg class=\"list\"/>\n"+
                        "    <extensions/>\n"+
                        "  </scm>\n"+
                        "  <assignedNode>10.0.0.1</assignedNode>\n"+
                        "  <canRoam>false</canRoam>\n"+
                        "  <disabled>false</disabled>\n"+
                        "  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>\n"+
                        "  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>\n"+
                        "  <triggers/>\n"+
                        "  <concurrentBuild>false</concurrentBuild>\n"+
                        "  <rootModule>\n"+
                        "    <groupId>com.accenture.cucumber</groupId>\n"+
                        "    <artifactId>Training_BDD</artifactId>\n"+
                        "  </rootModule>\n"+
                        "  <goals>clean verify -Dcucumber.options=$FEATURE_NAME</goals>\n"+
                        "  <aggregatorStyleBuild>true</aggregatorStyleBuild>\n"+
                        "  <incrementalBuild>false</incrementalBuild>\n"+
                        "  <ignoreUpstremChanges>true</ignoreUpstremChanges>\n"+
                        "  <ignoreUnsuccessfulUpstreams>false</ignoreUnsuccessfulUpstreams>\n"+
                        "  <archivingDisabled>false</archivingDisabled>\n"+
                        "  <siteArchivingDisabled>false</siteArchivingDisabled>\n"+
                        "  <fingerprintingDisabled>false</fingerprintingDisabled>\n"+
                        "  <resolveDependencies>false</resolveDependencies>\n"+
                        "  <processPlugins>false</processPlugins>\n"+
                        "  <mavenValidationLevel>-1</mavenValidationLevel>\n"+
                        "  <runHeadless>false</runHeadless>\n"+
                        "  <disableTriggerDownstreamProjects>false</disableTriggerDownstreamProjects>\n"+
                        "  <blockTriggerWhenBuilding>true</blockTriggerWhenBuilding>\n"+
                        "  <settings class=\"jenkins.mvn.DefaultSettingsProvider\"/>\n"+
                        "  <globalSettings class=\"jenkins.mvn.DefaultGlobalSettingsProvider\"/>\n"+
                        "  <reporters/>\n"+
                        "  <publishers>\n"+
                        "    <htmlpublisher.HtmlPublisher plugin=\"htmlpublisher@1.11\">\n"+
                        "      <reportTargets>\n"+
                        "        <htmlpublisher.HtmlPublisherTarget>\n"+
                        "          <reportName>BDD report</reportName>\n"+
                        "          <reportDir>\\"+"target\\cucumber-htmlreport</reportDir>\n"+
                        "          <reportFiles>index.html</reportFiles>\n"+
                        "          <alwaysLinkToLastBuild>false</alwaysLinkToLastBuild>\n"+
                        "          <keepAll>false</keepAll>\n"+
                        "          <allowMissing>false</allowMissing>\n"+
                        "        </htmlpublisher.HtmlPublisherTarget>\n"+
                        "      </reportTargets>\n"+
                        "    </htmlpublisher.HtmlPublisher>\n"+
                        "    <net.masterthought.jenkins.CucumberReportPublisher plugin=\"cucumber-reports@2.6.3\">\n"+
                        "      <jsonReportDirectory>\\target</jsonReportDirectory>\n"+
                        "      <jenkinsBasePath></jenkinsBasePath>\n"+
                        "      <fileIncludePattern></fileIncludePattern>\n"+
                        "      <fileExcludePattern></fileExcludePattern>\n"+
                        "      <skippedFails>false</skippedFails>\n"+
                        "      <pendingFails>false</pendingFails>\n"+
                        "      <undefinedFails>false</undefinedFails>\n"+
                        "      <missingFails>false</missingFails>\n"+
                        "      <ignoreFailedTests>false</ignoreFailedTests>\n"+
                        "      <parallelTesting>false</parallelTesting>\n"+
                        "    </net.masterthought.jenkins.CucumberReportPublisher>\n"+
                        "  </publishers>\n"+
                        "  <buildWrappers/>\n"+
                        "  <prebuilders/>\n"+
                        "  <postbuilders/>\n"+
                        "  <runPostStepsIfResult>\n"+
                        "    <name>FAILURE</name>\n"+
                        "    <ordinal>2</ordinal>\n"+
                        "    <color>RED</color>\n"+
                        "    <completeBuild>true</completeBuild>\n"+
                        "  </runPostStepsIfResult>\n"+
                        "</maven2-moduleset>");

            writer.flush();
            writer.close();

            //5. Create ALM job in Jenkins
            JenkinsCLIWrapper jenks = new JenkinsCLIWrapper(Jenkins_URL);
            jenks.CreateJob(tempJob.Job_Name,XML_Path);
            jenks.BuildJob("MoveJobIntoFolder","APPLICATION_NAME",tempJob.Application, "JOB_NAME",tempJob.Job_Name);

        }// end condition if job doesn't exist and is GitHub
    }



    /*
        Function to create Jenkins structure for new application (original version) + setup job that allows move jobs into folders (enhancement #1)
        Author: Fernanda Menks - Mar 14, 2017
        Enhancement: Fernanda Menks - Mar 20, 2017
    */
    public static void InitiateJenkinsStructure(){
        JenkinsCLIWrapper jenks = new JenkinsCLIWrapper(Jenkins_URL);
        jenks.CreateJob("SetupJenkinsStructure","./Template XML/Template_SetupJenkinsStructure.xml");
        jenks.CreateJob("MoveJobIntoFolder","./Template XML/Template_MoveJobIntoFolder.xml");
    }

    /*
        Function to create Jenkins structure for new application
        Author: Fernanda Menks - Mar 14, 2017
     */
    public void addApplicationInJenkins(String AppName){
        JenkinsCLIWrapper jenks = new JenkinsCLIWrapper(Jenkins_URL);
        jenks.BuildJob("SetupJenkinsStructure", "Application_Name", AppName);
        jenks.AddJobToView(AppName, "TCoE Job Dispatcher");
    }

/*
    Function to update CSV file with the Job name
        Sample:

            ****BEFORE****
            * Job Library CSV:
               Script ID    Job Name                        Application     Location    Auto Tool   etc.
               11                                           A               ALM         UFT
               13                                           A               ALM         Worksoft
               15                                           A               GitHub      Selenium

            ****AFTER****
               Script ID    Job Name                        Application     Location    Auto Tool   etc.
               11           A_ALM_UFT_Script_11             A               ALM         UFT
               13           A_ALM_Worksoft_Script_13        A               ALM         Worksoft
               15           A_GitHub_Selenium_Script_15     A               GitHub      Selenium

            Can be used as:
                Update_CSV("sampleApplication","SampleData", 1, 1)
        Author: Shahzad Rizvi - Mar 13, 2017
     */


    public static String Update_CSV(String sApplicationFolder, String sData, Integer iColumn, Integer iRow) throws IOException{
        String csvFile = "./CSVs/"+ sApplicationFolder +"/Job_Library.csv";
        //String csvFile = "./Job_Library.csv";
        //String output = "./Job_Library_updated.csv";
        String output = "./CSVs/"+ sApplicationFolder +"/Job_Library.csv";
        List<String> updatedLines = null;
        String[] sLine;
        CSVReader reader = new CSVReader(new FileReader(csvFile));
        List<String[]> csvBody = reader.readAll();

        //Update the Row and Column wit the data
        csvBody.get(iRow)[iColumn] = sData;
        reader.close();

        //write the updated CSV out
        CSVWriter writer = new CSVWriter(new FileWriter(output), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
        writer.writeAll(csvBody);
        writer.flush();
        writer.close();

        return output;
    }


    public void PreSetJobLibrary() throws IOException {
        JobContainer tempJob = new JobContainer();

        //1.1. Setup initial Jenkins structure
        InitiateJenkinsStructure();
        //1.2. Add new applications in Jenkins view
        addApplicationInJenkins(Application_Name);
        //1.3. Check if there are new jobs to load into Jobs Library
        Merge_New_Auto_Scope_into_Library(Application_Name);
        ReadJobLibrary(Application_Name);
        //1.4. Create pending jobs in the Library
        for(int i=0; i<JobLibrary.size(); i++) {
            tempJob = JobLibrary.get(i);
            switch (tempJob.Location){
                case "ALM":
                    Create_ALM_Job(tempJob, i);
                    break;

                case "GitHub":
                    Create_GitHub_Job(tempJob, i);
                    break;
            }// end of switch case per location
        }// end of loop for each job in the library
    }

    /*
        Main function to list all CSV files read in console output.
        This method isn't used in the real job creation. This is just for debug purposes.

        Author: Fernanda Menks - Feb 20, 2017
     */
    public static void main(String[] args) throws IOException {
        JobDispatcherClass objTemp = new JobDispatcherClass();

        //1. Pre-set Jobs Library
        Application_Name = "Application_A";
        objTemp.PreSetJobLibrary();

        //2. Crate and execute Pipeline
        objTemp.ReadScriptIDs();

        //3. Generate results CSV file
        objTemp.Export_Execution_Results();

    }
}

