

//import java.awt.AWTException;
//import java.awt.Robot;
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Random;
import java.sql.PreparedStatement;

import javafx.application.Application;
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Node;
//import javafx.scene.Parent;
import javafx.scene.Scene;
//import javafx.scene.control.TextField;
//import javafx.scene.control.TextInputControl;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
//import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
//import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;


public class gamehub extends Application {

    private static final String DB_URL = "jdbc:mysql://seprojectdb.sfven.xyz:10888/users";
    private static final String DB_USER = "app";
    private static final String DB_PASSWORD = "&*Mgw41#7evRnVym6CKmmc2jHoYG0*FX"; // this is a password. dont share it with bad people or they can jack with the database ¯\_(ツ)_/¯

    private String username;
    private String password;
    private int wordleWins;
    private int wordleLosses;
    private String wordleAttempts;
    private int tictacWins = 0;
    private int tictacLosses;
    private int tictacTies;
    gamehub hub;

    double gameSquareSize = 150;
    double profRectWidth = 250;
    double profRectHeight = 75;
    double playButtonWidth = 200;
    double playButtonHeight = 25;
    double profPicSize = 25;
    Font gameSquareFont = new Font("Arial Bold", 24);
    Font usernameFont = new Font("Arial Bold", 16);
    Font playButtonFont = new Font("Arial Bold Italic", 16);
    Font statFont = new Font("Arial Italic", 12);

    Background white = new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
    Background clear = new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY));
    Background green = new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY));
    Background yellow = new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY));
    Background grey = new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY));
    Background lightgrey = new Background(new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY));

    public static void main(String[] args) {
        launch(args);
    }

    public gamehub() {
        super();
        username = "null";
        wordleWins = 0;
        wordleLosses = 0;
        wordleAttempts = "";
        tictacWins = 0;
        tictacLosses = 0;
        tictacTies = 0;
        hub = this;
    }
	
	public gamehub(String uName, String uPass, int wWins, int wLoss, String wAtmt, int tWins, int tLoss, int tTies) {
        super();
        username = uName;
        password = uPass;
        wordleWins = wWins;
        wordleLosses = wLoss;
        wordleAttempts = wAtmt;
        tictacWins = tWins;
        tictacLosses = tLoss;
        tictacTies = tTies;
        hub = this;
    }

	@Override
	public void start(Stage stage) throws Exception {
        
        
        GridPane gameGrid = new GridPane();
        GridPane profileGrid = new GridPane();
        gameGrid.setPadding(new Insets(10, 10, 10, 10));
		gameGrid.setHgap(10);
		gameGrid.setVgap(10);
		
		profileGrid.setPadding(new Insets(10, 10, 10, 10));
		profileGrid.setHgap(10);
		profileGrid.setVgap(10);
        
        GridPane grid = new GridPane();
		grid.add(gameGrid, 1, 0);
		grid.add(profileGrid, 0, 0);
		Scene scene = new Scene(grid);

        Rectangle wordleSquare = new Rectangle(gameSquareSize, gameSquareSize, Color.GREY);
        Rectangle tictacSquare = new Rectangle(gameSquareSize, gameSquareSize, Color.GREY);

        Text wordleText = new Text("WORDLE");
		wordleText.setFont(gameSquareFont);
		
        Text tictacText = new Text("TIC\nTAC\nTOE");
        tictacText.setFont(gameSquareFont);

        StackPane wordleStack = new StackPane();
        wordleStack.getChildren().addAll(wordleSquare, wordleText);

        StackPane tictacStack = new StackPane();
        tictacStack.getChildren().addAll(tictacSquare, tictacText);

        gameGrid.add(tictacStack, 0, 0);
        gameGrid.add(wordleStack, 1, 0);

        for (int row = 0; row < 4; row++) {
            int j = 0;
            if (row == 0) {
                j = 2;
            }
            for (int col = j; col < 3; col++) {
                gameGrid.add(new Rectangle(gameSquareSize, gameSquareSize, Color.LIGHTGREY), col, row);
            }
        }


        Rectangle profileRectangle = new Rectangle(profRectWidth, profRectHeight, Color.GREY);
        Circle profileCircle = new Circle(profPicSize, Color.BLACK);
        Text profileUser = new Text(username);
        profileUser.setFont(usernameFont);

        GridPane profileHeaderGrid = new GridPane();
        //profileHeaderGrid.add(profileCircle, 0, 0);
        profileHeaderGrid.add(profileUser, 0, 0);
        profileHeaderGrid.setPadding(new Insets(10, 10, 10, 10));
        profileHeaderGrid.setHgap(10);
        profileHeaderGrid.setVgap(10);
        StackPane profileHeaderStack = new StackPane();
        profileHeaderStack.getChildren().addAll(profileRectangle, profileHeaderGrid);
        profileGrid.add(profileHeaderStack, 0, 0);

        GridPane wordlePane = new GridPane();
        GridPane tictacPane = new GridPane();

        EventHandler<MouseEvent> wordleClick = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                profileGrid.getChildren().remove(tictacPane);
                profileGrid.getChildren().remove(wordlePane);

                wordlePane.setPadding(new Insets(10, 10, 10, 10));
                wordlePane.setHgap(10);
                wordlePane.setVgap(10);

                Rectangle playRectangle = new Rectangle(playButtonWidth, playButtonHeight, Color.CYAN);
                Text playText = new Text("PLAY");
                playText.setFill(Color.WHITE);
                playText.setFont(playButtonFont);

                StackPane playButtonStack = new StackPane();
                playButtonStack.getChildren().addAll(playRectangle, playText);

                EventHandler<MouseEvent> wordlePlayButtonClick = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        //System.out.println("Play button clicked");
                        Stage wordleStage = new Stage();
                        wordle wordle = new wordle(username, password, hub);
                        try {
                            //stage.close();
                            wordle.start(wordleStage);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                playButtonStack.setOnMouseClicked(wordlePlayButtonClick);

                wordlePane.add(playButtonStack, 0, 0);

                GridPane statGrid = new GridPane();
                statGrid.setPadding(new Insets(10, 10, 10, 10));
                statGrid.setHgap(10);
                statGrid.setVgap(10);

                /*
                 * Stats
                 */
                //int wordleWins = 0;
                //int wordleLosses = 0;
                double wordleAverage = 0.0;
                int lowest = 6;
                if (wordleAttempts == "") {
                    wordleAttempts = null;
                }
                if (wordleAttempts != null) {
                    String[] strArray = wordleAttempts.split(",");
                    int[] wordleHistory = new int[strArray.length];
                    for(int i = 0; i < strArray.length; i++) {
                    wordleHistory[i] = Integer.parseInt(strArray[i]);
                    }
                    int totalAttempts = 0;
                    for (int i = 0; i < wordleHistory.length; i++) {
                        totalAttempts = totalAttempts + wordleHistory[i];
                        if (wordleHistory[i] < totalAttempts) {
                            lowest = wordleHistory[i];
                        }
                    }
                    wordleAverage = totalAttempts / wordleHistory.length;
                }
                int wordlePersonalBest = lowest;

                String totalWins = "Total Wins: ";
                String totalLosses = "Total Losses: ";
                String averageGuesses = "Average Guesses: ";
                String bestGuess = "Lowest Guess: ";

                Text winText = new Text(totalWins);
                winText.setFont(statFont);
                Text lossText = new Text(totalLosses);
                lossText.setFont(statFont);
                Text averageText = new Text(averageGuesses);
                averageText.setFont(statFont);
                Text bestText = new Text(bestGuess);
                bestText.setFont(statFont);

                Text winValue = new Text(""+ wordleWins);
                winValue.setFont(statFont);
                Text lossValue = new Text(""+ wordleLosses);
                lossValue.setFont(statFont);
                Text averageValue = new Text(""+ wordleAverage);
                averageValue.setFont(statFont);
                Text bestValue = new Text(""+ wordlePersonalBest);
                bestValue.setFont(statFont);

                statGrid.add(winText, 0, 0);
                statGrid.add(winValue, 1, 0);
                statGrid.add(lossText, 0, 1);
                statGrid.add(lossValue, 1, 1);
                statGrid.add(averageText, 0, 2);
                statGrid.add(averageValue, 1, 2);
                statGrid.add(bestText, 0, 3);
                statGrid.add(bestValue, 1, 3);

                wordlePane.add(statGrid, 0, 1);
                profileGrid.add(wordlePane, 0, 1);

            }
        };
        wordleStack.setOnMouseClicked(wordleClick);

        EventHandler<MouseEvent> tictacClick = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                profileGrid.getChildren().remove(wordlePane);
                profileGrid.getChildren().remove(tictacPane);

                tictacPane.setPadding(new Insets(10, 10, 10, 10));
                tictacPane.setHgap(10);
                tictacPane.setVgap(10);

                Rectangle playRectangle = new Rectangle(playButtonWidth, playButtonHeight, Color.CYAN);
                Text playText = new Text("PLAY");
                playText.setFill(Color.WHITE);
                playText.setFont(playButtonFont);

                StackPane playButtonStack = new StackPane();
                playButtonStack.getChildren().addAll(playRectangle, playText);

                EventHandler<MouseEvent> tictacPlayButtonClick = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        System.out.println("Play button clicked");

                        String command = "python ../../tic_tac_toe.py";
                        try {
                            Process p = Runtime.getRuntime().exec(command);
                            p.waitFor();
                            p.destroy();
                            //stage.close();
                        }
                        catch (IOException e) {
                            System.out.println("python executing error (IO Exception)");
                        }
                        catch (InterruptedException e) {
                            System.out.println("interrupted exception");
                        }
                        System.out.println("Is tic tac toe running?");

                    }
                };

                playButtonStack.setOnMouseClicked(tictacPlayButtonClick);

                tictacPane.add(playButtonStack, 0, 0);

                GridPane statGrid = new GridPane();
                statGrid.setPadding(new Insets(10, 10, 10, 10));
                statGrid.setHgap(10);
                statGrid.setVgap(10);

                /*
                 * Stats
                 */
                int tictacWins = 0;
                int tictacLosses = 0;
                int tictacTies = 0;
                double tictacAverage = 0.0;

                String totalWins = "Total Wins: ";
                String totalLosses = "Total Losses: ";
                String totalTies = "Total Ties: ";
                String winAverage = "Win Average: ";

                Text winText = new Text(totalWins);
                winText.setFont(statFont);
                Text lossText = new Text(totalLosses);
                lossText.setFont(statFont);
                Text tieText = new Text(totalTies);
                tieText.setFont(statFont);
                Text avgText = new Text(winAverage);
                avgText.setFont(statFont);

                Text winValue = new Text(""+ tictacWins);
                winValue.setFont(statFont);
                Text lossValue = new Text(""+ tictacLosses);
                lossValue.setFont(statFont);
                Text tieValue = new Text(""+ tictacTies);
                tieValue.setFont(statFont);
                Text avgValue = new Text(""+ tictacAverage);
                avgValue.setFont(statFont);

                statGrid.add(winText, 0, 0);
                statGrid.add(winValue, 1, 0);
                statGrid.add(lossText, 0, 1);
                statGrid.add(lossValue, 1, 1);
                statGrid.add(tieText, 0, 2);
                statGrid.add(tieValue, 1, 2);
                statGrid.add(avgText, 0, 3);
                statGrid.add(avgValue, 1, 3);

                tictacPane.add(statGrid, 0, 1);
                profileGrid.add(tictacPane, 0, 1);

            }
        };
        tictacStack.setOnMouseClicked(tictacClick);

        
		
		
		
		stage.setMaximized(false);
		stage.setScene(scene);
		stage.setTitle("GameHub");
		stage.show();
    }

    public void addWordleWin(int attemptCount) {
        wordleWins++;
        if (wordleAttempts == null) wordleAttempts = "";
        wordleAttempts.concat(attemptCount + ",");
        updateStats();
    }

    public void addWordleLoss(int attemptCount) {
        wordleLosses++;
        if (wordleAttempts == null) wordleAttempts = "";
        wordleAttempts.concat(attemptCount + ",");
        updateStats();
    }

    public void updateStats() {
        String query = "UPDATE users SET username=?,password=?,wordle_wins=?,wordle_losses=?,wordle_attempt=?,tictactoe_wins=?,tictactoe_losses=?,tictactoe_ties=? WHERE username = ?";
        Connection connection;
        try {
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(9, username);
                preparedStatement.setString(2, password);
                preparedStatement.setInt(3, wordleWins);
                preparedStatement.setInt(4, wordleLosses);
                preparedStatement.setString(5, wordleAttempts);
                preparedStatement.setInt(6, tictacWins);
                preparedStatement.setInt(7, tictacLosses);
                preparedStatement.setInt(8, tictacTies);
                //...etc
                preparedStatement.executeUpdate();
                //return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
