


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import javafx.stage.Stage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class Chess extends Application {

    private Font consolas = new Font("Consolas", 12);
    private ChessBoard board;
    private File gameFile;
    private GridPane midgrid;
    private GridPane elimGrid;
    
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
    private Text whiteScoreText;
    private Text blackScoreText;
    private int whiteScore;
    private int blackScore;
    private Text noteText;
    private ScrollPane textPane;

    private double greyOut = 0.0;

    private Image replay_beginning;
    private Image replay_previous;
    private Image replay_next;
    private Image replay_end;
    private Image replay_play;
    private Image replay_pause;
    private ImageView beginning;
    private ImageView previous;
    private ImageView next;
    private ImageView end;
    private ImageView play_pause;

    StackPane wQElimStack;
    StackPane wBElimStack;
    StackPane wNElimStack;
    StackPane wRElimStack;
    StackPane wPElimStack;
    StackPane bQElimStack;
    StackPane bBElimStack;
    StackPane bNElimStack;
    StackPane bRElimStack;
    StackPane bPElimStack;
    Image e1;
    Image e2;
    Image e3;
    Image e4;
    Image e5;
    Image e6;
    Image e7;
    Image e8;
    Image e9;
    Image e10;

    ArrayList<Image> elimNums;

    private final double ELIM_SIZE = 40;

    private ArrayList<String> moves;
    private HashMap<Integer, String> notes;

    boolean fileLoaded = false;
    boolean paused = true;

    Color dark = Color.rgb(51, 51, 51);
    Color lessdark = Color.rgb(27, 27, 27);

    Insets defaultPadding;

    public static void main(String[] args) throws IOException {
		launch(args);
	}
	

	@Override
	public void start(Stage stage) throws Exception {

        defaultPadding = new Insets(10, 10, 10, 10);
        moves = new ArrayList<String>();
        notes = new HashMap<Integer, String>();

        board = new ChessBoard(this);
        board.setPieces();

        MenuBar taskBar = createTaskBar(stage);

        GridPane lowgrid = createLowBar();
        initElimGrid();
        
        gameText = new Text();
        gameText.setFont(consolas);
        gameText.setFill(Color.WHITE);
        gameText.setStyle("-fx-padding: 0;");

        textPane = new ScrollPane(gameText);
        textPane.setPrefHeight(400);
        textPane.setPrefWidth(200);
        textPane.setBackground(new Background(new BackgroundFill(dark, CornerRadii.EMPTY, Insets.EMPTY)));
        textPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 10; -fx-background-insets: 0;");
        //textPane.setPadding(new Insets(20,20,20,20));


        GridPane grid = new GridPane();
        GridPane topgrid = new GridPane();
        topgrid.add(new StackPane(new Rectangle(645, 25, dark), taskBar), 0,0);
        midgrid = new GridPane();
        midgrid.add(board, 0,0);
        midgrid.add(elimGrid, 1,0);
        midgrid.add(textPane, 2,0);
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
		stage.setResizable(false);
		stage.setMaximized(false);
		stage.setScene(scene);
		stage.setTitle("Chess");
		stage.show();



    }
    
    public void print(String string) {
        gameText.setText(gameText.getText() + string);
        textPane.setVvalue(1.0);
    }

    public void initElimGrid() throws FileNotFoundException {
        elimGrid = new GridPane();
        
        wQElimStack = new StackPane();
        wBElimStack = new StackPane();
        wNElimStack = new StackPane();
        wRElimStack = new StackPane();
        wPElimStack = new StackPane();

        bQElimStack = new StackPane();
        bBElimStack = new StackPane();
        bNElimStack = new StackPane();
        bRElimStack = new StackPane();
        bPElimStack = new StackPane();

        /*
         * LAYOUT
         * 0 white pawns
         * 1 white knights
         * 2 white bishops
         * 3 white rooks
         * 4 white queens
         * 
         * 5 black queens
         * 6 black rooks
         * 7 black bishops
         * 8 black knights
         * 9 black pawns
         * 
         */
        elimGrid.add(wPElimStack, 0,0);
        elimGrid.add(wNElimStack, 0,1);
        elimGrid.add(wBElimStack, 0,2);
        elimGrid.add(wRElimStack, 0,3);
        elimGrid.add(wQElimStack, 0,4);
        elimGrid.add(bQElimStack, 0,5);
        elimGrid.add(bRElimStack, 0,6);
        elimGrid.add(bBElimStack, 0,7);
        elimGrid.add(bNElimStack, 0,8);
        elimGrid.add(bPElimStack, 0,9);

        for (Node node : elimGrid.getChildren()) {
            StackPane stack = (StackPane) node;
            stack.getChildren().add(new Rectangle(ELIM_SIZE,ELIM_SIZE,dark));
        }

        String path = "chess/icons/";
        e1 = new Image(new FileInputStream(path + "elim_1.png"));
        e2 = new Image(new FileInputStream(path + "elim_2.png"));
        e3 = new Image(new FileInputStream(path + "elim_3.png"));
        e4 = new Image(new FileInputStream(path + "elim_4.png"));
        e5 = new Image(new FileInputStream(path + "elim_5.png"));
        e6 = new Image(new FileInputStream(path + "elim_6.png"));
        e7 = new Image(new FileInputStream(path + "elim_7.png"));
        e8 = new Image(new FileInputStream(path + "elim_8.png"));
        e9 = new Image(new FileInputStream(path + "elim_9.png"));
        e10 = new Image(new FileInputStream(path + "elim_10.png"));

        /*
         * It appears that using ImageView means trying add any of these elements to the elimStacks means
         * also removing it from the stack it was in prior?
         * So I'm changing them to Images. And then creating new ImageView with each stack push
         * The above ImageView declarations are now pointless I think
         */
        elimNums = new ArrayList<Image>();
        elimNums.add(null);
        elimNums.add(e1);
        elimNums.add(e2);
        elimNums.add(e3);
        elimNums.add(e4);
        elimNums.add(e5);
        elimNums.add(e6);
        elimNums.add(e7);
        elimNums.add(e8);
        elimNums.add(e9);
        elimNums.add(e10);
    }

    public void updateCaptured(Piece piece) {

        ImageView elimPieceView = new ImageView(board.getElimIcon(piece.getPc(), piece.getColor()));
        elimPieceView.setFitWidth(ELIM_SIZE);
        elimPieceView.setFitHeight(ELIM_SIZE);
        elimPieceView.setPreserveRatio(true);
        elimPieceView.setOpacity(0.5);

        for (Node node : elimGrid.getChildren()) {
            StackPane stack = (StackPane) node;
            if (stack.getChildren().size() > 1) {
                stack.getChildren().remove(stack.getChildren().size()-1);
            }
        }

        if (piece.getColor() == Color.WHITE) {
            switch (piece.getPc()) {
                case KING: break;
                case QUEEN: ((StackPane) elimGrid.getChildren().get(4)).getChildren().add(elimPieceView); break;
                case ROOK: ((StackPane) elimGrid.getChildren().get(3)).getChildren().add(elimPieceView); break;
                case BISHOP: ((StackPane) elimGrid.getChildren().get(2)).getChildren().add(elimPieceView); break;
                case KNIGHT: ((StackPane) elimGrid.getChildren().get(1)).getChildren().add(elimPieceView); break;
                case PAWN: ((StackPane) elimGrid.getChildren().get(0)).getChildren().add(elimPieceView); break;
                default: ;
            }
        }
        if (piece.getColor() == Color.BLACK) {
            switch (piece.getPc()) {
                case KING: break;
                case QUEEN: ((StackPane) elimGrid.getChildren().get(5)).getChildren().add(elimPieceView); break;
                case ROOK: ((StackPane) elimGrid.getChildren().get(6)).getChildren().add(elimPieceView); break;
                case BISHOP: ((StackPane) elimGrid.getChildren().get(7)).getChildren().add(elimPieceView); break;
                case KNIGHT: ((StackPane) elimGrid.getChildren().get(8)).getChildren().add(elimPieceView); break;
                case PAWN: ((StackPane) elimGrid.getChildren().get(9)).getChildren().add(elimPieceView); break;
                default: ;
            }
        }

        for (Node node : elimGrid.getChildren()) {
            StackPane stack = (StackPane) node;
            if (stack.getChildren().size() > 1) {
                ImageView elimView = new ImageView(elimNums.get(stack.getChildren().size()-1));
                elimView.setFitWidth(ELIM_SIZE);
                elimView.setFitHeight(ELIM_SIZE);
                elimView.setPreserveRatio(true);
                elimView.setOpacity(0.5);
                stack.getChildren().add(elimView);
            }
        }

    }

    public void clearElimGrid() {
        for (Node node : elimGrid.getChildren()) {
            StackPane stack = (StackPane) node;
            stack.getChildren().clear();
            stack.getChildren().add(new Rectangle(ELIM_SIZE,ELIM_SIZE,dark));
        }

    }

    public MenuBar createTaskBar(Stage stage) {

        Menu file = new Menu();
        Label fileLabel = new Label("File");
        //fileLabel.setPrefWidth(40);
        fileLabel.setStyle("-fx-text-fill: white");
        file.setGraphic(fileLabel);

        Menu game = new Menu();
        Label gameLabel = new Label("Game");
        //gameLabel.setPrefWidth(40);
        gameLabel.setStyle("-fx-text-fill: white");
        game.setGraphic(gameLabel);

        Menu view = new Menu();
        Label viewLabel = new Label("View");
        //viewLabel.setPrefWidth(40);
        viewLabel.setStyle("-fx-text-fill: white");
        view.setGraphic(viewLabel);
        
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
                fileLoaded = true;
                if (moves.size() > 0) {
                    beginning.setOpacity(1);
                    previous.setOpacity(1);

                    play_pause.setOpacity(0.5);
                    next.setOpacity(0.5);
                    end.setOpacity(0.5);
                    greyOut = 0.5;
                }
            }
        });

        MenuItem resetGame = new MenuItem("Reset Game");
        game.getItems().addAll(resetGame);
        resetGame.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                board.clearBoard();
                notes.clear();
                moves.clear();
                try {
                    board.setPieces();
                    System.out.println("board set");
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                gameText.setText("");
            }
        });

        MenuItem flipBoard = new MenuItem("Flip Board");
        view.getItems().addAll(flipBoard);
        flipBoard.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                board.flip();
            }
        });



        //Creating a menu bar and adding menu to it.
        MenuBar menuBar = new MenuBar(file, game, view);
        //menuBar.setBackground(new Background(new BackgroundFill(dark, CornerRadii.EMPTY, Insets.EMPTY)));
        //menuBar.setStyle("-fx-base: #333333;");
        menuBar.setStyle("-fx-background-color: #1b1b1b;");
        //menuBar.setPrefWidth(stage.getWidth());
        return menuBar;
    }

    public GridPane createLowBar() throws FileNotFoundException {
        whitePlayer = new TextField();
        whitePlayer.setFont(consolas);
        whitePlayer.setStyle("-fx-text-fill: white;");
        whitePlayer.setBackground(new Background(new BackgroundFill(dark, CornerRadii.EMPTY, Insets.EMPTY)));
        Label whitePlayerLabel = new Label(" White: ");
        whitePlayerLabel.setFont(consolas);
        whitePlayerLabel.setTextFill(Color.WHITE);

        blackPlayer = new TextField();
        blackPlayer.setFont(consolas);
        blackPlayer.setStyle("-fx-text-fill: white;");
        blackPlayer.setBackground(new Background(new BackgroundFill(dark, CornerRadii.EMPTY, Insets.EMPTY)));
        Label blackPlayerLabel = new Label(" Black: ");
        blackPlayerLabel.setFont(consolas);
        blackPlayerLabel.setTextFill(Color.WHITE);

        Text whiteScoreLabel = new Text(" Score: ");
        whiteScoreLabel.setFont(consolas);
        whiteScoreLabel.setFill(Color.WHITE);
        Text blackScoreLabel = new Text(" Score: ");
        blackScoreLabel.setFont(consolas);
        blackScoreLabel.setFill(Color.WHITE);

        whiteScoreText = new Text(""+whiteScore);
        whiteScoreText.setFont(consolas);
        whiteScoreText.setFill(Color.WHITE);
        blackScoreText = new Text(""+blackScore);
        blackScoreText.setFont(consolas);
        blackScoreText.setFill(Color.WHITE);

        int replayButtonSize = 15;

        replay_beginning = new Image(new FileInputStream("chess/icons/replay_beginning.png"));
        beginning = new ImageView(replay_beginning);
        replay_previous = new Image(new FileInputStream("chess/icons/replay_previous.png"));
        previous = new ImageView(replay_previous);
        replay_next = new Image(new FileInputStream("chess/icons/replay_next.png"));
        next = new ImageView(replay_next);
        replay_end = new Image(new FileInputStream("chess/icons/replay_end.png"));
        end = new ImageView(replay_end);
        replay_play = new Image(new FileInputStream("chess/icons/replay_play.png"));
        replay_pause = new Image(new FileInputStream("chess/icons/replay_pause.png"));
        play_pause = new ImageView(replay_play);
        beginning.setOpacity(0.0);
        beginning.setFitWidth(replayButtonSize);
        beginning.setFitHeight(replayButtonSize);
        beginning.setPreserveRatio(true);
        previous.setOpacity(0.0);
        previous.setFitWidth(replayButtonSize);
        previous.setFitHeight(replayButtonSize);
        previous.setPreserveRatio(true);
        next.setOpacity(0.0);
        next.setFitWidth(replayButtonSize);
        next.setFitHeight(replayButtonSize);
        next.setPreserveRatio(true);
        end.setOpacity(0.0);
        end.setFitWidth(replayButtonSize);
        end.setFitHeight(replayButtonSize);
        end.setPreserveRatio(true);
        play_pause.setOpacity(0.0);
        play_pause.setFitWidth(replayButtonSize);
        play_pause.setFitHeight(replayButtonSize);
        play_pause.setPreserveRatio(true);
        EventHandler<MouseEvent> begClick = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (fileLoaded) {
                    replay(-1);
                    beginning.setOpacity(0.5);
                    previous.setOpacity(0.5);
                }
            }
        };
        EventHandler<MouseEvent> prevClick = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (fileLoaded) {
                    replay(board.getMoveList().size()-2);
                }
            }
        };
        EventHandler<MouseEvent> nextClick = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (fileLoaded) {
                    replay(board.getMoveList().size());
                }
            }
        };
        EventHandler<MouseEvent> endClick = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (fileLoaded) {
                    replay(moves.size()-1);
                }
            }
        };
        Chess game = this;
        Timeline player = new Timeline(
            new KeyFrame(Duration.seconds(0.5), event2 -> game.replay(board.getMoveList().size()))
        );
        player.setAutoReverse(false); //idk if this is necessary
        EventHandler<MouseEvent> playClick = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (fileLoaded) {
                    if (paused) {
                        paused = false;
                        play_pause.setImage(replay_pause);

                        player.setCycleCount(moves.size()-board.getMoveList().size());
                        player.play();
                        
                    }
                    else {
                        paused = true;
                        play_pause.setImage(replay_play);
                        player.pause();
                    }
                }
            }
        };
        beginning.setOnMouseClicked(begClick);
        previous.setOnMouseClicked(prevClick);
        next.setOnMouseClicked(nextClick);
        end.setOnMouseClicked(endClick);
        play_pause.setOnMouseClicked(playClick);
        GridPane replayGrid = new GridPane();
        replayGrid.add(beginning, 0,0);
        replayGrid.add(previous, 1,0);
        replayGrid.add(play_pause, 2,0);
        replayGrid.add(next, 3,0);
        replayGrid.add(end, 4,0);
        replayGrid.setHgap(10);

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
        nameGrid.add(whiteScoreLabel, 0,1);
        nameGrid.add(whiteScoreText, 1,1);
        nameGrid.add(blackScoreLabel, 2,1);
        nameGrid.add(blackScoreText, 3,1);
        nameGrid.setPadding(defaultPadding);
        nameGrid.setVgap(5);
        nameGrid.setHgap(5);
        lowGrid.add(noteGrid, 0,0);
        lowGrid.add(nameGrid, 0,1);

        nameGrid.add(new Rectangle(150, 10, lessdark), 4,1);
        nameGrid.add(replayGrid, 5,1);
        
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

    public void addScore(Piece piece) {
        if (piece.getColor() == Color.WHITE) {
            switch(piece.getPc()) {
                case KING: break; //king never gets captured
                case QUEEN: blackScore += 9; break;
                case BISHOP: blackScore += 3; break;
                case KNIGHT: blackScore += 3; break;
                case ROOK: blackScore += 5; break;
                case PAWN: blackScore += 1; break;
                default: blackScore += 0;
            }
            blackScoreText.setText(""+blackScore);
        }
        if (piece.getColor() == Color.BLACK) {
            switch(piece.getPc()) {
                case KING: break; //king never gets captured
                case QUEEN: whiteScore += 9; break;
                case BISHOP: whiteScore += 3; break;
                case KNIGHT: whiteScore += 3; break;
                case ROOK: whiteScore += 5; break;
                case PAWN: whiteScore += 1; break;
                default: whiteScore += 0;
            }
            whiteScoreText.setText(""+whiteScore);
        }
    }

    public void clearScore() {
        whiteScore = 0;
        blackScore = 0;
        whiteScoreText.setText(""+whiteScore);
        blackScoreText.setText(""+blackScore);
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
        notes.clear();
        moves.clear();
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
                            //System.out.println(tag + " " + val);
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
                        if (!tagStart && !valStart && !noteStart && !moveStart) {
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
                    
                    if ((c == ' ') && moveStart) {
                        moveStart = false;
                        moves.add(move);
                        move = "";
                        numStart = true;
                    }
                    if (moveStart) {
                        move = move+c;
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
                            if (!moveStart && !tagStart && !valStart && !noteStart) {
                                moveStart = true;
                                move = move+c;
                            }
                    }
                    if (i == line.length()-1 && moveStart) {
                        moveStart = false;
                        moves.add(move);
                        move = "";
                        numStart = true;
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
                System.out.println("-" + m + ",");
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

    public void replay(int movenum) {
        if (movenum < moves.size() && movenum >= -1) {
            beginning.setOpacity(1);
            previous.setOpacity(1);
            next.setOpacity(1);
            end.setOpacity(1);
            play_pause.setOpacity(1);

            board.clearBoard();
            gameText.setText("");
            //System.out.println("board cleared");
            try {
                board.setPieces();
                //System.out.println("board set");
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for (int i = 0; i <= movenum; i++) {
                //System.out.println("-" + moves.get(i) + ",");
                board.readMove(moves.get(i));
            }
        }
        if (moves.size() == board.getMoveList().size()) {
            next.setOpacity(0.5);
            end.setOpacity(0.5);
            play_pause.setOpacity(0.5);
            play_pause.setImage(replay_play);
        }
        if (board.getMoveList().size() == 0) {
            beginning.setOpacity(0.5);
            previous.setOpacity(0.5);
        }
    }

    public void greyOutForward() {
        next.setOpacity(greyOut);
        end.setOpacity(greyOut);
        play_pause.setOpacity(greyOut);
    }

    public void setMoveList(ArrayList<String> moveList) {
        moves.clear();
        moves.addAll(moveList);
    }

    
}
