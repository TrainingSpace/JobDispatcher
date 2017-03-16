import java.io.*;

/**
 * Created by Juliano on 3/7/2017.
 * /jnlpJars/jenkins-cli.jar
 */
/**
 * The <code>JenkinsCLIWrapper</code> class wraps the jenkins.cli in an
 * easy to use Java class.
 */
public class JenkinsCLIWrapper {

    private String sRootURL = "";
    private String sCommand = "";

    /**
     * Class constructor
     *
     * @param rootURL Jenkins URL
     */
    public JenkinsCLIWrapper (String rootURL) {
        sRootURL = rootURL;
    }

    /**
     * Runs a shell command
     *
     * @param command Command to be executed
     */
    public void RunCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void CreateGroovyFile(String content, String fileName) {
        try {
            PrintWriter groovyFile = new PrintWriter(fileName);
            groovyFile.println(content);
            groovyFile.close();
            System.out.println("Created file: " + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void DeleteGroovyFile(String fileName) {
        try {
            File groovyFile = new File(fileName);
            groovyFile.delete();
            System.out.println("Deleted file: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a xml template of the provided job
     *
     * @param jobName Name of the source job
     * @param destinationXML (Path and) Name of the generated xml
     */
    public void GetJob(String jobName, String destinationXML) {
        System.out.println("Extracting job " + jobName + " to " + destinationXML + "...");
        sCommand = "cmd /c java -jar jenkins-cli.jar -s \"" + sRootURL + "\"";
        sCommand = sCommand + " get-job \"" + jobName + "\" > \"" + destinationXML + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }

    public void CreateJob(String jobName, String sourceXML) {
        System.out.println("Creating job " + jobName + "...");
        sCommand = "cmd /c java -jar jenkins-cli.jar -s \"" + sRootURL + "\"";
        sCommand = sCommand + " create-job \"" + jobName + "\" < \"" + sourceXML + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }

    public void DeleteJob(String jobName) {
        System.out.println("Deleting job " + jobName + "...");
        sCommand = "cmd /c java -jar jenkins-cli.jar -s \"" + sRootURL + "\"";
        sCommand = sCommand + " delete-job \"" + jobName + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }

    public void BuildJob(String jobName) {
        System.out.println("Building job " + jobName + "...");
        sCommand = "cmd /c java -jar jenkins-cli.jar -s \"" + sRootURL + "\"";
        sCommand = sCommand + " build \"" + jobName + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }

    public void BuildJob(String jobName, String Parameter, String Parameter_value) {
        System.out.println("Building job with parameter " + jobName + "...");
        sCommand = "cmd /c java -jar jenkins-cli.jar -s \"" + sRootURL + "\"";
        sCommand = sCommand + " build \"" + jobName + "\"";
        sCommand = sCommand + " -p \"" + Parameter + "=" + Parameter_value + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }

    public void DisableJob(String jobName) {
        System.out.println("Disabling job " + jobName + "...");
        sCommand = "cmd /c java -jar jenkins-cli.jar -s \"" + sRootURL + "\"";
        sCommand = sCommand + " disable-job \"" + jobName + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }

    public void EnableJob(String jobName) {
        System.out.println("Enabling job " + jobName + "...");
        sCommand = "cmd /c java -jar jenkins-cli.jar -s \"" + sRootURL + "\"";
        sCommand = sCommand + " enable-job \"" + jobName + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }

    public void CreateView(String viewName) {
        System.out.println("Creating view " + viewName + "...");
        String groovyContent = "hudson.model.Hudson.instance.addView(new hudson.model.ListView(\"" + viewName + "\"))";
        System.out.println("Groovy: " + groovyContent);
        CreateGroovyFile(groovyContent, "createView.groovy");
        sCommand = "cmd /c java -jar jenkins-cli.jar -s \"" + sRootURL + "\"";
        sCommand = sCommand + " groovy createView.groovy";
        System.out.println(sCommand);
        RunCommand(sCommand);
        DeleteGroovyFile("createView.groovy");
        System.out.println("Done.");
    }

    public void DeleteView(String viewName) {
        System.out.println("Deleting view " + viewName + "...");
        sCommand = "cmd /c java -jar jenkins-cli.jar -s \"" + sRootURL + "\"";
        sCommand = sCommand + " delete-view \"" + viewName + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }

    public void CreateFolder(String folderName) {
        System.out.println("Creating folder " + folderName + "...");
        sCommand = "cmd /c java -jar jenkins-cli.jar -s \"" + sRootURL + "\"";
        sCommand = sCommand + " create-job \"" + folderName + "\" < \"Template XML/empty_folder.xml\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }

    public void DeleteFolder(String folderName) {
        System.out.println("Deleting folder " + folderName + "...");
        sCommand = "cmd /c java -jar jenkins-cli.jar -s \"" + sRootURL + "\"";
        sCommand = sCommand + " delete-job \"" + folderName + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }


    public void AddJobToView(String jobName, String viewName) {
        System.out.println("Adding job " + jobName +" into view " + viewName + " ...");
        sCommand = "cmd /c java -jar jenkins-cli.jar -s \"" + sRootURL + "\"";
        sCommand = sCommand + " add-job-to-view \"" + viewName + "\" \"" + jobName + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }

    public static void main(String args[]) {
        JenkinsCLIWrapper jenks = new JenkinsCLIWrapper("http://localhost:8080");
        jenks.CreateJob("C:/Program Files (x86)/Jenkins/jobs/Application_A/jobs/Job Library/jobs/XXXXX","./Template XML/Template_Maven_Job.xml");
        //jenks.AddJobToView("BLABLABLA", "TCoE Job Dispatcher");
        //jenks.BuildJob("SetupJenkinsStructure", "Application_Name", "xxxxxx");
        //jenks.DisableJob("TesteCLI");
        //jenks.EnableJob("TesteCLI");
        //jenks.GetJob("TesteCLI","testeCLI.xml");
        //jenks.DeleteJob("TesteCLI");
        //jenks.CreateView("Oi Fefe");
        //jenks.DeleteView("Oi Fefe");
        //jenks.CreateFolder("Oi Fefe folder");
        //jenks.DeleteFolder("Oi Fefe folder");
    }

}

