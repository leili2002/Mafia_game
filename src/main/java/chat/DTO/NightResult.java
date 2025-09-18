package chat.DTO;

public class NightResult {
    String killer;
    boolean alive;
    public  NightResult(String killer,boolean alive){
        this.killer=killer;
        this.alive=alive;
    }
    public String get_killer(){
        return killer;
    }
    public boolean getSaver(){
        return alive;
    }
}
