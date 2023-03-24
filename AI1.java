import java.util.Collections;
import java.util.Comparator;

public class AI1 implements IOthelloAI {

    private int maxDepth = 4;
    private int turn;
    private int size;

    private int positionsEvaluated = 0;

    public AI1(){}

    public AI1(int maxDepth){
        this.maxDepth = maxDepth;
    }

    @Override
    public Position decideMove(GameState s) {
        var start = System.nanoTime();
        var vMove = search(s);
        var stop = System.nanoTime();
        float time = (stop - start) / 1000000000f;
        String side = turn == 1 ? "Black" : "White";
        System.out.println(side + ": " + vMove.value + " | Positions evaluated: " + positionsEvaluated + " | Time: " + time + "s");
        return vMove.move;
    }

    private ValuedMove search(GameState s){
        size = s.getBoard().length;
        positionsEvaluated = 0;
        turn = s.getPlayerInTurn();
        var moves = s.legalMoves();
        
        int bestValue = Integer.MIN_VALUE;
        Position bestMove = null;

        for (Position move : moves) {
            positionsEvaluated++;
            var copy = new GameState(s.getBoard(), s.getPlayerInTurn());
            copy.insertToken(move);
            
            var value = min(copy, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if(value > bestValue){
                bestValue = value;
                bestMove = move;
            }
        }
        return new ValuedMove(bestMove, bestValue);
    }

    private int max(GameState s, int depth, int a, int b) {
        var moves = s.legalMoves();
        
        if(s.isFinished()){
            return getValue(s);
        }
        if(moves.size() == 0){
            s.changePlayer();
            return min(s, depth, a, b);
        }
        
        int bestValue = Integer.MIN_VALUE;
        /* Collections.sort(moves, comparator(s)); */
        for (Position move : moves) {
            positionsEvaluated++;
            var copy = copyAndMove(s, move);
            
            var value = min(copy, depth, bestValue, b);
            if(value > bestValue){
                bestValue = value;
            }
            if(value >= b){
                return bestValue;
            }
        }
        return bestValue;
    }

    private int min(GameState s, int depth, int a, int b){
        depth++;
        var moves = s.legalMoves();

        
        if(s.isFinished()){
            return getValue(s);
        }
        if(moves.size() == 0){
            s.changePlayer();
            return max(s, depth, a, b);
        }

        int bestValue = Integer.MAX_VALUE;
        /* Collections.sort(moves, comparator(s));
        Collections.reverse(moves); */
        for (Position move : moves) {
            positionsEvaluated++;
            var copy = copyAndMove(s, move);
            
            int value = depth < maxDepth ? max(copy, depth, a, bestValue) : eval(s);

            if(value < bestValue){
                bestValue = value;
            }

            if(value <= a){
                return bestValue;
            }
        }
        return bestValue;
    }

    private int getValue(GameState s){
        int[] tokens = s.countTokens();
        return tokens[turn - 1] - tokens[turn % 2];
    }

    private int eval(GameState s){
        return getValue(s);
    }

    private GameState copyAndMove(GameState s, Position p){
        var copy = new GameState(s.getBoard(), s.getPlayerInTurn());
        copy.insertToken(p);
        return copy;
    }

    private int v(int tokenColor, int value){
        return tokenColor == turn ? value : -value;
    }

    class ValuedMove{
        public int value;
        public Position move;

        public ValuedMove(Position move, int value){
            this.value = value;
            this.move = move;
        }
    }

    private Comparator<Position> comparator(GameState s){
        return new Comparator<Position>() {
            @Override
            public int compare(Position a, Position b) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                var copyA = copyAndMove(s, a);
                var copyB = copyAndMove(s, b);
                int valueA = getValue(copyA);
                int valueB = getValue(copyB);
                return -(valueA - valueB);
            }
        };
    }
}
