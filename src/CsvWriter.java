import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Класс для записи данных о новостях в csv-файл 
 */
public class CsvWriter {
    /**
     * Метод записи данных о новостях в csv-файл
     * @param dataLines - список новостей из строк
     * @param filename - название файл для записи данных
     * @throws IOException
     */
    public void write(List<String[]> dataLines, String filename) throws IOException {
        File csvOutputFile = new File(filename);
        try (FileWriter fw = new FileWriter(csvOutputFile, true)) {
            for (String[] data : dataLines) {
                for (int i = 0; i < data.length; i++) {
                    fw.write(escapeSpecialCharacters(data[i]) + ";");
                }
                fw.write("\n");
            }
            fw.close();
        }
    }

    /**
     * Метод замены специальных символов, 
     * таких как запятых и кавычек (двойных и одинарных)
     * @param data - строка с данными
     * @return возвращает отформатированную строку
     */
    private String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
