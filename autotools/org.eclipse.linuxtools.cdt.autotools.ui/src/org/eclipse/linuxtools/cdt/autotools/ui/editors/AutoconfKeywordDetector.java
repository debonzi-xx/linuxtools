/*******************************************************************************
 * Copyright (c) 2006 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Incorporated - initial API and implementation
 *******************************************************************************/
package org.eclipse.linuxtools.cdt.autotools.ui.editors;

import org.eclipse.jface.text.rules.IWordDetector;

public class AutoconfKeywordDetector implements IWordDetector {

	/**
	 * @see IWordDetector#isWordPart(character)
	 */
	public boolean isWordPart(char character) {
		return (Character.isLetter(character) && Character.isLowerCase(character));
	}

	/**
	 * @see IWordDetector#isWordStart(char)
	 */
	public boolean isWordStart(char character) {
		return (Character.isLetter(character) && Character.isLowerCase(character));
	}

}

