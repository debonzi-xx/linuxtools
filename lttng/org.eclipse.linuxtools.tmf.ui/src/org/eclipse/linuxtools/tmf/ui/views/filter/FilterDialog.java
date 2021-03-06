/*******************************************************************************
 * Copyright (c) 2010 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Patrick Tasse - Initial API and implementation
 *******************************************************************************/

package org.eclipse.linuxtools.tmf.ui.views.filter;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.linuxtools.tmf.filter.model.ITmfFilterTreeNode;
import org.eclipse.linuxtools.tmf.filter.model.TmfFilterNode;
import org.eclipse.linuxtools.tmf.ui.internal.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class FilterDialog extends Dialog {

	TmfFilterNode fRoot;
	FilterViewer fViewer;
	
	public FilterDialog(Shell shell) {
		super(shell);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
        getShell().setText(Messages.FilterDialog_FilterDialogTitle);
		getShell().setMinimumSize(getShell().computeSize(500, 200));
        Composite composite = (Composite) super.createDialogArea(parent);
        
        fViewer = new FilterViewer(composite, SWT.BORDER);
        fViewer.setInput(fRoot);
        return composite;
	}

	/**
	 * @param fFilter the filter to set
	 */
	public void setFilter(ITmfFilterTreeNode filter) {
		fRoot = new TmfFilterNode(null);
		if (filter != null) {
			fRoot.addChild(filter.clone());
		}
		if (fViewer != null) {
			fViewer.setInput(fRoot);
		}
	}
	
	/**
	 * @return the fFilter
	 */
	public ITmfFilterTreeNode getFilter() {
		if (fRoot != null && fRoot.hasChildren()) {
			return fRoot.getChild(0).clone();
		}
		return null;
	}

}
