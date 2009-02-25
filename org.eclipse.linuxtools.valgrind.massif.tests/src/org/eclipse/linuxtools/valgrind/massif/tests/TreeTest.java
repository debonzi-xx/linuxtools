/*******************************************************************************
 * Copyright (c) 2008, 2009 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Elliott Baron <ebaron@redhat.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.linuxtools.valgrind.massif.tests;

import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.linuxtools.valgrind.massif.MassifHeapTreeNode;
import org.eclipse.linuxtools.valgrind.massif.MassifLaunchConstants;
import org.eclipse.linuxtools.valgrind.massif.MassifSnapshot;
import org.eclipse.linuxtools.valgrind.massif.MassifViewPart;
import org.eclipse.linuxtools.valgrind.ui.ValgrindUIPlugin;

public class TreeTest extends AbstractMassifTest {
	@Override
	protected void setUp() throws Exception {
		proj = createProject("alloctest"); //$NON-NLS-1$
	}
	
	@Override
	protected void tearDown() throws Exception {
		deleteProject(proj);
	}
	
	public void testTreeNodes() throws Exception {
		IBinary bin = proj.getBinaryContainer().getBinaries()[0];
		ILaunchConfiguration config = createConfiguration(bin);
		ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
		wc.setAttribute(MassifLaunchConstants.ATTR_MASSIF_DETAILEDFREQ, 2);
		wc.doSave();
		doLaunch(config, "testTreeNodes"); //$NON-NLS-1$
				
		MassifViewPart view = (MassifViewPart) ValgrindUIPlugin.getDefault().getView().getDynamicView();
		TreeViewer treeViewer = view.getTreeViewer();
		
		MassifSnapshot[] snapshots = view.getSnapshots();
		MassifHeapTreeNode[] nodes = (MassifHeapTreeNode[]) treeViewer.getInput();
		for (int i = 0; i < nodes.length; i++) {
			// every odd snapshot should be detailed with --detailed-freq=2
			// and thus in the tree
			assertEquals(snapshots[2 * i + 1].getRoot(), nodes[i]);
		}
	}
	
	public void testNoDetailed() throws Exception {
		IBinary bin = proj.getBinaryContainer().getBinaries()[0];
		ILaunchConfiguration config = createConfiguration(bin);
		ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
		wc.setAttribute(MassifLaunchConstants.ATTR_MASSIF_DETAILEDFREQ, 12); // > #snapshots
		wc.doSave();
		doLaunch(config, "testNoDetailed"); //$NON-NLS-1$
				
		MassifViewPart view = (MassifViewPart) ValgrindUIPlugin.getDefault().getView().getDynamicView();
		TreeViewer treeViewer = view.getTreeViewer();
		
		MassifHeapTreeNode[] nodes = (MassifHeapTreeNode[]) treeViewer.getInput();
		
		assertNotNull(nodes);
		assertEquals(1, nodes.length); // should always contain peak
	}
	
}
