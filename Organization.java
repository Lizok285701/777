/**
 * Класс организаций, содержащий конструкторы, по кототорым создаются объекты класса, хранящиеся в коллекции
 */

public class Organization implements Comparable <Organization>{
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates Coordinates; //Поле не может быть null
    private String creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private float annualTurnover; //Значение поля должно быть больше 0
    private Integer employeesCount; //Поле не может быть null, Значение поля должно быть больше 0
    private OrganizationType type = null; //Поле может быть null
    private Address officialAddress = null; //Поле может быть null
    private int USERID;

    public Organization(long id, String name, Coordinates coordinates, String creationDate,
                        float annualTurnover, Integer employeesCount, OrganizationType type, Address officialAddress, int USERID){
        this.name = name;
        this.Coordinates = coordinates;
        this.creationDate = creationDate;
        this.annualTurnover = annualTurnover;
        this.employeesCount = employeesCount;
        this.type = type;
        this.officialAddress = officialAddress;
        this.id = id;
        this.USERID = USERID;
    }


    public void replace(String name, Coordinates coordinates, String creationDate,
                        float annualTurnover, Integer employeesCount, OrganizationType type, Address officialAddress){
        this.name = name;
        this.Coordinates = coordinates;
        this.annualTurnover = annualTurnover;
        this.employeesCount = employeesCount;
        this.type = type;
        this.creationDate = creationDate;
        this.officialAddress = officialAddress;
    }

    @Override
    public int compareTo(Organization o) {
        int result = this.name.compareTo(o.name);
        return result;
    }


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return Coordinates;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public float getAnnualTurnover() {
        return annualTurnover;
    }

    public Integer getEmployeesCount() {
        return employeesCount;
    }

    public int getUSERID() {return USERID;}

    public String getType() {
        if(type != null){ return type.toString();}else{return "Не указан";}
    }

    public String getOfficialAddress() {
        if(officialAddress != null){ return officialAddress.toString();}else{return "Не указан";}

    }

    @Override
    public String toString()
    {
        return ">Название: "+this.getName()+"\n"+
                "Id: " + this.getId()+"\n"+
                "Координаты: " + this.getCoordinates().toString()+"\n"+
                "Дата создания: " + this.getCreationDate() +"\n"+
                "Годовой оборот: " + this.getAnnualTurnover() +"\n"+
                "Количество сотрудников: " + this.getEmployeesCount()+"\n"+
                "Тип: " + this.getType()+"\n"+
                "Официальный адрес: " + this.getOfficialAddress()+'\n'+
                "Создана пользователем с ID = " + this.getUSERID()+'\n'+
                "\n"+"____________________________________"+"\n";
    }

}
