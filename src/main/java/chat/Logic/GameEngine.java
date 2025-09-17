//package src.main.java.chat;
//import src.main.java.chat.DTO.NightResult;
//import src.main.java.chat.DTO.SeerCheck;
//import src.main.java.chat.Repository.GameRepository;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class GameEngine {
//
//    private final GameRepository repository;
//    private Map<String, Integer> voteCount;
//    private boolean nightPhase;
//    private String mafiaTarget;
//    private String doctorSave;
//
//    // Constructor with Dependency Injection
//    public GameEngine(GameRepository repository) {
//        this.repository = repository;
//        this.voteCount = new HashMap<>();
//        this.nightPhase = false;
//        this.mafiaTarget = null;
//        this.doctorSave = null;
//    }
//
//    // Assign roles randomly at game start
//    public void assignRoles() {
//        List<String> players = repository.getAllPlayerNames();
//        Collections.shuffle(players); // randomize player order
//
//        int index = 0;
//
//        // Assign 4 Mafia
//        for (; index < 4; index++) {
//            repository.assignRole(players.get(index), "Mafia");
//        }
//
//        // Assign 1 Doctor
//        repository.assignRole(players.get(index++), "Doctor");
//
//        // Assign 1 Seer
//        repository.assignRole(players.get(index++), "Seer");
//
//        // Assign remaining Villagers
//        for (; index < players.size(); index++) {
//            repository.assignRole(players.get(index), "Villager");
//        }
//    }
//
//    // Start night phase
//    public void startNight() {
//        nightPhase = true;
//        mafiaTarget = null;
//        doctorSave = null;
//      //  System.out.println("Night has started!");
//    }
//
//    // Start day phase
//    public void startDay() {
//        nightPhase = false;
//        resolveNightActions();
//       // System.out.println("Day has started!");
//    }
//
//    // Mafia selects a target
//    public String mafiaSelectTarget(String targetName) {
//        if (nightPhase) {
//            mafiaTarget = targetName;
//            // System.out.println("Mafia chose: " + targetName);
//        }
//        return targetName;
//    }
//
//    // Doctor selects a player to save
//    public String doctorSavePlayer(String playerName) {
//        if (nightPhase) {
//            doctorSave = playerName;
//           // System.out.println("Doctor chose to save: " + playerName);
//        }
//        return playerName;
//    }
//
//    // Seer checks a player and returns role
//    public SeerCheck seerCheck(String playerName) {
//        if (nightPhase) {
//            String role = repository.getRole(playerName);
//            SeerCheck data= new SeerCheck(role,playerName);
//            //System.out.println("Seer checked " + playerName + " and found: " + role);
//            return data;
//        }
//        return null;
//    }
//
//    // Resolve night actions: Mafia kill vs Doctor save
//    private NightResult resolveNightActions() {
//        if (mafiaTarget != null) {
//            if (!mafiaTarget.equals(doctorSave)) {
//                repository.updatePlayerStatus(mafiaTarget, false);
//                // System.out.println(mafiaTarget + " has been killed by Mafia!");
//                return new NightResult(mafiaTarget,false);
//            } else {
//                //System.out.println(mafiaTarget + " was saved by the Doctor!");
//                return new NightResult(mafiaTarget,true);
//            }
//        }
//        return null;
//    }
//
//
//    public void castVote(String targetName) {
//        voteCount.put(targetName, voteCount.getOrDefault(targetName, 0) + 1);
//        System.out.println("Vote casted for: " + targetName);
//    }
//
//    // End of day → calculate who has most votes and eliminate
//    public String resolveVotes() {
//        if (voteCount.isEmpty()) return null;
//
//        String mostVoted = null;
//        int maxVotes = 0;
//        boolean tie = false;
//
//        // Find player with most votes
//        for (Map.Entry<String, Integer> entry : voteCount.entrySet()) {
//            if (entry.getValue() > maxVotes) {
//                mostVoted = entry.getKey();
//                maxVotes = entry.getValue();
//                tie = false;
//            } else if (entry.getValue() == maxVotes) {
//                tie = true; // tie detected
//            }
//        }
//
//        // If tie, no one is eliminated
//        if (tie) {
//            System.out.println("Vote tie! No one is eliminated.");
//            voteCount.clear();
//            return null;
//        }
//
//        // Eliminate most voted player
//        repository.updatePlayerStatus(mostVoted, false);
//        System.out.println(mostVoted + " was eliminated by vote!");
//        voteCount.clear(); // reset votes for next day
//        return mostVoted;
//    }
//
//
//    // Check win condition
//    public boolean checkWinCondition() {
//        int aliveMafia = repository.countAliveByRole("Mafia");
//        int aliveVillagers = repository.countAliveByRole("Villager") +
//                repository.countAliveByRole("Doctor") +
//                repository.countAliveByRole("Seer");
//
//        if (aliveMafia == 0) {
//            repository.setWinner("Villagers");
//            System.out.println("Villagers win!");
//            return true;
//        } else if (aliveMafia >= aliveVillagers) {
//            repository.setWinner("Mafia");
//            System.out.println("Mafia wins!");
//            return true;
//        }
//
//        return false;
//    }
//}
