/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Jeff Briggs, Henry Hughes, Ryan Morse, Anithra P J
 *******************************************************************************/

package org.eclipse.linuxtools.systemtap.ui.dashboardextension.dialogs;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import org.eclipse.linuxtools.systemtap.ui.graphingapi.nonui.datasets.IDataSet;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.nonui.filters.IDataSetFilter;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.nonui.structures.GraphData;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.wizards.filter.AvailableFilterTypes;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.wizards.filter.SelectFilterWizard;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.wizards.graph.GraphFactory;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.wizards.graph.SelectGraphWizard;
import org.eclipse.linuxtools.systemtap.ui.structures.TreeNode;
import org.eclipse.linuxtools.systemtap.ui.dashboardextension.internal.Localization;

/**
 * This class handles creating a dialog that the user is able to select
 * features that they want to be part of their new dashboard module.
 * Once the user is done configuring the module, it will create the new 
 * module for the dashboard from the active script in the editor.
 * @author Ryan Morse
 */
public class ExportScriptDialog extends Dialog {
	public ExportScriptDialog(Shell parentShell, IDataSet data) {
		super(parentShell);
		this.data = data;
		canceled = false;
	}

	/**
	 * This method will setup the size of the dialog window and set its title.
	 * @param shell The shell that will contain this dialog box
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Localization.getString("ExportScriptDialog.ExportScript"));
		shell.setSize(330, 375);
	}

	/**
	 * This method adds all of the components to the dialog and positions them.
	 * Actions are added to all of the buttons to deal with user interaction.
	 * @param parent The Composite that will contain all components created in this method
	 * @return The main Control created by this method.
	 */
	protected Control createDialogArea(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);

		//Dialog reference labels
		Label lblDisplay = new Label(comp, SWT.NONE);
		lblDisplay.setText(Localization.getString("ExportScriptDialog.Display"));
		lblDisplay.setBounds(10, 12, 100, 25);
		Label lblCategory = new Label(comp, SWT.NONE);
		lblCategory.setText(Localization.getString("ExportScriptDialog.Category"));
		lblCategory.setBounds(10, 42, 100, 25);
		Label lblDescription = new Label(comp, SWT.NONE);
		lblDescription.setText(Localization.getString("ExportScriptDialog.Description"));
		lblDescription.setBounds(10, 72, 100, 25);
		Label lblGraphs = new Label(comp, SWT.NONE);
		lblGraphs.setText(Localization.getString("ExportScriptDialog.Graphs"));
		lblGraphs.setBounds(10, 150, 100, 25);

		//Text boxes for how to display the new module in the dashboard
		txtDisplay = new Text(comp, SWT.BORDER);
		txtDisplay.setBounds(120, 10, 200, 25);
		txtCategory = new Text(comp, SWT.BORDER);
		txtCategory.setBounds(120, 40, 200, 25);
		txtDescription = new Text(comp, SWT.MULTI | SWT.WRAP | SWT.BORDER);
		txtDescription.setBounds(120, 70, 200, 75);
		
		treeGraphs = new Tree(comp, SWT.SINGLE | SWT.BORDER);
		treeGraphs.setBounds(10, 175, 200, 125);

		//Button to add another graph
		btnAdd = new Button(comp, SWT.PUSH);
		btnAdd.setText(Localization.getString("ExportScriptDialog.Add"));
		btnAdd.setBounds(220, 175, 100, 26);

		//Button to filter the script output data
		btnAddFilter = new Button(comp, SWT.PUSH);
		btnAddFilter.setText(Localization.getString("ExportScriptDialog.AddFilter"));
		btnAddFilter.setBounds(220, 205, 100, 26);
		btnAddFilter.setEnabled(false);
		
		//Button to remove the selected graph/filter
		btnRemove = new Button(comp, SWT.PUSH);
		btnRemove.setText(Localization.getString("ExportScriptDialog.Remove"));
		btnRemove.setBounds(220, 265, 100, 26);
		btnRemove.setEnabled(false);

		//Action to notify the buttons when to enable/disable themselves based on list selection
		treeGraphs.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				selectedTreeItem = (TreeItem)e.item;
				if(null == selectedTreeItem.getParentItem())
					btnAddFilter.setEnabled(true);
				else
					btnAddFilter.setEnabled(false);
				btnRemove.setEnabled(true);
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		//Brings up a new dialog box when user clicks the add button.  Allows selecting a new graph to display.
		btnAdd.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				SelectGraphWizard wizard = new SelectGraphWizard(data);
				IWorkbench workbench = PlatformUI.getWorkbench();
				wizard.init(workbench, null);
				WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
				dialog.create();
				dialog.open();

				GraphData gd = wizard.getGraphData();
				if(null != gd) {
					TreeItem item = new TreeItem(treeGraphs, SWT.NONE);
					item.setText(GraphFactory.getGraphName(gd.graphID) + ":" + gd.title);
					item.setData(gd);
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		//Brings up a new dialog for selecting filter options when the user clicks the filter button.
		btnAddFilter.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				SelectFilterWizard wizard = new SelectFilterWizard(data.getTitles());
				IWorkbench workbench = PlatformUI.getWorkbench();
				wizard.init(workbench, null);
				WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
				dialog.create();
				dialog.open();

				IDataSetFilter f = wizard.getFilter();
				if(null != f) {
					TreeItem item = new TreeItem(treeGraphs.getSelection()[0], SWT.NONE);
					item.setText(AvailableFilterTypes.getFilterName(f.getID()));
					item.setData(f);
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		//Removes the selected graph/filter from the tree
		btnRemove.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				selectedTreeItem.dispose();
				btnRemove.setEnabled(false);
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		return comp;
	}

	/**
	 * This method haddles what to do when the user clicks the ok or cancel button.
	 * If canceled it will just close without doing anything.  If the user clicked ok
	 * and the data entered is valid it will set up variables that can be accessed
	 * later to build the actual module.  This method should not be called explicitly.
	 * @param buttonID A reference to the button that was pressed. 0 - ID, 1- for others
	 */
	protected void buttonPressed(int buttonID) {
		if(0 == buttonID) {	//OK
			if(txtDisplay.getText().length() <= 0 || 
			   txtCategory.getText().length() <= 0 ||
			   txtDescription.getText().length() <= 0 ||
			   treeGraphs.getItemCount() <= 0) {
				String msg = MessageFormat.format(Localization.getString("ExportScriptDialog.FillFields"), (Object[])null);
				MessageDialog.openWarning(this.getShell(), Localization.getString("ExportScriptDialog.Error"), msg);
				return;
			}

			display = txtDisplay.getText();
			category = txtCategory.getText();
			description = txtDescription.getText();
			buildGraphData();
			buildFilterData();
		} else {
			canceled = true;
		}
		
		super.buttonPressed(buttonID);
	}

	/**
	 * This allows outside classes to determine if the user clicked ok or cancel.
	 * @return boolean representing whether the cancel button was pressed or not
	 */
	public boolean isCanceled() {
		return canceled;
	}
	
	/**
	 * This allows an outside class to determin what was chosen to be the Category.
	 * @return String representing the selected Category name.
	 */
	public String getCategory() {
		return category;
	}
	
	/**
	 * This allows an outside class to determin what was chosen to be the Description.
	 * @return String representing the selected Description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * This allows an outside class to determin what was chosen to be the Display name.
	 * @return String representing the selected Display name.
	 */
	public String getDisplay() {
		return display;
	}
	
	/**
	 * This allows an outside class to determin what graph types were selected.
	 * @return GraphData[] for each selected graph.
	 */
	public GraphData[] getGraphs() {
		return graphData;
	}
	
	/**
	 * This allows an outside class to determin what filter types were chosen.
	 * @return TreeNode organized as follows: Root->Graphs->Filters.
	 */
	public TreeNode getGraphFilters() {
		return filters;
	}
	
	/**
	 * This cleans up all internal references to objects.  No other method should
	 * be called after the dispose method.
	 */
	public void dispose() {
		if(null != txtDisplay)
			txtDisplay.dispose();
		if(null != txtCategory)
			txtCategory.dispose();
		if(null != txtDescription)
			txtDescription.dispose();
		if(null != treeGraphs)
			treeGraphs.dispose();
		if(null != btnAdd)
			btnAdd.dispose();
		if(null != btnRemove)
			btnRemove.dispose();
		if(null != btnAddFilter)
			btnAddFilter.dispose();
		if(null != selectedTreeItem)
			selectedTreeItem.dispose();
		txtDisplay = null;
		txtCategory = null;
		txtDescription = null;
		treeGraphs = null;
		btnAdd = null;
		btnRemove = null;
		btnAddFilter = null;
		selectedTreeItem = null;
		data = null;
	}
	
	/**
	 * This method converts what was selected in the tree into a simple array
	 * of all of the selected graphs and their data.
	 */
	private void buildGraphData() {
		TreeItem[] children = treeGraphs.getItems();
		graphData = new GraphData[children.length];
		for(int i=0; i<graphData.length; i++)
			graphData[i] = (GraphData)children[i].getData();
	}
	
	/**
	 * This mothod takes the data from the tree and builds another tree that
	 * has all the information about the graph and Filters in an easily
	 * accessable structure.
	 */
	private void buildFilterData() {
		TreeItem[] items = treeGraphs.getItems();
		TreeItem[] filterItems;

		filters = new TreeNode("", false); //$NON-NLS-1$
		TreeNode graphLevel;
		for(int i=0; i<items.length; i++) {
			filterItems = items[i].getItems();
	
			graphLevel = new TreeNode("graph", false); //$NON-NLS-1$
			filters.add(graphLevel);
			
			for(int j=0; j<filterItems.length; j++)
				graphLevel.add(new TreeNode(filterItems[j].getData(), false));
		}
	}
	
	private IDataSet data;
	private Tree treeGraphs;
	private Text txtDisplay, txtCategory, txtDescription;
	private Button btnAdd, btnRemove, btnAddFilter;
	private String display, category, description;
	private GraphData[] graphData;
	private TreeNode filters;
	private boolean canceled;
	private TreeItem selectedTreeItem;
}
