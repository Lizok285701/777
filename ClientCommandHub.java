import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ClientCommandHub {
    ObjectOutputStream toServer;
    ObjectInputStream fromServer;

    public ClientCommandHub(ObjectInputStream fromServer, ObjectOutputStream toServer) {
        this.toServer = toServer;
        this.fromServer = fromServer;

    }

    public void show() throws IOException, ClassNotFoundException {
        toServer.writeObject(new Command("show", false));
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void help(String command) throws IOException, ClassNotFoundException {
        toServer.writeObject(new Command(command, false));
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void info() throws IOException, ClassNotFoundException {
        toServer.writeObject(new Command("info", false));
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void add() throws IOException, ClassNotFoundException {

        Scanner in = new Scanner(System.in);
        System.out.println(">Введите название организации:");
        String name = in.nextLine();
        while (true) {
            System.out.println(name);
            if (!name.isEmpty()) break;
            System.out.println(">Имя не может быть пустой строкой. Введите имя ещё раз");
            name = in.nextLine();
        }

        System.out.println(">Введите координаты организации в целых числах через пробел без иных символов, при этом \n" +
                " _____________________________________________________________ \n" +
                "| координата x лежит в промежутке [-903, 9000000000000000000] |\n" +
                "| координата y лежит в промежутке [-2147483647, 551]          |\n" +
                "|_____________________________________________________________|\n");
        long x;
        int y;
        String[] coords;
        String coordinates;
        while (true) {
            try {
                coordinates = in.nextLine();
                coords = coordinates.split(" ", 2);
                x = Long.parseLong(coords[0]);
                y = Integer.parseInt(coords[1]);
                if (x <= -904 || y > 551 || x > 9.01E18 || y < -2147483647) x = 1 / 0;
                break;
            } catch (Exception e) {
                System.out.println(">Что-то пошло не так. Повторите ввод координат, ознакомившись с условиями");
            }
        }

        Coordinates newcoords = new Coordinates(x, y);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = now.format(formatter);

        System.out.println(">Введите годовой оборот (положительное число, меньшее 3.3E38): ");
        float annualTurnover;
        while (true) {
            try {
                annualTurnover = Float.parseFloat(in.nextLine());
                if (annualTurnover <= 0 || annualTurnover >= 3.301E38) annualTurnover = 1 / 0;
                break;
            } catch (Exception e) {
                System.out.println(">Вероятно, вы ошиблись с числом. Попробуйте еще раз");
            }
        }

        System.out.println(">Введите количество сотрудников (целое положительное число, меньше 2147483647): ");
        Integer employeesCount;

        while (true) {
            try {
                employeesCount = Integer.parseInt(in.nextLine());
                if (employeesCount <= 0 || employeesCount >= 2147483647) employeesCount = 1 / 0;
                break;
            } catch (Exception e) {
                System.out.println(">Что-то не так. Вводите число согласно указаниям");
            }
        }

        System.out.println(">Выберите тип организации среди предложенных(не обязательно): \n•COMMERCIAL\n" +
                "•PUBLIC\n" +
                "•TRUST ");

        OrganizationType type = OrganizationType.COMMERCIAL;
        while (true) {
            try {
                String buffer_string = in.nextLine().toUpperCase();
                if (buffer_string.isEmpty()) break;
                type = OrganizationType.valueOf(buffer_string);
                break;
            } catch (Exception e) {
                System.out.println(">Нет такого типа. Попробуйте снова");
            }
        }
        System.out.println(">Введите адрес организации (поле можно оставить пустым): ");

        Address officialAddress = null;
        while (true) {

            try {
                String zipCode = in.nextLine();
                if (zipCode.length() > 15) break;
                officialAddress = new Address(zipCode);
                break;
            } catch (Exception e) {
                System.out.println(">Длина адреса не может быть больше 15. Повторите ввод");
            }
        }
        OrganizationWrap org = new OrganizationWrap(name, newcoords, date, annualTurnover, employeesCount, type, officialAddress);

        toServer.writeObject(new Command("add", true));
        toServer.writeObject(org);
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void update_by_id(long id) throws IOException, ClassNotFoundException {

        toServer.writeObject(new Command("update_by_id " + id, true));

        if (!(fromServer.readObject()).toString().equals("vse ne ok")) {

            Scanner in = new Scanner(System.in);
            System.out.println(">Введите название организации:");
            String name = in.nextLine();
            while (true) {
                System.out.println(name);
                if (!name.isEmpty()) break;
                System.out.println(">Имя не может быть пустой строкой. Введите имя ещё раз");
                name = in.nextLine();
            }

            System.out.println(">Введите координаты организации в целых числах через пробел без иных символов, при этом \n" +
                    " _____________________________________________________________ \n" +
                    "| координата x лежит в промежутке [-903, 9000000000000000000] |\n" +
                    "| координата y лежит в промежутке [-2147483647, 551]          |\n" +
                    "|_____________________________________________________________|\n");
            long x;
            int y;
            String[] coords;
            String coordinates;
            while (true) {
                try {
                    coordinates = in.nextLine();
                    coords = coordinates.split(" ", 2);
                    x = Long.parseLong(coords[0]);
                    y = Integer.parseInt(coords[1]);
                    if (x <= -904 || y > 551 || x > 9.01E18 || y < -2147483647) x = 1 / 0;
                    break;
                } catch (Exception e) {
                    System.out.println(">Что-то пошло не так. Повторите ввод координат, ознакомившись с условиями");
                }
            }

            Coordinates newcoords = new Coordinates(x, y);

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String date = now.format(formatter);

            System.out.println(">Введите годовой оборот (положительное число, меньшее 3.3E38): ");
            float annualTurnover;
            while (true) {
                try {
                    annualTurnover = Float.parseFloat(in.nextLine());
                    if (annualTurnover <= 0 || annualTurnover >= 3.301E38) annualTurnover = 1 / 0;
                    break;
                } catch (Exception e) {
                    System.out.println(">Вероятно, вы ошиблись с числом. Попробуйте еще раз");
                }
            }

            System.out.println(">Введите количество сотрудников (целое положительное число, меньше 2147483647): ");
            Integer employeesCount;

            while (true) {
                try {
                    employeesCount = Integer.parseInt(in.nextLine());
                    if (employeesCount <= 0 || employeesCount >= 2147483647) employeesCount = 1 / 0;
                    break;
                } catch (Exception e) {
                    System.out.println(">Что-то не так. Вводите число согласно указаниям");
                }
            }

            System.out.println(">Выберите тип организации среди предложенных(не обязательно): \n•COMMERCIAL\n" +
                    "•PUBLIC\n" +
                    "•TRUST ");

            OrganizationType type = OrganizationType.COMMERCIAL;
            while (true) {
                try {
                    String buffer_string = in.nextLine().toUpperCase();
                    if (buffer_string.isEmpty()) break;
                    type = OrganizationType.valueOf(buffer_string);
                    break;
                } catch (Exception e) {
                    System.out.println(">Нет такого типа. Попробуйте снова");
                }
            }
            System.out.println(">Введите адрес организации (поле можно оставить пустым): ");

            Address officialAddress = null;
            while (true) {

                try {
                    String zipCode = in.nextLine();
                    if (zipCode.length() > 15) break;
                    officialAddress = new Address(zipCode);
                    break;
                } catch (Exception e) {
                    System.out.println(">Длина адреса не может быть больше 15. Повторите ввод");
                }
            }
            OrganizationWrap org = new OrganizationWrap(name, newcoords, date, annualTurnover, employeesCount, type, officialAddress);

            toServer.writeObject(org);
            System.out.println(((Response) fromServer.readObject()).content);
        } else {
            System.out.println(">Введенный id не найден");
        }
    }

    public void remove_by_id(Long id) throws IOException, ClassNotFoundException {
        toServer.writeObject(new Command("remove_by_id " + id, false));
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void clear() throws IOException, ClassNotFoundException {
        toServer.writeObject(new Command("clear", false));
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void add_if_min() throws IOException, ClassNotFoundException {
        toServer.writeObject(new Command("add_if_min", true));

        Scanner in = new Scanner(System.in);
        System.out.println(">Введите название организации:");
        String name = in.nextLine();
        while (true) {
            System.out.println(name);
            if (!name.isEmpty()) break;
            System.out.println(">Имя не может быть пустой строкой. Введите имя ещё раз");
            name = in.nextLine();
        }

        System.out.println(">Введите координаты организации в целых числах через пробел без иных символов, при этом \n" +
                " _____________________________________________________________ \n" +
                "| координата x лежит в промежутке [-903, 9000000000000000000] |\n" +
                "| координата y лежит в промежутке [-2147483647, 551]          |\n" +
                "|_____________________________________________________________|\n");
        long x;
        int y;
        String[] coords;
        String coordinates;
        while (true) {
            try {
                coordinates = in.nextLine();
                coords = coordinates.split(" ", 2);
                x = Long.parseLong(coords[0]);
                y = Integer.parseInt(coords[1]);
                if (x <= -904 || y > 551 || x > 9.01E18 || y < -2147483647) x = 1 / 0;
                break;
            } catch (Exception e) {
                System.out.println(">Что-то пошло не так. Повторите ввод координат, ознакомившись с условиями");
            }
        }

        Coordinates newcoords = new Coordinates(x, y);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = now.format(formatter);

        System.out.println(">Введите годовой оборот (положительное число, меньшее 3.3E38): ");
        float annualTurnover;
        while (true) {
            try {
                annualTurnover = Float.parseFloat(in.nextLine());
                if (annualTurnover <= 0 || annualTurnover >= 3.301E38) annualTurnover = 1 / 0;
                break;
            } catch (Exception e) {
                System.out.println(">Вероятно, вы ошиблись с числом. Попробуйте еще раз");
            }
        }

        System.out.println(">Введите количество сотрудников (целое положительное число, меньше 2147483647): ");
        Integer employeesCount;

        while (true) {
            try {
                employeesCount = Integer.parseInt(in.nextLine());
                if (employeesCount <= 0 || employeesCount >= 2147483647) employeesCount = 1 / 0;
                break;
            } catch (Exception e) {
                System.out.println(">Что-то не так. Вводите число согласно указаниям");
            }
        }

        System.out.println(">Выберите тип организации среди предложенных(не обязательно): \n•COMMERCIAL\n" +
                "•PUBLIC\n" +
                "•TRUST ");

        OrganizationType type = OrganizationType.COMMERCIAL;
        while (true) {
            try {
                String buffer_string = in.nextLine().toUpperCase();
                if (buffer_string.isEmpty()) break;
                type = OrganizationType.valueOf(buffer_string);
                break;
            } catch (Exception e) {
                System.out.println(">Нет такого типа. Попробуйте снова");
            }
        }
        System.out.println(">Введите адрес организации (поле можно оставить пустым): ");

        Address officialAddress = null;
        while (true) {

            try {
                String zipCode = in.nextLine();
                if (zipCode.length() > 15) break;
                officialAddress = new Address(zipCode);
                break;
            } catch (Exception e) {
                System.out.println(">Длина адреса не может быть больше 15. Повторите ввод");
            }
        }
        OrganizationWrap org = new OrganizationWrap(name, newcoords, date, annualTurnover, employeesCount, type, officialAddress);

        toServer.writeObject(org);
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void remove_greater(String name) throws IOException, ClassNotFoundException {
        toServer.writeObject(new Command("remove_greater " + name, false));
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void remove_lower(String name) throws IOException, ClassNotFoundException {
        toServer.writeObject(new Command("remove_lower " + name, false));
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void count_greater_than_type(String type) throws IOException, ClassNotFoundException {
        OrganizationType.valueOf(type.toUpperCase());
        toServer.writeObject(new Command("count_greater_than_type " + type, false));
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void print_descending() throws IOException, ClassNotFoundException {
        toServer.writeObject(new Command("print_descending", false));
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void print_field_ascending_official_address() throws IOException, ClassNotFoundException {
        toServer.writeObject(new Command("print_field_ascending_official_address", false));
        System.out.println(((Response) fromServer.readObject()).content);
    }

}