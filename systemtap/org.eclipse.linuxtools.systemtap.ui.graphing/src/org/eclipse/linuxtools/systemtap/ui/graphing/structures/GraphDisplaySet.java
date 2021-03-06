/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Jeff Briggs, Henry Hughes, Ryan Morse
 *******************************************************************************/

package org.eclipse.linuxtools.systemtap.ui.graphing.structures;

import java.util.ArrayList;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.linuxtools.systemtap.ui.graphing.internal.GraphingPlugin;
import org.eclipse.linuxtools.systemtap.ui.graphing.internal.Localization;
import org.eclipse.linuxtools.systemtap.ui.graphing.preferences.GraphingPreferenceConstants;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.nonui.datasets.IDataSet;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.nonui.structures.GraphData;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.chart.widget.ChartCanvas;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.charts.AbstractChartBuilder;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.datadisplay.DataGrid;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.widgets.GraphComposite;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.wizards.dataset.DataSetFactory;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.wizards.graph.GraphFactory;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.wizards.graph.SelectGraphWizard;
import org.eclipse.linuxtools.systemtap.ui.logging.LogManager;
import org.eclipse.linuxtools.systemtap.ui.structures.UpdateManager;
import org.eclipse.linuxtools.systemtap.ui.structures.listeners.ITabListener;
import org.eclipse.linuxtools.systemtap.ui.structures.listeners.IUpdateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;



/**
 * This class is used to contain all of the graphing components that can
 * be displayed as individual tabs in a single location.
 * @author Ryan Morse
 */
public class GraphDisplaySet {
	@SuppressWarnings("unchecked")
	public GraphDisplaySet(Composite parent, IDataSet data) {
		LogManager.logDebug("Start GraphSelectorView:", this);
		LogManager.logInfo("Initializing", this);
		IPreferenceStore p = GraphingPlugin.getDefault().getPreferenceStore();
		int delay = p.getInt(GraphingPreferenceConstants.P_GRAPH_UPDATE_DELAY);

		dataSet = data;
	//	if(null != cmd) {
			updater = new UpdateManager(delay);
			updater.addUpdateListener(new IUpdateListener() {
				public void handleUpdateEvent() {
		//			if(!cmd.isRunning())
			//			updater.stop();
				}
			});
	//	}
		createPartControl(parent);
		
		builders = new ArrayList();
	//	graphs = new ArrayList();
		tabListeners = new ArrayList();
		LogManager.logDebug("End GraphSelectorView:", this);
	}
	
	/**
	 * This method creates the framework for what will be displayed by this dialog box.
	 * @param parent The composite that will contain all the elements from this dialog
	 */
	public void createPartControl(Composite parent) {
		LogManager.logDebug("Start createPartControl: parent-" + parent, this);

		parent.setLayout(new FormLayout());
		FormData data1 = new FormData();
		Composite cmpCoolBar = new Composite(parent, SWT.NONE);
		data1.left = new FormAttachment(0, 0);
		data1.top = new FormAttachment(0, 0);
		data1.right = new FormAttachment(100, 0);
		data1.bottom = new FormAttachment(0, 10);
		cmpCoolBar.setLayoutData(data1);

		FormData data2 = new FormData();
		data2.left = new FormAttachment(0, 0);
		data2.top = new FormAttachment(cmpCoolBar);
		data2.right = new FormAttachment(100, 0);
		data2.bottom = new FormAttachment(100, 0);
		Composite cmpGraph = new Composite(parent, SWT.NONE);
		cmpGraph.setLayoutData(data2);

		//This is for the tab view
		cmpGraph.setLayout(new FormLayout());

		folder = new CTabFolder(cmpGraph, SWT.NONE);
		FormData data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(100, 0);
		folder.setLayoutData(data);

		listener = new ButtonClickListener();
		folder.addSelectionListener(listener);

		folder.addCTabFolder2Listener(new CTabFolder2Listener() {
			public void restore(CTabFolderEvent e) {}
			public void showList(CTabFolderEvent e) {}
			public void minimize(CTabFolderEvent e) {}
			public void maximize(CTabFolderEvent e) {}
			public void close(CTabFolderEvent e) {
				int selected = folder.indexOf((CTabItem)e.item)-2;
				if(null != updater)
					updater.removeUpdateListener((AbstractChartBuilder)builders.get(selected));
				builders.remove(selected);
				fireTabCloseEvent();
			}
		});

		//This is a tab/button for opening new graphs
		CTabItem newGraph = new CTabItem(folder, SWT.NONE);
		newGraph.setImage(GraphingPlugin.getImageDescriptor("icons/actions/new_wiz.gif").createImage());

		//Tab containing the data table
		CTabItem item = new CTabItem(folder, SWT.NONE);
		item.setText(Localization.getString("GraphDisplaySet.DataView"));
		Composite c = new Composite(folder, SWT.NONE);
		GridLayout grid = new GridLayout();
		grid.marginHeight = 0;
		grid.marginWidth = 0;
		c.setLayout(grid);

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		DataGrid table = DataSetFactory.getDataGrid(c, dataSet);
		if(null != updater)
			updater.addUpdateListener(table);
		table.getControl().setLayoutData(gd);
		item.setControl(c);
		folder.setSelection(item);
		lastSelectedTab = 1;

		LogManager.logDebug("End createPartControl", this);
	}
	
	public IDataSet getDataSet() {
		return dataSet;
	}
	
	/**
	 * Finds the graph that is open in the current tab
	 * @return The graph that is currently visible on the screen
	 */
	public ChartCanvas getActiveGraph() {
		if(0 == builders.size() || folder.getSelectionIndex() < 2)
			return null;
		return (ChartCanvas)builders.get(folder.getSelectionIndex()-2);
	}
	
	public void setFocus() {}

	/**
	 * Removes all internal references in this class.  Nothing should make any references
	 * to anyting in this class after calling the dispose method.
	 */
	public void dispose() {
		LogManager.logDebug("Start dispose:", this);
		LogManager.logInfo("Disposing", this);

		if(null != updater)
			updater.dispose();
		updater = null;

		dataSet = null;
		if(null != folder) {
			folder.removeSelectionListener(listener);
			folder.dispose();
			folder = null;
		}
		listener = null;
		
		LogManager.logDebug("End dispose:", this);
	}
	
	/**
	 * This class handles switching between tabs and creating new graphs.
	 * When the user selects the first tab a new dialog is displayed for
	 * them to slect what they want to display for the new graph.
	 */
	public class ButtonClickListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent event) {}
		@SuppressWarnings("unchecked")
		public void widgetSelected(SelectionEvent event) {
			CTabFolder folder = (CTabFolder)event.getSource();

			if(folder.getSelectionIndex() == 0) {
				folder.setSelection(lastSelectedTab);
				SelectGraphWizard wizard = new SelectGraphWizard(dataSet);
				IWorkbench workbench = PlatformUI.getWorkbench();
				wizard.init(workbench, null);
				WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
				dialog.create();
				dialog.open();

				GraphData gd = wizard.getGraphData();

				if(null != gd) {
					CTabItem item = new CTabItem(folder, SWT.CLOSE);
					item.setText(GraphFactory.getGraphName(gd.graphID));
					GraphComposite gc = new GraphComposite(folder, SWT.FILL, gd, dataSet);
					gc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
					folder.setSelection(item);
					
					AbstractChartBuilder g = gc.getCanvas();
					item.setControl(gc);
					
					if(null != g) {
						if(null != updater)
							updater.addUpdateListener(g);
						builders.add(g);
					}
				}
				wizard.dispose();
				fireTabOpenEvent();
			}
			lastSelectedTab = folder.getSelectionIndex();
			fireTabChangedEvent();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void addTabListener(ITabListener listener) {
		tabListeners.add(listener);
	}
	
	public void removeTabListener(ITabListener listener) {
		tabListeners.remove(listener);
	}
	
	private void fireTabCloseEvent() {
		for(int i=0; i<tabListeners.size(); i++)
			((ITabListener)tabListeners.get(i)).tabClosed();
	}
	
	private void fireTabOpenEvent() {
		for(int i=0; i<tabListeners.size(); i++)
			((ITabListener)tabListeners.get(i)).tabOpened();
	}
	
	private void fireTabChangedEvent() {
		for(int i=0; i<tabListeners.size(); i++)
			((ITabListener)tabListeners.get(i)).tabChanged();
	}

	private int lastSelectedTab;
	private IDataSet dataSet;
	private CTabFolder folder;
	private ButtonClickListener listener;
	private UpdateManager updater;
	@SuppressWarnings("unchecked")
	private ArrayList tabListeners;
	
//	private ArrayList graphs;
	@SuppressWarnings("unchecked")
	private ArrayList builders;
}