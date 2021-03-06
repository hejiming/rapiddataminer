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
package com.rapidminer;

/**
 * Interface managing the visualization of objects. This might be a dialog
 * showing the feature values ({@link com.rapidminer.gui.ExampleVisualizer})
 * or more sophisticated methods like displaying a text or playing a piece of
 * music. Please note that GUI components should not be constructed in the 
 * contstructor but in the method {@link #startVisualization(String)} in order
 * to ensure that the visualizer can be constructed also in environments where
 * graphical user interfaces are not allowed.
 * 
 * @author Michael Wurst, Ingo Mierswa
 * @version $Id: ObjectVisualizer.java,v 1.4 2008/05/09 19:23:19 ingomierswa Exp $
 */
public interface ObjectVisualizer {

	public void startVisualization(String objId);

	public void stopVisualization(String objId);

	public String getTitle(String objId);

	public boolean isCapableToVisualize(String id);
}
