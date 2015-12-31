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
package com.rapidminer.gui.plotter.charts;

import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.PieDataset;

/**
 * A simple 3D pie chart plotter.
 * 
 * @author Ingo Mierswa
 * @version $Id: PieChart3DPlotter.java,v 1.4 2008/05/09 19:22:58 ingomierswa Exp $
 */
public class PieChart3DPlotter extends AbstractPieChartPlotter {

	private static final long serialVersionUID = -2107283003284552898L;

	public JFreeChart createChart(PieDataset pieDataSet, boolean createLegend) {
		JFreeChart chart = ChartFactory.createPieChart3D(
				null,
                pieDataSet,
                createLegend, // legend
                true,
                false);
        
		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		plot.setSectionOutlinesVisible(false);
		plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 11));
		plot.setNoDataMessage("No data available");
		plot.setCircular(false);
		plot.setLabelGap(0.02);
		plot.setForegroundAlpha(0.5f);
		plot.setOutlinePaint(Color.WHITE);
		
		return chart;
	}
}