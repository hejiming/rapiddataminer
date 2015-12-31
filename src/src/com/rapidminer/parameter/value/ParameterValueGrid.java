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
package com.rapidminer.parameter.value;

import com.rapidminer.operator.Operator;
import com.rapidminer.parameter.ParameterType;


/**
 * A grid of numerical parameter values.
 * 
 * @author Tobias Malbrecht
 * @version $Id: ParameterValueGrid.java,v 1.2 2008/05/09 19:23:26 ingomierswa Exp $
 */
public class ParameterValueGrid extends ParameterValues {
	public static final int SCALE_LINEAR = 0;
	
	public static final int SCALE_QUADRATIC = 1;
	
	public static final int SCALE_LOGARITHMIC = 2;
	
	public static final String[] SCALES = { "linear", "quadratic", "logarithmic" };
	
	public static final int DEFAULT_STEPS = 10;
	
	public static final int DEFAULT_SCALE = SCALE_LINEAR;
	
	private double min;
	
	private double max;
	
	private int steps;
	
	private int scale;
	
	public ParameterValueGrid(Operator operator, ParameterType type, double min, double max) {
		this(operator, type, min, max, DEFAULT_STEPS, DEFAULT_SCALE);
	}
	
	public ParameterValueGrid(Operator operator, ParameterType type, double min, double max, double stepSize) {
		this(operator, type, min, max, (int) ((max - min) / stepSize), SCALE_LINEAR);
	}

	public ParameterValueGrid(Operator operator, ParameterType type, double min, double max, int steps, int scale) {
		super(operator, type);
		this.min = min;
		this.max = max;
		this.steps = steps;
		this.scale = scale;
	}

	public ParameterValueGrid(Operator operator, ParameterType type, double min, double max, int steps, String scaleName) {
		super(operator, type);
		this.min = min;
		this.max = max;
		this.steps = steps;
		this.scale = SCALE_LINEAR;
		for (int i = 0; i < SCALES.length; i++) {
			if (scaleName.equals(SCALES[i])) {
				this.scale = i;
				break;
			}
		}
	}
	
	public void setMin(double min) {
		this.min = min;
	}
	
	public double getMin() {
		return min;
	}
	
	public void setMax(double max) {
		this.max = max;
	}
	
	public double getMax() {
		return max;
	}
	
	public void setSteps(int steps) {
		this.steps = steps;
	}
	
	public int getSteps() {
		return steps;
	}
	
	public void setScale(int scale) {
		this.scale = scale;
	}
	
	public int getScale() {
		return scale;
	}

	public String[] getValuesArray() {
		double[] values = getValues();
		String[] valuesArray = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			valuesArray[i] = Double.toString(values[i]);
		}
		return valuesArray;
	}
	
	public double[] getValues() {
		switch (scale) {
        case SCALE_LINEAR:
        	return scalePolinomial(steps, 1);
        case SCALE_QUADRATIC:
        	return scalePolinomial(steps, 2);
        case SCALE_LOGARITHMIC:
        	return scaleLogarithmic(steps);
        default:
        	return scalePolinomial(steps, 1);
        }
	}

	private double[] scalePolinomial(int steps, double power) {
		double[] values = new double[steps + 1];
		for (int i = 0; i < steps + 1; i++) {
			values[i] = min + Math.pow((double) i / (double) steps, power) * (max - min);
		}
		return values;
	}
	
	private double[] scaleLogarithmic(int steps) {
		double[] values = new double[steps + 1];
		double offset = 1 - min;
		for (int i = 0; i < steps + 1; i++) {
			values[i] = Math.pow(max + offset, (double) i / (double) steps) - offset;
		}
		return values;
	}

	public int getNumberOfValues() {
		return steps + 1;
	}
	
	public String getValuesString() {
		return "[" + Double.toString(min) +
		       ";" + Double.toString(max) +
		       ";" + Integer.toString(steps) + 
		       ";" + SCALES[scale] + "]";
	}
	
	public String toString() {
		return "grid: " + min + " - " + max + " (" + steps + ", " + SCALES[scale] + ")";
	}
}
