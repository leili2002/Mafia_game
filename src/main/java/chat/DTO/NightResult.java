package chat.DTO;

public class NightResult {
    public String target;
    public boolean alive;
    public  NightResult(String target,boolean alive){
        this.target=target;
        this.alive=alive;
    }
    public String get_killer(){
        return target;
    }
    public boolean getSaver(){
        return alive;
    }
}
