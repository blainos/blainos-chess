
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;

public class Piece extends ImageView {

    Color color;
    Pc pc;
    Image icon;
    Image check_icon;
    Image king_icon;
    Piece promotePiece;
    ChessBoard board;
    int size = 50;
    char file;
    int rank;
    String initial;

    boolean enPassantVulnerable = false;
    boolean hasMoved = false;
    boolean castled = false;
    boolean inCheck = false;
    boolean isReadMovePromote = false;

    public Piece() {
        super();
        color = Color.GREY;
        pc = Pc.PAWN;
        file = 0;
        rank = 0;
        this.setInitial();
    }

    public Piece(ChessBoard board, Pc piece, Color clr) throws FileNotFoundException {

        super();

        pc = piece;
        color = clr;
        file = 0;
        rank = 0;
        this.setInitial();
        this.board = board;

        icon = board.getPieceIcon(pc, color);
        check_icon = board.getKingCheckIcon(color);
        king_icon = board.getPieceIcon(Pc.KING, color);

        this.setImage(icon);
        this.setFitHeight(size);
        this.setFitWidth(size);
        this.setPreserveRatio(true);

    }

    public Piece(ChessBoard board, Pc piece, Color clr, char pieceFile, int pieceRank) throws FileNotFoundException {

        super();

        pc = piece;
        color = clr;
        file = pieceFile;
        rank = pieceRank;
        this.setInitial();
        this.board = board;

        icon = board.getPieceIcon(pc, color);
        check_icon = board.getKingCheckIcon(color);
        king_icon = board.getPieceIcon(Pc.KING, color);

        this.setImage(icon);
        this.setFitHeight(size);
        this.setFitWidth(size);
        this.setPreserveRatio(true);

    }

    private void setInitial() {
        switch(pc) {
            case BISHOP:
                initial = "B";
                break;
            case KING:
                initial = "K";
                break;
            case KNIGHT:
                initial = "N";
                break;
            case PAWN:
                initial = "";
                break;
            case QUEEN:
                initial = "Q";
                break;
            case ROOK:
                initial = "R";
                break;
            default:
                initial = "";
                break;
            
        }
    }

    public String getInitial() {
        return initial;
    }

    public void setFile(char pieceFile) {
        file = pieceFile;
    }

    public void setRank(int pieceRank) {
        rank = pieceRank;
    }

    public void setPos(char pieceFile, int pieceRank) {
        file = pieceFile;
        rank = pieceRank;
    }

    public char getFile() {
        return file;
    }

    public int getRank() {
        return rank;
    }

    public Color getColor() {
        return color;
    }

    public Pc getPc() {
        return pc;
    }

    public HashSet<Tile> getMoves(ChessBoard board) {
        HashSet<Tile> moves = new HashSet<Tile>();
        HashSet<Tile> toBeRemoved = new HashSet<Tile>();
        int j;
        switch(pc) {
            case KING:
                
                moves.add(board.getTile(file-1, rank));
                moves.add(board.getTile(file-1, rank+1));
                moves.add(board.getTile(file, rank+1));
                moves.add(board.getTile(file+1, rank+1));
                moves.add(board.getTile(file+1, rank));
                moves.add(board.getTile(file+1, rank-1));
                moves.add(board.getTile(file, rank-1));
                moves.add(board.getTile(file-1, rank-1));

                moves.remove(null);

                for (Tile tile : moves) {
                    if (tile.hasPiece()) {
                        if (tile.getPiece().getColor() == color) {
                            toBeRemoved.add(tile);
                        }
                    }
                }


                /*CASTLING */
                if (!hasMoved && !inCheck) {
                    //with Rook on left
                    Tile leftCorner = board.getTile('a', rank);
                    if (leftCorner.hasPiece() && leftCorner.getPiece().getPc() == Pc.ROOK && !leftCorner.getPiece().hasMoved) {
                        if (!board.getTile(file-1, rank).hasPiece() && !board.getTile(file-2, rank).hasPiece() && !board.getTile(file-3, rank).hasPiece()) {
                            //TODO: The king is not in check and does not pass through or finish on a square attacked by an enemy piece. 
                            moves.add(board.getTile(file-2, rank));
                        }
                    }
                    //with Rook on right
                    Tile rightCorner = board.getTile('h', rank);
                    if (rightCorner.hasPiece() && rightCorner.getPiece().getPc() == Pc.ROOK && !rightCorner.getPiece().hasMoved) {
                        if (!board.getTile(file+1, rank).hasPiece() && !board.getTile(file+2, rank).hasPiece()) {
                            //TODO: The king is not in check and does not pass through or finish on a square attacked by an enemy piece. 
                            moves.add(board.getTile(file+2, rank));
                        }
                    }
                }
                
                break;
            case QUEEN:
                //moving right
                for (int i = file+1; i < 105; i++) {
                    Tile tile = board.getTile(i, rank);
                    if (tile.hasPiece()) {
                        if (tile.getPiece().getColor() == color) {
                            break;
                        }
                        else {
                            moves.add(tile);
                            break;
                        }
                    }
                    moves.add(tile);
                }
                //moving left
                for (int i = file-1; i > 96; i--) {
                    Tile tile = board.getTile(i, rank);
                    if (tile.hasPiece()) {
                        if (tile.getPiece().getColor() == color) {
                            break;
                        }
                        else {
                            moves.add(tile);
                            break;
                        }
                    }
                    moves.add(tile);
                }
                //moving up
                for (int i = rank+1; i < 9; i++) {
                    Tile tile = board.getTile(file, i);
                    if (tile.hasPiece()) {
                        if (tile.getPiece().getColor() == color) {
                            break;
                        }
                        else {
                            moves.add(tile);
                            break;
                        }
                    }
                    moves.add(tile);
                }
                //moving down
                for (int i = rank-1; i > 0; i--) {
                    Tile tile = board.getTile(file, i);
                    if (tile.hasPiece()) {
                        if (tile.getPiece().getColor() == color) {
                            break;
                        }
                        else {
                            moves.add(tile);
                            break;
                        }
                    }
                    moves.add(tile);
                }
                //moving up and right
                j = file + 1;
                for (int i = rank+1; i < 9; i++) {
                    if (j >= 105) break;

                    Tile tile = board.getTile(j, i);
                    if (tile.hasPiece()) {
                        if (tile.getPiece().getColor() == color) {
                            break;
                        }
                        else {
                            moves.add(tile);
                            break;
                        }
                    }
                    moves.add(tile);

                    j++;
                }
                //moving down and right
                j = file + 1;
                for (int i = rank-1; i > 0; i--) {
                    if (j >= 105) break;

                    Tile tile = board.getTile(j, i);
                    if (tile.hasPiece()) {
                        if (tile.getPiece().getColor() == color) {
                            break;
                        }
                        else {
                            moves.add(tile);
                            break;
                        }
                    }
                    moves.add(tile);

                    j++;
                }
                //moving down and left
                j = file - 1;
                for (int i = rank-1; i > 0; i--) {
                    if (j <= 96) break;

                    Tile tile = board.getTile(j, i);
                    if (tile.hasPiece()) {
                        if (tile.getPiece().getColor() == color) {
                            break;
                        }
                        else {
                            moves.add(tile);
                            break;
                        }
                    }
                    moves.add(tile);

                    j--;
                }
                //moving up and left
                j = file - 1;
                for (int i = rank+1; i < 9; i++) {
                    if (j <= 96) break;

                    Tile tile = board.getTile(j, i);
                    if (tile.hasPiece()) {
                        if (tile.getPiece().getColor() == color) {
                            break;
                        }
                        else {
                            moves.add(tile);
                            break;
                        }
                    }
                    moves.add(tile);

                    j--;
                }
                
                break;
            case BISHOP:
                //moving up and right
                j = file + 1;
                for (int i = rank+1; i < 9; i++) {
                    if (j >= 105) break;

                    Tile tile = board.getTile(j, i);
                    if (tile.hasPiece()) {
                        if (tile.getPiece().getColor() == color) {
                            break;
                        }
                        else {
                            moves.add(tile);
                            break;
                        }
                    }
                    moves.add(tile);

                    j++;
                }
                //moving down and right
                j = file + 1;
                for (int i = rank-1; i > 0; i--) {
                    if (j >= 105) break;

                    Tile tile = board.getTile(j, i);
                    if (tile.hasPiece()) {
                        if (tile.getPiece().getColor() == color) {
                            break;
                        }
                        else {
                            moves.add(tile);
                            break;
                        }
                    }
                    moves.add(tile);

                    j++;
                }
                //moving down and left
                j = file - 1;
                for (int i = rank-1; i > 0; i--) {
                    if (j <= 96) break;

                    Tile tile = board.getTile(j, i);
                    if (tile.hasPiece()) {
                        if (tile.getPiece().getColor() == color) {
                            break;
                        }
                        else {
                            moves.add(tile);
                            break;
                        }
                    }
                    moves.add(tile);

                    j--;
                }
                //moving up and left
                j = file - 1;
                for (int i = rank+1; i < 9; i++) {
                    if (j <= 96) break;

                    Tile tile = board.getTile(j, i);
                    if (tile.hasPiece()) {
                        if (tile.getPiece().getColor() == color) {
                            break;
                        }
                        else {
                            moves.add(tile);
                            break;
                        }
                    }
                    moves.add(tile);

                    j--;
                }
                break;
            case KNIGHT:
                moves.add(board.getTile(file-1, rank+2));
                moves.add(board.getTile(file-2, rank+1));
                moves.add(board.getTile(file+1, rank+2));
                moves.add(board.getTile(file+2, rank+1));
                moves.add(board.getTile(file+1, rank-2));
                moves.add(board.getTile(file+2, rank-1));
                moves.add(board.getTile(file-1, rank-2));
                moves.add(board.getTile(file-2, rank-1));
                moves.remove(null);

                for (Tile tile : moves) {
                    if (tile.hasPiece()) {
                        if (tile.getPiece().getColor() == color) {
                            toBeRemoved.add(tile);
                        }
                    }
                }
                break;
            case ROOK:
                //moving right
                for (int i = file+1; i < 105; i++) {
                    Tile tile = board.getTile(i, rank);
                    if (tile.hasPiece()) {
                        if (tile.getPiece().getColor() == color) {
                            break;
                        }
                        else {
                            moves.add(tile);
                            break;
                        }
                    }
                    moves.add(tile);
                }
                //moving left
                for (int i = file-1; i > 96; i--) {
                    Tile tile = board.getTile(i, rank);
                    if (tile.hasPiece()) {
                        if (tile.getPiece().getColor() == color) {
                            break;
                        }
                        else {
                            moves.add(tile);
                            break;
                        }
                    }
                    moves.add(tile);
                }
                //moving up
                for (int i = rank+1; i < 9; i++) {
                    Tile tile = board.getTile(file, i);
                    if (tile.hasPiece()) {
                        if (tile.getPiece().getColor() == color) {
                            break;
                        }
                        else {
                            moves.add(tile);
                            break;
                        }
                    }
                    moves.add(tile);
                }
                //moving down
                for (int i = rank-1; i > 0; i--) {
                    Tile tile = board.getTile(file, i);
                    if (tile.hasPiece()) {
                        if (tile.getPiece().getColor() == color) {
                            break;
                        }
                        else {
                            moves.add(tile);
                            break;
                        }
                    }
                    moves.add(tile);
                }
                break;
            case PAWN:
                if (color == Color.WHITE) {
                    Tile forward = board.getTile(file, rank+1);
                    if (forward != null && !forward.hasPiece()) {
                        moves.add(forward);
                    }
                    if (rank == 2) {
                        if (!board.getTile(file, rank+2).hasPiece()) {
                            moves.add(board.getTile(file, rank+2));
                        }
                    }
                    Tile leftJab = board.getTile(file-1, rank+1);
                    Tile rightJab = board.getTile(file+1, rank+1);
                    if (leftJab != null && leftJab.hasPiece() && leftJab.getPiece().getColor() != color) {
                        moves.add(leftJab);
                    }
                    if (rightJab != null && rightJab.hasPiece() && rightJab.getPiece().getColor() != color) {
                        moves.add(rightJab);
                    }

                    Tile leftSide = board.getTile(file-1, rank);
                    Tile rightSide = board.getTile(file+1, rank);
                    if (leftSide != null && leftSide.hasPiece() && leftSide.getPiece().enPassantVulnerable) {
                        moves.add(board.getTile(file-1, rank+1));
                    }
                    if (rightSide != null && rightSide.hasPiece() && rightSide.getPiece().enPassantVulnerable) {
                        moves.add(board.getTile(file+1, rank+1));
                    }
                }
                if (color == Color.BLACK) {
                    Tile forward = board.getTile(file, rank-1);
                    if (forward != null && !forward.hasPiece()) {
                        moves.add(forward);
                    }
                    if (rank == 7) {
                        if (!board.getTile(file, rank-2).hasPiece()) {
                            moves.add(board.getTile(file, rank-2));
                        }
                    }
                    Tile leftJab = board.getTile(file-1, rank-1);
                    Tile rightJab = board.getTile(file+1, rank-1);
                    if (leftJab != null && leftJab.hasPiece() && leftJab.getPiece().getColor() != color) {
                        moves.add(leftJab);
                    }
                    if (rightJab != null && rightJab.hasPiece() && rightJab.getPiece().getColor() != color) {
                        moves.add(rightJab);
                    }

                    Tile leftSide = board.getTile(file-1, rank);
                    Tile rightSide = board.getTile(file+1, rank);
                    if (leftSide != null && leftSide.hasPiece() && leftSide.getPiece().enPassantVulnerable) {
                        moves.add(board.getTile(file-1, rank-1));
                    }
                    if (rightSide != null && rightSide.hasPiece() && rightSide.getPiece().enPassantVulnerable) {
                        moves.add(board.getTile(file+1, rank-1));
                    }
                }
                break;
            default:
                break;
        }

        for (Tile tile : toBeRemoved) {
            moves.remove(tile);
        }

        return moves;
    }

    public void move(ChessBoard board, Tile source, Tile target) {

        if (this.getMoves(board).contains(target) && source.getFile() == file && source.getRank() == rank) {

            String move = "";
            String from = "";
            String to = target.getInitial();
            String capture = "";
            String enPassant = "";
            String promotion = "";
            String check = "";
            //String space = " ";

            /* turn EN PASSANT vulnerable off for all pieces of this piece's team */
            for (Node node : board.getChildren()) {
                Tile tile = (Tile) node;
                if (tile.hasPiece() && tile.getPiece().getColor() == color) {
                    tile.getPiece().enPassantVulnerable = false;
                }
            }

            if (pc == Pc.PAWN) {
                from = "";
                /* EN PASSANT */
                if (enPassantVulnerable) {
                    enPassantVulnerable = false; //this is probably redundant now
                }
                if (color == Color.WHITE) {
                    if (source.getRank() == 2 && target.getRank() == 4) {
                        enPassantVulnerable = true;
                    }
                }
                if (color == Color.BLACK) {
                    if (source.getRank() == 7 && target.getRank() == 5) {
                        enPassantVulnerable = true;
                    }
                }
                if (target.getFile() != source.getFile()) {
                    from = "" + source.getFile();
                    if (!target.hasPiece()) {
                        Tile enPassantTile = board.getTile(target.getFile(), source.getRank());
                        if (enPassantTile.hasPiece()) {
                            if (enPassantTile.getPiece().enPassantVulnerable) {
                                Piece capturedPiece = enPassantTile.removePiece();
                                board.addCaptured(capturedPiece);
                                to = target.getInitial();
                                capture = "x";
                                //enPassant = " e.p.";
                            }
                        }
                    }
                }
            }
            /* CASTLING */
            if (pc == Pc.KING) {
                //castle left
                if (source.getFile() == 'e' && target.getFile() == 'c') {
                    Tile leftCorner = board.getTile('a', rank);
                    Piece rook = leftCorner.getPiece();
                    rook.castled = true;
                    rook.move(board, leftCorner, board.getTile('d', rank));
                    castled = true;
                    move = "O-O-O";
                }
                //castle right
                if (source.getFile() == 'e' && target.getFile() == 'g') {
                    Tile rightCorner = board.getTile('h', rank);
                    Piece rook = rightCorner.getPiece();
                    rook.castled = true;
                    rook.move(board, rightCorner, board.getTile('f', rank));
                    castled = true;
                    move = "O-O";
                }
            }

            /* ambiguity check */
            for (Node node : board.getChildren()) {
                Tile tile = (Tile) node;
                if (tile.hasPiece() 
                    && tile.getPiece().getColor() == color 
                    && tile.getPiece().getPc() == pc
                    && tile.getFile() != file
                    && tile.getRank() != rank) {
                    if (tile.getPiece().getMoves(board).contains(target)) {
                        if (tile.getPiece().getFile() != file) {
                            from = "" + file;
                        }
                        else if (tile.getPiece().getRank() != rank) {
                            from = "" + rank;
                        }
                        else {
                            from = source.getInitial();
                        }
                    }
                }
            }


            source.removePiece();
            if (target.hasPiece()) {
                Piece capturedPiece = target.getPiece();
                target.removePiece();
                board.addCaptured(capturedPiece);
                capture = "x";
            }
            target.setPiece(this);
            this.setPos(target.getFile(), target.getRank());
            hasMoved = true;


            /* PROMOTION */
            if (pc == Pc.PAWN) {
                if (target.getRank() == 1 || target.getRank() == 8) {
                    target.removePiece();
                    try {
                        Piece promote;
                        if (isReadMovePromote) {
                            System.out.print("isReadMovePromote:");
                            promote = promotePiece;
                        }
                        else {
                            promote = selectPromotion(color, target);
                        }
                        target.setPiece(promote);
                        promotion = "=" + promote.getInitial();
                    }
                    catch (FileNotFoundException e) {
                        System.out.print(e.getStackTrace());
                    }
                }
            }

            /* 
            for (Node node : board.getChildren()) {
                Tile tile = (Tile) node;
                if (tile.hasPiece() && tile.getPiece().getColor() != color && tile.getPiece().getPc() == Pc.KING) {
                    Piece king = tile.getPiece();
                    if (king.inCheck(board)) {
                        check = "+";
                    }
                }
            }
            */
            for (Node node : board.getChildren()) {
                        Tile kingTile = (Tile) node;
                        boolean inCheck = false;
                        if (kingTile.hasPiece() && kingTile.getPiece().getPc() == Pc.KING) {
                            Piece king = kingTile.getPiece();
                            if (king.inCheck(board)) {
                                check = "+";
                                king.setCheckIcon();
                                inCheck = true;
                            }
                            else {
                                king.setUncheckIcon();
                            }

                            Color team = king.getColor();

                            ArrayList<HashSet<Tile>> totalMoves = new ArrayList<HashSet<Tile>>();

                            for (Node n : board.getChildren()) {
                                Tile tile = (Tile) n;
                                if (tile.hasPiece() && tile.getPiece().getColor() == team) {
                                    HashSet<Tile> moves = tile.getPiece().getMoves(board);

                                    HashSet<Tile> toBeRemoved = new HashSet<Tile>();
                                    for (Tile dest : moves) {
                                        Tile start = tile;
                                        if(!tile.getPiece().tryMove(board, start, dest)) {
                                            toBeRemoved.add(dest);
                                        }
                                    }

                                    for (Tile t : toBeRemoved) {
                                        moves.remove(t);
                                    }

                                    totalMoves.add(moves);
                                }
                            }

                            boolean noMoves = true;
                            for (HashSet<Tile> moves : totalMoves) {
                                if (moves.size() > 0) {
                                    noMoves = false;
                                }
                            }
                            if (noMoves && inCheck) {
                                check = "#";
                                if (team == Color.WHITE) {
                                    board.setResult("0-1");
                                    //board.getGame().print("\n0-1");
                                }
                                if (team == Color.BLACK) {
                                    board.setResult("1-0");
                                    //board.getGame().print("\n1-0");
                                }
                                //System.out.println("Checkmate!");
                            }
                            if (noMoves && !inCheck) {
                                //System.out.println("1/2-1/2");
                                board.setResult("1/2-1/2");
                                //board.getGame().print("\n1/2-1/2");
                            }

                        }
                    }
            

            if (!castled) {
                move = this.getInitial() + from + capture + to + enPassant + promotion + check;
                //System.out.print(move + space);
                board.addMove(move);
            }
            else {
                if (pc == Pc.ROOK) { 
                    move = "";
                    //space = "";
                    castled = false;
                }
                else if (pc == Pc.KING) {
                    board.addMove(move);
                    castled = false;
                }
                else {
                    move = this.getInitial() + from + capture + to + enPassant + promotion + check;

                    //System.out.print(move + space);
                    board.addMove(move);
                }
            }

        }
        board.setLastMove(source, target);
        board.highlightLastMove();
    }

    public void setReadMovePromote(boolean b) {
        isReadMovePromote = b;
    }

    public void setPromotePiece(char p, Color color, Tile target) {
        try {
            switch(p) {
                case ('Q'): promotePiece = new Piece(board, Pc.QUEEN, color, target.getFile(), target.getRank()); break;
                case ('B'): promotePiece = new Piece(board, Pc.BISHOP, color, target.getFile(), target.getRank()); break;
                case ('N'): promotePiece = new Piece(board, Pc.KNIGHT, color, target.getFile(), target.getRank()); break;
                case ('R'): promotePiece = new Piece(board, Pc.ROOK, color, target.getFile(), target.getRank()); break;
                default: promotePiece = new Piece(board, Pc.PAWN, color, target.getFile(), target.getRank());
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public HashSet<Tile> getCheckSet(ChessBoard board) {
        HashSet<Tile> checkSet = new HashSet<Tile>();
        for (Node node : board.getChildren()) {
            Tile tile = (Tile) node;
            if (tile.hasPiece() && tile.getPiece().getColor() != color) {
                checkSet.addAll(tile.getPiece().getMoves(board));
            }
        }
        return checkSet;
    }

    public boolean inCheck(ChessBoard board) {
        boolean check = false;
        HashSet<Tile> checkSet = getCheckSet(board);
        for (Node node : board.getChildren()) {
            Tile tile = (Tile) node;
            if (tile.hasPiece() && tile.getPiece().getColor() == color && tile.getPiece().getPc() == Pc.KING) {
                if (checkSet.contains(tile)) {
                    check = true;
                    tile.getPiece().inCheck = true;
                    //tile.getPiece().setImage(check_icon);

                }
                else {
                    check = false; //this needs to be here right?
                    tile.getPiece().inCheck = false;
                    //tile.getPiece().setImage(king_icon);
                }
            }
        }
        return check;
    }

    public void setCheckIcon() {
        this.setImage(check_icon);
    }

    public void setUncheckIcon() {
        this.setImage(icon);
    }

    /**
     * Tries the move to see if it puts the King in check or not.
     * Returns TRUE if the piece can move without putting the King in check.
     * Returns FALSE if this move will put the King in check.
     */
    public boolean tryMove(ChessBoard board, Tile source, Tile target) {
        boolean canMove = true;
        Piece self = board.getTile(file, rank).removePiece();
        Piece capture = target.getPiece();
        Tile captureTile = target;
        boolean isCapture = false;
        if (target.hasPiece()) isCapture = true;

        if (pc == Pc.PAWN) {

            /* EN PASSANT */
            if (target.getFile() != source.getFile()) {
                if (!target.hasPiece()) {
                    Tile enPassantTile = board.getTile(target.getFile(), source.getRank());
                    if (enPassantTile.hasPiece()) {
                        if (enPassantTile.getPiece().enPassantVulnerable) {
                            capture = enPassantTile.removePiece();
                            captureTile = enPassantTile;
                            isCapture = true;
                        }
                    }
                }
            }
        }
       


        if (target.hasPiece()) {
            capture = target.removePiece();
        }
        target.setPiece(self);
        this.setPos(target.getFile(), target.getRank());

        if (inCheck(board)) { //right here lol
            canMove = false;
        }

        target.removePiece();
        if (isCapture) captureTile.setPiece(capture);
        source.setPiece(self);

        this.setPos(source.getFile(), source.getRank());


        return canMove;
    }

    public Piece selectPromotion(Color color, Tile target) throws FileNotFoundException {

        //board.getScene().getWindow().wait();

        Piece queen = new Piece(board, Pc.QUEEN, color, target.getFile(), target.getRank());
        Tile qT = new Tile(queen);
        Piece bishop = new Piece(board, Pc.BISHOP, color, target.getFile(), target.getRank());
        Tile bT = new Tile(bishop);
        Piece knight = new Piece(board, Pc.KNIGHT, color, target.getFile(), target.getRank());
        Tile kT = new Tile(knight);
        Piece rook = new Piece(board, Pc.ROOK, color, target.getFile(), target.getRank());
        Tile rT = new Tile(rook);
        
        GridPane selectionGrid = new GridPane();
        selectionGrid.setBackground(new Background(new BackgroundFill(target.getShade(), CornerRadii.EMPTY, Insets.EMPTY)));
        selectionGrid.add(qT, 0,0);
        selectionGrid.add(bT, 1,0);
        selectionGrid.add(kT, 2,0);
        selectionGrid.add(rT, 3,0);
        Scene selectionScene = new Scene(selectionGrid);
        Stage selectionStage = new Stage();
        selectionStage.setScene(selectionScene);
        selectionStage.setMaximized(false);
        selectionStage.setTitle("Select Promotion");
        selectionStage.initStyle(StageStyle.UNDECORATED);
        selectionStage.setAlwaysOnTop(true);
        selectionStage.initModality(Modality.WINDOW_MODAL);
        selectionStage.initOwner(board.getScene().getWindow());
        Bounds bounds = target.getBoundsInLocal();
        Bounds screenBounds = target.localToScreen(bounds);
        int x = (int) screenBounds.getMinX();
        int y = (int) screenBounds.getMinY();
        selectionStage.setX(x);
        selectionStage.setY(y);

        qT.setOnMouseClicked(event -> {
            promotePiece = queen;
            selectionStage.close();
            //board.getScene().getWindow().notify();
        });

        bT.setOnMouseClicked(event -> {
            promotePiece = bishop;
            selectionStage.close();
            //board.getScene().getWindow().notify();
        });

        kT.setOnMouseClicked(event -> {
            promotePiece = knight;
            selectionStage.close();
            //board.getScene().getWindow().notify();
        });

        rT.setOnMouseClicked(event -> {
            promotePiece = rook;
            selectionStage.close();
            //board.getScene().getWindow().notify();
        });

        selectionStage.showAndWait();

        return promotePiece;
    }


}
