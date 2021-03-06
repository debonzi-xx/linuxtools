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
package org.eclipse.linuxtools.dataviewers.actions;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.linuxtools.dataviewers.abstractviewers.AbstractSTTreeViewer;
import org.eclipse.linuxtools.dataviewers.abstractviewers.STDataViewersImages;
import org.eclipse.linuxtools.dataviewers.abstractviewers.STDataViewersMessages;


/**
 * This action collapse the selected items of the tree
 *
 */
public class STCollapseSelectionAction extends Action {
	
	private final AbstractSTTreeViewer stViewer;
	
	/**
	 * Constructor
	 * @param stViewer the stViewer to collapse
	 */
	public STCollapseSelectionAction(AbstractSTTreeViewer stViewer) {
		super(STDataViewersMessages.collapseSelectionAction_title,
				STDataViewersImages.getImageDescriptor(STDataViewersImages.IMG_COLLAPSEALL));
		this.stViewer = stViewer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		TreeSelection selection = (TreeSelection)stViewer.getViewer().getSelection();
		if (selection != null && selection != TreeSelection.EMPTY) {
			for (Iterator<?> itSel = selection.iterator(); itSel.hasNext();) {
				stViewer.getViewer().collapseToLevel(itSel.next(), TreeViewer.ALL_LEVELS);
			}
		}
	}
}
