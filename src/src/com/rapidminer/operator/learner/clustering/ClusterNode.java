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
package com.rapidminer.operator.learner.clustering;

import java.util.Iterator;

/**
 * Represents a node in a cluster tree.
 * 
 * @author Michael Wurst, Ingo Mierswa
 * @version $Id: ClusterNode.java,v 1.3 2008/05/09 19:23:12 ingomierswa Exp $
 */
public interface ClusterNode extends Cluster {

	/**
	 * Get all children of this node.
	 * 
	 * @return Iterator of ClusterNode
	 */
	public Iterator<ClusterNode> getSubNodes();

	/**
	 * Get the number of subnodes.
	 * 
	 * @return the number of subnodes.
	 */
	public int getNumberOfSubNodes();

	/**
	 * Get the subnode at position i.
	 * 
	 * @param i
	 *            the index
	 * @return a ClusterNode
	 */
	public ClusterNode getSubNodeAt(int i);

	/**
	 * Get the weight of this node.
	 * 
	 * @return double
	 */
	public double getWeight();

	/**
	 * Get all objects (as representend by their IDs) in the subtree including the objects located directly at this node. Note that objects can occur
	 * more than once.
	 * 
	 * @return Iterator of String
	 */
	public Iterator<String> getObjectsInSubtree();

	/**
	 * Returns the number of objects in the subtree including the objects at the current node. Note that objects can occur more than once and are
	 * counted as such. To get the number of distinct objects use the getObjectsInSubtree function first and use the resulting list to calculate the
	 * number of distinct objects.
	 * 
	 * @return number of objects
	 */
	public int getNumberOfObjectsInSubtree();

	/**
	 * Returns true, if the current node or any of the nodes in the subtree contain the object referenced by the given id.
	 * 
	 * @param objId
	 *            the if of the object
	 * @return a boolean
	 */
	public boolean containsInSubtree(String objId);
}
