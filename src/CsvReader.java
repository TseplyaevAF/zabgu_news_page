import java.io.FileReader;
import java.util.List;

import com.opencsv.*;

/**
 * Класс для чтения csv-файла с данными о новостях 
 * и формирования списка из них
 */
public class CsvReader {

    /**
     * Метод чтения данных из csv-файла
     * @param filename - название файла с данными
     * @return возвращает список новостей из строк
     * @throws Exception
     */
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
