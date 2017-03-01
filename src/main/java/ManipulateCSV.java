import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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
        Main function to list all CSV files read in console output.
        This method isn't used in the real job creation. This is just for debug purposes.

        Author: Fernanda Menks - Feb 20, 2017
     */
    public static void main(String[] args) {
        List<String> tempCSVcontent = new ArrayList<String>();
        ManipulateCSV tempCSVfile = new ManipulateCSV();

        //1. List input file with recommended job IDs
        tempCSVcontent = tempCSVfile.ReadScriptIDs();

        System.out.println("Total Scripts = " + tempCSVcontent.size());
        for(String line : tempCSVcontent){
            System.out.println(line);
        }

        //2. List input file with script IDs pending preset jobs in library

        //3. List preset jobs library
    }
}

