public class LowDepthAI implements IOthelloAI {

    private MinMaxAI ai;

    public LowDepthAI(){
        this.ai = new MinMaxAI(2);
    }

    @Override
    public Position decideMove(GameState s) {
        return ai.decideMove(s);
    }
}
