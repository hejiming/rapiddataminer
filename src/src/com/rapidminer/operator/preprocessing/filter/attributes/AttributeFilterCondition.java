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

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.set.ConditionCreationException;

/**
 * This interface must be implemented by classes implementing an AttributeFilterCondition for
 * the AttributeFilter operator.
 * 
 * @author Sebastian Land
 * @version $Id: AttributeFilterCondition.java,v 1.3 2008/05/09 19:23:26 ingomierswa Exp $
 */
public interface AttributeFilterCondition {
	
	/**
	 * This method inits this condition an resets all counters.
	 * It returns true, if the attribute can be removed withour checking examples.
	 * If it has been removed, no checking during examples will occour.
	 * @param attribute this is the attribute, the filter will have to check for.
	 * @throws ConditionCreationException 
	 */
	public boolean initCheck(Attribute attribute, String parameter, boolean invert) throws ConditionCreationException;
	
	/**
	 * This method has to return true, if the associated attribute, given in the init phase,
	 * should be filtered out from example set.
	 * This method will be called after the scan.
	 * @return true to filter the attribute, false to keep it in the exampleSet
	 */
	public boolean shouldBeRemoved();
	
	/**
	 * This method checks the given example. During this method the filter might check data to 
	 * decide if attribute should be filtered out.
	 */
	public void check(Example example);
	
}
