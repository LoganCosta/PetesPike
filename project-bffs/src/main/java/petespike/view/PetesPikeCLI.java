package petespike.view;

import java.util.List;
import java.util.Scanner;

import petespike.model.Direction;
import petespike.model.GameState;
import petespike.model.Move;
import petespike.model.PetesPike;
import petespike.model.PetesPikeSolver;
import petespike.model.Position;

public class PetesPikeCLI {

    private static Scanner scanner = new Scanner(System.in);

    private static void helpMenu() {
        System.out.println("Commands");
        System.out.println("    help - this help menu");
        System.out.println("    board - display current board");
        System.out.println("    reset - reset current puzzle to start");
        System.out.println("    new <puzzle_filename> - start a new puzzle");
        System.out.println("    move <row> <col> <direction> - move the piece at <row>, <col>");
        System.out.println("        where <direction> one of u(p), d(own), l(eft), r(ight)");
        System.out.println("    hint - get a valid move if one exists");
        System.out.println("    quit - quit");
    }

    private static String userInput() {
        System.out.println(" ");
        System.out.print("Command: ");
        String command = scanner.nextLine();
        System.out.println(" ");
        return command;
    }

    private static PetesPike moveMaker(PetesPike game, String[] commandSplitter){
        Direction direction;
        if (commandSplitter[3].equals("u")) {
            direction = Direction.UP;
        }
        else if (commandSplitter[3].equals("d")) {
            direction = Direction.DOWN;
        }
        else if (commandSplitter[3].equals("l")) {
            direction = Direction.LEFT;
        }
        else if (commandSplitter[3].equals("r")) {
            direction = Direction.RIGHT;
        }
        else {
            return null;
        }
        Position position = new Position(Integer.parseInt(commandSplitter[1]), Integer.parseInt(commandSplitter[2]));
        Move move = new Move(position, direction);
        try {
            game.makeMove(move);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        System.out.println("There is no piece in that direction to stop you");
        return game;
    }

    public static void main(String[] args) {
        try {
            String command = " ";
            
            System.out.print("Puzzle filename:");

            //uncomment when done
            //String filename = scanner.nextLine();

            //uncomment for more convienent testing
            String filename = "data/petes_pike_5_5_4_0.txt";
            
            PetesPike game = new PetesPike(filename);
            helpMenu();
            System.out.println(" ");
            System.out.println(game); 
            System.out.println("Moves: " + game.getMoveCount()); 
            
            while(!command.equals("quit")) {
                command = userInput();
                String[] commandSplitter = command.split(" ");

                if(commandSplitter[0].equals("help")) {
                    helpMenu();
                }
                else if(commandSplitter[0].equals("board")) {
                    System.out.println(game);
                }
                else if(commandSplitter[0].equals("reset")) {
                    game = new PetesPike(filename);
                }
                else if(commandSplitter[0].equals("new")) {
                    game = new PetesPike(commandSplitter[1]);
                }
                else if(commandSplitter[0].equals("move")) {
                    if(game.getGameState() == GameState.WON) {
                        System.out.println("There must be an active game to use this command");
                    }
                    else {
                        PetesPike tempGame = moveMaker(game, commandSplitter);
                        if (tempGame == null) {
                            continue;
                        } else {
                            game = tempGame;
                            System.out.println(game);
                            System.out.println("Moves: " + game.getMoveCount());
                            if(game.getGameState() == GameState.WON) {
                                System.out.println("Congratulations, you have scaled the mountain!");
                            }
                        }
                    }
                }
                else if(commandSplitter[0].equals("hint")) {
                    //List<Move> hints = PetesPikeSolver.solve(game).getMoves();
                    //System.out.println("Try: " + hints.get(0));
                    PetesPikeSolver solver = PetesPikeSolver.solve(game);
                    List<Move> results = solver.getMoves();
                    if(results != null) {
                        System.out.println("Moves: " + results);
                    } else {
                        System.out.println("No solution found.");
                    }
                }
                else {
                    System.out.println("Invalid command");
                }
            }
            System.out.println("Goodbye!");
    
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        scanner.close();
    }
}
