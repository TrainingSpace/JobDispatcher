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
        sCommand = "cmd /c java -jar jenkins-cli.jar -s \"" + sRootURL + "\"";
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
        sCommand = sCommand + " get-job \"" + jobName + "\" > \"" + destinationXML + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }

    public void CreateJob(String jobName, String sourceXML) {
        sCommand = sCommand + " create-job \"" + jobName + "\" < \"" + sourceXML + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }

    public void DeleteJob(String jobName) {
        sCommand = sCommand + " delete-job \"" + jobName + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }

    public void BuildJob(String jobName) {
        sCommand = sCommand + " build \"" + jobName + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }

    public void DisableJob(String jobName) {
        sCommand = sCommand + " disable-job \"" + jobName + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }

    public void EnableJob(String jobName) {
        sCommand = sCommand + " enable-job \"" + jobName + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }

    public void CreateView(String viewName) {
        String groovyContent = "hudson.model.Hudson.instance.addView(new hudson.model.ListView(\"" + viewName + "\"))";
        System.out.println("Groovy: " + groovyContent);
        CreateGroovyFile(groovyContent, "createView.groovy");
        sCommand = sCommand + " groovy createView.groovy";
        System.out.println(sCommand);
        RunCommand(sCommand);
        DeleteGroovyFile("createView.groovy");
        System.out.println("Done.");
    }

    public void DeleteView(String viewName) {
        System.out.println("Deleting view "+ viewName +"...");
        sCommand = sCommand + " delete-view \"" + viewName + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }

    public void CreateFolder(String folderName) {
        System.out.println("Creating folder "+ folderName +"...");
        sCommand = sCommand + " create-job \"" + folderName + "\" < empty_folder.xml";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }

    public void DeleteFolder(String folderName) {
        System.out.println("Deleting folder "+ folderName +"...");
        sCommand = sCommand + " delete-job \"" + folderName + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
        System.out.println("Done.");
    }


    public static void main(String args[]) {
        JenkinsCLIWrapper jenks = new JenkinsCLIWrapper("http://fefezinha.com:8080/jenkins");
        //jenks.CreateJob("BLABLABLA","teste.xml");
        //jenks.BuildJob("TesteCLI");
        //jenks.DisableJob("TesteCLI");
        //jenks.EnableJob("TesteCLI");
        //jenks.GetJob("TesteCLI","testeCLI.xml");
        //jenks.DeleteJob("TesteCLI");
        //jenks.CreateView("Oi Fefe");
        //jenks.DeleteView("Oi Fefe");
        jenks.CreateFolder("Oi Fefe folder");
        //jenks.DeleteFolder("Oi Fefe folder");
    }

}

