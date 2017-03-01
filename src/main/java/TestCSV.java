import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TestCSV {

    public List<String> ReadScriptIDs(){
        String csvFile = "./src/main/java/test.csv";
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

               // System.out.println("Script ID = " + line);
            }

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




    public static void main(String[] args) {
        List<String> IDs = new ArrayList<String>();
        TestCSV Recommended_Jobs = new TestCSV();
        IDs = Recommended_Jobs.ReadScriptIDs();

        System.out.println("Total Scripts = " + IDs.size());
        for(String line : IDs){
            System.out.println(line);
        }
    }
}

