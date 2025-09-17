package src.main.java.chat.Chatroom;

import src.main.java.chat.Chatroom.ClientHandler;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 5000; // Port the server listens on
    private static Set<ClientHandler> clientHandlers = new HashSet<>(); // Active clients

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Wait for a client
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket); // Create handler
                addClient(clientHandler); // Add to list
                new Thread(clientHandler).start(); // Start thread for client
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

    // Add a client to the set
    public static void addClient(ClientHandler client) {
        clientHandlers.add(client);
    }

    // Remove a client from the set
    public static void removeClient(ClientHandler client) {
        clientHandlers.remove(client);
    }
}