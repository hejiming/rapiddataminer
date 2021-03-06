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
package com.rapidminer.operator;

import com.rapidminer.example.ExampleSet;

/**
 * This operator updates a {@link Model} with an {@link ExampleSet}. Please note
 * that the model must return true for {@link Model#isUpdatable()} in order to be 
 * usable with this operator.
 * 
 * @author Ingo Mierswa
 * @version $Id: ModelUpdater.java,v 1.3 2008/05/09 19:23:18 ingomierswa Exp $
 */
public class ModelUpdater extends Operator {

	public ModelUpdater(OperatorDescription description) {
		super(description);
	}

	/**
	 * Applies the operator and labels the {@link ExampleSet}. The example set
	 * in the input is not consumed.
	 */
	public IOObject[] apply() throws OperatorException {
		ExampleSet inputExampleSet = getInput(ExampleSet.class);
		Model model = getInput(Model.class);
		if (!model.isUpdatable())
			throw new UserError(this, 135, model.getClass());

		try {
			model.updateModel(inputExampleSet);
		} catch (UserError e) {
			if (e.getOperator() == null)
				e.setOperator(this);
			throw e;
		}
		return new IOObject[] { model };
	}

	/** Indicates that the consumption of Models can be user defined. */
	public InputDescription getInputDescription(Class cls) {
		if (ExampleSet.class.isAssignableFrom(cls)) {
			return new InputDescription(cls, false, true);
		} else {
			return super.getInputDescription(cls);
		}
	}

	public Class[] getInputClasses() {
		return new Class[] { Model.class, ExampleSet.class };
	}

	public Class[] getOutputClasses() {
		return new Class[] { Model.class };
	}
}
