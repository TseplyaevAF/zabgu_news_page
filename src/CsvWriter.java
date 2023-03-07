import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CsvWriter {
    public void write(ArrayList<String[]> dataLines, String filename) throws IOException {
        File csvOutputFile = new File(filename);
        try (FileWriter fw = new FileWriter(csvOutputFile, true)) {
            for (String[] data : dataLines) {
                for (int i = 0; i < data.length; i++) {
                    fw.write(data[i] + ";");
                }
                fw.write("\n");
            }
            fw.close();
        }
    }
}
