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

/**
 * This class implements a missing value filter for attribtues. Attributes are filtered and hence be removed from 
 * exampleSet if there are NO missing value in one of the examples in this attribute.
 * 
 * @author Sebastian Land
 * @version $Id: MissingValuesAttributeFilter.java,v 1.3 2008/05/09 19:23:26 ingomierswa Exp $
 */
public class MissingValuesAttributeFilter implements AttributeFilterCondition {
	private Attribute attribute;
	private boolean keep;
	private boolean invert;
	
	public void check(Example example) {
		keep = keep || Double.isNaN(example.getValue(attribute));
	}

	public boolean shouldBeRemoved() {
		return !keep ^ invert;
	}

	public boolean initCheck(Attribute attribute, String parameter, boolean invert) {
		this.attribute = attribute;
		this.keep = false;
		this.invert = invert;
		return false;
	}
}
