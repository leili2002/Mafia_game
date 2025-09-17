package src.main.java.chat.DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/mafia?useSSL=false&serverTimezone=UTC";
    private static final String USER = "myusername";
    private static final String PASSWORD = "mypassword";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }}


    //docker run --name mafia -e MYSQL_ROOT_PASSWORD=rootpasword -e MYSQL_DATABASE=mafia -e MYSQL_USER=myusername -e MYSQL_PASSWORD=mypassword -p 3306:3306 -d mysql:latest
