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
    static String webSiteUrl = "https://zabgu.ru";
    static String newsPageUrl = webSiteUrl + "/php/news.php?category=1&page=";

    public static void main(String[] args) throws Exception {
        List<String[]> dataLines = new ArrayList<>();
        int pageCount = 1;
        String filename = "data.csv";

        try {
            if (args.length == 2) {
                pageCount = Integer.parseInt(args[0]);
                filename = args[1];
            }
            int newsNum = 0;
            int totalNewsNum = 9 * pageCount;
            for (int i = 0; i < pageCount; i++) {
                Document page = getPage(newsPageUrl + (i + 1));
                
                // метод select возвращает список элементов
                // получаем блок из 9 новостей
                var newsBlock = page.select(
                    "body > div#main > div#container > div#content > div#news > div.news_line > div"
                );
                
                for (Element newsEl : newsBlock) {
                    // пробегаемся по странице, парсим определенный элемент данных,
                    // из которых формируем массив строк
                    dataLines.add(parseNewsPage(newsEl));

                    // парсим превью новости в указанную директорию
                    parseNewsPreview(newsEl, "previews");

                    // очистка консоли
                    System.out.print("\033[H\033[2J");

                    newsNum++;
                    System.out.print("Спарсено " + newsNum + " из " + totalNewsNum + " новостей");
                }
            }
            CsvWriter csvWriter = new CsvWriter();
            csvWriter.write(dataLines, filename);

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Функция скачивания web-страницы
     * @param url - адрес web-страницы
     * @return возвращает HTML-страницу типа Document
     * @throws IOException
     */
    private static Document getPage(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    /**
     * Функция получения полного текста новости
     * @param page - HTML-страница типа Document
     * @return возвращает текст новости типа String
     */
    private static String getNewsTextFromPage(Document page) {
        return page.select(
            "body > div#main > div#container > " + 
            "div#content > div#open_news > div#news_text > div#full_text > " +
            ":not(.date, .markersContainer, h2)")
        .text();
    }

    /**
     * Функция скачивания превью новости в указанный каталог
     * @param imageUrl - адрес изображения
     * @param directory - каталог для сохранения изображения
     * @throws IOException
     */
    private static void downloadNewsPreview(String imageUrl, String directory) throws IOException {
        InputStream inputStream = new URL(imageUrl).openStream();
        String imgFilename = new File(imageUrl).getName();
        Files.copy(inputStream, Paths.get(directory + "/" + imgFilename), StandardCopyOption.REPLACE_EXISTING);
        inputStream.close();
    }

    /***
     * Функция парсинга новостной страницы сайта ЗабГУ
     * @param page - HTML-страница типа Document
     * @return массив строк с пятью распарсенными данными 
     * (ссылка на новость, дата, теги, заголовок, полный текст новости)
     * @throws IOException
     */
    private static String[] parseNewsPage(Element page) throws IOException {
        var tags = page.select("div.markersContainer > a");
        var date = page.select("a > div.dateOnImage");
        String newsUrlStr = webSiteUrl + page.select("a").attr("href");
        String dateStr = date.select("p.day").text() + " " + date.select("p.yearInTileNewsOnPageWithAllNews").text();
        String tagsStr = "";
        for (Element tag : tags) {
            tagsStr += "#" + tag.text() + "\n";
        }
        String titleStr = page.select("a > div.headline").get(0).text();
        // получаем полную страницу новости для извлечения её текста
        Document newsPage = getPage(newsUrlStr);
        String newsTextStr = getNewsTextFromPage(newsPage);

        return new String[] {newsUrlStr, dateStr, tagsStr, titleStr, newsTextStr};
    }

    /**
     * Функция парсинга превью новости в указанный каталог
     * @param page - HTML-страница типа Document
     * @param directory - каталог для сохранения изображения
     * @throws IOException
     */
    private static void parseNewsPreview(Element page, String directory) throws IOException {
        String imageUrl = page.select("a > img").attr("src");
        imageUrl = webSiteUrl +  imageUrl.replace("..", "");
        downloadNewsPreview(imageUrl, directory);
    }
}
