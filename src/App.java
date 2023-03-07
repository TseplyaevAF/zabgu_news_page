import java.util.ArrayList;

public class App {
    static String webSiteUrl = "https://zabgu.ru";
    static String newsPageUrl = "/php/news.php?category=1&page=";
    static String previewsDirectroy = "previews";

    public static void main(String[] args) throws Exception {
        ArrayList<String[]> dataLines = new ArrayList<String[]>();
        int pageCount = 1;
        String filename = "data.csv";

        try {
            if (args.length == 2) {
                pageCount = Integer.parseInt(args[0]);
                filename = args[1];
            }
            NewsParser newsParser = new NewsParser(webSiteUrl, newsPageUrl, previewsDirectroy);
        
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
}
