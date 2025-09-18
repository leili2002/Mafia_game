package chat.Logic;
import chat.DTO.SeerCheck;
import chat.Repository.GameRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Players {

    private final GameRepository repository;
    private Map<String, Integer> voteCount;
    private boolean nightPhase;
    private String mafiaTarget;
    private String doctorSave;

    // Constructor with Dependency Injection
    public Players(GameRepository repository) {
        this.repository = repository;
        this.voteCount = new HashMap<>();
        this.nightPhase = false;
        this.mafiaTarget = null;
        this.doctorSave = null;
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
    public String mafiaSelectTarget(String targetName) {
        if (nightPhase) {
            mafiaTarget = targetName;
            // System.out.println("Mafia chose: " + targetName);
        }
        return targetName;
    }
    //---------------------------------------------------------------------

    // Doctor selects a player to save
    public String doctorSavePlayer(String playerName) {
        if (nightPhase) {
            doctorSave = playerName;
            // System.out.println("Doctor chose to save: " + playerName);
        }
        return playerName;
    }
    //---------------------------------------------------------------------

    // Seer checks a player and returns role
    public SeerCheck seerCheck(String playerName) {
        if (nightPhase) {
            String role = repository.getRole(playerName);
            SeerCheck data = new SeerCheck(role, playerName);
            //System.out.println("Seer checked " + playerName + " and found: " + role);
            return data;
        }
        return null;
    }
}
    //----------------------------------------------------------------------


