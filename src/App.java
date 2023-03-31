import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class App {
    static String webSiteUrl = "https://zabgu.ru";
    static String newsPageUrl = "/php/news.php?category=1&page=";
    static String previewsDir = "previews2";
    static List<String[]> dataLines = new ArrayList<>();
    static int pageCount = 12;
    static String filename = "data2.csv";
    static String searchStr = "ЗабГУ";
    static int threadCount = 6;

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

    public static void newsParse() throws Exception {
        NewsParser newsParser = new NewsParser(webSiteUrl, newsPageUrl, previewsDir);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
    
        for (int i = 0; i < pageCount; i++) {
            final int ii = i;
            executor.submit(() -> {
                try {
                    dataLines.addAll(newsParser.parseNewsPage(ii + 1));
                            // очистка консоли
                    System.out.print("\033[H\033[2J");

                    System.out.print("Спарсено " + (ii + 1) + " из " + pageCount + " страниц");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdown();

        // блокировка основного потока
        while (true) {
            boolean result_ = executor.awaitTermination(1, TimeUnit.DAYS);

            if(result_)
                break;
        }

        // этот код выполнится после завершения всех созданных ранее потоков
        CsvWriter csvWriter = new CsvWriter();
        csvWriter.write(dataLines, filename);
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
