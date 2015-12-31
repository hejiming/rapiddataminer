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
package com.rapidminer.operator.learner.bayes;

import java.util.Collection;
import java.util.HashMap;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.table.NominalMapping;
import com.rapidminer.tools.Tools;


/**
 * DiscreteDistribution is an distribution for nominal values. For probability calculation it counts the frequency of all values and returns this
 * number + 1 of the given value divided by the total number of all examples + the number of different values.
 * 
 * @author Sebastian Land, Ingo Mierswa
 * @version $Id: DiscreteDistribution.java,v 1.9 2008/05/09 19:23:21 ingomierswa Exp $
 */
public class DiscreteDistribution implements Distribution {

	private static final long serialVersionUID = 7573474548080998479L;

	private HashMap<Double, Double> valueWeights = new HashMap<Double, Double>();
	private double totalWeight;
	private Attribute attribute;
	private NominalMapping mapping; 
	
	public DiscreteDistribution(Attribute attribute, HashMap<Double, Double> valueWeights, double totalWeight) {
		this.attribute = attribute;
		this.valueWeights = valueWeights;
		this.totalWeight = totalWeight;
		this.mapping = attribute.getMapping();
	}

	public double getProbability(double x) {
		Double weight = valueWeights.get(x);
		if (weight != null) {
			return weight / totalWeight;
		}
		return 0;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		NominalMapping mapping = attribute.getMapping();
		for (Double valueKey: valueWeights.keySet()) {
			String valueName;
			if (Double.isNaN(valueKey))
				valueName = "unkown";
			else
				valueName = mapping.mapIndex(valueKey.intValue());
			buffer.append(valueName + "\t");
		}
		buffer.append(Tools.getLineSeparator());
		for (Double valueKey : valueWeights.keySet()) {
			Double weightObject = valueWeights.get(valueKey);
			if (weightObject != null)
				buffer.append(Tools.formatIntegerIfPossible(weightObject.doubleValue() / totalWeight) + "\t");
			else
				buffer.append("?\t");
		}
		return buffer.toString();
	}

	public double getLowerBound() {
		return Double.NaN;
	}

	public double getUpperBound() {
		return Double.NaN;
	}

	public Collection<Double> getValues() {
		return valueWeights.keySet();
	}
	public double getTotalWeight() {
		return this.totalWeight;
	}
	public String mapValue(double value) {
		return mapping.mapIndex((int)value);
	}
}
