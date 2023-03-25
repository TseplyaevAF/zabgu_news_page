import java.util.ArrayList;
import java.util.List;

public class App {
    static String webSiteUrl = "https://zabgu.ru";
    static String newsPageUrl = "/php/news.php?category=1&page=";
    static String previewsDir = "previews";
    static List<String[]> dataLines = new ArrayList<>();
    static int pageCount = 1;
    static String filename = "data.csv";
    static String searchStr = "ЗабГУ";

    public static void main(String[] args) throws Exception {
        if (args.length != 0) {
            if (args[0].equals("parse")) {
                pageCount = Integer.parseInt(args[1]);
                filename = args[2];
                previewsDir = args[3];
                newsParse();
            } else if (args[0].equals("search")) {
                filename = args[1];
                searchStr = args[2];
                countMatchesFromCsv();
            }
        } else {
            newsParse();
            countMatchesFromCsv();
        }
    }

    public static void newsParse() {
        try {
            NewsParser newsParser = new NewsParser(webSiteUrl, newsPageUrl, previewsDir);
        
            for (int i = 0; i < pageCount; i++) {
                dataLines.addAll(newsParser.parseNewsPage(i + 1));

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

    public static void countMatchesFromCsv()  {
        try {
            CsvReader csvReader = new CsvReader();
            dataLines = csvReader.read(filename);
            int count = 0;
            CountMatcher countMatcher = new CountMatcher();
            for (String[] data : dataLines) {
                count += countMatcher.getCountMatches(data[4], searchStr);
            }
            CsvWriter csvWriter = new CsvWriter();
            List<String[]> countMatchesRes = new ArrayList<>();
            countMatchesRes.add(new String[] {searchStr, Integer.toString(count)});
            csvWriter.write(countMatchesRes, "count_matches.csv");

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
}
