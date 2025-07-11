import java.util.HashSet;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends StackPane {
    
    private char file; 
    private int rank;
    private Color shade;
    private int size = 50;
    private Rectangle tileSquare;
    private String initial;
    boolean hasPiece = false;
    boolean clicked = false;
    boolean highlighted = false;

    Color dark = Color.rgb(51, 51, 51);
    Color lessdark = Color.rgb(27, 27, 27);

    public Tile() {
        file = 0;
        rank = 0;
        shade = Color.GREY;
        this.setInitial();

        tileSquare = new Rectangle(size, size, shade);
        this.getChildren().add(tileSquare);
    }

    public Tile(Piece piece) {
        file = 0;
        rank = 0;
        shade = Color.DARKSLATEBLUE;
        this.setInitial();

        tileSquare = new Rectangle(size, size, shade);
        this.getChildren().add(tileSquare);
        this.getChildren().add(piece);
    }

    public Tile(char tileFile, int tileRank) {
        file = tileFile;
        rank = tileRank;
        this.setInitial();
        if (((rank % 2 == 1) && (file == 'a' || file == 'c' || file == 'e' || file == 'g')) || ((rank % 2 == 0) && (file == 'b' || file == 'd' || file == 'f' || file == 'h'))) {
            shade = lessdark; //lighter
        }
        else {
            shade = dark; //darker
        }

        tileSquare = new Rectangle(size, size, shade);
        
        this.getChildren().add(tileSquare);
    }

    private void setInitial() {
        initial = file + "" + rank;
    }

    public String getInitial() {
        return initial;
    }

    public Color getShade() {
        return shade;
    }

    public void setClickAction(ChessBoard board) {
        EventHandler<MouseEvent> click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Tile canCap = null;
                HashSet<Tile> moves = null;
                //System.out.print(file + "" + rank + " ");
                if (highlighted) {
                    /*
                     * maybe create move method to handle most of this. 
                     * put in Piece class? 
                     * Piece.move(board, fromTile, targetTile)
                     * with validity check
                     * methodology for en passant, promotion, castling, 
                     */
                    //clicked = true;
                    Piece attackPiece = board.getClicked().getPiece();
                    attackPiece.move(board, board.getClicked(), board.getTile(file, rank));
                    board.getGame().setMoveList(board.getMoveList());
                    board.getGame().greyOutForward();
                    // maybe put back big thing here. moved to move method in Piece
                    board.setClicked(null);
                }
                for (Node node : board.getChildren()) {
                    Tile tile = (Tile) node;
                    tile.unhighlight();
                }
                
                /*
                 * TODO: setFill on from and to tiles of most recent move. 
                 * Looks like easiest way would be to read the most recent move, and get the startTile and destTile.
                 */
                if (hasPiece()) {
                    if (!clicked) {
                        clicked = true;
                        if (board.getClicked() != null) {
                            board.getClicked().clicked = false;
                        }
                        board.setClicked(board.getTile(file, rank));

                        if ((board.whiteTurn() && getPiece().getColor() == Color.WHITE) || (board.blackTurn() && getPiece().getColor() == Color.BLACK)) {

                            moves = getPiece().getMoves(board);

                            HashSet<Tile> toBeRemoved = new HashSet<Tile>();
                            for (Tile target : moves) {
                                Tile source = board.getTile(file, rank);
                                if(!getPiece().tryMove(board, source, target)) {
                                    toBeRemoved.add(target);
                                }
                            }

                            for (Tile tile : toBeRemoved) {
                                moves.remove(tile);
                            }

                            tileSquare.setFill(Color.SLATEBLUE);
                            for (Tile tile : moves) {
                                if (event.getButton() == MouseButton.SECONDARY) {
                                    tile.highlight(Color.DARKSLATEBLUE);
                                }
                                else tile.highlight(tile.getShade());
                                if (tile.hasPiece() && tile.getPiece().getColor() != getPiece().getColor()) {
                                    if (event.getButton() == MouseButton.SECONDARY) {
                                        tile.highlight(Color.DARKRED);
                                        canCap = tile;
                                    }
                                    else tile.highlight(tile.getShade());
                                }
                            }
                        }
                    }
                        
                    else {
                        clicked = false;
                        board.setClicked(null);
                        moves = getPiece().getMoves(board);
                        unhighlight();
                        for (Tile tile : moves) {
                            tile.unhighlight();
                        }
                    }
                    
                    
                }
                else {
                    if (board.getClicked() != null) {
                        board.getClicked().clicked = false;
                    }
                    board.setClicked(board.getTile(file, rank));
                }
                board.highlightLastMove();
                if (canCap != null) {
                    canCap.getTileSquare().setFill(Color.DARKRED);
                }
            }
        };
        this.setOnMouseClicked(click);
    }

    public Piece getPiece() {
        if (hasPiece) {
            return (Piece) this.getChildren().get(1);
        }
        else {
            return null;
        }
    }

    public Piece removePiece() {
        if (hasPiece) {
            Piece removed = (Piece) this.getChildren().remove(1);
            hasPiece = false;
            return removed;
        }
        else {
            return null;
        }
    }

    public void setPiece(Piece piece) {
        if (!hasPiece) {
            hasPiece = true;
            this.getChildren().add(piece);
        }
        else {
            this.getChildren().remove(1);
            this.getChildren().add(piece);
        }
    }

    public char getFile() {
        return file;
    }

    public int getRank() {
        return rank;
    }

    public boolean hasPiece() {
        return hasPiece;
    }

    public void highlight(Color highlight) {
        tileSquare.setFill(highlight);
        highlighted = true;
    }

    public void unhighlight() {
        tileSquare.setFill(shade);
        highlighted = false;
    }

    public Rectangle getTileSquare() {
        return tileSquare;
    }



    




}
