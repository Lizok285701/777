import java.io.Serializable;



public class OrganizationWrap implements Serializable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates Coordinates; //Поле не может быть null
    private String creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private float annualTurnover; //Значение поля должно быть больше 0
    private Integer employeesCount; //Поле не может быть null, Значение поля должно быть больше 0
    private OrganizationType type = null; //Поле может быть null
    private Address officialAddress = null; //Поле может быть null

    public OrganizationWrap(String name, Coordinates coordinates, String creationDate,
                            float annualTurnover, Integer employeesCount, OrganizationType type, Address officialAddress){
        this.name = name;
        this.Coordinates = coordinates;
        this.creationDate = creationDate;
        this.annualTurnover = annualTurnover;
        this.employeesCount = employeesCount;
        this.type = type;
        this.officialAddress = officialAddress;

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

    public float getAnnualTurnover() { return annualTurnover; }

    public Integer getEmployeesCount() {
        return employeesCount;
    }

    public OrganizationType getType() {
        return type;
    }

    public Address getOfficialAddress() {
        return officialAddress;
    }

}
