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
package com.rapidminer.operator.similarity.bregmandivergences;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;

/**
 * The &quot;Logistic loss &quot;.
 * 
 * @author Regina Fritsch
 * @version $Id: LogisticLoss.java,v 1.2 2008/05/09 19:23:19 ingomierswa Exp $
 */
public class LogisticLoss extends AbstractBregmanDivergence {

	public LogisticLoss(ExampleSet es) throws InstantiationException {
		super(es);
	}

	public double distance(Example x, double[] y) {
		double result = 0;
		double xValue = 0;
		for (Attribute att : x.getAttributes()) {
			xValue = x.getValue(att);
		}
		result = xValue * Math.log(xValue / y[0]) + (1 - xValue) *  Math.log( (1 - xValue) / (1 - y[0]) ); 
		
		return result;
	}

	public boolean isApplicable(Example x) {
		if (x.getAttributes().size() != 1) {
			return false;
		}
		for (Attribute att : x.getAttributes()) {
			/* greater/equal - if y is 0 or 1 there will be a division by zero*/
			if (x.getValue(att) <= 0 || x.getValue(att) >= 1 ) {
				return false;
			}
		}
		return true;
	}

}
