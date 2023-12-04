import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends Application {

    private static final String DB_URL = "jdbc:mysql://seprojectdb.sfven.xyz:10888/users";
    private static final String DB_USER = "app";
    private static final String DB_PASSWORD = "&*Mgw41#7evRnVym6CKmmc2jHoYG0*FX"; // this is a password. dont share it with bad people or they can jack with the database ¯\_(ツ)_/¯
    private String uName;
    private int wWins;
    private int wLoss;
    private String wAtmt;
    private int tWins;
    private int tLoss;
    private int tTies;
    private int cWins;
    private int cLoss;
    private int cDraws;
    Color darkModeSquare = Color.rgb(51, 51, 51);
    Color darkModeBackground = Color.rgb(27, 27, 27);
    Color textColor = Color.WHITE;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage loginStage) {

        loginStage.setTitle("Login");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setBackground(new Background(new BackgroundFill(darkModeBackground, CornerRadii.EMPTY, Insets.EMPTY)));

        TextField usernameField = new TextField();
        usernameField.setBackground(new Background(new BackgroundFill(darkModeSquare, CornerRadii.EMPTY, Insets.EMPTY)));
        usernameField.setStyle("-fx-text-fill: white;");
        PasswordField passwordField = new PasswordField();
        passwordField.setBackground(new Background(new BackgroundFill(darkModeSquare, CornerRadii.EMPTY, Insets.EMPTY)));
        passwordField.setStyle("-fx-text-fill: white;");
        Button loginButton = new Button("Login");
        loginButton.setBackground(new Background(new BackgroundFill(darkModeSquare, CornerRadii.EMPTY, Insets.EMPTY)));
        loginButton.setTextFill(textColor);
        Button registerButton = new Button("Register");
        registerButton.setBackground(new Background(new BackgroundFill(darkModeSquare, CornerRadii.EMPTY, Insets.EMPTY)));
        registerButton.setTextFill(textColor);

        Label userLabel = new Label("Username:");
        userLabel.setTextFill(textColor);
        Label passwordLabel = new Label("Password:");
        passwordLabel.setTextFill(textColor);

        grid.add(userLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(loginButton, 1, 2);
        grid.add(registerButton, 1, 3);

        loginButton.setOnAction(e -> {
            System.out.println("1");
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (validateLogin(username, password)) {
                System.out.println("2");
                Stage gamehubStage = new Stage();
                gamehub gamehub = new gamehub(uName, password, wWins, wLoss, wAtmt, tWins, tLoss, tTies, cWins, cLoss, cDraws);
                try {
                    loginStage.close();
                    gamehub.start(gamehubStage);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                showAlert("Login Failed", "Invalid username or password.");
            }
        });

        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (registerUser(username, password)) {
                validateLogin(username, password);
                Stage gamehubStage = new Stage();
                gamehub gamehub = new gamehub(uName, password, wWins, wLoss, wAtmt, tWins, tLoss, tTies);
                try {
                    loginStage.close();
                    gamehub.start(gamehubStage);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                showAlert("Registration Failed", "Username already exists or provided username/password are invalid.");
            }
        });

        Scene scene = new Scene(grid, 300, 200);
        scene.setFill(darkModeBackground);
        //System.out.println(scene.getFill().toString());
        loginStage.setScene(scene);
        loginStage.show();
    }

    private boolean validateLogin(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("3");
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                System.out.println("4");
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    System.out.println("5");
                    resultSet.next();
                    uName = resultSet.getString("username");
                    wWins = resultSet.getInt("wordle_wins");
                    wLoss = resultSet.getInt("wordle_losses");
                    wAtmt = resultSet.getString("wordle_attempt");
                    tWins = resultSet.getInt("tictactoe_wins");
                    tLoss = resultSet.getInt("tictactoe_losses");
                    tTies = resultSet.getInt("tictactoe_ties");
                    cWins = resultSet.getInt("chess_wins");
                    cLoss = resultSet.getInt("chess_losses"); 
                    cDraws = resultSet.getInt("chess_draws");
                    System.out.println("6");
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean usernameExists(String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // if user found, true
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean registerUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            if (usernameExists(username)) {
                return false;
            }
            if (username.length() > 65 || password.length() > 65) {
                showAlert("Invalid username/password", "Username/password cannot exceed 65 characters.");
                return false;
            }
            String query = "INSERT INTO users (username, password, wordle_wins, wordle_losses, wordle_attempt, tictactoe_wins, tictactoe_losses, tictactoe_ties, chess_wins, chess_losses, chess_draws) VALUES (?, ?, 0, 0, NULL, 0, 0, 0, 0, 0, 0)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

