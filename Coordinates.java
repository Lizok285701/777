import java.io.Serializable;

public class Coordinates implements Serializable {
    private long x; //min -903
    private int y; // max 551
    public Coordinates(long x, int y){
        this.x = x;
        this.y = y;
    }

    public long getX()
    {
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    @Override
    public String toString(){
        return this.x + " " + this.y;
    }

}
