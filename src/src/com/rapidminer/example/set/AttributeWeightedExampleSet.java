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
package com.rapidminer.example.set;

import java.util.Iterator;
import java.util.Random;

import com.rapidminer.datatable.DataTable;
import com.rapidminer.datatable.DataTableExampleSetAdapter;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.AttributeTransformation;
import com.rapidminer.example.AttributeWeights;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.tools.Tools;


/**
 * An implementation of ExampleSet that allows the weighting of the attributes.
 * Weights can be queried by the method {@link #getWeight(Attribute)}.
 * 
 * @author Ingo Mierswa
 * @version $Id: AttributeWeightedExampleSet.java,v 1.36 2006/03/21 15:35:39
 *          ingomierswa Exp $
 */
public class AttributeWeightedExampleSet extends AbstractExampleSet {

	private static final long serialVersionUID = -5662936146589379273L;
	
	/** The parent example set. */
	private ExampleSet parent;
    
	private AttributeWeights attributeWeights = new AttributeWeights();
	
	/**
	 * Constructs a new AttributeWeightedExampleSet. Initially all attributes
	 * are weighted with 1.0.
	 */
	public AttributeWeightedExampleSet(ExampleSet exampleSet) {
		this(exampleSet, null);
	}

	/**
	 * Constructs a new AttributeWeightedExampleSet. The attributes are weighted
	 * with the given weights. Attributes which are not stored in the map are
	 * weighted with 1.0.
	 */
	public AttributeWeightedExampleSet(ExampleSet exampleSet, AttributeWeights weights) {
		this(exampleSet, weights, 1.0d);
	}

	/**
	 * Constructs a new AttributeWeightedExampleSet. The attributes are weighted
	 * with the given weights. Attributes which are not stored in the map are
	 * weighted with the given default weight.
	 */
	public AttributeWeightedExampleSet(ExampleSet exampleSet, AttributeWeights weights, double defaultWeight) {
		this.parent = (ExampleSet)exampleSet.clone();
		this.attributeWeights = weights;
		if (weights == null) {
			this.attributeWeights = new AttributeWeights();
			for (Attribute attribute : this.parent.getAttributes()) {
				setWeight(attribute, defaultWeight);
			}
		}
		
		for (Attribute attribute : this.parent.getAttributes()) {
            attribute.addTransformation(new AttributeTransformationWeighting(this.attributeWeights));
        }
	}

	/** Clone constructor. */
	public AttributeWeightedExampleSet(AttributeWeightedExampleSet exampleSet) {
    	this.parent = (ExampleSet)exampleSet.parent.clone();
    	this.attributeWeights = (AttributeWeights)exampleSet.attributeWeights.clone();
		for (Attribute attribute : this.parent.getAttributes()) {
			AttributeTransformation transformation = attribute.getLastTransformation();
			if (transformation != null)
				if (transformation instanceof AttributeTransformationWeighting)
					((AttributeTransformationWeighting)transformation).setAttributeWeights(this.attributeWeights);
        }
	}

	public AttributeWeights getAttributeWeights() {
	    return this.attributeWeights;
	}

	/** Returns a clone where the zero weighted attributes are not delivered. */
	public AttributeWeightedExampleSet createCleanClone() {
		AttributeWeightedExampleSet clone = (AttributeWeightedExampleSet)clone();
		Iterator<Attribute> a = clone.getAttributes().iterator();
		while (a.hasNext()) {
			Attribute attribute = a.next();
			double weight = this.attributeWeights.getWeight(attribute.getName());
			if (Tools.isZero(weight)) {
				a.remove();
			}
		}
		return clone;
	}
	
	public Attributes getAttributes() {
		return this.parent.getAttributes();
	}
	
	/** Returns the weight of the attribute. */
	public double getWeight(Attribute attribute) {
        if (this.attributeWeights == null)
            return 1.0d;
        else
            return this.attributeWeights.getWeight(attribute.getName());
	}

	/**
	 * Sets the weight of the attribute.
	 */
	public void setWeight(Attribute attribute, double weightValue) {
        this.attributeWeights.setWeight(attribute.getName(), weightValue);
	}

	/** Returns the number of selected attributes. */
	public int getNumberOfUsedAttributes() {
		int counter = 0;
		for (Attribute attribute : getAttributes()) {
			if (!Tools.isEqual(getWeight(attribute), 0.0d))
				counter++;
		}
		return counter;
	}

	// -------------------- wrapper methods --------------------

	/** Sets the weights of all attributes to 1.0. */
	public void selectAll() {
		setAll(1.0d);
	}

	/** Sets the weights of all attributes to 0.0. */
	public void deselectAll() {
		setAll(0.0d);
	}

	/** Sets all flags to the given value. */
	private void setAll(double weight) {
		for (Attribute attribute : getAttributes())
			setWeight(attribute, weight);
	}

	// -------------------- selection methods --------------------

	/** Returns the selection state of the attribute. */
	public boolean isAttributeUsed(Attribute attribute) {
		return getWeight(attribute) != 0.0d;
	}

	/** Sets the selection state of the attribute. */
	public void setAttributeUsed(Attribute attribute, boolean selected) {
		setWeight(attribute, (selected ? 1.0d : 0.0d));
	}

	/**
	 * Flips the selection state of the attribute with the given index.
	 * (Convenience method for evolutionary algorithms). If a block starts with
	 * this attribute the whole block will be switched. Returns the index of the
	 * attribute which is the last one in the block or the index of the given
	 * attribute itself if it is not the start attribute of a block.
	 */
	public void flipAttributeUsed(Attribute attribute) {
		setWeight(attribute, isAttributeUsed(attribute) ? 0.0d : 1.0d);
	}

    /** Randomly selects approximately the given number of attributes. Will not
     *  throw an exception if the given number exceeds the current number of 
     *  features. If no attributes would be selected a single attribute will
     *  be selected. */
    public void selectRandomSubset(int n, Random random) {
        double ratio = (double)n / (double)getAttributes().size();
        for (Attribute attribute : getAttributes()) {
            if (random.nextDouble() < ratio) {
                setWeight(attribute, 1.0d);
            } else {
                setWeight(attribute, 0.0d);
            }
        }
        
        if (getNumberOfUsedAttributes() == 0) {
            double probDelta = 1.0d / getAttributes().size();
            double prob = random.nextDouble();
            double currentMax = probDelta;
            for (Attribute attribute : getAttributes()) {
            	if (prob < currentMax) {
            		setWeight(attribute, 1.0d);
            		break;
            	}
            	currentMax += probDelta;
            }
        }
    }

	// -------------------- overridden methods --------------------


	public boolean equals(Object o) {
		if (!super.equals(o))
			return false;
		if (!(o instanceof AttributeWeightedExampleSet))
			return false;
		return super.equals(o) && this.attributeWeights.equals(((AttributeWeightedExampleSet)o).attributeWeights);
	}

	public int hashCode() {
		return super.hashCode() ^ attributeWeights.hashCode();
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer(super.toString());
		buffer.append(Tools.getLineSeparator() + "Weights: ");
		int i = 0;
		for (Attribute attribute : getAttributes()) {
			if (i != 0)
				buffer.append(", ");
			if (i > 50) {
				buffer.append("...");
				break;
			}
			buffer.append(attribute.getName() + ":" + getWeight(attribute));
			i++;
		}
		return buffer.toString();
	}

    public DataTable createDataTable(IOContainer container) {
        return new DataTableExampleSetAdapter(this, getAttributeWeights());
    }

    /**
     * Creates a new example set reader.
     */
    public Iterator<Example> iterator() {
        return new AttributesExampleReader(parent.iterator(), this);
    }

    public Example getExample(int index) {
    	return this.parent.getExample(index);
    }
    
	public ExampleTable getExampleTable() {
		return parent.getExampleTable();
	}

	public int size() {
		return parent.size();
	}
}