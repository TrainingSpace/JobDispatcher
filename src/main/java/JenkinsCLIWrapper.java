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
    }

    public void CreateJob(String jobName, String sourceXML) {
        sCommand = sCommand + " create-job \"" + jobName + "\" < \"" + sourceXML + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
    }

    public void DeleteJob(String jobName) {
        sCommand = sCommand + " delete-job \"" + jobName + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
    }

    public void BuildJob(String jobName) {
        sCommand = sCommand + " build \"" + jobName + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
    }

    public void DisableJob(String jobName) {
        sCommand = sCommand + " disable-job \"" + jobName + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
    }

    public void EnableJob(String jobName) {
        sCommand = sCommand + " enable-job \"" + jobName + "\"";
        System.out.println(sCommand);
        RunCommand(sCommand);
    }

    public static void main(String args[]) {
        JenkinsCLIWrapper jenks = new JenkinsCLIWrapper("http://fefezinha.com:8080/jenkins");
        //jenks.CreateJob("TesteCLI","teste.xml");
        //jenks.BuildJob("TesteCLI");
        //jenks.DisableJob("TesteCLI");
        //jenks.EnableJob("TesteCLI");
        //jenks.GetJob("TesteCLI","testeCLI.xml");
        //jenks.DeleteJob("TesteCLI");
    }

}

