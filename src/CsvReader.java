import java.io.FileReader;
import java.util.List;

import com.opencsv.*;

public class CsvReader {

    public List<String[]> read(String filename) throws Exception {
        try {
            FileReader filereader = new FileReader(filename);
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                                      .withCSVParser(parser)
                                      .build();
            List<String[]> allData = csvReader.readAll();

            return allData;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
