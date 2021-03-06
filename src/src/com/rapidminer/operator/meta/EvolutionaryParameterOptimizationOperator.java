/*
 *  RapidMiner
 *
 *  Copyright (C) 2001-2008 by Rapid-I and the contributors
 *
 *  Complete list of developers available at our web site:
 *
 *       http://rapid-i.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package com.rapidminer.operator.meta;

import java.util.Iterator;
import java.util.List;

import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.Value;
import com.rapidminer.operator.performance.PerformanceVector;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.value.ParameterValues;
import com.rapidminer.parameter.value.ParameterValueRange;
import com.rapidminer.tools.RandomGenerator;
import com.rapidminer.tools.math.optimization.ec.es.ESOptimization;
import com.rapidminer.tools.math.optimization.ec.es.Individual;
import com.rapidminer.tools.math.optimization.ec.es.OptimizationValueType;


/**
 * This operator finds the optimal values for a set of parameters using an evolutionary
 * strategies approach which is often more appropriate than a grid search or a greedy search
 * like the quadratic programming approach and leads to better results. The parameter 
 * <var>parameters</var> is a list of key value pairs
 * where the keys are of the form <code>operator_name.parameter_name</code> and
 * the value for each parameter must be a semicolon separated pair of a minimum and a maximum value
 * in squared parantheses, e.g. [10;100] for a range of 10 until 100. <br/> 
 * The operator returns an
 * optimal {@link ParameterSet} which can as well be written to a file with a
 * {@link com.rapidminer.operator.io.ParameterSetWriter}. This parameter set
 * can be read in another process using a
 * {@link com.rapidminer.operator.io.ParameterSetLoader}. <br/> 
 * The file format of the parameter set file is straightforward and can easily be
 * generated by external applications. Each line is of the form 
 * <center><code>operator_name.parameter_name = value</code></center> <br/> 
 * Please refer to section
 * {@rapidminer.ref sec:parameter_optimization|Advanced Processes/Parameter and performance analysis}
 * for an example application.
 * 
 * @author Ingo Mierswa, Tobias Malbrecht
 * @version $Id: EvolutionaryParameterOptimizationOperator.java,v 1.10 2008/05/09 19:22:38 ingomierswa Exp $
 */
public class EvolutionaryParameterOptimizationOperator extends ParameterOptimizationOperator {
    
	/** The parameter name for &quot;Stop after this many evaluations&quot; */
	public static final String PARAMETER_MAX_GENERATIONS = "max_generations";

	/** The parameter name for &quot;Stop after this number of generations without improvement (-1: optimize until max_iterations).&quot; */
	public static final String PARAMETER_GENERATIONS_WITHOUT_IMPROVAL = "generations_without_improval";

	/** The parameter name for &quot;The population size (-1: number of examples)&quot; */
	public static final String PARAMETER_POPULATION_SIZE = "population_size";

	/** The parameter name for &quot;The fraction of the population used for tournament selection.&quot; */
	public static final String PARAMETER_TOURNAMENT_FRACTION = "tournament_fraction";

	/** The parameter name for &quot;Indicates if the best individual should survive (elititst selection).&quot; */
	public static final String PARAMETER_KEEP_BEST = "keep_best";

	/** The parameter name for &quot;The type of the mutation operator.&quot; */
	public static final String PARAMETER_MUTATION_TYPE = "mutation_type";

	/** The parameter name for &quot;The type of the selection operator.&quot; */
	public static final String PARAMETER_SELECTION_TYPE = "selection_type";

	/** The parameter name for &quot;The probability for crossover.&quot; */
	public static final String PARAMETER_CROSSOVER_PROB = "crossover_prob";

	/** The parameter name for &quot;Use the given random seed instead of global random numbers (-1: use global).&quot; */
	public static final String PARAMETER_LOCAL_RANDOM_SEED = "local_random_seed";

	/** The parameter name for &quot;Indicates if a dialog with a convergence plot should be drawn.&quot; */
	public static final String PARAMETER_SHOW_CONVERGENCE_PLOT = "show_convergence_plot";
    /** This variable holds a reference to the input container which is copied before each evaluation. */
    private IOContainer input;
    
    /** The actual optimizer. */
    private ESOptimization optimizer;
    
    /** The operators for which parameters should be optimized. */
    private Operator[] operators;
    
    /** The names of the parameters which should be optimized. */
    private String[] parameters;
    
    /** The parameter types. */
    private OptimizationValueType[] types;
    
    
    public EvolutionaryParameterOptimizationOperator(OperatorDescription description) {
        super(description);
        addValue(new Value("best", "best performance ever") {
            public double getValue() {
                return optimizer.getBestFitnessEver();
            }
        });
    }

    public int getParameterValueMode() {
    	return VALUE_MODE_CONTINUOUS;
    }
    
    public double getCurrentBestPerformance() {
        return optimizer.getBestFitnessInGeneration();
    }
    
    public IOObject[] apply() throws OperatorException {
        // init
        this.input = getInput();

        // check parameter values list
        List<ParameterValues> parameterValuesList = parseParameterValues(getParameterList("parameters"));
        if (parameterValuesList == null) {
        	throw new UserError(this, 922);
        }
		for (Iterator<ParameterValues> iterator = parameterValuesList.iterator(); iterator.hasNext(); ) {
			ParameterValues parameterValues = iterator.next();
			if (!(parameterValues instanceof ParameterValueRange)) {
				logWarning("found (and deleted) unsupported parameter value definition. Parameters have to be given as range (e.g. as [2;5.7]).");
                iterator.remove();
			}
		}
		if (parameterValuesList.size() == 0) {
			throw new UserError(this, 922);
		}
		
		// get parameters to optimize
        this.operators = new Operator[parameterValuesList.size()];
        this.parameters = new String[parameterValuesList.size()];
        double[] min = new double[parameterValuesList.size()];
        double[] max = new double[parameterValuesList.size()];
        this.types = new OptimizationValueType[parameterValuesList.size()];

		int index = 0;
		for (Iterator<ParameterValues> iterator = parameterValuesList.iterator(); iterator.hasNext(); ) {
			ParameterValueRange parameterValueRange = (ParameterValueRange) iterator.next();
			operators[index] = parameterValueRange.getOperator();
			parameters[index] = parameterValueRange.getParameterType().getKey();
			min[index] = parameterValueRange.getMin();
			max[index] = parameterValueRange.getMax();
			
            ParameterType targetType = parameterValueRange.getParameterType();
            if (targetType == null) {
                throw new UserError(this, 906, parameterValueRange.getOperator() + "." + targetType.getKey());
            }
            if (targetType instanceof ParameterTypeDouble) {
                types[index] = OptimizationValueType.VALUE_TYPE_DOUBLE;
                log("Parameter type of parameter " + targetType.getKey() + ": double");
            } else if (targetType instanceof ParameterTypeInt) {
                types[index] = OptimizationValueType.VALUE_TYPE_INT;
                log("Parameter type of parameter " + targetType.getKey() + ": int");
            } else {
                throw new UserError(this, 909, targetType.getKey());
            }
			index++;
		}
        
        // create and start optimizer
        RandomGenerator random = RandomGenerator.getRandomGenerator(getParameterAsInt(PARAMETER_LOCAL_RANDOM_SEED));
        this.optimizer = new ESParameterOptimization(
                this, 
                operators.length, 
                ESOptimization.INIT_TYPE_RANDOM, 
                getParameterAsInt(PARAMETER_MAX_GENERATIONS), 
                getParameterAsInt(PARAMETER_GENERATIONS_WITHOUT_IMPROVAL),
                getParameterAsInt(PARAMETER_POPULATION_SIZE), 
                getParameterAsInt(PARAMETER_SELECTION_TYPE), 
                getParameterAsDouble(PARAMETER_TOURNAMENT_FRACTION), 
                getParameterAsBoolean(PARAMETER_KEEP_BEST), 
                getParameterAsInt(PARAMETER_MUTATION_TYPE), 
                getParameterAsDouble(PARAMETER_CROSSOVER_PROB),
                getParameterAsBoolean(PARAMETER_SHOW_CONVERGENCE_PLOT), 
                random,
                this);
        
        for (int i = 0; i < min.length; i++) {
            this.optimizer.setMin(i, min[i]);
            this.optimizer.setMax(i, max[i]);
            this.optimizer.setValueType(i, types[i]);
        }
        
        optimizer.optimize();

        // create result and return it
        double[] bestParameters = optimizer.getBestValuesEver();
        String[] bestValues = null;
        if (bestParameters != null) {
            bestValues = new String[bestParameters.length];
            for (int i = 0; i < bestParameters.length; i++) {
                if (types[i].equals(OptimizationValueType.VALUE_TYPE_DOUBLE))
                    bestValues[i] = bestParameters[i] + "";
                else 
                    bestValues[i] = (int)Math.round(bestParameters[i]) + "";
            }
        } else {
            bestValues = new String[operators.length];
            for (int i = 0; i < bestValues.length; i++) {
                bestValues[i] = "unknown";
            }
        }
        ParameterSet bestSet = new ParameterSet(operators, parameters, bestValues, optimizer.getBestPerformanceEver());
        return new IOObject[] { bestSet, bestSet.getPerformance() };
    }

    public PerformanceVector setParametersAndEvaluate(Individual individual) throws OperatorException {
        double[] currentValues = individual.getValues();
        for (int j = 0; j < currentValues.length; j++) {
            String value;
            if (types[j].equals(OptimizationValueType.VALUE_TYPE_DOUBLE))
                value = currentValues[j] + "";
            else
                value = (int)Math.round(currentValues[j]) + "";
            operators[j].getParameters().setParameter(parameters[j], value);
            log(operators[j] + "." + parameters[j] + " = " + value);
        }
        setInput(this.input.copy());
        return getPerformance();    
    }
    
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.add(new ParameterTypeInt(PARAMETER_MAX_GENERATIONS, "Stop after this many evaluations", 1, Integer.MAX_VALUE, 50));
        types.add(new ParameterTypeInt(PARAMETER_GENERATIONS_WITHOUT_IMPROVAL, "Stop after this number of generations without improvement (-1: optimize until max_iterations).", -1, Integer.MAX_VALUE, 1));
        types.add(new ParameterTypeInt(PARAMETER_POPULATION_SIZE, "The population size (-1: number of examples)", -1, Integer.MAX_VALUE, 5));
        types.add(new ParameterTypeDouble(PARAMETER_TOURNAMENT_FRACTION, "The fraction of the population used for tournament selection.", 0.0d, Double.POSITIVE_INFINITY, 0.25d));
        types.add(new ParameterTypeBoolean(PARAMETER_KEEP_BEST, "Indicates if the best individual should survive (elititst selection).", true));
        types.add(new ParameterTypeCategory(PARAMETER_MUTATION_TYPE, "The type of the mutation operator.", ESOptimization.MUTATION_TYPES, ESOptimization.GAUSSIAN_MUTATION));
        types.add(new ParameterTypeCategory(PARAMETER_SELECTION_TYPE, "The type of the selection operator.", ESOptimization.SELECTION_TYPES, ESOptimization.TOURNAMENT_SELECTION));
        types.add(new ParameterTypeDouble(PARAMETER_CROSSOVER_PROB, "The probability for crossover.", 0.0d, 1.0d, 0.9d));
        types.add(new ParameterTypeInt(PARAMETER_LOCAL_RANDOM_SEED, "Use the given random seed instead of global random numbers (-1: use global).", -1, Integer.MAX_VALUE, -1));
        types.add(new ParameterTypeBoolean(PARAMETER_SHOW_CONVERGENCE_PLOT, "Indicates if a dialog with a convergence plot should be drawn.", false));
        return types;
    }
}
