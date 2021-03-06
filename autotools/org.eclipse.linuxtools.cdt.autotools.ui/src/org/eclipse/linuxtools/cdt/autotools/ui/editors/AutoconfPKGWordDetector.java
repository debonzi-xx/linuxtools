/*******************************************************************************
 * Copyright (c) 2008 NOKIA Inc
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ed Swartz (Nokia) - initial API and implementation
 *******************************************************************************/
package org.eclipse.linuxtools.cdt.autotools.ui.editors;

import org.eclipse.jface.text.rules.IWordDetector;

public class AutoconfPKGWordDetector implements IWordDetector {

	public boolean isWordPart(char c) {
		return ((Character.isLetter(c) && Character.isUpperCase(c)) || 
				Character.isDigit(c) ||	c == '_');
	}

	public boolean isWordStart(char c) {
		return (c == 'P');
	}

}
