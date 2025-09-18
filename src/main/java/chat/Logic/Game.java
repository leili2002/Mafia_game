package chat.Logic;
import chat.DTO.NightResult;
import chat.Repository.GameRepository;
import java.util.HashMap;
import java.util.Map;

public class Game {

    private final GameRepository repository;
    private final Map<String, Integer> voteCount;
    private boolean nightPhase;
    private String mafiaTarget;
    private boolean gameStarted ;
    private String doctorSave;

    // Constructor with Dependency Injection
    public Game(GameRepository repository) {
        this.repository = repository;
        this.voteCount = new HashMap<>();
        this.nightPhase = false;
        this.mafiaTarget = null;
        this.doctorSave = null;
        this.gameStarted=false;
    }


   //-----------------------------------------------------------------------
    // Start night phase
    public void startNight() {
        nightPhase = true;
        mafiaTarget = null;
        doctorSave = null;
        //  System.out.println("Night has started!");
    }
    //------------------------------------------------------------------------

    public boolean isGameStarted() {
        return gameStarted;
    }

    // Setter method
    public void setGameStarted(boolean started) {
        this.gameStarted = started;
    }
    //---------------------------------------------------------------------

    // Start day phase
    public void startDay() {
        nightPhase = false;
        resolveNightActions();
        // System.out.println("Day has started!");
    }

    //----------------------------------------------------------------------

    // Resolve night actions: Mafia kill vs Doctor save
    private NightResult resolveNightActions() {
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
//------------------------------------------------------------------------------

    public void castVote(String targetName) {
        voteCount.put(targetName, voteCount.getOrDefault(targetName, 0) + 1);
        System.out.println("Vote casted for: " + targetName);
    }

    // End of day → calculate who has most votes and eliminate
    public String resolveVotes() {
        if (voteCount.isEmpty()) return null;

        String mostVoted = null;
        int maxVotes = 0;
        boolean tie = false;

        // Find player with most votes
        for (Map.Entry<String, Integer> entry : voteCount.entrySet()) {
            if (entry.getValue() > maxVotes) {
                mostVoted = entry.getKey();
                maxVotes = entry.getValue();
                tie = false;
            } else if (entry.getValue() == maxVotes) {
                tie = true; // tie detected
            }
        }

        // If tie, no one is eliminated
        if (tie) {
            System.out.println("Vote tie! No one is eliminated.");
            voteCount.clear();
            return null;
        }

        // Eliminate most voted player
        repository.updatePlayerStatus(mostVoted, false);
        System.out.println(mostVoted + " was eliminated by vote!");
        voteCount.clear(); // reset votes for next day
        return mostVoted;
    }

//-----------------------------------------------------------------------
    // Check win condition
    public boolean checkWinCondition() {
        int aliveMafia = repository.countAliveByRole("Mafia");
        int aliveVillagers = repository.countAliveByRole("Villager") +
                repository.countAliveByRole("Doctor") +
                repository.countAliveByRole("Seer");

        if (aliveMafia == 0) {
            repository.setWinner("Villagers");
            System.out.println("Villagers win!");
            return true;
        } else if (aliveMafia >= aliveVillagers) {
            repository.setWinner("Mafia");
            System.out.println("Mafia wins!");
            return true;
        }

        return false;
    }
}
