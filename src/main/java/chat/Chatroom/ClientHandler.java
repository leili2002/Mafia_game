package chat.Chatroom;

import chat.Logic.Game;
import chat.Repository.GameRepository;
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

    public ClientHandler(Socket socket, Game game) {
        GameRepository gameRepository = new GameRepository();
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Enter your username: ");
            username = in.readLine();
            // Add player to database if game hasn't started
            if (!game.isGameStarted()) {
                gameRepository.addPlayer(username, ""); // role empty for now
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

    @Override
    public void run() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                if (message.startsWith("/w ")) {
                    // Private message format: /w recipient message
                    String[] parts = message.split(" ", 3);
                    if (parts.length >= 3) {
                        String recipient = parts[1];
                        String privateMsg = parts[2];
                      ChatServer.sendPrivateMessage(username, recipient, privateMsg);
                    } else {
                        sendMessage("Usage: /w <username> <message>");
                    }
                } else {
                    // Normal public message
                    ChatServer.broadcastMessage(username + ": " + message, this);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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
