package chat.Chatroom;
import chat.Logic.Game;
import chat.Repository.GameRepository;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 5000; // Port the server listens on
    private static final Set<ClientHandler> clientHandlers = new HashSet<>(); // Active clients
    private static final GameRepository gameRepository = new GameRepository();
    private static final Game game = new Game(gameRepository);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Wait for a client
                System.out.println("New client connected: " + clientSocket.getInetAddress());


                ClientHandler clientHandler = new ClientHandler(clientSocket, game);
                addClient(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast a message to all clients except the sender
    public static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clientHandlers) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    // Find a client handler by username
    public static ClientHandler getClientByName(String username) {
        for (ClientHandler client : clientHandlers) {
            if (client.getUsername().equalsIgnoreCase(username)) {
                return client;
            }
        }
        return null; // not found
    }

    public static void sendPrivateMessage(String sender, String recipient, String message) {
        ClientHandler client = getClientByName(recipient);
        if (client != null) {
            client.sendMessage("[Private from " + sender + "]: " + message);
        }
    }

    // Add a client to the set
    public static void addClient(ClientHandler client) {
        clientHandlers.add(client);
    }

    // Remove a client from the set
    public static void removeClient(ClientHandler client) {
        clientHandlers.remove(client);
    }
}