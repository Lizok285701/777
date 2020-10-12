

import java.net.ServerSocket;
import java.sql.*;
import java.util.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class Server {

    public static final String DB_URL = "jdbc:postgresql://localhost:5432/Xoma";
    public static String login;
    public static String password;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Hello, введите логин для подключения к БД");
        Scanner in = new Scanner(System.in);
        login = in.nextLine();
        System.out.println("Введите пароль для подключения к БД");
        password = in.nextLine();



        TreeSet<Organization> empty_set = new TreeSet<Organization>();
        Set<Organization> organizations = Collections.synchronizedSet(empty_set);


        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(DB_URL, login, password);
            organizations = update_collection(connection);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("JDBC драйвер для СУБД не найден!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка SQL!");
        }


        try {
            try (ServerSocket ss = new ServerSocket(6666)) {
                System.out.println("ServerSocket awaiting connections...");
                ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(16);
                while (true) {
                    Worker w = new Worker(ss.accept(), connection, organizations);
                    executor.execute(w);
                }
            }
        } catch (Exception e) {
        }
    }

    public static Set<Organization> update_collection(Connection connection) throws SQLException {

        TreeSet<Organization> organizations = new TreeSet<Organization>();

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM LIZOK_ORGANIZATIONS;");
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String[] str_cords = rs.getString("coordinates").split(" ");
            Coordinates coordinates = new Coordinates(Long.parseLong(str_cords[0]), Integer.parseInt(str_cords[1]));
            String creationdate = rs.getDate("creationdate").toString();
            Float annualTurnover = rs.getFloat("annualturnover");
            Integer employeesCount = rs.getInt("emploeescount");
            OrganizationType type = null;
            if (rs.getString("type") != null)
                type = OrganizationType.valueOf(rs.getString("type"));
            Address address = new Address(rs.getString("officialaddress"));
            int USERID = rs.getInt("userid");
            Organization temp = new Organization(id, name, coordinates, creationdate, annualTurnover, employeesCount, type, address, USERID);
            organizations.add(temp);
            System.out.println(temp.getName() + " добавлена");
        }
        System.out.println("В локальную коллекцию успешно добавлены " + organizations.size() + " организаций");
        rs.close();
        stmt.close();
        return organizations;


    }
}