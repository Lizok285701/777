import java.io.Serializable;

/**
 * Класс, ответственный за адрес организаций
 */
public class Address implements Comparable<Address>, Serializable {
    private String zipCode; //Длина строки не должна быть больше 15, Поле не может быть null
    public Address(String zipCode){
        this.zipCode=zipCode;
    }

    public String toString(){ return this.zipCode; }

    @Override
    public int compareTo(Address o) {

        if (o.zipCode.isEmpty() && this.zipCode.isEmpty()) return 0;
        if (o.zipCode.isEmpty()) return 1;
        if (this.zipCode.isEmpty()) return -1;
        return this.zipCode.compareTo(o.zipCode);
    }
}
