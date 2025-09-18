package chat.GameManegment;

import chat.Chatroom.ChatServer;
import chat.DTO.NightResult;
import chat.Repository.GameRepository;
import chat.Logic.Game;
import chat.Logic.Players;

import java.util.Timer;
import java.util.TimerTask;

public class GameManager {

    public enum NightRole {
        MAFIA,
        DOCTOR,
        CHEERLEADER
    }

    private NightRole currentNightRole;
    private GamePhase currentPhase = GamePhase.WAITING_FOR_PLAYERS;
    private final GameRepository gameRepository;
    private boolean gameStarted = false;

    private final Game game;
    private final Players players;
    private final Timer timer = new Timer();

    public GameManager(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        this.game = new Game(gameRepository);
        this.players = new Players(gameRepository);
    }

    // ---------------------- Player Management ----------------------
    public void addPlayer(String username, String role) {
        players.addplayer(username, role);
    }

    public String getRole(String player) {
        String role = gameRepository.getRole(player);
        return (role != null && !role.isEmpty()) ? role : "UNKNOWN";
    }

    public NightRole getCurrentNightRole() {
        return currentNightRole;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean started) {
        this.gameStarted = started;
    }

    // ---------------------- Game Start ----------------------
    public void startGame() {
        gameStarted = true;

        // Assign roles
        players.assignRoles();
        ChatServer.broadcastMessage("🎲 Roles have been assigned!", null);

        // Start first day
        startDay();
    }

    // ---------------------- Day ----------------------
    public void startDay() {
        currentPhase = GamePhase.DAY;
        game.startDay(); // resolve previous night actions if needed
        ChatServer.broadcastMessage("☀️ Day has started! Discuss and talk for 2 minutes.", null);

        // Schedule next phase: night after 2 minutes
        scheduleNextPhase(2 * 60, this::startNight);
    }

    // ---------------------- Night ----------------------
    public void startNight() {
        currentPhase = GamePhase.NIGHT;
        currentNightRole = NightRole.MAFIA;
        players.setNightPhase(true); // allow night actions
        ChatServer.broadcastMessage("🌙 Night has begun! Mafia, Doctor, and Cheerleader act now.", null);

        // Schedule next phase: voting after 1 minute
        scheduleNextPhase(60, this::startVoting);
    }

    // ---------------------- Voting ----------------------
    public void startVoting() {
        currentPhase = GamePhase.VOTING;
        players.setNightPhase(false); // disable night actions
        resolveNightActions();        // resolve night kills

        ChatServer.broadcastMessage("🗳️ Voting phase started! You have 1 minute to vote.", null);

        // Schedule next phase: day after 1 minute
        scheduleNextPhase(60, this::endVoting);
    }

    private void endVoting() {
        resolveVoting();

        if (checkWinCondition()) {
            endGame();
        } else {
            startDay();
        }
    }

    private void resolveNightActions() {
        NightResult result = players.resolveNightActions();
        if (result != null) {
            if (!result.alive) {
                ChatServer.broadcastMessage(result.target + " was killed by the mafia.", null);
            } else {
                ChatServer.broadcastMessage(result.target + " was saved by the doctor.", null);
            }
        } else {
            ChatServer.broadcastMessage("Nobody was killed last night.", null);
        }
    }

    private void resolveVoting() {
        game.resolveVotes();
    }

    // ---------------------- Night Actions ----------------------
    public void setNightAction(String player, String target) {
        String role = getRole(player);

        switch (role.toUpperCase()) {
            case "MAFIA":
                players.mafiaSelectTarget(target);
                break;
            case "DOCTOR":
                players.doctorSavePlayer(target);
                break;
            case "CHEERLEADER":
                players.seerCheck(target);
                break;
        }

        // Move to next night role
        nextNightRole();
    }

    public void nextNightRole() {
        if (currentNightRole == null) return;

        switch (currentNightRole) {
            case MAFIA:
                currentNightRole = NightRole.DOCTOR;
                break;
            case DOCTOR:
                currentNightRole = NightRole.CHEERLEADER;
                break;
            case CHEERLEADER:
                currentNightRole = null; // night ends
                break;
        }
    }

    // ---------------------- Voting ----------------------
    public void vote(String target) {
        game.castVote(target);
    }

    public boolean checkWinCondition() {
        return game.checkWinCondition();
    }

    // ---------------------- End Game ----------------------
    public void endGame() {
        currentPhase = GamePhase.GAME_OVER;
        ChatServer.broadcastMessage("🏁 Game over!", null);
        timer.cancel();
    }

    // ---------------------- Scheduler ----------------------
    private void scheduleNextPhase(int seconds, Runnable nextPhase) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                nextPhase.run();
            }
        }, seconds * 1000L);
    }
}
