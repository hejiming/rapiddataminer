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
 * This condition keeps all attributes which are numeric or nominal and containing
 * specified values. This values are specified by the parameter string, which is interpreted as follows:
 * The nominal values are seperated by either || or && but not mixed. If || is used an attributes has to 
 * contain at least one value equal to one of the nominal values, if && every value has to be contained.
 * For example:
 * "rainy && cloudy" would keep all attributes containing at least one time "rainy" and one time "cloudy".
 * "rainy || sunny" would keep all attributes containing at least one time "rainy" or one time "sunny".
 * 
 * @author Sebastian Land
 * @version $Id: NominalValueAttributeFilter.java,v 1.3 2008/05/09 19:23:26 ingomierswa Exp $
 */
public class NominalValueAttributeFilter implements AttributeFilterCondition {
	
	private ArrayList<Condition> conditions;
	private Attribute attribute;
	private boolean keep;
	private boolean invert;
	private boolean conjunctiveMode;
	
	private class Condition {
		private String value;
		public Condition(String value) {
			this.value = value;
		}

		public boolean check(String value) {
			return (this.value.equals(value));
		}
	}
	
	public void check(Example example) {
		if (attribute.isNominal()) {
			boolean exampleResult = false;
			String checkValue = attribute.getMapping().mapIndex((int) example.getValue(attribute));
			
			if (conjunctiveMode) {
				for(Condition condition: conditions) {
					exampleResult = exampleResult && condition.check(checkValue);
				}
			} else {
				for(Condition condition: conditions) {
					exampleResult = exampleResult || condition.check(checkValue);
				}	
			}
			keep = keep || exampleResult;
		}
	}

	public boolean initCheck(Attribute attribute, String parameter, boolean invert) throws ConditionCreationException {
		if ((parameter == null) || (parameter.length() == 0))
			throw new ConditionCreationException("The condition for nominal values needs a parameter string.");
		
		this.invert = invert;
		// testing if not allowed combination of and and or
		if (parameter.contains("||") && parameter.contains("&&")) {
			throw new ConditionCreationException("|| and && not allowed in one condition");
		}
		this.conjunctiveMode = parameter.contains("&&");
		conditions = new ArrayList<Condition>();
		for (String conditionString: parameter.split("[|&]{2}")) {
			conditions.add(new Condition(conditionString.trim()));
		}
		
		this.attribute = attribute;
		this.keep = false;
		return false;
	}

	public boolean shouldBeRemoved() {
		return (invert ^ !keep) && attribute.isNominal();
	}

}
