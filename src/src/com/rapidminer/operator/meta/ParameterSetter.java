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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeList;
import com.rapidminer.parameter.ParameterTypeString;


/**
 * Sets a set of parameters. These parameters can either be generated by a
 * {@link ParameterOptimizationOperator} or read by a
 * {@link com.rapidminer.operator.io.ParameterSetLoader}. This operator is
 * useful, e.g. in the following scenario. If one wants to find the best
 * parameters for a certain learning scheme, one usually is also interested in
 * the model generated with this parameters. While the first is easily possible
 * using a {@link ParameterOptimizationOperator}, the latter is not possible
 * because the {@link ParameterOptimizationOperator} does not return the
 * IOObjects produced within, but only a parameter set. This is, because the
 * parameter optimization operator knows nothing about models, but only about
 * the performance vectors produced within. Producing performance vectors does
 * not necessarily require a model. <br/> To solve this problem, one can use a
 * <code>ParameterSetter</code>. Usually, a process with a
 * <code>ParameterSetter</code> contains at least two operators of the same
 * type, typically a learner. One learner may be an inner operator of the
 * {@link ParameterOptimizationOperator} and may be named &quot;Learner&quot;,
 * whereas a second learner of the same type named &quot;OptimalLearner&quot;
 * follows the parameter optimization and should use the optimal parameter set
 * found by the optimization. In order to make the <code>ParameterSetter</code>
 * set the optimal parameters of the right operator, one must specify its name.
 * Therefore, the parameter list <var>name_map</var> was introduced. Each
 * parameter in this list maps the name of an operator that was used during
 * optimization (in our case this is &quot;Learner&quot;) to an operator that
 * should now use these parameters (in our case this is
 * &quot;OptimalLearner&quot;).
 * 
 * @author Simon Fischer, Ingo Mierswa
 * @version $Id: ParameterSetter.java,v 1.5 2008/05/09 19:22:38 ingomierswa Exp $
 */
public class ParameterSetter extends Operator {


	/** The parameter name for &quot;A list mapping operator names from the set to operator names in the process setup.&quot; */
	public static final String PARAMETER_NAME_MAP = "name_map";
	private static final Class[] INPUT_CLASSES = new Class[] { ParameterSet.class };

	public ParameterSetter(OperatorDescription description) {
		super(description);
	}

	public IOObject[] apply() throws OperatorException {
		ParameterSet parameterSet = getInput(ParameterSet.class);

		Map<Object, Object> nameMap = new HashMap<Object, Object>();
		List nameList = getParameterList(PARAMETER_NAME_MAP);
		Iterator i = nameList.iterator();
		while (i.hasNext()) {
			Object[] keyValue = (Object[]) i.next();
			nameMap.put(keyValue[0], keyValue[1]);
		}
		parameterSet.applyAll(getProcess(), nameMap);

		return new IOObject[0];
	}

	public Class[] getInputClasses() {
		return INPUT_CLASSES;
	}

	public Class[] getOutputClasses() {
		return new Class[0];
	}

	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.add(new ParameterTypeList(PARAMETER_NAME_MAP, "A list mapping operator names from the set to operator names in the process setup.", new ParameterTypeString("operator_name", "The keys are the operator names in the parameter set, the values are names of the operators in the process setup.")));
		return types;
	}
}
