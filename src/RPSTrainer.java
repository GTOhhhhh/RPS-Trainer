import java.util.Arrays;
import java.util.Random;

public class RPSTrainer {
    public static final int ROCK = 0, PAPER = 1, SCISSORS = 2, NUM_ACTIONS = 3;
    public static final Random random = new Random();
    double[] regretSum = new double[NUM_ACTIONS],
            strategy = new double[NUM_ACTIONS],
            strategySum = new double[NUM_ACTIONS];

    double[] regretSum2 = new double[NUM_ACTIONS],
            strategy2 = new double[NUM_ACTIONS],
            strategySum2 = new double[NUM_ACTIONS];


    private double[] getStrategy() {
        double normalizingSum = 0;
        for (int a = 0; a < NUM_ACTIONS; a++) {
            strategy[a] = regretSum[a] > 0 ? regretSum[a] : 0;
            normalizingSum += strategy[a];
        }
        for (int a = 0; a < NUM_ACTIONS; a++) {
            if (normalizingSum > 0)
                strategy[a] /= normalizingSum;
            else
                strategy[a] = 1.0 / NUM_ACTIONS;
            strategySum[a] += strategy[a];
        }
        return strategy;
    }

    private double[] getOppStrategy() {
        double normalizingSum = 0;
        for (int a = 0; a < NUM_ACTIONS; a++) {
            strategy2[a] = regretSum2[a] > 0 ? regretSum2[a] : 0;
            normalizingSum += strategy2[a];
        }
        for (int a = 0; a < NUM_ACTIONS; a++) {
            if (normalizingSum > 0)
                strategy2[a] /= normalizingSum;
            else
                strategy2[a] = 1.0 / NUM_ACTIONS;
            strategySum2[a] += strategy2[a];
        }
        return strategy2;
    }


    public int getAction(double[] strategy) {
        double r = random.nextDouble();
        int a = 0;
        double cumulativeProbability = 0;
        while (a < NUM_ACTIONS - 1) {
            cumulativeProbability += strategy[a];
            if (r < cumulativeProbability)
                break;
            a++;
        }
        return a;
    }

    public void train(int iterations) {
        double[] actionUtility = new double[NUM_ACTIONS];
        double[] actionUtility2 = new double[NUM_ACTIONS];
        for (int i = 0; i < iterations; i++) {
            // get regret matched mixed-strategy actions
            double[] strategy = getStrategy();
            double[] oppStrategy = getOppStrategy();
            int myAction = getAction(strategy);
            int otherAction = getAction(oppStrategy);

            // compute action utilities
            actionUtility[otherAction] = 0;
            actionUtility[otherAction == NUM_ACTIONS - 1 ? 0 : otherAction + 1] = 1;
            actionUtility[otherAction == 0 ? NUM_ACTIONS - 1 : otherAction - 1] = -1;

            actionUtility2[myAction] = 0;
            actionUtility2[myAction == NUM_ACTIONS - 1 ? 0 : myAction + 1] = 1;
            actionUtility2[myAction == 0 ? NUM_ACTIONS - 1 : myAction - 1] = -1;


            // accumulate action regrets
            for (int a = 0; a < NUM_ACTIONS; a++)
                regretSum[a] += actionUtility[a] - actionUtility[myAction];

            for (int a = 0; a < NUM_ACTIONS; a++)
                regretSum2[a] += actionUtility2[a] - actionUtility2[otherAction];
        }
    }


    // Get average mixed strategy across all training iterationsi
    public double[] getAverageStrategy() {
        double[] avgStrategy = new double[NUM_ACTIONS];
        double normalizingSum = 0;
        for (int a = 0; a < NUM_ACTIONS; a++)
            normalizingSum += strategySum[a];
        for (int a = 0; a < NUM_ACTIONS; a++)
            if (normalizingSum > 0)
                avgStrategy[a] = strategySum[a] / normalizingSum;
            else
                avgStrategy[a] = 1.0 / NUM_ACTIONS;
        return avgStrategy;
    }

    public double[] getAverageStrategy2() {
        double[] avgStrategy = new double[NUM_ACTIONS];
        double normalizingSum = 0;
        for (int a = 0; a < NUM_ACTIONS; a++)
            normalizingSum += strategySum2[a];
        for (int a = 0; a < NUM_ACTIONS; a++)
            if (normalizingSum > 0)
                avgStrategy[a] = strategySum2[a] / normalizingSum;
            else
                avgStrategy[a] = 1.0 / NUM_ACTIONS;
        return avgStrategy;
    }


    public static void main(String[] args) {
        RPSTrainer trainer = new RPSTrainer();
        trainer.train(100000);
        System.out.println(Arrays.toString(trainer.getAverageStrategy()));
        System.out.println(Arrays.toString(trainer.getAverageStrategy2()));

    }
}
