import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.jsoup.*;
import org.jsoup.nodes.*;

public class NewsParser {
    private String webSiteUrl;
    private String newsPageUrl;
    private String previewsDirectory;

    private ArrayList<String[]> dataLines = new ArrayList<>();

    public NewsParser(String webSiteUrl, String newsPageUrl, String previewsDirectory) {
        this.webSiteUrl = webSiteUrl;
        this.newsPageUrl = webSiteUrl + newsPageUrl;
        this.previewsDirectory = previewsDirectory;
    }

    /***
     * Функция парсинга новостей, находящихся на указанной странице
     * @param pageNum - номер web-страницы
     * @return возвращает коллекцию из данных по каждой новости
     * @throws Exception
     */
    public ArrayList<String[]> parseNewsPage(int pageNum) throws Exception {
        Document page = getPage(newsPageUrl + pageNum);
                
        // метод select возвращает список элементов
        // получаем блок из 9 новостей
        var newsBlock = page.select(
            "body > div#main > div#container > div#content > div#news > div.news_line > div"
        );
                
        for (Element newsEl : newsBlock) {
            // пробегаемся по странице, парсим определенный элемент данных,
            // из которых формируем массив строк
            dataLines.add(parseNewsElement(newsEl));

            // парсим превью новости в указанную директорию
            parseNewsPreview(newsEl, previewsDirectory);
        }
        return dataLines;
    }

    /**
     * Функция скачивания web-страницы
     * @param url - адрес web-страницы
     * @return возвращает HTML-страницу типа Document
     * @throws IOException
     */
    public Document getPage(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    /***
     * Функция парсинга новостного элемента на странице с новостями
     * @param element - HTML-элемент типа Document
     * @return массив строк с пятью распарсенными данными 
     * (ссылка на новость, дата, теги, заголовок, полный текст новости)
     * @throws IOException
     */
    public String[] parseNewsElement(Element element) throws IOException {
        var tags = element.select("div.markersContainer > a");
        var date = element.select("a > div.dateOnImage");
        String newsUrlStr = this.webSiteUrl + element.select("a").attr("href");
        String dateStr = date.select("p.day").text() + " " + date.select("p.yearInTileNewsOnPageWithAllNews").text();
        String tagsStr = "";
        for (Element tag : tags) {
            tagsStr += "#" + tag.text() + "\n";
        }
        String titleStr = element.select("a > div.headline").get(0).text();
        // получаем полную страницу новости для извлечения её текста
        Document newsPage = getPage(newsUrlStr);
        String newsTextStr = getNewsTextFromPage(newsPage);

        return new String[] {newsUrlStr, dateStr, tagsStr, titleStr, newsTextStr};
    }

    /**
     * Функция получения полного текста новости
     * @param page - HTML-страница типа Document
     * @return возвращает текст новости типа String
     */
    private String getNewsTextFromPage(Document page) {
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
    private void downloadNewsPreview(String imageUrl, String directory) throws IOException {
        InputStream inputStream = new URL(imageUrl).openStream();
        String imgFilename = new File(imageUrl).getName();
        Files.copy(inputStream, Paths.get(directory + "/" + imgFilename), StandardCopyOption.REPLACE_EXISTING);
        inputStream.close();
    }

    /**
     * Функция парсинга превью новости в указанный каталог
     * @param page - HTML-страница типа Document
     * @param directory - каталог для сохранения изображения
     * @throws IOException
     */
    private void parseNewsPreview(Element page, String directory) throws IOException {
        String imageUrl = page.select("a > img").attr("src");
        imageUrl = this.webSiteUrl +  imageUrl.replace("..", "");
        downloadNewsPreview(imageUrl, directory);
    }
}
