import java.io.Serializable;

public class Command implements Serializable {
    String description;
    boolean need_smth;
    public Command(String description, boolean need_smth){
        this.description = description;
        this.need_smth = need_smth;
    }

    public String content(){
        return this.description;
    }

    public boolean applied(){
        return need_smth;
    }
}
