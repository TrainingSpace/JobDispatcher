/**
 * Created by Juliano on 3/2/2017.
 */

public class Job{
    public static int Script_ID;
    public static String Job_Name;
    public static String Application;
    public static String Location;
    public static String Auto_Tool;
    public static String ALM_Domain;
    public static String ALM_Project;
    public static String ALM_Execution_Path;
    public static String GitHub_Feature;
    public static String GitHub_Repository_URL;

    void addJob(int pScript_ID, String pJob_name, String pApplication, String pLocation, String pAuto_Tool
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
