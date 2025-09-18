package chat.Chatroom;

import chat.GameManegment.GameManager;
import chat.GameManegment.GamePhase;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private final GameManager gameManager;

    public ClientHandler(Socket socket, GameManager gameManager) {
        this.socket = socket;
        this.gameManager = gameManager;


        // accept a player socket and make a handler
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Enter your username: ");
            username = in.readLine();


            //add players until game start
            if (!gameManager.isGameStarted()) {
                gameManager.addPlayer(username, ""); // role empty for now
                ChatServer.broadcastMessage(username + " has joined the chat!", this);
            } else {
                out.println("Game has already started. You cannot join now!");
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    // ------------------ MESSAGE HANDLERS ------------------

    private void handlePublicMessage(String message) {
        ChatServer.broadcastMessage(username + ": " + message, this);
    }

    private void handlePrivateMessage(String recipient, String message) {
        ChatServer.sendPrivateMessage(username, recipient, message);
    }

    private void handleNight(String message) {
        String role = gameManager.getRole(username);
        GameManager.NightRole expectedRole = gameManager.getCurrentNightRole();

        if (!role.equalsIgnoreCase(expectedRole.name())) {
            sendMessage("You cannot act now. Please wait for your turn.");
            return;
        }

        if (message.startsWith("/w ")) {
            String[] parts = message.split(" ", 3);
            if (parts.length >= 3) {
                handlePrivateMessage(parts[1], parts[2]);
            } else {
                sendMessage("Usage: /w <username> <message>");
            }
            return;
        }

        // Handle night action based on role
        switch (expectedRole) {
            case MAFIA:
                if (message.startsWith("/kill ")) {
                    String target = message.split(" ")[1];
                    gameManager.setNightAction(username, target);
                    sendMessage("You chose to kill " + target);
                } else {
                    sendMessage("Use /kill <player> to select your target.");
                    return;
                }
                break;

            case DOCTOR:
                if (message.startsWith("/save ")) {
                    String target = message.split(" ")[1];
                    gameManager.setNightAction(username, target);
                    sendMessage("You chose to save " + target);
                } else {
                    sendMessage("Use /save <player> to select your target.");
                    return;
                }
                break;

            case CHEERLEADER:
                if (message.startsWith("/cheer ")) {
                    String target = message.split(" ")[1];
                    gameManager.setNightAction(username, target);
                    sendMessage("You cheered " + target);
                } else {
                    sendMessage("Use /cheer <player> to select your target.");
                    return;
                }
                break;
        }

    }


    // speak in public and private
    private void handleDay(String message) {
        if (message.startsWith("/w ")) {
            String[] parts = message.split(" ", 3);
            if (parts.length >= 3) {
                handlePrivateMessage(parts[1], parts[2]);
            } else {
                sendMessage("Usage: /w <username> <message>");
            }
        } else {
            handlePublicMessage(message);
        }
    }


    private void handleVoting(String message) {
        if (message.startsWith("/vote ")) {
            String[] parts = message.split(" ", 2);
            if (parts.length == 2) {
                String target = parts[1];
                gameManager.vote( target);
                sendMessage("You voted for " + target);
                ChatServer.broadcastMessage(username + " has voted.", this);
            } else {
                sendMessage("Usage: /vote <playername>");
            }
        } else {
            sendMessage("Use /vote <playername> to vote.");
        }
    }



    private void handleGameOver(String message){
      boolean is_over=  gameManager.checkWinCondition();
      if (is_over){
          gameManager.endGame();
      }
    }


    // ------------------ RUN ------------------

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                GamePhase phase = gameManager.getCurrentPhase();

                switch (phase) {
                    case DAY :handleDay(message);
                    break;
                    case NIGHT :handleNight(message);
                    break;
                    case VOTING : handleVoting(message);
                    break;
                    case  GAME_OVER: handleGameOver(message);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Cleanup
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ChatServer.removeClient(this);
            ChatServer.broadcastMessage(username + " has left the chat.", this);
        }
    }
}
