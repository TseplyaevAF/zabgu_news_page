import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CountMatcher {
    /* Проверяет, является ли строка пустой ("") или нулевой. */
    public boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }
 
    /***
     * Функция подсчета кол-ва упоминаний в тексте
     * @param text - текст, в котором производится поиск
     * @param str - строка, которая ищется в тексте
     * @return - количество упоминаний
     */
    public int getCountMatches(String text, String str)
    {
        if (isEmpty(text) || isEmpty(str)) {
            return 0;
        }

        int count = 0;
        Pattern pattern = Pattern.compile("(?iu)\\b(" + Pattern.quote(str) + ")\\b");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            count++;
        }
 
        return count;
    }
}
