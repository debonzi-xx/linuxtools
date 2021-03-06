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

package org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.wizards.filter;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.nonui.filters.IDataSetFilter;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.internal.Localization;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;



public class SelectFilterWizard extends Wizard implements INewWizard {
	public SelectFilterWizard(String[] series) {
		filter = null;
		this.series = series;
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
	
	public void addPages() {
		setWindowTitle(Localization.getString("SelectFilterWizard.CreateFilter"));
		selectFilterPage = new SelectFilterWizardPage();
		addPage(selectFilterPage);
		
		filterOptionsPages = new FilterWizardPage[AvailableFilterTypes.filterIDs.length];
		for(int i=0; i<AvailableFilterTypes.filterIDs.length; i++) {
			filterOptionsPages[i] = AvailableFilterTypes.getFilterWizardPage(AvailableFilterTypes.filterIDs[i]);
			addPage(filterOptionsPages[i]);
		}
	}
	
	public boolean canFinish() {
		IWizardPage page = this.getContainer().getCurrentPage(); 
		if((null != filter) && (page instanceof FilterWizardPage))
			return true;
		return false;
	}

	public boolean performCancel() {
		return true;
	}
	
	public boolean performFinish() {
		return true;
	}

	public IDataSetFilter getFilter() {
		return filter;
	}
	
	public void dispose() {
		if(null != selectFilterPage)
			selectFilterPage.dispose();
		selectFilterPage = null;
		if(null != filterOptionsPages) {
			for(int i=0; i<filterOptionsPages.length; i++) {
				filterOptionsPages[i].dispose();
				filterOptionsPages[i] = null;
			}
		}
		filterOptionsPages = null;
		series = null;

		super.dispose();
	}
	
	private SelectFilterWizardPage selectFilterPage;
	private FilterWizardPage[] filterOptionsPages;
	public String[] series;
	
	public IDataSetFilter filter;
}