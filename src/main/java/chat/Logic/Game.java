package chat.Logic;
import chat.Repository.GameRepository;
import java.util.HashMap;
import java.util.Map;

public class Game {

    private final GameRepository repository;
    private final Map<String, Integer> voteCount;

    // Constructor with Dependency Injection
    public Game(GameRepository repository) {
        this.repository = repository;
        this.voteCount = new HashMap<>();
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

    public void startDay() {
        // Optional: resolve previous night actions if needed
        System.out.println("Day phase started.");
    }

    public void startNight() {
        // Optional: prepare for night actions
        System.out.println("Night phase started.");
    }

    public void resolveVoting() {
        resolveVotes();
    }
}
