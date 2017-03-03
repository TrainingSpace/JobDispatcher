/**
 * Created by Juliano on 3/2/2017.
 */

public class JobContainer{

    private int Script_ID;
    String Job_Name;
    String Application;
    String Location;
    String Auto_Tool;
    String ALM_Domain;
    String ALM_Project;
    String ALM_Execution_Path;
    String GitHub_Feature;
    String GitHub_Repository_URL;

    public JobContainer(int pScript_ID, String pJob_name, String pApplication, String pLocation, String pAuto_Tool
            , String pALM_Domain, String pALM_Project, String pALM_Execution_Path, String pGitHub_Feature
            , String pGitHub_Repository_URL){
        Script_ID = pScript_ID;
        Job_Name = pJob_name;
        Application = pApplication;
        Location = pLocation;
        Auto_Tool = pAuto_Tool;
        ALM_Domain = pALM_Domain;
        ALM_Project = pALM_Project;
        ALM_Execution_Path = pALM_Execution_Path;
        GitHub_Feature = pGitHub_Feature;
        GitHub_Repository_URL = pGitHub_Repository_URL;
    }
}
