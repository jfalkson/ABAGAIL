package opt.example;

import util.linalg.Vector;
import opt.EvaluationFunction;
import shared.Instance;

/**
 * A four peaks evaluation function
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class MaxOneBitEvaluationFunction implements EvaluationFunction {
    /**
    /**
     * @see opt.EvaluationFunction#value(opt.OptimizationData)
     */
    public double value(Instance d) {
        Vector data = d.getData();
        int i = 0;
        int count = 1;
        while (i < data.size() ) {
        	if (data.get(i) == 1) {
               count += 1;
            }
        	
            i++;
        }
        
        return count;
    }
    
    
}
