import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ChessBoard extends GridPane {

    Tile clicked;
    ArrayList<Piece> captured;
    ArrayList<String> moves;
    private boolean whiteTurn = true;
    private boolean blackTurn = false;
    String result = "*";

    Image white_king;
    Image white_king_check;
    Image white_queen;
    Image white_bishop;
    Image white_knight;
    Image white_rook;
    Image white_pawn;

    Image black_king;
    Image black_king_check;
    Image black_queen;
    Image black_bishop;
    Image black_knight;
    Image black_rook;
    Image black_pawn;

    Chess game;

    
    public ChessBoard() {
        captured = new ArrayList<Piece>();
        moves = new ArrayList<String>();

        createTiles();

        try {
            initializePieceImages();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ChessBoard(Chess game) {
        this.game = game;
        captured = new ArrayList<Piece>();
        moves = new ArrayList<String>();

        createTiles();

        try {
            initializePieceImages();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createTiles() {
        char file;
        int rank;
        for (file = 97; file < 105; file++) {
            for (rank = 8; rank > 0; rank--) {
                Tile tile = new Tile(file, rank);
                tile.setClickAction(this);
                this.add(tile, file-97, Math.abs(rank-8));
            }
        }
    }

    private void initializePieceImages() throws FileNotFoundException {
        String path = "gamehub/icons/";
        String white = "white";
        String black = "black";
        white_king = new Image(new FileInputStream(path + white + "_king.png"));
        white_king_check = new Image(new FileInputStream(path + white + "_king_check.png"));
        white_queen = new Image(new FileInputStream(path + white + "_queen.png"));
        white_bishop = new Image(new FileInputStream(path + white + "_bishop.png"));
        white_knight = new Image(new FileInputStream(path + white + "_knight.png"));
        white_rook = new Image(new FileInputStream(path + white + "_rook.png"));
        white_pawn = new Image(new FileInputStream(path + white + "_pawn.png"));

        black_king = new Image(new FileInputStream(path + black + "_king.png"));
        black_king_check = new Image(new FileInputStream(path + black + "_king_check.png"));
        black_queen = new Image(new FileInputStream(path + black + "_queen.png"));
        black_bishop = new Image(new FileInputStream(path + black + "_bishop.png"));
        black_knight = new Image(new FileInputStream(path + black + "_knight.png"));
        black_rook = new Image(new FileInputStream(path + black + "_rook.png"));
        black_pawn = new Image(new FileInputStream(path + black + "_pawn.png"));
    }

    public gamehub getHub() {
        return game.getHub();
    }

    public Tile getTile(char file, int rank) {
        if (file < 97 || file > 104 || rank < 1 || rank > 8) {
            return null;
        }
        int col = file - 97;
        int row = Math.abs(rank-8);
        int index = (col*8)+row;
        return (Tile) this.getChildren().get(index);
    }

    public Image getPieceIcon(Pc piece, Color color) {
        if (color == Color.WHITE) {
            switch (piece) {
                case KING: return white_king;
                case QUEEN: return white_queen;
                case BISHOP: return white_bishop;
                case KNIGHT: return white_knight;
                case ROOK: return white_rook;
                case PAWN: return white_pawn;
            }
        }
        if (color == Color.BLACK) {
            switch (piece) {
                case KING: return black_king;
                case QUEEN: return black_queen;
                case BISHOP: return black_bishop;
                case KNIGHT: return black_knight;
                case ROOK: return black_rook;
                case PAWN: return black_pawn;
            }
        }
        return null;
    }

    public Image getKingCheckIcon(Color color) {
        if (color == Color.WHITE) {
            return white_king_check;
        }
        if (color == Color.BLACK) {
            return black_king_check;
        }
        return null;
    }

    public Tile getTile(int file, int rank) {
        if (file < 97 || file > 104 || rank < 1 || rank > 8) {
            return null;
        }
        int col = file - 97;
        int row = Math.abs(rank-8);
        int index = (col*8)+row;
        return (Tile) this.getChildren().get(index);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String res) {
        result = res;
    }

    public void setClicked(Tile tile) {
        clicked = tile;
    }

    public Tile getClicked() {
        return clicked;
    }

    public void addCaptured(Piece piece) {
        captured.add(piece);
    }

    public void addMove(String move) {
        moves.add(move);
        if (moves.size() % 2 == 0) {
            String moveString = move +"\n";
            System.out.print(moveString);
            game.print(moveString);
            setWhiteTurn();
        }
        if (moves.size() % 2 == 1) {
            int turnNum = (moves.size() / 2) + 1;
            String moveString = turnNum + ". " + move + " ";
            System.out.print(moveString);
            game.print(moveString);
            setBlackTurn();
        }
    }

    public void readMove(String move) {
        Pc piece;
        Color color = null;
        if (moves.size()%2 == 0) {
            color = Color.WHITE;
        }
        if (moves.size()%2 == 1) {
            color = Color.BLACK;
        }
        char startFile = 0;
        int startRank = 0;
        char destFile = 0;
        int destRank = 0;
        char c = move.charAt(0);
        switch(c) {
            case ('K'): piece = Pc.KING; break;
            case ('O'): piece = Pc.KING; break;
            case ('Q'): piece = Pc.QUEEN; break;
            case ('B'): piece = Pc.BISHOP; break;
            case ('N'): piece = Pc.KNIGHT; break;
            case ('R'): piece = Pc.ROOK; break;
            default: piece = Pc.PAWN;
        }

        char firstFile = 0;
        char secondFile = 0;
        int firstRank = 0;
        int secondRank = 0;

        int numFiles = 0;
        int numRanks = 0;
        for (int i = 0; i < move.length(); i++) {
            if (charIsFile(move.charAt(i))) {
                numFiles++;
                if (numFiles == 1) {
                    firstFile = move.charAt(i);
                }
                if (numFiles == 2) {
                    secondFile = move.charAt(i);
                }
            }
            if (charIsRank(move.charAt(i))) {
                numRanks++;
                if (numRanks == 1) {
                    firstRank = Integer.parseInt(""+move.charAt(i));
                }
                if (numRanks == 2) {
                    secondRank = Integer.parseInt(""+move.charAt(i));
                }
            }
        }
        if (numFiles == 1) {
            destFile = firstFile;
        }
        if (numFiles == 2) {
            startFile = firstFile;
            destFile = secondFile;
        }
        if (numRanks == 1) {
            destRank = firstRank;
        }
        if (numRanks == 2) {
            startRank = firstRank;
            destRank = secondRank;
        }

        if (move.contains("O-O")) {
            //piece = Pc.KING;
            if (color == Color.WHITE) {
                startFile = 'e';
                startRank = 1;
                destFile = 'g';
                destRank = 1;
            }
            if (color == Color.BLACK) {
                startFile = 'e';
                startRank = 8;
                destFile = 'g';
                destRank = 8;
            }
        }
        if (move.contains("O-O-O")) {
            if (color == Color.WHITE) {
                startFile = 'e';
                startRank = 1;
                destFile = 'c';
                destRank = 1;
            }
            if (color == Color.BLACK) {
                startFile = 'e';
                startRank = 8;
                destFile = 'c';
                destRank = 8;
            }
        }

        for (Node node : this.getChildren()) {
            Tile tile = (Tile) node;
            if (tile.hasPiece() && tile.getPiece().getPc() == piece && tile.getPiece().getColor() == color) {
                if (tile.getPiece().getMoves(this).contains(this.getTile(destFile, destRank))) {
                    if ((startFile != 0 && tile.getFile() == startFile) || 
                        (startRank != 0 && tile.getRank() == startRank) ||
                        (startFile == 0) ||
                        (startRank == 0)) {
                            tile.getPiece().move(this, tile, this.getTile(destFile, destRank));
                    }
                }
            }
        }
        /* 
        if (piece == Pc.PAWN) {
            for (int i = 0; i < move.length(); i++) {
                if (i == 0) {
                    destFile = move.charAt(i);
                }
                if (i == 1) {
                    if (move.charAt(i) == 'x') {
                        startFile = move.charAt(0);
                        destFile = move.charAt(2);
                        destRank = Integer.parseInt(""+move.charAt(3)); 
                    }
                    if (move.charAt(i) == '0' || 
                        move.charAt(i) == '1' || 
                        move.charAt(i) == '2' || 
                        move.charAt(i) == '3' || 
                        move.charAt(i) == '4' || 
                        move.charAt(i) == '5' || 
                        move.charAt(i) == '6' || 
                        move.charAt(i) == '7' || 
                        move.charAt(i) == '8' || 
                        move.charAt(i) == '9') {
                            destRank = Integer.parseInt(""+move.charAt(i));
                    }
                }
            }
        }
        else {
            for (int i = 0; i < move.length(); i++) {
                if (i == 1) {
                    if (charIsInt(move.charAt(i))) { // N5
                        startRank = Integer.parseInt(""+move.charAt(i));
                    }
                    else { // Ne
                        destFile = move.charAt(i);
                    }
                }
                if (i == 2) {
                    if (move.charAt(i) == 'x') { // Nex or N5x
                        if (charIsInt(move.charAt(i-1))) { // N5x
                            startRank = Integer.parseInt(""+move.charAt(i-1));
                        } 
                        else { // Nex
                            startFile = move.charAt(i-1);
                        }
                    }
                    if (charIsInt(move.charAt(i))) { // Ne5
                        destRank = Integer.parseInt(""+move.charAt(i));
                    }
                    else { // Nec
                        destFile = move.charAt(i);
                        if (charIsInt(move.charAt(i-1))) { // N5
                            startRank = Integer.parseInt(""+move.charAt(i));
                        }
                        else { // Ne
                            startFile = move.charAt(i-1);
                        }
                    }
                }
                if (i == 3) {
                    if (move.charAt(i) == 'x') { // Ne5x
                        startFile = move.charAt(i-2);
                        startRank = Integer.parseInt(""+move.charAt(i-1));
                    }
                    if (charIsInt(move.charAt(i))) { //Nec6
                        startFile = move.charAt(i-2);
                        destFile = move.charAt(i-1);
                        destRank = Integer.parseInt(""+move.charAt(i));
                    }
                    else { // Ne5c or Nexc or N5xc
                        if (move.charAt(i-1) == 'x') { //Nexc or N5xc
                            if (charIsInt(move.charAt(i-2))) { // N5xc
                                startRank = Integer.parseInt(""+move.charAt(i-2));
                                destRank = move.charAt(i);
                            }
                            else { // Nexc
                                startFile = move.charAt(i-2);
                                destRank = move.charAt(i);
                            }
                        }
                        else { // Ne5c
                            startFile = move.charAt(i-2);
                            startRank = Integer.parseInt(""+move.charAt(i-1));
                            destRank = move.charAt(i);
                        }
                    }
                }
                if (i == 4) {
                    if (charIsInt(move.charAt(i))) { //Nexc6 or N5xc6 or Ne5c6
                        startFile = move.charAt(i-4);
                        startRank = Integer.parseInt(""+move.charAt(i-3));
                        destFile = move.charAt(i-1);
                        destRank = Integer.parseInt(""+move.charAt(i));
                    }
                    else { //Ne5xc
                        /*
                         * note to future Blaine:
                         * you're close to finishing this but its a mess.
                         * maybe start over and create two methods (repurpose charIsInt for one)
                         * charIsFile and charIsRank. and then maybe charIsCapture?
                         * and then just loop through the move string and see if each char is one of the three.
                         * then, depending on if it's file's first or second appearance in the string (and same with rank),
                         * designate it as start or dest.
                         
                    }
                }
            }
        }*/

    }

    public static boolean charIsFile(char c) {
        if (
            c == 'a' || 
            c == 'b' || 
            c == 'c' || 
            c == 'd' || 
            c == 'e' || 
            c == 'f' || 
            c == 'g' || 
            c == 'h'
            ) {
                return true;
        }
        else {
            return false;
        }
    }

    public static boolean charIsRank(char c) {
        if (
            c == '1' || 
            c == '2' || 
            c == '3' || 
            c == '4' || 
            c == '5' || 
            c == '6' || 
            c == '7' || 
            c == '8'
            ) {
                return true;
        }
        else {
            return false;
        }
    }

    public static boolean charIsCapture(char c) {
        if (c == 'x') {
            return true;
        }
        else {
            return false;
        }
    }

    public void setWhiteTurn() {
        whiteTurn = true;
        blackTurn = false;
    }

    public void setBlackTurn() {
        blackTurn = true;
        whiteTurn = false;
    }

    public boolean whiteTurn() {
        return whiteTurn;
    }

    public boolean blackTurn() {
        return blackTurn;
    }

    public void setPieces() throws FileNotFoundException {
        this.getTile('a', 1).setPiece(new Piece(this, Pc.ROOK, Color.WHITE, 'a', 1)); //white rook at a1
        this.getTile('b', 1).setPiece(new Piece(this, Pc.KNIGHT, Color.WHITE, 'b', 1)); //white knight at b1
        this.getTile('c', 1).setPiece(new Piece(this, Pc.BISHOP, Color.WHITE, 'c', 1)); //white bishop at c1
        this.getTile('d', 1).setPiece(new Piece(this, Pc.QUEEN, Color.WHITE, 'd', 1)); //white queen at d1
        this.getTile('e', 1).setPiece(new Piece(this, Pc.KING, Color.WHITE, 'e', 1)); //white king at e1
        this.getTile('f', 1).setPiece(new Piece(this, Pc.BISHOP, Color.WHITE, 'f', 1)); //white bishop at f1
        this.getTile('g', 1).setPiece(new Piece(this, Pc.KNIGHT, Color.WHITE, 'g', 1)); //white knight at g1
        this.getTile('h', 1).setPiece(new Piece(this, Pc.ROOK, Color.WHITE, 'h', 1)); //white rook at h1
        
        this.getTile('a', 2).setPiece(new Piece(this, Pc.PAWN, Color.WHITE, 'a', 2)); //white pawn at a2
        this.getTile('b', 2).setPiece(new Piece(this, Pc.PAWN, Color.WHITE, 'b', 2)); //white pawn at b2
        this.getTile('c', 2).setPiece(new Piece(this, Pc.PAWN, Color.WHITE, 'c', 2)); //white pawn at c2
        this.getTile('d', 2).setPiece(new Piece(this, Pc.PAWN, Color.WHITE, 'd', 2)); //white pawn at d2
        this.getTile('e', 2).setPiece(new Piece(this, Pc.PAWN, Color.WHITE, 'e', 2)); //white pawn at e2
        this.getTile('f', 2).setPiece(new Piece(this, Pc.PAWN, Color.WHITE, 'f', 2)); //white pawn at f2
        this.getTile('g', 2).setPiece(new Piece(this, Pc.PAWN, Color.WHITE, 'g', 2)); //white pawn at g2
        this.getTile('h', 2).setPiece(new Piece(this, Pc.PAWN, Color.WHITE, 'h', 2)); //white pawn at h2
        
        
        this.getTile('a', 8).setPiece(new Piece(this, Pc.ROOK, Color.BLACK, 'a', 8)); //black rook at a8
        this.getTile('b', 8).setPiece(new Piece(this, Pc.KNIGHT, Color.BLACK, 'b', 8)); //black knight at b8
        this.getTile('c', 8).setPiece(new Piece(this, Pc.BISHOP, Color.BLACK, 'c', 8)); //black bishop at c8
        this.getTile('d', 8).setPiece(new Piece(this, Pc.QUEEN, Color.BLACK, 'd', 8)); //black queen at d8
        this.getTile('e', 8).setPiece(new Piece(this, Pc.KING, Color.BLACK, 'e', 8)); //black king at e8
        this.getTile('f', 8).setPiece(new Piece(this, Pc.BISHOP, Color.BLACK, 'f', 8)); //black bishop at f8
        this.getTile('g', 8).setPiece(new Piece(this, Pc.KNIGHT, Color.BLACK, 'g', 8)); //black knight at g8
        this.getTile('h', 8).setPiece(new Piece(this, Pc.ROOK, Color.BLACK, 'h', 8)); //black rook at h8
        
        this.getTile('a', 7).setPiece(new Piece(this, Pc.PAWN, Color.BLACK, 'a', 7)); //black pawn at a2
        this.getTile('b', 7).setPiece(new Piece(this, Pc.PAWN, Color.BLACK, 'b', 7)); //black pawn at b2
        this.getTile('c', 7).setPiece(new Piece(this, Pc.PAWN, Color.BLACK, 'c', 7)); //black pawn at c2
        this.getTile('d', 7).setPiece(new Piece(this, Pc.PAWN, Color.BLACK, 'd', 7)); //black pawn at d2
        this.getTile('e', 7).setPiece(new Piece(this, Pc.PAWN, Color.BLACK, 'e', 7)); //black pawn at e2
        this.getTile('f', 7).setPiece(new Piece(this, Pc.PAWN, Color.BLACK, 'f', 7)); //black pawn at f2
        this.getTile('g', 7).setPiece(new Piece(this, Pc.PAWN, Color.BLACK, 'g', 7)); //black pawn at g2
        this.getTile('h', 7).setPiece(new Piece(this, Pc.PAWN, Color.BLACK, 'h', 7)); //black pawn at h2
        
    }

    public void clearBoard() {
        moves.clear();
        captured.clear();
        for (Node node : this.getChildren()) {
            Tile tile = (Tile) node;
            tile.removePiece();
        }
    }
}
