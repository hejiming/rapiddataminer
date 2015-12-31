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
package com.rapidminer.operator.learner.weka;

import javax.swing.Icon;

import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.operator.ResultObjectAdapter;

import weka.associations.Associator;

/**
 * In contrast to models generated by normal learners, the association rules
 * cannot be applied to an example set. Hence, there is no way to evaluate the
 * performance of association rules yet.
 * 
 * @author Ingo Mierswa, Simon Fischer
 * @version $Id: WekaAssociator.java,v 1.5 2008/05/09 19:23:19 ingomierswa Exp $
 */
public class WekaAssociator extends ResultObjectAdapter {

	private static final long serialVersionUID = 5270922650382578487L;

	private static final String RESULT_ICON_NAME = "lightbulb_on.png";
	
	private static Icon resultIcon = null;
	
	static {
		resultIcon = SwingTools.createIcon("16/" + RESULT_ICON_NAME);
	}
	
	private Associator associator;

	private String name;

	public WekaAssociator(String name, Associator associator) {
		this.name = name;
		this.associator = associator;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return associator.toString();
	}
    
    public String getExtension() { return "ass"; }
    
    public String getFileDescription() { return "association rules"; }
    
    public Icon getResultIcon() {
    	return resultIcon;
    }
}
