/*******************************************************************************
 * Copyright (c) 2000, 2006 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *******************************************************************************/
/*
 */
package org.eclipse.linuxtools.internal.cdt.autotools.ui.editors.automake;

import org.eclipse.ui.texteditor.IDocumentProvider;

/**
 */
public interface IMakefileDocumentProvider extends IDocumentProvider {

	/**
	 * Shuts down this provider.
	 */
	void shutdown();

	/**
	 * Returns the working copy for the given element.
	 *
	 * @param element the element
	 * @return the working copy for the given element
	 */
	IMakefile getWorkingCopy(Object element);

}
