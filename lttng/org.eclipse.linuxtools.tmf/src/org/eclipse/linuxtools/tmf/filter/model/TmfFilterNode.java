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

package org.eclipse.linuxtools.tmf.filter.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.linuxtools.tmf.event.TmfEvent;


public class TmfFilterNode extends TmfFilterTreeNode {
	
	public static final String NODE_NAME = "FILTER"; //$NON-NLS-1$
	public static final String NAME_ATTR = "name"; //$NON-NLS-1$
	
	String fFilterName;
	
	public TmfFilterNode(String filterName) {
		super(null);
		fFilterName = filterName;
	}

	public TmfFilterNode(ITmfFilterTreeNode parent, String filterName) {
		super(parent);
		fFilterName = filterName;
	}

	public String getFilterName() {
		return fFilterName;
	}

	public void setFilterName(String filterName) {
		fFilterName = filterName;
	}

	@Override
	public String getNodeName() {
		return NODE_NAME;
	}

	@Override
	public boolean matches(TmfEvent event) {
		// There should be at most one child
		for (ITmfFilterTreeNode node : getChildren()) {
			if (node.matches(event)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> getValidChildren() {
		if (getChildrenCount() == 0) {
			return super.getValidChildren();
		} else {
			return new ArrayList<String>(0); // only one child allowed
		}
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		if (getChildrenCount() > 1) {
			buf.append("( "); //$NON-NLS-1$
		}
		for (int i = 0; i < getChildrenCount(); i++) {
			ITmfFilterTreeNode node = getChildren()[i];
			buf.append(node.toString());
			if (i < getChildrenCount() - 1) {
				buf.append(" and "); //$NON-NLS-1$
			}
		}
		if (getChildrenCount() > 1) {
			buf.append(" )"); //$NON-NLS-1$
		}
		return buf.toString();
	}
}
