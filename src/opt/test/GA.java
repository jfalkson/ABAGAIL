package opt.test;

import dist.*;
import opt.*;
import opt.example.*;
import opt.ga.*;
import shared.*;
import func.nn.backprop.*;

import java.util.*;
import java.io.*;
import java.text.*;

/**
 * REMINDER: we loop through population, mating portion (always set to .5 in this experiment)
 * and a mutation set at 10%
 */

/**
 * Implementation of randomized hill climbing, simulated annealing, and genetic algorithm to
 * find optimal weights to a neural network that is classifying abalone as having either fewer 
 * or more than 15 rings. 
 *
 * @author Hannah Lau
 * @version 1.0
 */
public class GA {
    private static Instance[] instances = initializeInstances();
/**
 * use a loop to test different training iterations and hidden layers,
 * as well as parameters for the randomized algorithms, such as starting
 * temperature for annealing, or population size for genetic algo
 */
    private static int inputLayer = 57, hiddenLayer = 5, outputLayer = 1, trainingIterations = 1000;
    private static BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();
    
    private static ErrorMeasure measure = new SumOfSquaresError();

    private static DataSet set = new DataSet(instances);

    private static BackPropagationNetwork networks[] = new BackPropagationNetwork[3];
    private static NeuralNetworkOptimizationProblem[] nnop = new NeuralNetworkOptimizationProblem[3];

    private static OptimizationAlgorithm[] oa = new OptimizationAlgorithm[3];
    private static String[] oaNames = {"RHC", "SA", "GA"};
    private static String results = "";
    private static String results2 = "";

    private static DecimalFormat df = new DecimalFormat("0.000");

    public static void main(String[] args) {
        for(int i = 0; i < oa.length; i++) {
            networks[i] = factory.createClassificationNetwork(
                new int[] {inputLayer, hiddenLayer, outputLayer});
            nnop[i] = new NeuralNetworkOptimizationProblem(set, networks[i], measure);
        }

        oa[0] = new RandomizedHillClimbing(nnop[0]);
        oa[1] = new SimulatedAnnealing(1E11, .95, nnop[1]);
        

        for(int i = 2; i == 2; i++) {
        	
        	for (int popSize = 240; popSize < 241; popSize = popSize + 20)
			{
        	oa[2] = new StandardGeneticAlgorithm(popSize, popSize/2, popSize/10, nnop[2]); 
            double start = System.nanoTime(), end, trainingTime, testingTime, correct = 0, incorrect = 0;
            train(oa[i], networks[i], oaNames[i]); //trainer.train();
            end = System.nanoTime();
            trainingTime = end - start;
            trainingTime /= Math.pow(10,9);

            Instance optimalInstance = oa[i].getOptimal();
            networks[i].setWeights(optimalInstance.getData());

            double predicted, actual;
            start = System.nanoTime();
            for(int j = 2000; j < 3320; j++) {
                networks[i].setInputValues(instances[j].getData());
                networks[i].run();

                predicted = Double.parseDouble(instances[j].getLabel().toString());
                actual = Double.parseDouble(networks[i].getOutputValues().toString());

                double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;

            }
            end = System.nanoTime();
            testingTime = end - start;
            testingTime /= Math.pow(10,9);

            results +=  "\nResults for " + oaNames[i] + ": \nCorrectly classified " + correct + " instances." +
                        "\nIncorrectly classified " + incorrect + " instances.\nPercent correctly classified: "
                        + df.format(correct/(correct+incorrect)*100) + "%\nTraining time: " + df.format(trainingTime)
                        + " seconds\nTesting time: " + df.format(testingTime) + " seconds\n"
                        + "Population size is " + popSize + "mate is 1/2, mutate is 1/10";
            
            results2 =  "\nResults for " + oaNames[i] + ": \nCorrectly classified " + correct + " instances." +
                    "\nIncorrectly classified " + incorrect + " instances.\nPercent correctly classified: "
                    + df.format(correct/(correct+incorrect)*100) + "%\nTraining time: " + df.format(trainingTime)
                    + " seconds\nTesting time: " + df.format(testingTime) + " seconds\n"
                    + "Population size is " + popSize + "mate is 1/2, mutate is 1/10";
            System.out.println(results2);
			}
        }
        //print all results at the end
        System.out.println(results);
    }

    private static void train(OptimizationAlgorithm oa, BackPropagationNetwork network, String oaName) {
        System.out.println("\nError results for " + oaName + "\n---------------------------");

        for(int i = 0; i < trainingIterations; i++) {
            oa.train();

            double error = 0;
            double correct = 0, incorrect = 0, correctTest=0, incorrectTest=0;
            for(int j = 0; j < 2000; j++) {
            	double predicted, actual;
                network.setInputValues(instances[j].getData());
                predicted = Double.parseDouble(instances[j].getLabel().toString());
                actual = Double.parseDouble(networks[2].getOutputValues().toString());

                double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;
                network.run();

                Instance output = instances[j].getLabel(), example = new Instance(network.getOutputValues());
                example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));
                error += measure.value(output, example);
            }
            for(int k = 2000; k < 3220; k++) {
 ///testing info
            	double predicted, actual, predictedTest, actualTest;
                networks[2].setInputValues(instances[k].getData());
                networks[2].run();

                predictedTest = Double.parseDouble(instances[k].getLabel().toString());
                actualTest = Double.parseDouble(networks[2].getOutputValues().toString());

                double trash2 = Math.abs(predictedTest - actualTest) < 0.5 ? correctTest++ : incorrectTest++;
            }
            
////shows training error at each iteration
            System.out.println("Train " + df.format(correct/(correct+incorrect)*100)+ " Test " + df.format(correctTest/(correctTest+incorrectTest)*100));
        }
        }

    private static Instance[] initializeInstances() {

        double[][][] attributes = new double[4601][][];

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("/Users/joefalkson/Desktop/spam_dataFULL.txt")));

            for(int i = 0; i < attributes.length; i++) {
                Scanner scan = new Scanner(br.readLine());
                scan.useDelimiter(",");
                //for each instance we have a 2 dimen array
                //first dimension determines if it is inputs or the output
                //the second has the array of inputs or the binary output
                attributes[i] = new double[2][];
                //when the second dimension is 0 we feed in the inputs
                attributes[i][0] = new double[57]; 
                // when the second dimension is 1 we feed the output label
                attributes[i][1] = new double[1];
                //format of array is:
                //Row number, if 0 then inputs, if 2 then outputs, label value or input value
                for(int j = 0; j < 57; j++)
                //for each dimension
                //store the attributes for each j
                attributes[i][0][j] = Double.parseDouble(scan.next());
                //store the label (only element in [i][1][0]
                attributes[i][1][0] = Double.parseDouble(scan.next());
                }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        Instance[] instances = new Instance[attributes.length];

        for(int i = 0; i < instances.length; i++) {
            instances[i] = new Instance(attributes[i][0]);
            instances[i].setLabel(new Instance(attributes[i][1][0] == 0 ? 0 : 1));
        }

        return instances;
    }
}