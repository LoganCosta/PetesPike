package petespike.view;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import petespike.model.Direction;
import petespike.model.Move;
import petespike.model.PetesPike;
import petespike.model.PetesPikeSolver;
import petespike.model.Position;

public class PetesPikeGUI extends Application {
    private PetesPike petesPike;
    private Position selectedPosition;
    private GridPane boardGrid;
    
    @Override
    public void start(Stage primaryStage) {
        
        try {
            petesPike = new PetesPike("data/petes_pike_5_5_4_0.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextField fileInput = new TextField();

        createBoardGrid(boardGrid);
        
        Label status = new Label("New Game");
        Label moveCount = new Label("Moves: " + petesPike.getMoveCount());
        moveCount.setMinWidth(Region.USE_PREF_SIZE);
        moveCount.setPrefWidth(Region.USE_COMPUTED_SIZE);
        moveCount.setMaxWidth(Double.MAX_VALUE);
        
        
        Label hintGoat = new Label();
        hintGoat.setMinWidth(Region.USE_PREF_SIZE);
        hintGoat.setPrefWidth(Region.USE_COMPUTED_SIZE);
        hintGoat.setMaxWidth(Double.MAX_VALUE);
        Label hintDir = new Label();

        Button getHint = new Button("Get Hint");
        getHint.setOnAction(e -> {
            try {
                PetesPikeSolver solver = PetesPikeSolver.solve(petesPike);
                List<Move> results = solver.getMoves();
                if(results != null) {
                    hintGoat.setText("Move the piece at " + results.get(0).getPosition().toString());
                    hintDir.setText("In direction " + results.get(0).getDirection().toString());
                } else {
                    hintGoat.setText("No valid moves");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                hintGoat.setText("No valid moves");
            }
        });


        boardGrid = new GridPane();
        createBoardGrid(this.boardGrid);
        

        GridPane movementGrid = new GridPane();
        movementGrid.setHgap(5);
        movementGrid.setVgap(5);
        List<Button> buttons = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            Button button = new Button();
            button.setPrefSize(50, 50);

            button.setOnAction(e -> {
                if (selectedPosition != null) {
                    try {
                        Position stopped = petesPike.makeMove(new Move(selectedPosition, dir));
                        if (stopped != null) {
                            boardGrid.getChildren().forEach(b -> {
                                if (GridPane.getRowIndex(b) == selectedPosition.getRow() && GridPane.getColumnIndex(b) == selectedPosition.getCol()) {
                                    setButtonImage((Button) b, selectedPosition);
                                    boardGrid.getChildren().forEach(b2 -> {
                                        if (GridPane.getRowIndex(b2) == stopped.getRow() && GridPane.getColumnIndex(b2) == stopped.getCol()) {
                                            setButtonImage((Button) b2, stopped);
                                        }
                                    });
                                }
                            });
                            moveCount.setText("Moves: " + petesPike.getMoveCount());
                            status.setText(petesPike.getGameState().toString());
                            selectedPosition = stopped;
                            hintDir.setText(""); //resetting the hint each time, so it doesn't persist
                            hintGoat.setText("");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            buttons.add(button);
        }

        movementGrid.add(buttons.get(0), 1, 0);
        movementGrid.add(buttons.get(1), 1, 2);
        movementGrid.add(buttons.get(2), 0, 1);
        movementGrid.add(buttons.get(3), 2, 1);

        Button resetButton = new Button("Reset");
        resetButton.setPrefSize(50, 25);
        resetButton.setOnAction(e -> {
            try {
                petesPike = petesPike.reset();
                moveCount.setText("Moves: 0");
                status.setText(petesPike.getGameState().toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            for (Node node : boardGrid.getChildren()) {
                if (node instanceof Button) {
                    Button button = (Button) node;
                    setButtonImage(button, new Position(GridPane.getRowIndex(button), GridPane.getColumnIndex(button)));
                }
            }
            
        });

        //moved this because some thing needed to be defined but they were defined later
        Button newPuzzle = new Button("New Puzzle");
        newPuzzle.setPrefSize(Region.USE_COMPUTED_SIZE, 25);
        newPuzzle.setOnAction(e -> {
            try {
                petesPike = new PetesPike(fileInput.getText());
                fileInput.clear();

                moveCount.setText("Moves: 0");
                status.setText(petesPike.getGameState().toString());
                createBoardGrid(boardGrid);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        

        HBox topHBox = new HBox();
        topHBox.getChildren().addAll(resetButton, fileInput, newPuzzle);

        VBox dirVBox = new VBox();
        dirVBox.getChildren().addAll(movementGrid, getHint, hintGoat, hintDir);

        HBox bottomHBox = new HBox();
        bottomHBox.getChildren().addAll(status, moveCount);
        bottomHBox.setSpacing(10);

        HBox middleHBox = new HBox();
        middleHBox.getChildren().addAll(boardGrid, dirVBox);

        VBox root = new VBox();
        root.getChildren().addAll(topHBox, middleHBox, bottomHBox);
        

        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setButtonImage(Button button, Position position) {
        try {
            String imagePath = "";
            if (Character.isDigit(petesPike.getSymbolAt(position))) {
                imagePath = "data/media/images/goat" + Character.valueOf(petesPike.getSymbolAt(position)) % 4 + ".png";
            } else if (petesPike.getSymbolAt(position) == 'P') {
                imagePath = "data/media/images/pete.png";
            } else if (petesPike.getSymbolAt(position) == 'T') {
                imagePath = "data/media/images/summit.png";
            }
    
            if (!imagePath.isEmpty()) {
                FileInputStream input = new FileInputStream(new File(imagePath));
                Image image = new Image(input);
                BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false));
                button.setBackground(new Background(backgroundImage));
            } else {
                button.setBackground(null);
            }
    
            // Set a border on the button
            BorderStroke borderStroke = new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT);
            button.setBorder(new Border(borderStroke));
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createBoardGrid(GridPane board) {
        try {
            board.getChildren().clear();
        } catch (Exception e) {
            board = new GridPane();
        }
        board.setHgap(5);
        board.setVgap(5);

        for (int i = 0; i < petesPike.getRow(); i++) {
            for (int j = 0; j < petesPike.getCol(); j++) {
                Button button = new Button();
                button.setPrefSize(50, 50);
        
                button.setOnAction(e -> {
                    selectedPosition = new Position(GridPane.getRowIndex(button), GridPane.getColumnIndex(button));
                });
                board.add(button, j, i);
        
                setButtonImage(button, new Position(i, j));
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
