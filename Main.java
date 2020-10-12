import java.sql.*;

public class Main{

    public static final String DB_URL = "jdbc:postgresql://localhost:5432/Xoma";
    public static final String login = "postgres";
    public static final String password = "2020";

    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URL, login, password);
            connection.setAutoCommit(false);
            System.out.println("Соединение с СУБД выполнено.");
            Statement stmt = connection.createStatement();
            String sql =
                    "CREATE SEQUENCE users_lizok_sequence INCREMENT BY 1;"+
                    "CREATE SEQUENCE organizations_lizok_sequence INCREMENT BY 1;"+
                    "CREATE TABLE LIZOK_USERS (ID int PRIMARY KEY NOT NULL, LOGIN TEXT NOT NULL, PASSWORD text);" +
                    "CREATE TABLE LIZOK_ORGANIZATIONS " +
                    "(ID int PRIMARY KEY     NOT NULL," +
                    " NAME           TEXT    NOT NULL, "+
                    " COORDINATES    TEXT     NOT NULL, " +
                    " CREATIONDATE   TIMESTAMP , "+
                    " ANNUALTURNOVER FLOAT        , " +
                    " EMPLOEESCOUNT INTEGER NOT NULL,"+
                    " TYPE TEXT NULL," +
                    " OFFICIALADDRESS        VARCHAR(30) NULL," +
                    " USERID int NOT NULL)";

            stmt.executeUpdate(sql);
            stmt.close();
            connection.commit();
            connection.close();
            System.out.println("Отключение от СУБД выполнено.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("JDBC драйвер для СУБД не найден!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка SQL!");
        }
    }
}