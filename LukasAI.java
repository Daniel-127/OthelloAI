public class LukasAI implements IOthelloAI {
    private int turn;
    private int depth;
    private int maxDepth = 1000000;
    private static int maxPoints = 100;
    private int[][] pointBoard;

    public Position decideMove(GameState s) {
        System.out.println("Running evaluation search...");
        SetupPointBoard(s);
        ValueAction va;
        depth = 0;
        turn = s.getPlayerInTurn();
        va = MaxValue(s, Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.println("Best value: " + va.value);
        return va.action;
    }

    private void SetupPointBoard(GameState state){
        pointBoard = new GameState(state.getBoard(), state.getPlayerInTurn()).getBoard();
        int boardSize = pointBoard.length;

        for (int i = 0; i < pointBoard.length; i++) {
            for (int j = 0; j < pointBoard[i].length; j++) {
                pointBoard[i][j] = 5;
            }
        }

        //Corners
        pointBoard[0][0] = maxPoints;
        pointBoard[boardSize-1][0] = maxPoints;
        pointBoard[0][boardSize-1] = maxPoints;
        pointBoard[boardSize-1][boardSize-1] = maxPoints;

        //Next to corners
        pointBoard[0][1] = - (maxPoints / 6);
        pointBoard[1][0] = - (maxPoints / 6);

        pointBoard[boardSize-2][0] = - (maxPoints / 6);
        pointBoard[boardSize-1][1] = - (maxPoints / 6);

        pointBoard[0][boardSize-2] = - (maxPoints / 6);
        pointBoard[1][boardSize-1] = - (maxPoints / 6);

        pointBoard[boardSize-2][boardSize-1] = - (maxPoints / 6);
        pointBoard[boardSize-1][boardSize-2] = - (maxPoints / 6);

        //Directional to corners
        pointBoard[1][1] = - (maxPoints / 3);
        pointBoard[boardSize-2][1] = - (maxPoints / 3);
        pointBoard[1][boardSize-2] = - (maxPoints / 3);
        pointBoard[boardSize-2][boardSize-2] = - (maxPoints / 3);
    }

    private int Utility(GameState state) {
        int tempPoints = 0;
        int[][] board = state.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == turn){
                    tempPoints += pointBoard[i][j];
                }
            }
        }
        int[] tokenCount = state.countTokens();
        int tokenLead = (turn == 1 ? tokenCount[0] - tokenCount[1] : tokenCount[1] - tokenCount[0]);

        return tempPoints + (tokenLead * 5);
    }

    private ValueAction MaxValue(GameState state, int alpha, int beta) {
        depth++;

        if (state.isFinished() || depth > maxDepth) {
            return new ValueAction(Utility(state), new Position(-1, -1));
        }

        ValueAction va = new ValueAction(Integer.MIN_VALUE, new Position(-1, -1));

        if (state.legalMoves().size() == 0) {
            state.changePlayer();
            ValueAction va2 = MinValue(state, alpha, beta);
            alpha = Math.max(alpha, va2.value);
            return va2;
        }
        for (Position action : state.legalMoves()) {
            GameState copyState = new GameState(state.getBoard(), state.getPlayerInTurn());

            copyState.insertToken(action);

            ValueAction va2 = MinValue(copyState, alpha, beta);
            if (va2.value > va.value) {
                va.value = va2.value;
                va.action = action;
                alpha = Math.max(alpha, va.value);
            }
            if (va.value >= beta) return va;
        }
        return va;
    }

    private ValueAction MinValue(GameState state, int alpha, int beta) {
        depth++;

        if (state.isFinished() || depth > maxDepth) {
            return new ValueAction(Utility(state), new Position(-1, -1));
        }

        ValueAction va = new ValueAction(Integer.MAX_VALUE, new Position(-1, -1));

        if (state.legalMoves().size() == 0) {
            state.changePlayer();
            ValueAction va2 = MaxValue(state, alpha, beta);
            beta = Math.min(beta, va2.value);
            return va2;
        }
        for (Position action : state.legalMoves()) {
            GameState copyState = new GameState(state.getBoard(), state.getPlayerInTurn());

            copyState.insertToken(action);

            ValueAction va2 = MaxValue(copyState, alpha, beta);
            if (va2.value < va.value) {
                va.value = va2.value;
                va.action = action;
                beta = Math.min(beta, va.value);
            }
            if (va.value <= alpha) return va;
        }
        return va;
    }

    class ValueAction{
        public int value;
        public Position action;

        public ValueAction(int value, Position action){
            this.value = value;
            this.action = action;
        }
    }
}