import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class ManipulateCSV {


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
        String csvFile = "./CSVs/JobIDs.csv";
        BufferedReader br = null;
        String line = "\n";
        String cvsSplitBy = ",";
        List<String> IDs = new ArrayList<String>();

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
        Function to read CSV file from new auto scope and merge it into Job Library
        Sample:

            ****BEFORE****
            * New Auto Scope CSV:
               Script ID    Job Name                        Application     Location    Auto Tool   etc.
               12                                           A               ALM         LeanFT
               14                                           A               GitHub      Selenium

            * Job Library CSV:
               Script ID    Job Name                        Application     Location    Auto Tool   etc.
               11           A_ALM_UFT_Script_11             A               ALM         UFT
               13           A_ALM_Worksoft_Script_13        A               ALM         Worksoft
               15           A_GitHub_Selenium_Script_15     A               GitHub      Selenium




            ****AFTER****
            * New Auto Scope CSV: <empty>
               Script ID    Job Name                        Application     Location    Auto Tool   etc.

            * Job Library CSV:
               Script ID    Job Name                        Application     Location    Auto Tool   etc.
               11           A_ALM_UFT_Script_11             A               ALM         UFT
               12                                           A               ALM         LeanFT
               13           A_ALM_Worksoft_Script_13        A               ALM         Worksoft
               14                                           A               GitHub      Selenium
               15           A_GitHub_Selenium_Script_15     A               GitHub      Selenium

        Author: Fernanda Menks - Mar 1, 2017
     */
    public List<String> Merge_New_Auto_Scope_into_Library() {
        List<Path> paths = Arrays.asList(Paths.get("./CSVs/Application_A/Job_Library.csv"), Paths.get("./CSVs/Application_A/New_Auto_Scope.csv"));
        List<String> mergedLines = null;
        try {
            mergedLines = getMergedLines(paths);
            Path target = Paths.get("./CSVs/Application_A/Job_Library.csv");
            Files.write(target, mergedLines, Charset.forName("UTF-8"));
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
        Main function to list all CSV files read in console output.
        This method isn't used in the real job creation. This is just for debug purposes.

        Author: Fernanda Menks - Feb 20, 2017
     */
    public static void main(String[] args) {
        List<String> tempCSVcontent = new ArrayList<String>();
        ManipulateCSV tempCSVfile = new ManipulateCSV();

        //1. List input file with recommended job IDs
        tempCSVcontent = tempCSVfile.ReadScriptIDs();
        System.out.println("Total Scripts in recommendation list = " + tempCSVcontent.size());
        for(String line : tempCSVcontent){
            System.out.println(line);
        }

        //2. Merge new auto scope into pre-set job library
        tempCSVcontent = tempCSVfile.Merge_New_Auto_Scope_into_Library();
        System.out.println("\n\nTotal jobs in library = " + tempCSVcontent.size());
        for(String line : tempCSVcontent){
            System.out.println(line);
        }


    }
}

