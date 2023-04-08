import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.sqlite.JDBC;

/**
 * Класс для работы с базой данных
 * Реализован с помощью паттерна Singleton
 */
public class DBHandler {
    // Константа, в которой хранится адрес подключения
    private static final String CON_STR = "jdbc:sqlite:db/news.db";

    // Приватное статическое поле, содержащее одиночный объект
    private static DBHandler instance = null;

    // Объект, в котором будет храниться соединение с БД
    private Connection connection;

    /**
     * Конструктор по умолчанию с закрытым доступом за пределами класса,
     * чтобы он не смог возвращать новые объекты
     * @throws SQLException
     */
    private DBHandler() throws SQLException {
        // Регистрируем драйвер, с которым будем работать
        // в нашем случае Sqlite
        DriverManager.registerDriver(new JDBC());
        // Выполняем подключение к базе данных
        this.connection = DriverManager.getConnection(CON_STR);
    }
 
    // Используем synchronized, чтобы в момент обращения к базе одним потоком, 
    // другие потоки ожидали его завершения, чтобы не нарушить целостность базы
    /**
     * Статический создающий метод
     * @return возвращает единственный экземпляр класса
     * @throws SQLException
     */
    public static synchronized DBHandler getInstance() throws SQLException {
        if (instance == null)
            instance = new DBHandler();
        return instance;
    }

    /**
     * Метод добавления новостной записи в БД
     * @param record - строковый массив с данными о записи
     */
    public void addRecord(String[] record) {
        // Создадим подготовленное выражение, чтобы избежать SQL-инъекций
        try (PreparedStatement statement = this.connection.prepareStatement(
                        "INSERT INTO news(`link`, `created_date`, `tags`, `title` , `description`) " +
                         "VALUES(?, ?, ?, ?, ?)")) {
            statement.setObject(1, record[0]);
            statement.setObject(2, record[1]);
            statement.setObject(3, record[2]);
            statement.setObject(4, record[3]);
            statement.setObject(5, record[4]);
            // Выполняем запрос
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
