/**
 * Created by Juliano on 3/15/2017.
 */

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.dom4j.Document;
import org.dom4j.Element;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class JenkinsXMLWrapper {

    public static class XMLRoot {
        List<JenkinsJob> jobs = new LinkedList<>();

        public XMLRoot(String sourceURL) {
            try {
                URL rootURL = new URL(sourceURL);
                Document dom =  new SAXReader().read(rootURL);
                for( Element job : (List<Element>)dom.getRootElement().elements("job")) {
                    JenkinsJob jenkinsJob = new JenkinsJob(job.elementText("name"), job.elementText("url"), job.elementText("name"));
                    jobs.add(jenkinsJob);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }

        private class JenkinsJob {
            String name;
            String url;
            String color;
            public JenkinsJob(String jobName, String jobURL, String jobColor) {
                name = jobName;
                url = jobURL;
                color = jobColor;
            }
        }

        public void ListAllJobs() {
            for (int i = 0; i < jobs.size(); i++) {
                System.out.println(String.format("Name: %s\tURL: %s\tStatus: %s", jobs.get(i).name, jobs.get(i).url, jobs.get(i).color));
            }
        }

    }




    public static void main(String[] args) throws Exception {
        // every Hudson model object exposes the .../api/xml, but in this example
        // we'll just take the root object as an example
        XMLRoot root;
        root = new XMLRoot("http://localhost:8080/jenkins/api/xml");
        // scan through the job list and print its data
        root.ListAllJobs();
    }
}

