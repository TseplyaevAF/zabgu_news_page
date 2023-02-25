import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.*;
import org.jsoup.nodes.*;

public class App {
    public static void main(String[] args) throws Exception {
        String webSiteUrl = "https://zabgu.ru";
        String newsPageUrl = webSiteUrl + "/php/news.php?category=1&page=";
        List<String[]> dataLines = new ArrayList<>();
        int pageCount = 1;
        String filename = "data.csv";
        if (args.length == 2) {
            pageCount = Integer.parseInt(args[0]);
            filename = args[1];
        }

        try {
            int newsNum = 0;
            int totalNewsNum = 9 * pageCount;
            for (int i = 0; i < pageCount; i++) {
                Document page = getPage(newsPageUrl + (i + 1));
                var newsBlock = page.select(
                    "body > div#main > div#container > div#content > div#news > div.news_line > div"
                );
                for (Element newsEl : newsBlock) {
                    var tags = newsEl.select("div.markersContainer > a");
                    var date = newsEl.select("a > div.dateOnImage");
                    String newsUrlStr = webSiteUrl + newsEl.select("a").attr("href");
                    String dateStr = date.select("p.day").text() + " " + date.select("p.yearInTileNewsOnPageWithAllNews").text();
                    String tagsStr = "";
                    for (Element tag : tags) {
                        tagsStr += "#" + tag.text() + "\n";
                    }
                    String titleStr = newsEl.select("a > div.headline").get(0).text();

                    Document newsPage = getPage(newsUrlStr);
                    String newsTextStr = getNewsTextFromPage(newsPage);

                    dataLines.add(new String[] {newsUrlStr, dateStr, tagsStr, titleStr, newsTextStr});

                    String imageUrl = newsEl.select("a > img").attr("src");
                    imageUrl = webSiteUrl +  imageUrl.replace("..", "");
                    downloadNewsPreview(imageUrl);

                    // очистка консоли
                    System.out.print("\033[H\033[2J");

                    newsNum++;
                    System.out.print("Осталось " + (totalNewsNum - newsNum) + " из " + totalNewsNum);
                }
            }
            CsvWriter csvWriter = new CsvWriter();
            csvWriter.write(dataLines, filename);

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static Document getPage(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    private static String getNewsTextFromPage(Document page) {
        return page.select(
            "body > div#main > div#container > " + 
            "div#content > div#open_news > div#news_text > div#full_text > " +
            ":not(.date, .markersContainer, h2)")
        .text();
    }

    private static void downloadNewsPreview(String imageUrl) throws IOException {
        InputStream inputStream = new URL(imageUrl).openStream();
        String imgFilename = new File(imageUrl).getName();
        Files.copy(inputStream, Paths.get("previews/" + imgFilename), StandardCopyOption.REPLACE_EXISTING);
        inputStream.close();
    }
}
