import java.util.ArrayList;
import java.util.List;

public class App {
    static String webSiteUrl = "https://zabgu.ru";
    static String newsPageUrl = "/php/news.php?category=1&page=";
    static String previewsDir = "previews";
    static List<String[]> dataLines = new ArrayList<>();
    static int pageCount = 1;
    static String filename = "data.csv";

    public static void main(String[] args) throws Exception {
        if (args.length == 3) {
            pageCount = Integer.parseInt(args[0]);
            filename = args[1];
            previewsDir = args[2];
        }
        newsParse();
        countMatches();
    }

    public static void newsParse() {
        try {
            NewsParser newsParser = new NewsParser(webSiteUrl, newsPageUrl, previewsDir);
        
            for (int i = 0; i < pageCount; i++) {
                dataLines = newsParser.parseNewsPage(i + 1);

                // очистка консоли
                System.out.print("\033[H\033[2J");

                System.out.print("Спарсено " + (i + 1) + " из " + pageCount + " страниц");
            }
            
            CsvWriter csvWriter = new CsvWriter();
            csvWriter.write(dataLines, filename);

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    public static void countMatches()  {
        try {
            CsvReader csvReader = new CsvReader();
            dataLines = csvReader.read(filename);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
}
