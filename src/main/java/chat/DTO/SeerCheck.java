package src.main.java.chat.DTO;

public class SeerCheck {
    String rule;
    String player_name;
    public  SeerCheck(String rule,String player_name){
        this.rule=rule;
        this.player_name=player_name;
    }
    public  String get_rule(){
        return rule;
    }
    public  String getPlayer_name(){
        return player_name;
    }
}
