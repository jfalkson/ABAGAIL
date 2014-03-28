package opt.test;

import java.util.Arrays;
import java.util.Random;
import java.lang.*;

import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;
import opt.DiscreteChangeOneNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.ga.UniformCrossOver;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * A test of the knap sack problem
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class KnapsackTest {
    /** Random number generator */
    private static final Random random = new Random();
    /** The number of items */
    private static final int NUM_ITEMS = 40;
    /** The number of copies each */
    //thus we have 40*4=160 items total
    private static final int COPIES_EACH = 4;
    /** The maximum weight for a single element */
    private static final double MAX_WEIGHT = 50;
    /** The maximum volume for a single element */
    private static final double MAX_VOLUME = 50;
    /** The volume of the knapsack */
    private static final double KNAPSACK_VOLUME = 
         MAX_VOLUME * NUM_ITEMS * COPIES_EACH * .4;
    /**
     * The test main
     * @param args ignored
     */
    public static void main(String[] args) {
        int[] copies = new int[NUM_ITEMS];
        Arrays.fill(copies, COPIES_EACH);
        double[] weights = new double[NUM_ITEMS];
        double[] volumes = new double[NUM_ITEMS];
        for (int i = 0; i < NUM_ITEMS; i++) {
            weights[i] = Math.floor(random.nextDouble() * MAX_WEIGHT);
            volumes[i] = Math.floor(random.nextDouble() * MAX_VOLUME);
        }
         int[] ranges = new int[NUM_ITEMS];
        Arrays.fill(ranges, COPIES_EACH + 1);
        EvaluationFunction ef = new KnapsackEvaluationFunction(weights, volumes, KNAPSACK_VOLUME, copies);
        Distribution odd = new DiscreteUniformDistribution(ranges);
        NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new UniformCrossOver();
        Distribution df = new DiscreteDependencyTree(.1, ranges); 
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        String annealingresults = "";
        String garesults = "";
        String mimicresults = "";
        
        
        //loop through number of iterations
        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
       FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 200000);
       // fit.train();
      //  System.out.println(ef.value(rhc.getOptimal()));

       for (int iterations = 1000; iterations < 100000; iterations=iterations+1000) {
       
        //loop through different temperatures and cooling rates
//double[] temp = { 100, 200 , 300, 400,500, 600, 700, 800 };
        double[] temp = { 200 };	
    	//double[] coolingRate = {.90,.91,.92,.93,.94,.95,.96,.97,.98,.99};
double[] coolingRate = {.96};
  /*          for (double temperature : temp)
    			{ 
            	
                for (double cool : coolingRate)
    			{	
        
        
        SimulatedAnnealing sa = new SimulatedAnnealing(700, .92, hcp);
        fit = new FixedIterationTrainer(sa, iterations);
        double start = System.nanoTime();
        fit.train();
        double end = System.nanoTime();
        double testingTime = end - start;
        testingTime /= Math.pow(10,9);
        System.out.println("annealing " + iterations + " value " + ef.value(sa.getOptimal()) + " TestTime " + testingTime);
        annealingresults += "\n"+ ef.value(sa.getOptimal()) + "for the following parameters" + 
        "start temperature=" + temperature + "cooling rate=" + cool + "testingtime is " + testingTime;
        
    			}
    			};*/
    	//System.out.println(annealingresults);
        
        //Loop through different populations & try different iterations of the best
        //population
/*        for (int popSize = 260; popSize < 261; popSize = popSize + 20){
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(popSize, popSize/2, popSize/10, gap);
        double start = System.nanoTime();
        fit = new FixedIterationTrainer(ga, iterations);
        fit.train();
        double end = System.nanoTime();
        double testingTime = end - start;
        testingTime /= Math.pow(10,9);
        System.out.println("GA " + iterations + " value " + ef.value(ga.getOptimal()) + " TestTime " + testingTime);
        garesults += "\n"+ ef.value(ga.getOptimal()) + "for the following parameters, Population size of "
        + popSize + " mate= " + popSize/2 + " mutate= "+ popSize/10 
        + "testingtime is " + testingTime;
        
        };*/
       // System.out.println(garesults);
        //Loop through mimic params
int[] samplespace = { 800 };
    	
int[] randsamples = {60};
            for (int samples : samplespace)
    			{ 
            
                for (int randomsamples : randsamples)
    			{	
        MIMIC mimic = new MIMIC(samples, randomsamples, pop);
        double start = System.nanoTime();
        fit = new FixedIterationTrainer(mimic, iterations);
        fit.train();
        double end = System.nanoTime();
        double testingTime = end - start;
        testingTime /= Math.pow(10,9);
        System.out.println("MIMIC " + iterations +" value " + ef.value(mimic.getOptimal()) + " TestTime " + testingTime);
        mimicresults += "\n"+ ef.value(mimic.getOptimal()) + "for the following parameters " +
        		" number of samples = " + samples + " number of random samples= " + randomsamples
        		+ "testingtime is " + testingTime;
        
    			}
    			};
    	//System.out.println(mimicresults);		
    }
    }
       
}
