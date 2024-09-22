package petespike.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import backtracker.Backtracker;
import backtracker.Configuration;

/**
 * PetesPikeSolver
 */
public class PetesPikeSolver implements Configuration<PetesPikeSolver>{
    PetesPike petesPike;
    List<Move> moves;
    HashSet<PetesPikeSolver> previous; //should be a shared resource, since same reference

    public PetesPikeSolver(PetesPike petesPike) {
        this(petesPike, new ArrayList<Move>(), new HashSet<PetesPikeSolver>());
    }

    private PetesPikeSolver(PetesPike petesPike, List<Move> moves, HashSet<PetesPikeSolver> previous) {
        this.petesPike = petesPike;
        this.moves = moves;
        this.previous = previous;
    }

    @Override
    public Collection<PetesPikeSolver> getSuccessors(){
        List<PetesPikeSolver> succsessors = new ArrayList<>();
        for(Move move : petesPike.getPossibleMoves()){ 
            PetesPike temp = new PetesPike(this.petesPike); //inital board state, should also reset each time but apprnot
            try {
                temp.makeMove(move); //for some reason this is moving the original too, check dupe constr
            } 
            catch (Exception e) {
                System.out.println("err");
            }


            List<Move> newMoves = new ArrayList<>();
            for(Move movee : this.moves){ //copy the list
                newMoves.add(movee);
            }
            newMoves.add(move);
            
            PetesPikeSolver sucessor = new PetesPikeSolver(temp, newMoves, previous);
            if(!previous.contains(sucessor)){
                succsessors.add(sucessor);
                previous.add(sucessor);
                System.out.println(sucessor.petesPike.toString());
                System.out.println(newMoves.size());
            }
        }
        return succsessors;
    }

    @Override
    public boolean isValid() {
        return petesPike.getPossibleMoves().size() > 0 || petesPike.getGameState() == GameState.WON;
    }

    @Override
    public boolean isGoal() {
        return petesPike.getGameState() == GameState.WON;
    }

    public List<Move> getMoves() {
        return moves;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PetesPikeSolver){
            PetesPikeSolver other = (PetesPikeSolver)obj;
            return other.petesPike.getPieces().equals(this.petesPike.getPieces());
        }
        return false;
    }

    public static PetesPikeSolver solve(PetesPike pike) {
        PetesPikeSolver solver = new PetesPikeSolver(pike);
        Backtracker<PetesPikeSolver> actualSolver = new Backtracker<>(false);
        return actualSolver.solve(solver);
    }
}