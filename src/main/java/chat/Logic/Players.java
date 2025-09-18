package chat.Logic;
import chat.DTO.NightResult;
import chat.DTO.SeerCheck;
import chat.Repository.GameRepository;
import java.util.Collections;
import java.util.List;


public class Players {

    private final GameRepository repository;
    private boolean nightPhase;
    private String mafiaTarget;
    private String doctorSave;
    private String CheerTarget;

    // Constructor with Dependency Injection
    public Players(GameRepository repository) {
        this.repository = repository;
        this.nightPhase = false;
        this.mafiaTarget = null;
        this.doctorSave = null;
    }


    public void addplayer(String username,String role){
        repository.addPlayer(username,role);
    }
    // Assign roles randomly at game start
    public void assignRoles() {
        List<String> players = repository.getAllPlayerNames();
        Collections.shuffle(players); // randomize player order

        int index = 0;

        // Assign 4 Mafia
        for (; index < 4; index++) {
            repository.assignRole(players.get(index), "Mafia");
        }
        // Assign 1 Doctor
        repository.assignRole(players.get(index++), "Doctor");
        // Assign 1 Seer
        repository.assignRole(players.get(index++), "Seer");
        // Assign remaining Villagers
        for (; index < players.size(); index++) {
            repository.assignRole(players.get(index), "Villager");
        }
    }

    //---------------------------------------------------------------------
    // Mafia selects a target
    public void mafiaSelectTarget(String targetName) {
        if (nightPhase) {
            mafiaTarget = targetName;
            // System.out.println("Mafia chose: " + targetName);
        }
    }
    //---------------------------------------------------------------------

    // Doctor selects a player to save
    public void doctorSavePlayer(String playerName) {
        if (nightPhase) {
            doctorSave = playerName;
            // System.out.println("Doctor chose to save: " + playerName);
        }
    }

    //---------------------------------------------------------------------

    // Seer checks a player and returns role
    public void seerCheck(String playerName) {
        if (nightPhase) {
            String role = repository.getRole(playerName);
            SeerCheck data = new SeerCheck(role, playerName);
            //System.out.println("Seer checked " + playerName + " and found: " + role);
        }
    }

    public NightResult resolveNightActions() {
        if (mafiaTarget != null) {
            if (!mafiaTarget.equals(doctorSave)) {
                repository.updatePlayerStatus(mafiaTarget, false);
                // System.out.println(mafiaTarget + " has been killed by Mafia!");
                return new NightResult(mafiaTarget,false);
            } else {
                //System.out.println(mafiaTarget + " was saved by the Doctor!");
                return new NightResult(mafiaTarget,true);
            }
        }
        return null;
    }
    // Setter for nightPhase
    public void setNightPhase(boolean nightPhase) {
        this.nightPhase = nightPhase;
    }
}
    //----------------------------------------------------------------------


