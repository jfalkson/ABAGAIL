package opt.test;

import java.util.Arrays;

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
import opt.ga.SingleCrossOver;
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
 */ //answer should be 60 + Max(7, 53)  = 113 
public class FourPeaksTest {
    /** The n value */
	//let's loop through different values of N
    private static final int N = 60;
    /** The t value */
    //see charles' paper for an explanation of the scope of the problem.
    //reminder to change to but string 
    private static final int T = N / 10;
    
    public static void main(String[] args) {
        int[] ranges = new int[N];
        Arrays.fill(ranges, 2);
        EvaluationFunction ef = new FourPeaksEvaluationFunction(T);
        Distribution odd = new DiscreteUniformDistribution(ranges);
        NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new SingleCrossOver();
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
        fit.train();
        System.out.println(ef.value(rhc.getOptimal()));
        
        
        //loop through different temperatures and cooling rates
double[] temp = { 100, 200 , 300, 400,500, 600, 700, 800 };
    	
    	double[] coolingRate = {.90,.91,.92,.93,.94,.95,.96,.97,.98,.99};
   	
            for (double temperature : temp)
    			{ 
            	
                for (double cool : coolingRate)
    			{	
        
        
        SimulatedAnnealing sa = new SimulatedAnnealing(100, .95, hcp);
        fit = new FixedIterationTrainer(sa, 2000);
        double start = System.nanoTime();
        fit.train();
        double end = System.nanoTime();
        double testingTime = end - start;
        System.out.println(ef.value(sa.getOptimal()));
        annealingresults += "\n"+ ef.value(sa.getOptimal()) + "for the following parameters" + 
        "start temperature=" + temperature + "cooling rate=" + cool + "testingtime is " + testingTime;
        
    			}
    			};
    	System.out.println(annealingresults);
        
        //Loop through different populations & try different iterations of the best
        //population
        for (int popSize = 180; popSize < 300; popSize = popSize + 20){
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(popSize, popSize/2, popSize/10, gap);
        double start = System.nanoTime();
        fit = new FixedIterationTrainer(ga, 2000);
        fit.train();
        double end = System.nanoTime();
        double testingTime = end - start;
        System.out.println(ef.value(ga.getOptimal()));
        garesults += "\n"+ ef.value(ga.getOptimal()) + "for the following parameters, Population size of "
        + popSize + " mate= " + popSize/2 + " mutate= "+ popSize/10 
        + "testingtime is " + testingTime;
        
        };
        System.out.println(garesults);
        //Loop through mimic params
int[] samplespace = { 100, 200 , 300, 400,500, 600, 700, 800 };
    	
int[] randsamples = {10,20, 30 ,40 , 50, 60 , 70, 80, 90};
            for (int samples : samplespace)
    			{ 
            
                for (int randomsamples : randsamples)
    			{	
        MIMIC mimic = new MIMIC(samples, randomsamples, pop);
        double start = System.nanoTime();
        fit = new FixedIterationTrainer(mimic, 2000);
        fit.train();
        double end = System.nanoTime();
        double testingTime = end - start;
        System.out.println(ef.value(mimic.getOptimal()));
        mimicresults += "\n"+ ef.value(mimic.getOptimal()) + "for the following parameters " +
        		" number of samples = " + samples + " number of random samples= " + randomsamples
        		+ "testingtime is " + testingTime;
        
    			}
    			};
    	System.out.println(mimicresults);		
    }

}
