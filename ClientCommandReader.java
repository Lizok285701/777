import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
public class ClientCommandReader {

    ObjectInputStream fromServer;
    ObjectOutputStream toServer;

    ClientCommandReader(ObjectInputStream fromServer, ObjectOutputStream toServer){
        this.fromServer = fromServer;
        this.toServer = toServer;
    }

    public void start_reading(HashSet<String> is_console, String Path) throws IOException, ClassNotFoundException {
        Boolean is_ok = true;

        ClientCommandHub hub = new ClientCommandHub(fromServer, toServer);
        Scanner sc = new Scanner(System.in);

        if (is_console.contains(Path)) {
            is_ok = false;
            System.out.println(">Вы попытались вызвать из скрипта тот же самый скрипт. Действие было пропущено");
        }

        is_console.add(Path);

        if (is_console.size()==1 && is_ok) sc = new Scanner(System.in);
        else
            try {
                sc = new Scanner(new File(Path));
            } catch (Exception e) {
                System.out.println(">Проблема с файлом");
                is_ok = false;
            }

        if (is_ok) {
            String command = sc.nextLine().toLowerCase();
            String[] commandParts;
            while (!command.equals("exit")) {
                commandParts = command.split(" ", 3);
                switch (commandParts[0]) {
                    case "":
                        break;
                    case "help":
                        hub.help(command);
                        break;
                    case "info":
                        hub.info();
                        break;
                    case "show":
                        hub.show();
                        break;
                    case "add":
                        hub.add();
                        break;
                    case "update_by_id":
                        hub.update_by_id(Long.parseLong(commandParts[1]));
                        break;
                    case "remove_by_id":
                        hub.remove_by_id(Long.parseLong(commandParts[1]));
                        break;
                    case "clear":
                        hub.clear();
                        break;
                    case "execute_script":
                        try {
                            this.start_reading(is_console, commandParts[1]);
                        }
                        catch (Exception e)
                        {
                            System.out.println("Не удаь выполнить скрипт");
                        }
                        finally {
                            is_console.remove(commandParts[1]);
                        }
                        break;
                    case "add_if_min":
                        hub.add_if_min();
                        break;
                    case "remove_greater":
                        try {
                            hub.remove_greater(commandParts[1]);}
                        catch (Exception e)
                        {
                            System.out.println("Попробуйте еще раз"); }
                        break;
                    case "remove_lower":
                        try {
                            hub.remove_lower(commandParts[1]);}
                        catch (Exception e)
                        {
                            System.out.println("Попробуйте еще раз"); }
                        break;
                    case "count_greater_than_type":
                        try {
                            hub.count_greater_than_type(commandParts[1]);}
                        catch (Exception e)
                        {
                            System.out.println("Попробуйте еще раз"); }
                        break;
                    case "print_descending":
                        hub.print_descending();
                        break;
                    case "print_field_ascending_official_address":
                        hub.print_field_ascending_official_address();
                        break;
                    default:
                        System.out.println('"' + command + "\" не является командой. Используйте help, чтобы узнать список доступных команд.");
                        break;
                }
                System.out.print('>');
                command = sc.nextLine().toLowerCase();
            }
        }
    }
}
