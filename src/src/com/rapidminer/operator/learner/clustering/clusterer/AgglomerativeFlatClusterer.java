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
package com.rapidminer.operator.learner.clustering.clusterer;

import java.util.List;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.learner.clustering.ClusterModel;
import com.rapidminer.operator.learner.clustering.FlatCrispClusterModel;
import com.rapidminer.operator.learner.clustering.hierarchical.AgglomerativeClusterer;
import com.rapidminer.operator.learner.clustering.hierarchical.clustersimilarity.ClusterSimilarity;
import com.rapidminer.operator.similarity.DistanceSimilarityConverter;
import com.rapidminer.operator.similarity.SimilarityMeasure;
import com.rapidminer.operator.similarity.SimilarityUtil;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeInt;

/**
 * This operator performs generic agglomorative clustering based on a set of ids and a similarity measure. Clusters are merged as long as their number
 * is lower than a given maximum number of clusters. The algorithm implemented here is currently very simple and not very efficient (cubic).
 * 
 * @author Michael Wurst, Ingo Mierswa
 * @version $Id: AgglomerativeFlatClusterer.java,v 1.6 2008/05/09 19:22:49 ingomierswa Exp $
 */
public class AgglomerativeFlatClusterer extends AbstractFlatClusterer {

	/** The parameter name for &quot;the maximal number of clusters&quot; */
	public static final String PARAMETER_K = "k";
	
	private AgglomerativeClusterer clusterer = new AgglomerativeClusterer();

	public AgglomerativeFlatClusterer(OperatorDescription description) {
		super(description);
	}

	public ClusterModel createClusterModel(ExampleSet es) throws OperatorException {
		SimilarityMeasure sim = SimilarityUtil.resolveSimilarityMeasure(getParameters(), getInput(), es);
		if (sim.isDistance())
			sim = new DistanceSimilarityConverter(sim);
		
		ClusterSimilarity csim = AgglomerativeClusterer.resolveClusterSimilarity(getParameters());
		FlatCrispClusterModel result = clusterer.clusterFlat(es, sim, csim, getParameterAsInt(PARAMETER_K));
		return result;
	}

	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.add(SimilarityUtil.generateSimilarityParameter());
		types.add(AgglomerativeClusterer.createClusterSimilarityParameter());
		ParameterType type = new ParameterTypeInt(PARAMETER_K, "the maximal number of clusters", 2, (int) Double.POSITIVE_INFINITY, 2);
		type.setExpert(false);
		types.add(type);
		return types;
	}
}
