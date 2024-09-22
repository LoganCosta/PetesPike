package petespike.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PetesPikeTest {
    @Test
    public void testFindMoves(){
        try{
            PetesPike pike = new PetesPike("data/petes_pike_5_5_2_0.txt");
            List<Move> expected = new ArrayList<>();

            expected.add(new Move(new Position(0, 1), Direction.RIGHT));
            expected.add(new Move(new Position(0, 4), Direction.LEFT));
            List<Move> actual = pike.getPossibleMoves();

            assertEquals(expected, actual);;
        }  
        catch(Exception e){
            System.out.println("RAHHH");
        }
    }

    @Test
    public void testMakeMove() {
        try {
            PetesPike pike = new PetesPike("data/petes_pike_5_5_2_0.txt");
            pike.makeMove(new Move(new Position(0, 1), Direction.RIGHT));

            // Board change
            assertEquals('-', pike.getSymbolAt(new Position(0, 1)));
            assertEquals('0', pike.getSymbolAt(new Position(0, 3)));

            // Moves increased
            assertEquals(1, pike.getMoveCount());
            
        } catch (Exception e) {
            assertFalse(true);
        }
    }

    @Test
    public void testGetSymbolAtValidPosition() {
        try {
            PetesPike game = new PetesPike("data/petes_pike_5_5_2_0.txt");
            assertEquals('-', game.getSymbolAt(new Position(0, 0)));
            assertEquals('P', game.getSymbolAt(new Position(0, 4)));
        } catch (Exception e) {
            assertFalse(true);
        }
    }
}