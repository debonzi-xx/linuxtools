/*******************************************************************************
 * Copyright (c) 2010 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.linuxtools.changelog.tests.fixtures;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * Helper class for a IStorageEditorInput fixture.
 *
 */
public class CStringStorage implements IStorage {
	private String string;

	public CStringStorage(String input) {
		this.string = input;
	}

	public InputStream getContents() throws CoreException {
		return new ByteArrayInputStream(string.getBytes());
	}

	public IPath getFullPath() {
		return null;
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		return null;
	}

	public String getName() {
		int len = Math.min(5, string.length());
		return string.substring(0, len).concat("..."); //$NON-NLS-1$
	}

	public boolean isReadOnly() {
		return true;
	}
}
