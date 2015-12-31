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
package com.rapidminer.operator.preprocessing.filter.attributes;

import java.util.ArrayList;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.set.ConditionCreationException;

/**
 * This class implements a condition for the AttributeFilter operator. 
 * It provides the possibility to check if all values of a numerical attribute
 * match a condition. This conditions might be specified by != or <>, =, <, <=, >, >= 
 * followed by a value. For example like this: "> 6.5" would keep all attributes having only values
 * greater 6.5. 
 * This single conditions might be combined by || or && but not mixed. Example: "> 6.5 && < 11" would keep
 * all attributes containing only values between 6.5 and 11.
 * 
 * @author Sebastian Land
 * @version $Id: NumericValueAttributeFilter.java,v 1.3 2008/05/09 19:23:26 ingomierswa Exp $
 */
public class NumericValueAttributeFilter implements AttributeFilterCondition {
	
	private ArrayList<Condition> conditions;
	private Attribute attribute;
	private boolean keep;
	private boolean invert;
	private boolean conjunctiveMode;
	
	private class Condition {
		private int condition;
		private double value;
		public Condition(String condition, String value) {
			this.value = Double.parseDouble(value);
			if (condition.equals("<>") || condition.equals("!=")) {
				this.condition = 1;
			} else if (condition.equals("<=")) {
				this.condition = 2;
			} else if (condition.equals("<")) {
				this.condition = 3;
			} else if (condition.equals(">=")) {
				this.condition = 4;
			} else if (condition.equals(">")) {
				this.condition = 5;
			} else if (condition.equals("=")) {
				this.condition = 0;
			}
		}

		public boolean check(double value) {
			switch (condition) {
			case 0: return (value == this.value);
			case 1: return (value != this.value);
			case 2: return (value <= this.value);
			case 3: return (value < this.value);
			case 4: return (value >= this.value);
			case 5: return (value > this.value);
			}
			return false;
		}
	}
	
	public void check(Example example) {
		if (!attribute.isNominal()) {
			boolean exampleResult;
			double checkValue = example.getValue(attribute);
			
			if (conjunctiveMode) {
				exampleResult = true;
				for(Condition condition: conditions) {
					exampleResult = exampleResult && condition.check(checkValue);
				}
			} else {
				exampleResult = false;
				for(Condition condition: conditions) {
					exampleResult = exampleResult || condition.check(checkValue);
				}	
			}
			keep = keep && exampleResult;
		}
	}

	public boolean initCheck(Attribute attribute, String parameter, boolean invert) throws ConditionCreationException {
		if ((parameter == null) || (parameter.length() == 0))
			throw new ConditionCreationException("The condition for numerical values needs a parameter string.");
		
		this.invert = invert;
		// testing if not allowed combination of and and or
		if (parameter.contains("||") && parameter.contains("&&")) {
			throw new ConditionCreationException("|| and && not allowed in one condition");
		}
		this.conjunctiveMode = parameter.contains("&&");
		conditions = new ArrayList<Condition>();
		for (String conditionString: parameter.split("[|&]{2}")) {
			String[] parts = conditionString.trim().split("\\s+");
			if (parts.length != 2) {
				throw new ConditionCreationException("number of condition arguments not correct");
			} else {
				conditions.add(new Condition(parts[0], parts[1]));
			}
		}
		
		this.attribute = attribute;
		this.keep = true;
		return false;
	}

	public boolean shouldBeRemoved() {
		return (invert ^ !keep) && !attribute.isNominal();
	}

}
