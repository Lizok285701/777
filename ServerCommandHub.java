import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Stream;


public class ServerCommandHub {
    ObjectOutputStream toClient;
    ObjectInputStream fromClient;

    Connection connection;
    long userid;

    private HashMap<String, String> CommandHelpList = new HashMap<String, String>();


    public ServerCommandHub(ObjectOutputStream toClient, ObjectInputStream fromClient,  Connection connection, long userid) throws SQLException {
        this.toClient = toClient;
        this.fromClient = fromClient;
        this.connection = connection;
        this.userid = userid;
        System.out.println("USERID5: " + userid);
        connection.setAutoCommit(false);
        CommandHelpList.put("help", "Команда help выведет справку по доступным командам.");
        CommandHelpList.put("info", "Команда info выведет информацию о коллекции.");
        CommandHelpList.put("show", "Команда show выведет все элементы коллекции.");
        CommandHelpList.put("add", "Команда add добавит новый элемент, созданный по указанным параметрам, в коллекцию.");
        CommandHelpList.put("update_by_id", "Команда update id обновит значение элемента коллекции, id которого равен заданному.");
        CommandHelpList.put("remove_by_id", "Команда remove_by_id удалит из коллекции элемент с указанным id.");
        CommandHelpList.put("clear", "Команда clear очистит коллекцию.");
        CommandHelpList.put("execute_script", "Команда execute_script cчитает и исполнит скрипт из указанного файла.");
        CommandHelpList.put("exit","Команда exit завершит работу программы.");
        CommandHelpList.put("add_if_min", "Команда add_if_min добавит новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента коллекции.");
        CommandHelpList.put("remove_greater", "Команда remove_greater удалит из коллекции все элементы, превышающие заданный.");
        CommandHelpList.put("remove_lower","Команда remove_lower удалит из коллекции все элементы, превышающие заданный.");
        CommandHelpList.put("count_greater_than_type", "Команда count_greater_than_type выведет количество элементов, значение поля type которых больше заданного.");
        CommandHelpList.put("print_descending", "Команда print_descending выведет элементы коллекции в порядке убывания.");
        CommandHelpList.put("print_field_ascending_official_address", "Команда print_field_ascending_official_address выведет значения поля officialAddress в порядке возрастания.");

    }

    public void show(Set<Organization> collection) throws IOException {
        Response resp = new Response("");
        collection.forEach(o -> resp.addText(o.toString()));

        this.toClient.writeObject(resp);
    }

    public void info(Set<Organization> collection) throws IOException {
        this.toClient.writeObject(new Response(">Тип коллекции: " + collection.getClass() + '\n'+
                ">Количество элементов: " +collection.size()+'\n'));
    }

    public void add(Set<Organization> collection) throws IOException, ClassNotFoundException, SQLException {
        OrganizationWrap wrap = (OrganizationWrap)fromClient.readObject();


        String sql = "INSERT INTO lizok_organizations " + "(id, name, creationdate, annualturnover, emploeescount, type, officialaddress, userid, coordinates)"+
                "VALUES (NEXTVAL('ORGANIZATIONS_LIZOK_SEQUENCE'), '" + wrap.getName() + "', '" + wrap.getCreationDate() + "', " + wrap.getAnnualTurnover() +
                ", " + wrap.getEmployeesCount()+ ", '" + wrap.getType().toString() + "', '" +wrap.getOfficialAddress().toString()+"', "+userid+ ", '" + wrap.getCoordinates().toString()+"');";
        Statement strm = this.connection.createStatement();
        strm.execute(sql);
        connection.commit();

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery( "SELECT * FROM LIZOK_ORGANIZATIONS WHERE ID=CURRVAL('ORGANIZATIONS_LIZOK_SEQUENCE')" );
        while ( rs.next() ) {
            int id = rs.getInt("id");
            String  name = rs.getString("name");
            String[] str_cords = rs.getString("coordinates").split(" ");
            Coordinates coordinates  = new Coordinates(Long.parseLong(str_cords[0]), Integer.parseInt(str_cords[1]));
            String creationdate = rs.getDate("creationdate").toString();
            Float annualTurnover = rs.getFloat("annualturnover");
            Integer employeesCount = rs.getInt("emploeescount");
            OrganizationType type = null;
            if (rs.getString("type")!=null)
                type= OrganizationType.valueOf(rs.getString("type"));
            Address address = new Address(rs.getString("officialaddress"));
            int USERID = rs.getInt("userid");
            Organization temp = new Organization(id, name, coordinates, creationdate, annualTurnover, employeesCount, type, address, USERID);
            collection.add(temp);
            System.out.println(temp.getName() + " добавлена");
        }

        toClient.writeObject(new Response(">Организация успешно добавлена в коллекцию"));
    }
    public void update_by_id(Set<Organization> collection, long id) throws IOException, ClassNotFoundException, SQLException {

        Stream stream = collection.stream().filter(o -> o.getId()==id);
        if (stream.count()==1) {
            if (collection.stream().filter(o -> o.getId()==id).findFirst().get().getUSERID()!=this.userid) {
                toClient.writeObject(new Response("vse ne ok"));
                return;
            }
            toClient.writeObject(new Response("vse ok"));
            OrganizationWrap wrap = (OrganizationWrap)fromClient.readObject();
            collection.stream().filter(o -> o.getId()==id).findFirst().get().replace(wrap.getName(),
                    wrap.getCoordinates(),
                    wrap.getCreationDate(),
                    wrap.getAnnualTurnover(),
                    wrap.getEmployeesCount(),
                    wrap.getType(),
                    wrap.getOfficialAddress());

            String sql =
                    "UPDATE lizok_organizations SET" + "(name, creationdate, annualturnover, emploeescount, type, officialaddress, coordinates)"+
                            "= ('" + wrap.getName() + "', '" + wrap.getCreationDate() + "', " + wrap.getAnnualTurnover() +
                            ", " + wrap.getEmployeesCount()+ ", '" + wrap.getType().toString() + "', '" +wrap.getOfficialAddress().toString()+"', '" + wrap.getCoordinates().toString()+"') " +
                            "WHERE ID="+ id+";";
            Statement strm = this.connection.createStatement();
            strm.execute(sql);
            connection.commit();

            toClient.writeObject(new Response(">Элемент по id "+ id + " успешно обновлен"));
        }
        else  {
            toClient.writeObject(new Response("vse ne ok"));
        }

    }
    public void remove_by_id(long id, Set<Organization> collection) throws IOException, SQLException {

        Stream stream = collection.stream().filter(o -> o.getId()==id);
        if (stream.count()==1) {
            if (collection.stream().filter(o -> o.getId()==id).findFirst().get().getUSERID()!=this.userid) {
                toClient.writeObject(new Response(">Нет доступа к элементу с таким id"));
                return;
            }
            collection.remove(collection.stream().filter(o -> o.getId()==id).findFirst().get());

            String sql = "DELETE FROM LIZOK_ORGANIZATIONS WHERE ID="+ id+";";
            Statement strm = this.connection.createStatement();
            strm.execute(sql);
            connection.commit();

            toClient.writeObject(new Response(">Элемент с заданным id успешно удален"));
        }
        else {
            toClient.writeObject(new Response(">Элемента с таким id не существует"));
        }
    }

    public void clear(Set<Organization> collection) throws IOException, SQLException {

        if (collection.stream().filter(o-> o.getUSERID()==userid).count()>0) {
            for (Object o : collection.stream().filter(o-> o.getUSERID()==userid).toArray())
                collection.remove((Organization) o);
            String sql = "DELETE FROM LIZOK_ORGANIZATIONS WHERE USERID="+ userid+";";
            Statement strm = this.connection.createStatement();
            strm.execute(sql);
            connection.commit();
            toClient.writeObject(new Response(">Коллекция успешно очищена"));
        }
        else
        {
            toClient.writeObject(new Response(">У вас нет элементов в этой коллекции"));
        }

    }

    public void add_if_min(Set<Organization> collection) throws IOException, ClassNotFoundException, SQLException {
        OrganizationWrap wrap = (OrganizationWrap)fromClient.readObject();
        Organization temp = new Organization(-1, wrap.getName(),
                wrap.getCoordinates(),
                wrap.getCreationDate(),
                wrap.getAnnualTurnover(),
                wrap.getEmployeesCount(),
                wrap.getType(),
                wrap.getOfficialAddress(), (int)this.userid);

        if (collection.stream().filter(o -> o.compareTo(temp)<0).count()==0)
        {
            String sql = "INSERT INTO lizok_organizations " + "(id, name, creationdate, annualturnover, emploeescount, type, officialaddress, userid, coordinates)"+
                    "VALUES (NEXTVAL('ORGANIZATIONS_LIZOK_SEQUENCE'), '" + wrap.getName() + "', '" + wrap.getCreationDate() + "', " + wrap.getAnnualTurnover() +
                    ", " + wrap.getEmployeesCount()+ ", '" + wrap.getType().toString() + "', '" +wrap.getOfficialAddress().toString()+"', "+userid+ ", '" + wrap.getCoordinates().toString()+"');";
            Statement strm = this.connection.createStatement();
            strm.execute(sql);
            connection.commit();

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM LIZOK_ORGANIZATIONS WHERE ID=CURRVAL('ORGANIZATIONS_LIZOK_SEQUENCE')" );
            while ( rs.next() ) {
                int id = rs.getInt("id");
                String  name = rs.getString("name");
                String[] str_cords = rs.getString("coordinates").split(" ");
                Coordinates coordinates  = new Coordinates(Long.parseLong(str_cords[0]), Integer.parseInt(str_cords[1]));
                String creationdate = rs.getDate("creationdate").toString();
                Float annualTurnover = rs.getFloat("annualturnover");
                Integer employeesCount = rs.getInt("emploeescount");
                OrganizationType type = null;
                if (rs.getString("type")!=null)
                    type= OrganizationType.valueOf(rs.getString("type"));
                Address address = new Address(rs.getString("officialaddress"));
                int USERID = rs.getInt("userid");
                Organization temporary = new Organization(id, name, coordinates, creationdate, annualTurnover, employeesCount, type, address, USERID);
                collection.add(temporary);
                System.out.println(temp.getName() + " добавлена");
            }

            toClient.writeObject(new Response(">Элемент минимален \n" + temp.getName() +" - успешно добавлен в коллекцию"));
        } else {
            toClient.writeObject(new Response(">Элемент " + temp.getName() + " не является минимальным - данный метод не может добавить его в коллекцию"));
        }
    }

    public void remove_greater(Set<Organization> collection, String name) throws IOException, SQLException {

        if (collection.stream().filter(o-> o.getName().toLowerCase().equals(name)).count()>0){
            for (Object o: collection.stream().filter(o -> o.getName().toLowerCase().compareTo(name)>0 && o.getUSERID()==userid).toArray())
            {
                collection.remove((Organization) o);
                String sql = "DELETE FROM LIZOK_ORGANIZATIONS WHERE ID="+ ((Organization) o).getId()+";";
                Statement strm = this.connection.createStatement();
                strm.execute(sql);
                connection.commit();
            }
            toClient.writeObject(new Response(">Объекты, бОльшие чем " + name + " ,удалены"));
        }
        else{
            toClient.writeObject(new Response(">Не найдено организации с названием " + name));
        }

    }
    public void remove_lower(Set<Organization> collection, String name) throws IOException, SQLException {

        if (collection.stream().filter(o-> o.getName().toLowerCase().equals(name) && o.getUSERID()==userid).count()>0){
            for (Object o: collection.stream().filter(o -> o.getName().toLowerCase().compareTo(name)<0 && o.getUSERID()==userid).toArray())
            {
                collection.remove((Organization) o);
                String sql = "DELETE FROM LIZOK_ORGANIZATIONS WHERE ID="+ ((Organization) o).getId()+";";
                Statement strm = this.connection.createStatement();
                strm.execute(sql);
                connection.commit();
            }
            toClient.writeObject(new Response(">Объекты, меньшие чем " + name + " ,удалены"));
        }
        else{
            toClient.writeObject(new Response(">Не найдено организации с названием " + name));
        }

    }

    public void count_greater_than_type(Set<Organization> collection, String type) throws IOException {
        System.out.println(OrganizationType.COMMERCIAL.toString().toLowerCase().compareTo(type));
        toClient.writeObject(new Response(">Найдено " + collection.stream().filter(o -> OrganizationType.valueOf(o.getType()).ordinal()> OrganizationType.valueOf(type.toUpperCase()).ordinal()).count() + " элементов"));

    }

    public void print_descending(Set<Organization> collection) throws IOException {
        Response resp = new Response("");
        collection.stream().forEachOrdered(o -> resp.addTextForward(o.toString()));
        toClient.writeObject(resp);
    }

    public void print_field_ascending_official_address(Set<Organization> collection) throws IOException {
        ArrayList<String> addresses = new ArrayList<String>();
        collection.forEach(o -> addresses.add(o.getOfficialAddress()));
        Response resp = new Response("");
        addresses.stream().sorted().forEachOrdered(o->resp.addText(o));
        toClient.writeObject(resp);
    }

    void help(String [] commandParts) throws IOException {
        if (commandParts.length==1)
        {
            toClient.writeObject(new Response(">Список доступных команд:\n"  + CommandHelpList.keySet()));
        }
        else
        {
            if (CommandHelpList.containsKey(commandParts[1])) {
                toClient.writeObject(new Response(CommandHelpList.get(commandParts[1])));
            }
            else
                toClient.writeObject(new Response(">Такая команда не найдена"));
        }
    }
}
