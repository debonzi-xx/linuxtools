/*******************************************************************************
 * Copyright (c) 2008 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kent Sebastian <ksebasti@redhat.com> - initial API and implementation 
 *******************************************************************************/ 
package org.eclipse.linuxtools.oprofile.ui.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.eclipse.linuxtools.oprofile.core.model.OpModelImage;
import org.eclipse.linuxtools.oprofile.ui.OprofileUiMessages;
import org.eclipse.linuxtools.oprofile.ui.OprofileUiPlugin;
import org.eclipse.swt.graphics.Image;

public class UiModelDependent implements IUiModelElement {
	private IUiModelElement _parent;
	private OpModelImage _dataModelDependents[];
	private UiModelImage _dependents[];
	private int _totalCount;
	private int _depCount;
	
	public UiModelDependent(IUiModelElement parent, OpModelImage dependents[], int totalCount, int depCount) {
		_parent = parent;
		_dataModelDependents = dependents;
		_dependents = null;
		_totalCount = totalCount;
		_depCount = depCount;
		refreshModel();
	}

	private void refreshModel() {
		_dependents = new UiModelImage[_dataModelDependents.length];
		
		for (int i = 0; i < _dataModelDependents.length; i++) {
			_dependents[i] = new UiModelImage(this, _dataModelDependents[i], _totalCount, 0);
		}
	}
	
	@Override
	public String toString() {
		NumberFormat nf = NumberFormat.getPercentInstance();
		if (nf instanceof DecimalFormat) {
			nf.setMinimumFractionDigits(2);
			nf.setMaximumFractionDigits(2);
		}
		double countPercentage = (double)_depCount / (double)_totalCount;

		String percentage;
		if (countPercentage < OprofileUiPlugin.MINIMUM_SAMPLE_PERCENTAGE) {
			percentage = "<" + nf.format(OprofileUiPlugin.MINIMUM_SAMPLE_PERCENTAGE); //$NON-NLS-1$
		} else {
			percentage = nf.format(countPercentage);
		}

		return percentage + " " + OprofileUiMessages.getString("uimodel.percentage.in") + OprofileUiMessages.getString("uimodel.dependent.dependent.images"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
	}

	/** IUiModelElement functions **/
	public String getLabelText() {
		return toString();
	}

	public IUiModelElement[] getChildren() {
		return _dependents;
	}

	public boolean hasChildren() {
		return true;	//must have children, or this object wouldn't be created
	}

	public IUiModelElement getParent() {
		return _parent;
	}

	public Image getLabelImage() {
		return OprofileUiPlugin.getImageDescriptor(OprofileUiPlugin.DEPENDENT_ICON).createImage();
	}
}