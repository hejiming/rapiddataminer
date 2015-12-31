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
package com.rapidminer.operator.performance;

import java.util.Iterator;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.math.Averagable;


/**
 * The logistic loss of a classifier, defined as 
 * the average over all ln(1 + exp(-y * f(x)))
 *  
 * @author Ingo Mierswa
 * @version $Id: LogisticLoss.java,v 1.5 2008/05/09 19:22:43 ingomierswa Exp $
 */
public class LogisticLoss extends MeasuredPerformance {

    private static final long serialVersionUID = -2987795640706342168L;

    /** The value of the loss. */
    private double loss = Double.NaN;

    private double counter = 0.0d;
    
    /** Clone constructor. */
    public LogisticLoss() {}

    public LogisticLoss(LogisticLoss m) {
        super(m);
        this.loss = m.loss;
        this.counter = m.counter;
    }

    /** Calculates the margin. */
    public void startCounting(ExampleSet exampleSet, boolean useExampleWeights) throws OperatorException {
    	super.startCounting(exampleSet, useExampleWeights);
        // compute margin
        Iterator<Example> reader = exampleSet.iterator();
        this.loss = 0.0d;
        Attribute labelAttr = exampleSet.getAttributes().getLabel();
        Attribute weightAttr = null;
        if (useExampleWeights)
        	weightAttr = exampleSet.getAttributes().getWeight();
        
        while (reader.hasNext()) {
            Example example = reader.next();
            String trueLabel = example.getNominalValue(labelAttr);
            double confidence = example.getConfidence(trueLabel);
            double weight = 1.0d;
            if (weightAttr != null)
            	weight = example.getValue(weightAttr);
            double currentMargin = weight * Math.log(1.0d + Math.exp(-1 * confidence));
            this.loss += currentMargin;
        }
        this.loss /= exampleSet.size();
    }

    /** Does nothing. Everything is done in {@link #startCounting(ExampleSet, boolean)}. */
    public void countExample(Example example) {}

    public double getExampleCount() {
        return counter;
    }

    public double getMikroVariance() {
        return Double.NaN;
    }

    public double getMikroAverage() {
        return this.loss / counter;
    }

    /** Returns 0.
     */
    public double getMaxFitness() {
        return 0.0d;
    }
    
    /** Returns the fitness. */
    public double getFitness() {
        return -1 * getAverage();
    }

    public String getName() {
        return "logistic_loss";
    }

    public String getDescription() {
        return "The logistic loss of a classifier, defined as the average of ln(1 + exp(- [confidence of the correct class]))";
    }

    public void buildSingleAverage(Averagable performance) {
        LogisticLoss other = (LogisticLoss) performance;
        this.loss += other.loss;
        this.counter += other.counter;
    }

    /** Returns the super class implementation of toString(). */
    public String toString() {
        return super.toString();
    }
}
