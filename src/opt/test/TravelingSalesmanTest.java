package opt.test;

import java.util.Arrays;
import java.util.Random;

import dist.DiscreteDependencyTree;
import dist.DiscretePermutationDistribution;
import dist.DiscreteUniformDistribution;
import dist.Distribution;
import opt.SwapNeighbor;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.SwapMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * 
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class TravelingSalesmanTest {
    /** The n value */
    private static final int N = 50;
    /**
     * The test main
     * @param args ignored
     */
    /** The maximum volume for a single element */
    //private static final double MAX_DISTANCE = 50;
    public static void main(String[] args) {
        Random random = new Random();
        // create the random points
        double[][] points = new double[N][2];
        for (int i = 0; i < points.length; i++) {
            points[i][0] = random.nextDouble();
            points[i][1] = random.nextDouble();   
        }
        // for rhc, sa, and ga we use a permutation based encoding
        TravelingSalesmanEvaluationFunction ef = new TravelingSalesmanRouteEvaluationFunction(points);
        Distribution odd = new DiscretePermutationDistribution(N);
        NeighborFunction nf = new SwapNeighbor();
        MutationFunction mf = new SwapMutation();
        CrossoverFunction cf = new TravelingSalesmanCrossOver(ef);
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        String annealingresults = "";
        String garesults = "";
        String mimicresults = "";
        //loop through number of iterations
        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
       FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 200000);
       // fit.train();
      //  System.out.println(ef.value(rhc.getOptimal()));

       for (int iterations = 100000; iterations < 10000000; iterations=iterations+100000) {
       
        //loop through different temperatures and cooling rates
//double[] temp = { 100, 200 , 300, 400,500, 600, 700, 800 };
        double[] temp = { 700 };	
    	//double[] coolingRate = {.90,.91,.92,.93,.94,.95,.96,.97,.98,.99};
double[] coolingRate = {.92};
            for (double temperature : temp)
    			{ 
            	
                for (double cool : coolingRate)
    			{	
        
        
        SimulatedAnnealing sa = new SimulatedAnnealing(100, .98, hcp);
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
    			};
    	//System.out.println(annealingresults);
        
        //Loop through different populations & try different iterations of the best
        //population
 /*       for (int popSize = 180; popSize < 181; popSize = popSize + 20){
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
int[] samplespace = { 700 };
    	
int[] randsamples = {10};
            for (int samples : samplespace)
    			{ 
            
                for (int randomsamples : randsamples)
    			{	

     /*           	//for mimic we use a sort encoding
                	ef = new TravelingSalesmanSortEvaluationFunction(points);
                	int[] ranges = new int[N];
                	Arrays.fill(ranges, N);
                	odd = new  DiscreteUniformDistribution(ranges);
                	Distribution df = new DiscreteDependencyTree(.1, ranges); 
                	ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);        	
                	
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
        */
    			}
    			};
    	//System.out.println(mimicresults);		
    }
    }
       
}







