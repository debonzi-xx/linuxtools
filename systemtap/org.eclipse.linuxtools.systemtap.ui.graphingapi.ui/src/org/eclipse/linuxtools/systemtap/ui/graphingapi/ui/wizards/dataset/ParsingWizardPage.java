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

package org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.wizards.dataset;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.internal.Localization;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;



public abstract class ParsingWizardPage extends WizardPage {
	public ParsingWizardPage(String title) {
		super(title);
	}
	
	public void createControl(Composite parent) {
		wizard = (DataSetWizard)super.getWizard();
	}

	protected void createColumnSelector(Composite parent) {
		Label lblSeries = new Label(parent, SWT.NONE);
		lblSeries.setText(Localization.getString("ParsingWizardPage.NumberOfColumns"));
		lblSeries.setBounds(0, 5, 130, 25);
		
		txtSeries = new Text(parent, SWT.BORDER);
		txtSeries.setBounds(135, 0, 100, 25);
		txtSeries.setText("2"); //$NON-NLS-1$
		txtSeries.setTextLimit(2);
		txtSeries.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if(!"".equals(txtSeries.getText().trim())) {
					displayTextBoxes();
					refreshRegEx();
				}
			}
		});
		txtSeries.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if(('0' > e.character || '9' < e.character) && 31 < e.character && 127 > e.character)
					e.doit = false;
			}
			
			public void keyReleased(KeyEvent e) {}
		});


		Label lblRegExTitle = new Label(parent, SWT.NONE);
		lblRegExTitle.setText(Localization.getString("ParsingWizardPage.RegularExpression"));
		lblRegExTitle.setBounds(5, 325, 150, 20);
		
		lblRegEx = new Label(parent, SWT.NONE);
		lblRegEx.setBounds(155, 325, 300, 20);

		
		Label lblTitle = new Label(parent, SWT.NONE);
		lblTitle.setText(Localization.getString("ParsingWizardPage.Title"));
		lblTitle.setBounds(0, 45, 150, 25);
		Label lblExpr = new Label(parent, SWT.NONE);
		lblExpr.setText(Localization.getString("ParsingWizardPage.RegularExpression"));
		lblExpr.setBounds(160, 45, 150, 25);
		Label lblSpace = new Label(parent, SWT.NONE);
		lblSpace.setText(Localization.getString("ParsingWizardPage.Delimiter"));
		lblSpace.setBounds(310, 45, 150, 25);
		
		ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		sc1.setBounds(0, 70, 475, 250);
		cmpTextFields = new Composite(sc1, SWT.NONE);
		sc1.setContent(cmpTextFields);
		
		txtRegExpr = new Text[MAX_SERIES*COLUMNS];
		for(int i=0; i<txtRegExpr.length; i++) {
			txtRegExpr[i] = new Text(cmpTextFields, SWT.BORDER);
			txtRegExpr[i].setBounds(150*(i%COLUMNS), 30*(i/COLUMNS), 140, 25);

			if(2 == i%COLUMNS)
				txtRegExpr[i].setText("\\D+");
			else if(1 == i%COLUMNS)
				txtRegExpr[i].setText("\\d+");
		}
		
		readParsingExpression();

		//Do this after readingParsingExpressions so events arn't fired
		for(int i=0; i<txtRegExpr.length; i++) {
			if(0 != i%COLUMNS)
				txtRegExpr[i].addModifyListener(regExListener);
			else
				txtRegExpr[i].addModifyListener(textListener);
		}
		
		displayTextBoxes();
		refreshRegEx();
	}
	
	private void displayTextBoxes() {
		int series = Integer.parseInt(txtSeries.getText());
		if(series > MAX_SERIES) {
			txtSeries.setText("" +MAX_SERIES);
			return;
		}
		cmpTextFields.setSize(450, series * 30);
		series *= COLUMNS;

		for(int i=0; i<txtRegExpr.length; i++) {
			if(i < series)
				txtRegExpr[i].setVisible(true);
			else
				txtRegExpr[i].setVisible(false);
		}
	}
	
	private void refreshRegEx() {
		int series = Integer.parseInt(txtSeries.getText());
		series *= COLUMNS;
		StringBuilder s = new StringBuilder();
		for(int i=0; i<series; i++)
			if(0 != i%COLUMNS)
				s.append(txtRegExpr[i].getText());
		lblRegEx.setText(s.toString());
	}
	
	public boolean canFlipToNextPage() {
		return false;
	}
	
	public void dispose() {
		if(null != txtRegExpr) {
			for(int i=0; i<txtRegExpr.length; i++) {
				if(null != txtRegExpr[i]) {
					if(0 != i%COLUMNS)
						txtRegExpr[i].removeModifyListener(regExListener);
					else
						txtRegExpr[i].removeModifyListener(textListener);
					txtRegExpr[i].dispose();
				}
				txtRegExpr[i] = null;
			}
			txtRegExpr = null;
		}
		super.dispose();
	}
	
	public boolean checkComplete() {
		int series = Integer.parseInt(txtSeries.getText());
		labels = new String[series];
		regEx = new String[series*(COLUMNS-1)];
		
		for(int i=0, j=0, k=0; i<(series*COLUMNS); i++) {
			if("".equals(txtRegExpr[i].getText())) {
				regEx = null;
				wizard.parser = null;
				wizard.dataSet = null;
				return false;
			}

			if(0 == i%COLUMNS) {
				labels[j] = txtRegExpr[i].getText();
				j++;
			} else {
				regEx[k] = txtRegExpr[i].getText();
				k++;
			}
		}
		return true;
	}

	abstract boolean readParsingExpression();
	abstract void copyExisting(IMemento oldMeta, IMemento newMeta);

	protected class TextModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			checkComplete();
			wizard.getContainer().updateButtons();
		}
	}

	private class RegExModifyListener extends TextModifyListener {
		public void modifyText(ModifyEvent e) {
			super.modifyText(e);
			refreshRegEx();
		}
	}
	
	protected ModifyListener textListener = new TextModifyListener();
	protected ModifyListener regExListener = new RegExModifyListener();

	protected DataSetWizard wizard;
	public static final int COLUMNS = 3;
	private static final int MAX_SERIES = 24;

	protected Text txtSeries;
	protected Text[] txtRegExpr;
	protected Label lblRegEx;
	private Composite cmpTextFields;
	
	protected String[] labels;
	protected String[] regEx;
}
