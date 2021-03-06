/*******************************************************************************
 * Copyright (c) 2009 STMicroelectronics.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marzia Maugeri <marzia.maugeri@st.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.linuxtools.dataviewers.charts.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.ActionType;
import org.eclipse.birt.chart.model.attribute.AxisType;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.IntersectionType;
import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.attribute.TickStyle;
import org.eclipse.birt.chart.model.attribute.TriggerCondition;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.GradientImpl;
import org.eclipse.birt.chart.model.attribute.impl.TooltipValueImpl;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.TextDataSet;
import org.eclipse.birt.chart.model.data.Trigger;
import org.eclipse.birt.chart.model.data.impl.ActionImpl;
import org.eclipse.birt.chart.model.data.impl.NumberDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.TextDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.TriggerImpl;
import org.eclipse.birt.chart.model.impl.ChartWithAxesImpl;
import org.eclipse.birt.chart.model.impl.ChartWithoutAxesImpl;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.BarSeries;
import org.eclipse.birt.chart.model.type.PieSeries;
import org.eclipse.birt.chart.model.type.impl.BarSeriesImpl;
import org.eclipse.birt.chart.model.type.impl.PieSeriesImpl;
import org.eclipse.linuxtools.dataviewers.abstractviewers.AbstractSTViewer;
import org.eclipse.linuxtools.dataviewers.abstractviewers.ISTDataViewersField;

/**
 * A utility class that handles the charts creation (pie chart & bar chart)
 * 
 * @author Marzia Maugeri <marzia.maugeri@st.com>
 *
 */
public class ChartFactory {
	
	/** 
	 * Produces a 3D pie chart from the input objects.
	 * 
	 * @param objects the input data
	 * @param nameField the field used to get the labels of the objects (colored parts in the pie).
	 * @param valField the field providing the values for the pie parts.
	 * @return a new 3D pie chart
	 */
	public static final Chart producePieChart(Object[] objects, ISTDataViewersField nameField, List<IChartField> valFields)
	{
		ChartWithoutAxes cwoaPie = ChartWithoutAxesImpl.create( );
		cwoaPie.setSeriesThickness( 20 );
		cwoaPie.setGridColumnCount( valFields.size());
		cwoaPie.getBlock( ).setBackground( ColorDefinitionImpl.WHITE( ) );
		
		//2D dimensional with DEPTH (birt DOESN'T SUPPORT 3D for Pie Chart)
		cwoaPie.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);
		
		// Plot
		Plot p = cwoaPie.getPlot( );
		p.getClientArea( ).setBackground( null );
		p.getClientArea( ).getOutline( ).setVisible( true );
		p.getOutline( ).setVisible( true );

		// Legend
		Legend lg = cwoaPie.getLegend( );
		lg.getText( ).getFont( ).setSize( 16 );
		lg.setBackground( null );
		lg.getOutline( ).setVisible( true );

		// Title
		cwoaPie.getTitle( ).getLabel( ).getCaption( ).setValue( nameField.getColumnHeaderText());
		cwoaPie.getTitle( ).getOutline( ).setVisible( true );
		
		//Base Data Set
		List<String> textLabels = new ArrayList<String>();
		for (Object obj : objects) {
			String label = nameField.getValue(obj);
			textLabels.add(label);
		}
		TextDataSet categoryValues = TextDataSetImpl.create(textLabels );

		// Base Series
		Series seCategory = SeriesImpl.create( );
		seCategory.setDataSet( categoryValues );
		
		SeriesDefinition sdBase = SeriesDefinitionImpl.create( );
		cwoaPie.getSeriesDefinitions( ).add( sdBase );
		sdBase.getSeriesPalette( ).shift( -1 );
		sdBase.getSeries( ).add( seCategory );
		
		SeriesDefinition sdValue = SeriesDefinitionImpl.create( );
		sdBase.getSeriesDefinitions( ).add( sdValue );


		for (IChartField field : valFields) {
			List<Double> doubleValues = new ArrayList<Double>();
			for (Object obj : objects) {
				doubleValues.add(field.getNumber(obj).doubleValue());
			}
			
			NumberDataSet Values = NumberDataSetImpl.create(doubleValues);
			
			// Pie Series
			PieSeries sePie = (PieSeries) PieSeriesImpl.create( );
			sePie.setSeriesIdentifier( field.getColumnHeaderText());
			sePie.setExplosion(3);
			sePie.setDataSet( Values );
			
			//Mouse over the Serie to Show Tooltips
			setTriggering(sePie);
			
			sdValue.getSeries( ).add( sePie);
		}
	
	
		return cwoaPie;
	}
		
	/** 
	 * Produces a 2D bar chart from the input objects.
	 * 
	 * @param objects the input data
	 * @param nameField the field used to get the labels of the objects (the labels of the series groups).
	 * @param valFields the fields providing the values for the different bars in a series group.
	 * @param horizontal if true the bars are displayed horizontally, else vertically.
	 * @return a new 2D bar chart
	 */
	
	public static final Chart produceBarChart( Object[] objects, final ISTDataViewersField nameField, List<IChartField> valFields, String title,boolean horizontal)
	{
		ChartWithAxes cwaBar = ChartWithAxesImpl.create( );

		// Plot
		cwaBar.getBlock( ).setBackground( ColorDefinitionImpl.WHITE( ) );
		Plot p = cwaBar.getPlot( );
		p.getClientArea( )
				.setBackground( GradientImpl.create( ColorDefinitionImpl.create( 225,
						225,
						255 ),
						ColorDefinitionImpl.create( 255, 255, 225 ),
						-35,
						false ) );
		p.getOutline( ).setVisible( true );

		// Title
		cwaBar.getTitle( ).getLabel( ).getCaption( ).setValue(title);
		cwaBar.getTitle( ).getOutline( ).setVisible( true );
		
		//Orientation
		if (horizontal) cwaBar.setTransposed(true);

		// Legend
		Legend lg = cwaBar.getLegend( );
		lg.getText( ).getFont( ).setSize( 16 );

		// X-Axis
		Axis xAxisPrimary = cwaBar.getPrimaryBaseAxes( )[0];
		xAxisPrimary.getTitle( ).setVisible( true );
		xAxisPrimary.setTitlePosition( Position.BELOW_LITERAL );
		xAxisPrimary.setType( AxisType.TEXT_LITERAL );
		xAxisPrimary.getMajorGrid( ).setTickStyle( TickStyle.BELOW_LITERAL );
		xAxisPrimary.getOrigin( ).setType( IntersectionType.VALUE_LITERAL );
		xAxisPrimary.setLabelPosition( Position.BELOW_LITERAL );
		xAxisPrimary.getTitle().getCaption( ).setValue(nameField.getColumnHeaderText());

		// Y-Axis
		Axis yAxisPrimary = cwaBar.getPrimaryOrthogonalAxis( xAxisPrimary );
		yAxisPrimary.getMajorGrid( ).setTickStyle( TickStyle.LEFT_LITERAL );


		double max = -1D;
		List<String> textLabels = new ArrayList<String>();
		for (Object obj : objects) {
			String label = nameField.getValue(obj);
			textLabels.add(label);
		}
		TextDataSet categoryValues = TextDataSetImpl.create(textLabels );
		SeriesDefinition sdY = SeriesDefinitionImpl.create( );
		sdY.getSeriesPalette( ).shift( -1 );

		yAxisPrimary.getSeriesDefinitions( ).add( sdY );
	
		// Data Set
		for (IChartField field : valFields) {
			List<Double> doubleValues = new ArrayList<Double>();
			for (Object obj : objects) {

				Number num = field.getNumber(obj);
				double longVal = num.doubleValue();
				max = longVal > max ? longVal : max;
				doubleValues.add(longVal);
			}
			
			NumberDataSet orthoValues = NumberDataSetImpl.create(doubleValues);
			// Y-Series
			BarSeries bs = (BarSeries) BarSeriesImpl.create( );
			bs.setSeriesIdentifier( field.getColumnHeaderText() );
			bs.setDataSet( orthoValues );
			bs.setRiserOutline( null );

			//Mouse over the Serie to Show Tooltips
			setTriggering(bs);
			
			sdY.getSeries( ).add( bs );
		}

		// X-Series
		Series seCategory = SeriesImpl.create( );
		seCategory.setDataSet( categoryValues );
		SeriesDefinition sdX = SeriesDefinitionImpl.create( );
		xAxisPrimary.getSeriesDefinitions( ).add( sdX );
		sdX.getSeries( ).add( seCategory );

		return cwaBar;
	}
	
	/**
	 * @param viewer
	 * @return the field used to provide the labels to the series
	 */
	public ISTDataViewersField getLabelField(AbstractSTViewer viewer) {
		return viewer.getAllFields()[0];
	}
	
	public static void setTriggering(Series series){
		//Mouse over the Serie to Show Tooltips
		Trigger tr = TriggerImpl.create(TriggerCondition.ONMOUSEOVER_LITERAL, 
				ActionImpl.create(ActionType.SHOW_TOOLTIP_LITERAL, TooltipValueImpl.create(200, null)));
		series.getTriggers().add(tr);
	}
}
