


import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

public class Chess extends Application {

    private Font arial = new Font("Arial", 12);
    private Font consolas = new Font("Consolas", 12);
    private ChessBoard board;
    private File gameFile;
    GridPane midgrid;
    gamehub hub;
    
    private String eventTag;
    private String siteTag;
    private String dateTag;
    private String roundTag;
    private String whiteTag;
    private String blackTag;
    private String resultTag;


    private Text gameText;
    private TextField whitePlayer;
    private TextField blackPlayer;
    private String username;
    private Text noteText;

    Color dark = Color.rgb(51, 51, 51);
    Color lessdark = Color.rgb(27, 27, 27);

    Insets defaultPadding;

    public static void main(String[] args) throws IOException {
		launch(args);
	}

    public Chess() {
        super();
        hub = null;
        username = "test";
    }

    public Chess(gamehub hub) {
        super();
        this.hub = hub;
        username = hub.getUsername();
    }
	

	@Override
	public void start(Stage stage) throws Exception {

        defaultPadding = new Insets(5, 5, 5, 5);

        board = new ChessBoard(this);
        board.setPieces();

        MenuBar taskBar = createTaskBar(stage);
        taskBar.setPrefWidth(610);

        GridPane lowgrid = createLowBar();
        
        gameText = new Text();
        gameText.setFont(consolas);
        gameText.setFill(Color.WHITE);
        gameText.setStyle("-fx-padding: 0;");

        ScrollPane textPane = new ScrollPane(gameText);
        textPane.setPrefHeight(400);
        textPane.setPrefWidth(200);
        textPane.setBackground(new Background(new BackgroundFill(dark, CornerRadii.EMPTY, Insets.EMPTY)));
        textPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 10; -fx-background-insets: 0;");
        //textPane.setPadding(new Insets(20,20,20,20));


        GridPane grid = new GridPane();
        GridPane topgrid = new GridPane();
        topgrid.add(taskBar, 0,0);
        midgrid = new GridPane();
        midgrid.add(board, 0,0);
        midgrid.add(textPane, 1,0);
        grid.add(topgrid, 0,0);
        grid.add(midgrid, 0,1);
        grid.add(lowgrid, 0,2);
        grid.setBackground(new Background(new BackgroundFill(dark, CornerRadii.EMPTY, Insets.EMPTY)));



        /* Tag Related Stuff */
        siteTag = "[Site \"" + "BlainoChess" + "\"]\n";

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        String dateString = formatter.format(date);
        dateTag = "[Date \"" + dateString + "\"]\n";

        int roundInt = 1;
        roundTag = "[Round \"" + roundInt + "\"]\n";



        Scene scene = new Scene(grid);
        scene.setFill(lessdark);
		
		stage.setMaximized(false);
		stage.setScene(scene);
		stage.setTitle("Chess");
		stage.show();



    }

    public gamehub getHub() {
        return hub;
    }
    
    public void print(String string) {
        gameText.setText(gameText.getText() + string);
    }

    public MenuBar createTaskBar(Stage stage) {
        int buttonWidth = 25;
        int buttonHeight = 10;
        Color buttonColor = Color.DARKSLATEGRAY;

        Menu file = new Menu("File");
        
        MenuItem saveGame = new MenuItem("Save Game");
        
        file.getItems().addAll(saveGame);
        //Creating a File chooser
        FileChooser saveFileChooser = new FileChooser();
        saveFileChooser.setTitle("Save Game");
        saveFileChooser.getExtensionFilters().addAll(new ExtensionFilter("PGN", "*.pgn"));
        //Adding action on the menu item
        saveGame.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //Opening a dialog box
                File saveFile = saveFileChooser.showSaveDialog(stage);
                buildPGN(saveFile);
                /* 
                try {
                    OutputStream os = new FileOutputStream(saveFile);
                    Files.copy(Paths.get(gameFile.getPath()), os);
                    os.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                */
                
            }
        });
        MenuItem loadGame = new MenuItem("Load Game");
        Chess game = this;
        
        file.getItems().addAll(loadGame);
        FileChooser loadFileChooser = new FileChooser();
        loadFileChooser.setTitle("Load Game");
        loadFileChooser.getExtensionFilters().addAll(new ExtensionFilter("PGN", "*.pgn"));
        loadGame.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                gameFile = loadFileChooser.showOpenDialog(stage);
                board.clearBoard();
                gameText.setText("");
                System.out.println("board cleared");
                try {
                    board.setPieces();
                    System.out.println("board set");
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                readGame(gameFile);
            }
        });
        //Creating a menu bar and adding menu to it.
        MenuBar menuBar = new MenuBar(file);
        //menuBar.setBackground(new Background(new BackgroundFill(dark, CornerRadii.EMPTY, Insets.EMPTY)));
        menuBar.setStyle("-fx-base: #FFFFFF;");
        //menuBar.setStyle("-fx-background-color: #333333;")
        return menuBar;
    }

    public GridPane createLowBar() {
        whitePlayer = new TextField();
        whitePlayer.setFont(consolas);
        whitePlayer.setStyle("-fx-text-fill: white;");
        whitePlayer.setBackground(new Background(new BackgroundFill(dark, CornerRadii.EMPTY, Insets.EMPTY)));
        Label whitePlayerLabel = new Label(" White: ");
        whitePlayerLabel.setFont(consolas);
        whitePlayerLabel.setTextFill(Color.WHITE);

        if (username != null) {
            whitePlayer.setText(username);
            whitePlayer.setEditable(false);
            whitePlayer.setFocusTraversable(false);
        }

        blackPlayer = new TextField();
        blackPlayer.setFont(consolas);
        blackPlayer.setStyle("-fx-text-fill: white;");
        blackPlayer.setBackground(new Background(new BackgroundFill(dark, CornerRadii.EMPTY, Insets.EMPTY)));
        Label blackPlayerLabel = new Label(" Black: ");
        blackPlayerLabel.setFont(consolas);
        blackPlayerLabel.setTextFill(Color.WHITE);

        noteText = new Text();
        noteText.setFont(consolas);
        noteText.setFill(Color.WHITE);

        GridPane lowGrid = new GridPane();
        GridPane noteGrid = new GridPane();
        GridPane nameGrid = new GridPane();
        lowGrid.setBackground(new Background(new BackgroundFill(lessdark, CornerRadii.EMPTY, Insets.EMPTY)));
        noteGrid.add(noteText, 0,0);
        nameGrid.add(whitePlayerLabel, 0,0);
        nameGrid.add(whitePlayer, 1,0);
        nameGrid.add(blackPlayerLabel, 2,0);
        nameGrid.add(blackPlayer, 3,0);
        lowGrid.add(noteGrid, 0,0);
        lowGrid.add(nameGrid, 0,1);
        
        return lowGrid;
    }

    public String getWhite() {
        return whitePlayer.getText();
    }

    public String getBlack() {
        return blackPlayer.getText();
    }

    public void setNote(String note) {
        noteText.setText(note);
    }

    public void buildPGN(File file) {

        eventTag = "[Event \"" + file.getName() + "\"]\n";

        whiteTag = "[White \"" + getWhite() + "\"]\n";
        blackTag = "[Black \"" + getBlack() + "\"]\n";

        resultTag = "[Result \"" + board.getResult() + "\"]\n";

        String gameString = gameText.getText();
        gameString = gameString.replaceAll("\n", " ");
        int newline = 0;
        for (int i = 0; i < gameString.length(); i++) {
            
            if (i == newline+79) {
                int j = 0;
                while (!(gameString.charAt(i-j) == ' ')) {
                    j++;
                }
                newline = i-j;
                StringBuilder editString = new StringBuilder(gameString);
                editString.setCharAt(newline, '\n');
                gameString = editString.toString();
            }
        }

        FileWriter writer;
        try {
            writer = new FileWriter(file);
            System.out.println(file.getName());
            writer.write(eventTag);
            System.out.print(eventTag);
            writer.write(siteTag);
            System.out.print(siteTag);
            writer.write(dateTag);
            System.out.print(dateTag);
            writer.write(roundTag);
            System.out.print(roundTag);
            writer.write(whiteTag);
            System.out.print(whiteTag);
            writer.write(blackTag);
            System.out.print(blackTag);
            writer.write(resultTag);
            System.out.print(resultTag);
            writer.write("\n");
            System.out.print("\n");
            writer.write(gameString);
            System.out.print(gameString);
            writer.write(board.getResult());
            System.out.print(board.getResult());
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readGame(File file) {
        HashMap<String, String> tagPairs = new HashMap<String, String>();
        HashMap<Integer, String> notes = new HashMap<Integer, String>();
        ArrayList<String> moves = new ArrayList<String>();
        ArrayList<String> nums = new ArrayList<String>();
        boolean tagStart = false;
        boolean valStart = false;
        boolean numStart = true;
        boolean moveStart = false;
        boolean noteStart = false;
        boolean resultStart = false;
        String tag = "";
        String val = "";
        String num = "";
        String move = "";
        String note = "";
        String result = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (reader.ready()) {
                String line = reader.readLine();
                //System.out.println(line);
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if (tagStart && c == ' ') {
                        tagStart = false;
                    }
                    if (tagStart) {
                        tag = tag+c;
                    }
                    if (!tagStart) {
                        if (c == '[') {
                            tagStart = true;
                        }
                    }
                    
                    if (valStart && c == '\"') {
                        valStart = false;
                        if (tag.length() > 0) {
                            tagPairs.put(tag, val);
                            System.out.println(tag + " " + val);
                            tag = "";
                            val = "";
                        }
                    }
                    else if (valStart) {
                        val = val+c;
                    }
                    else if (!valStart) {
                        if (c == '\"') {
                            valStart = true;
                        }
                    }

                    if (c == '0' || 
                        c == '1' || 
                        c == '2' || 
                        c == '3' || 
                        c == '4' || 
                        c == '5' || 
                        c == '6' || 
                        c == '7' || 
                        c == '8' || 
                        c == '9') {
                        if (!tagStart && !valStart && !noteStart) {
                            if (numStart) {
                                num = num+c;
                            }
                        }
                    }
                    if (c == '.' && !tagStart && !valStart && !noteStart) {
                        numStart = false;
                        nums.add(num);
                        num = "";
                    }

                    if (c == 'K' ||
                        c == 'Q' ||
                        c == 'B' ||
                        c == 'N' ||
                        c == 'R' ||
                        c == 'a' ||
                        c == 'b' ||
                        c == 'c' ||
                        c == 'd' ||
                        c == 'e' ||
                        c == 'f' ||
                        c == 'g' ||
                        c == 'h' ||
                        c == 'O') {
                            if (!tagStart && !valStart && !noteStart) {
                                moveStart = true;
                                move = move+c;
                            }
                    }
                    if ((c == ' ' || c == '\n') && moveStart) {
                        moveStart = false;
                        moves.add(move);
                        move = "";
                        numStart = true;
                    }
                    if (moveStart) {
                        move = move+c;
                    }

                    if(noteStart && c == '}') {
                        noteStart = false;
                        notes.put(moves.size()-1, note);
                        note = "";
                    }
                    if (noteStart) {
                        note = note+c;
                    }
                    if (!moveStart && !valStart && !tagStart) {
                        if (c == '{') {
                            noteStart = true;
                        }
                    }
                    if (!noteStart && !valStart && !moveStart) {
                        if (resultStart) {
                            result = result + c;
                        }
                        if (c == '-') {
                            result = num + c;
                            resultStart = true;
                        }
                        if (c == '*') {
                            result = ""+c;
                        }
                        if (c == '/') {
                            result = "1/2-1/2";
                        }
                    }
                }

            }
            reader.close();

            /* 
            private String eventTag;
            private String siteTag;
            private String dateTag;
            private String roundTag;
            private String whiteTag;
            private String blackTag;
            private String resultTag;
            */

            eventTag = tagPairs.get("Event");
            siteTag = tagPairs.get("Site");
            dateTag = tagPairs.get("Date");
            roundTag = tagPairs.get("Round");
            whiteTag = tagPairs.get("White");
            blackTag = tagPairs.get("Black");
            resultTag = tagPairs.get("Result");

            whitePlayer.setText(whiteTag);
            blackPlayer.setText(blackTag);

            for (String m : moves) {
                System.out.println(m);
                board.readMove(m);
            }

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }



    
}
