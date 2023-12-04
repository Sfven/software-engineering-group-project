public enum Pc {
    KING,
    QUEEN,
    BISHOP,
    KNIGHT,
    ROOK,
    PAWN;
    //edit

    public String toString() {
        switch(this) {
            case KING:
            return "king";
            case QUEEN:
            return "queen";
            case BISHOP:
            return "bishop";
            case KNIGHT:
            return "knight";
            case ROOK:
            return "rook";
            case PAWN:
            return "pawn";
            default:
            return "";
        }
    }
}
