package petespike.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import petespike.view.AsciiColorCodes;
import petespike.view.PetesPikeObserver;


/**
 * The PetesPike class represents the game board and its state for the Petes Pike game.
 * It provides methods to initialize the game board, make moves, retrieve symbols,
 * and get information about the game state.
 */
public class PetesPike {
    
    public char EMPTY_SYMBOL = '-';
    public char PETE_SYMBOL = 'P';
    public Set<Character> GOAT_SYMBOLS = Set.of('0', '1', '2', '3', '4', '5', '6', '7', '8');
    private char MOUNTAINTOP_SYMBOL = 'T';
    private String fileName;

    private int moveCount;
    private int[] boardSize;
    private char[][] board;
    private Position mountaintop;
    private List<Position> pieces;
    private GameState gameState;

    private List<PetesPikeObserver> observers;

    /**
     * Initializes the game board from a file.
     * 
     * @param filename String: the relative path of the file containing the game board
     * @throws PetesPikeException if there is an error while parsing the file
     */
    public PetesPike(String filename) throws Exception {
        this.moveCount = 0;
        this.gameState = GameState.IN_PROGRESS;
        this.pieces = new ArrayList<>(); //keeping track of what positions are pieces
        this.fileName = filename;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))){
            String[] size = reader.readLine().split(" ");
            this.boardSize = new int[]{Integer.parseInt(size[0]), Integer.parseInt(size[1])};// Sets the board size
            this.board = new char[this.boardSize[0]][this.boardSize[1]];// Initialize the board with the given size
            for (int i = 0; i < this.boardSize[0]; i++) {
                String line = reader.readLine();
                for (int j = 0; j < this.boardSize[1]; j++) {
                    this.board[i][j] = line.charAt(j);// Stores the symbol in the board

                    // Check for Special Characters and stores the position
                    if (this.board[i][j] == this.MOUNTAINTOP_SYMBOL) {
                        this.mountaintop = new Position(i, j);
                    } else if(this.board[i][j] == PETE_SYMBOL || GOAT_SYMBOLS.contains(this.board[i][j])){
                        pieces.add(new Position(i, j));
                    }
                }
            }
        } catch (Exception e) {
            throw new PetesPikeException("Error parsing file: " + e.getMessage());
        }
    }

    protected PetesPike(PetesPike other){
        this.moveCount = other.moveCount;
        this.board = new char[other.board.length][other.board[0].length];
        this.boardSize = Arrays.copyOf(other.boardSize, other.boardSize.length);

        for(int i = 0; i < this.board.length; i++){
            this.board[i] = Arrays.copyOf(other.board[i], other.board[i].length);
        }

        this.mountaintop = new Position(other.mountaintop.getRow(), other.mountaintop.getCol());
        this.pieces = new ArrayList<Position>();
        for (Position pos : other.pieces) {
            this.pieces.add(new Position(pos.getRow(), pos.getCol()));
        }

        this.gameState = other.gameState;
    }

    public int getMoveCount() {
        return this.moveCount;
    }

    public int getRow() {
        return this.boardSize[0];
    }

    public int getCol() {
        return this.boardSize[1];
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public List<Position> getPieces() {
        return pieces;
    }

    /**
     * Makes a move on the game board.
     * 
     * @param move Move: the move to be made
     * @throws Exception 
     */
    public Position makeMove(Move move) throws Exception {
        if (this.gameState == GameState.WON) {
            throw new PetesPikeException("Game is over");
        }

        int[] movement = move.getDirection().getMovement();
        Position position = move.getPosition();

        // Check if there is a piece at the specified position
        if (!pieces.contains(position)) {
            throw new PetesPikeException("There is not a piece at the specified position");
        }

        // Check if Direction is valid
        for (int i = position.getRow() + (movement[0]), j = position.getCol() + (movement[1]);
          i >= 0 && i < this.board.length && j >= 0 && j < this.board[0].length; 
          i += movement[0], j += movement[1]) {
            //my bad for making this if super long
            //it's just that if there were pieces arranged similar to this
            //---
            //-p-
            //-p-
            //---
            //-p-
            //---
            //the topmost piece could travel through the middle piece - Logan
            if (this.pieces.contains(new Position(i, j)) && (position.getRow() != i || position.getCol() != j) && !this.pieces.contains(new Position(position.getRow() + movement[0], position.getCol() + movement[1]))){
                // Update the pieces list
                this.pieces.add(new Position(i - movement[0], j - movement[1]));
                this.pieces.remove(position);

                // Move the piece
                this.board[i - movement[0]][j - movement[1]] = this.board[position.getRow()][position.getCol()];
                this.board[position.getRow()][position.getCol()] = this.mountaintop == position ? this.MOUNTAINTOP_SYMBOL : this.EMPTY_SYMBOL;
                this.moveCount++;

                // Checks if Mountiantop is visible
                if (this.mountaintop.equals(position)) {
                    this.board[position.getRow()][position.getCol()] = this.MOUNTAINTOP_SYMBOL;
                }

                // Check if the game is over
                if (this.board[this.mountaintop.getRow()][this.mountaintop.getCol()] == this.PETE_SYMBOL) {
                    this.gameState = GameState.WON;
                }
                return new Position(i - movement[0], j - movement[1]);
            }
        }
        // If the move is not valid
        throw new PetesPikeException("Invalid move");
    }

    public char getSymbolAt(Position position) throws Exception {
        return this.board[position.getRow()][position.getCol()];
    }

    public Position getMountaintop() {
        return this.mountaintop;
    }

    /**
     * Returns a list of possible moves for the current game state.
     * 
     * @return a list of possible moves
     */
    public List<Move> getPossibleMoves() {
        List<Move> moves = new ArrayList<>();

        for (Position position : this.pieces) {
            for (Direction direction : Direction.values()) {
                int[] movement = direction.getMovement();
                
                // Checks if the direction for piece is valid
                for (int i = position.getRow() + (movement[0]), j = position.getCol() + (movement[1]); 
                  i >= 0 && i < this.board.length && j >= 0 && j < this.board[0].length; 
                  i += movement[0], j += movement[1]) {
                    if (this.pieces.contains(new Position(i, j)) && !this.pieces.contains(new Position(position.getRow() + movement[0], position.getCol() + movement[1]))){
                        moves.add(new Move(position, direction));
                        break;
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Adds color to the symbol.
     * 
     * @param symbol char: the symbol to be colored
     * @param color String: the color to be added
     * @return String: the colored symbol
     */
    private String addColor(char symbol, String color) {
        return color + String.valueOf(symbol) + AsciiColorCodes.RESET;
    }

    @Override
    public String toString() {
        String sb = "  ";
        for (int i = 0; i < this.boardSize[1]; i++) {
            sb += i + " ";
        }
        sb += "\n";

        for (int i = 0; i < this.boardSize[0]; i++) {
            sb += i + " ";
            //System.out.println(sb);
            for (int j = 0; j < this.boardSize[1]; j++) {
                if (this.board[i][j] == this.EMPTY_SYMBOL) {
                    sb += this.EMPTY_SYMBOL + " ";
                } else if (this.GOAT_SYMBOLS.contains(this.board[i][j])) {
                    sb += addColor('G', AsciiColorCodes.GREEN) + " ";
                } else if (this.board[i][j] == this.PETE_SYMBOL) {
                    sb += addColor(this.PETE_SYMBOL, AsciiColorCodes.PURPLE) + " ";
                } else if (this.board[i][j] == this.MOUNTAINTOP_SYMBOL) {
                    sb += addColor(this.MOUNTAINTOP_SYMBOL, AsciiColorCodes.BLUE) + " ";
                }
            }
            sb += "\n";
        }
        return sb;
    }

    public void registerObserver(PetesPikeObserver observer) {
        observers.add(observer);
    }

    public void notifyObserver(Position from, Position to) {
        for (PetesPikeObserver observer : observers) {
            observer.pieceMoved(from, to);
        }
    }

    public PetesPike reset() throws Exception {
        this.moveCount = 0;
        this.gameState = GameState.IN_PROGRESS;
        return new PetesPike(this.fileName);

    }
}
