import java.io.Serializable;

public class Response implements Serializable {

    String content;

    public Response(String text){
        this.content = text;
    }

    public void addText(String addition) {
    this.content+=addition;
    }

    public void addTextForward(String addition) {
        this.content=addition+this.content;
    }

    @Override
    public String toString(){
        return this.content;
    }
}
