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
package com.rapidminer.operator.learner.igss.hypothesis;


import java.io.Serializable;
import java.util.LinkedList;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;

/** Abstract superclass for all possible kinds of hypothesis. 
 * 
 *  @author Dirk Dach 
 *  @version $Id: Hypothesis.java,v 1.4 2008/05/09 19:23:25 ingomierswa Exp $
 */
public abstract class Hypothesis implements Serializable {

	public static final String[] HYPOTHESIS_SPACE_TYPES={
        "rule"
	};
    
	public static final int FIRST_TYPE_INDEX=0;
	public static final int TYPE_RULE=0;
	public static final int LAST_TYPE_INDEX=0;
	
	public static final int POSITIVE_CLASS=1;
	public static final int NEGATIVE_CLASS=0;

	/** The label attribute.*/
	private Attribute label;
	
	/** Indicates if rejection sampling is used to draw the examples that this rule is applied to.
	 * In this case (positive)example counter is incremented by one for every (positive)example the rule is applicable to.
	 * In the other case(weights are used directly) the weight of the example is added to the counters. */
	protected boolean rejectionSampling;
	
	/** Stores the total weight of all examples covered by this hypothesis.*/
	protected double coveredWeight;

	/** Stores the weight of examples covered by this hypothesis with correct prediction. */
	protected double positiveWeight;
	
	/** Create h->Y+/Y- or h->Y+ only.*/
	protected boolean createAllHypothesis; 
	
	/** Create a new dummy hypothesis to allow calling the 'init' method, 
	 * initialize the regularAttributes, label and p0 fields.*/
	public Hypothesis (Attribute[] regulars, Attribute l, boolean rs, boolean createAll) {
		rejectionSampling=rs;
		createAllHypothesis=createAll;
		label=l;
	}
	public Hypothesis () {
		this.coveredWeight=0.0d;
		this.positiveWeight=0.0d;
	}
	
	/** Clone method. */
	public abstract Hypothesis clone();
	
	/** Returns the label index the hypothesis predicts. */
	public abstract int getPrediction();
	
	/** Returns the label.*/
	public Attribute getLabel() {
		return label;
	}
	/** Sets 'coveredWeight' and 'positiveWeight' back to 0.0d.*/
	public void reset() {
		this.coveredWeight=0.0d;
		this.positiveWeight=0.0d;
	}
	
	/** Returns the covered weight of this hypothesis.*/
	public double getCoveredWeight() {
		return this.coveredWeight;
	}
	
	/** Sets the covered weight of this hypothesis.*/
	public void setCoveredWeight(double value) {
		this.coveredWeight=value;
	}
	
	/** Returns the covered positive weight of this hypothesis.*/
	public double getPositiveWeight() {
		return this.positiveWeight;
	}
	
	/** Sets the covered positive weight of this hypothesis.*/
	public void setPositiveWeight(double value) {
		this.positiveWeight=value;
	}
	
	/** Hypothesis is applied to the example and internal statistics of the hypothesis are updated.*/
	public abstract void apply (Example e);

	/** Tests if the hypothesis is applicable to the example without updating the internal statistics of the hypothesis.*/
	public abstract boolean applicable (Example e);
	
	/** Generates new hypothesis by adding one degree of complexity and creating all rules that are possible now.
	 * Classes extending this abstract class define the way the hypothesis are generated by the IGSS alsgorithm by
	 * implementing the refine() method. Be sure to avoid the creation of duplicate hypothesis, for example
	 * by overwriting and using the equals method.
	 * For example refine could increase the allowed depth of a decision tree or the number of literals of a conjunctive rule.
	 * Must return null if the hypothesis cannot be refined any more.
	 * */
	public abstract LinkedList<Hypothesis> refine();

	/** Returns true only if this hypothesis can still be refined.*/
	public abstract boolean canBeRefined();
	
	/** Used to generate the first hypothesis or the first group of hypothesis.*/
	public abstract LinkedList<Hypothesis> init(int minComplexity);

	/** Returns complexity of the hypothesis.*/
	public abstract int getComplexity();
	
}