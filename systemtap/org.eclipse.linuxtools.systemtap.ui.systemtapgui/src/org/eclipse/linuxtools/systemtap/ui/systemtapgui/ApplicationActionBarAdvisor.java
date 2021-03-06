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

package org.eclipse.linuxtools.systemtap.ui.systemtapgui;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.linuxtools.systemtap.ui.logging.LogManager;
import org.eclipse.linuxtools.systemtap.ui.systemtapgui.internal.Localization;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.part.CoolItemGroupMarker;



public final class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	public ApplicationActionBarAdvisor(IActionBarConfigurer actionBarConfigurer) {
		super(actionBarConfigurer);
		LogManager.logDebug("Start/End ApplicationActionBarAdvisor: actionBarConfigurer-" + actionBarConfigurer, this);
	}

	/**
	 * Populates the passed in coolbar with the appropriate actions.
	 * 
	 * @param cbManager the ICoolBarManager object that recieves the actions.
	 */
	protected void fillCoolBar(ICoolBarManager cbManager) {
		LogManager.logDebug("Start fillCollBar: cbManager-" + cbManager, this);
		cbManager.add(new GroupMarker("group.file"));
		{
			// File Group
			IToolBarManager fileToolBar = new ToolBarManager(cbManager.getStyle());
			fileToolBar.add(new Separator(IWorkbenchActionConstants.FILE_START));
			fileToolBar.add(new GroupMarker(IWorkbenchActionConstants.NEW_GROUP));
			fileToolBar.add(new GroupMarker(IWorkbenchActionConstants.NEW_EXT));
			fileToolBar.add(new GroupMarker(IWorkbenchActionConstants.OPEN_EXT));
			fileToolBar.add(new GroupMarker(IWorkbenchActionConstants.IMPORT_EXT));
			fileToolBar.add(new GroupMarker("export.ext"));

			fileToolBar.add(new Separator(IWorkbenchActionConstants.SAVE_GROUP));
			fileToolBar.add(new GroupMarker(IWorkbenchActionConstants.SAVE_EXT));
			fileToolBar.add(new GroupMarker(IWorkbenchActionConstants.PRINT_EXT));
			fileToolBar.add(new Separator(IWorkbenchActionConstants.FILE_END));

			//Edit
			fileToolBar.add(new Separator(IWorkbenchActionConstants.EDIT_START));
			fileToolBar.add(new GroupMarker(IWorkbenchActionConstants.CUT_EXT));
			fileToolBar.add(new Separator(IWorkbenchActionConstants.EDIT_END));

			//History
			fileToolBar.add(new GroupMarker(IWorkbenchActionConstants.HISTORY_GROUP));

			//Build
			fileToolBar.add(new Separator(IWorkbenchActionConstants.BUILD_GROUP));
			fileToolBar.add(new CoolItemGroupMarker(IWorkbenchActionConstants.BUILD_EXT));
			fileToolBar.add(new CoolItemGroupMarker("stop.ext"));
			
			//Other
			fileToolBar.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			
			// Add to the cool bar manager
			cbManager.add(new ToolBarContributionItem(fileToolBar,IWorkbenchActionConstants.TOOLBAR_FILE));
		}
		
		cbManager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		
		cbManager.add(new GroupMarker(IWorkbenchActionConstants.GROUP_EDITOR));
		LogManager.logDebug("End fillCoolBar:", this);
	}
	
	/**
	 * Adds the menu groups to the passed in IMenuManager.
	 * 
	 * @param menubar the IMenuManager object to populate
	 */
	protected void fillMenuBar(IMenuManager menubar) {
		LogManager.logDebug("Start fillMenuBar: menubar-" + menubar, this);
		menubar.add(createFakeFileMenu());
		menubar.add(createFakeHelpMenu());
		menubar.add(createFileMenu());
		menubar.add(createEditMenu());
		menubar.add(createNavigateMenu());
		menubar.add(createBuildMenu());
		menubar.add(createWindowMenu());
		menubar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		menubar.add(createHelpMenu());
		LogManager.logDebug("End fillMenuBar:", this);
	}
	
	/**
	 * Creates the file menu different from the standard eclipe edition.
	 * 
	 * @return the MenuManager object created
	 */
	private MenuManager createFakeFileMenu() {
		LogManager.logDebug("Start createFakeFileMenu:", this);
		MenuManager menu = new MenuManager(Localization.getString("ApplicationActionBarAdvisor.File"), IWorkbenchActionConstants.M_FILE);
		menu.setVisible(false);

		LogManager.logDebug("End createFakeFileMenu:", this);
		return menu;
	}
	
	/**
	 * Creates the help menu different from the standard eclipe edition.
	 * 
	 * @return the MenuManager object created
	 */
	private MenuManager createFakeHelpMenu() {
		LogManager.logDebug("Start createFakeHelpMenu:", this);
		MenuManager menu = new MenuManager(Localization.getString("ApplicationActionBarAdvisor.Help"), IWorkbenchActionConstants.M_HELP);
		menu.setVisible(false);

		LogManager.logDebug("End createFakeHelpMenu:", this);
		return menu;
	}
	
	/**
	 * Creates and returns the File menu.
	 * 
	 * @return the MenuManager object created
	 */
	private MenuManager createFileMenu() {
		LogManager.logDebug("Start createFileMenu:", this);

		MenuManager menu = new MenuManager(Localization.getString("ApplicationActionBarAdvisor.File"), IWorkbenchActionConstants.M_FILE + "2");
		//menu.remove("org.eclipse.ui.edit.text.openExternalFile");
		menu.add(new Separator(IWorkbenchActionConstants.FILE_START));
		menu.add(new Separator(IWorkbenchActionConstants.NEW_EXT));
		menu.add(new Separator(IWorkbenchActionConstants.CLOSE_EXT));
		menu.add(new Separator(IWorkbenchActionConstants.SAVE_EXT));
		menu.add(new Separator(IWorkbenchActionConstants.PRINT_EXT));
		menu.add(new Separator(IWorkbenchActionConstants.IMPORT_EXT));
		menu.add(new GroupMarker("export.ext"));
		menu.add(new Separator("recentFiles.ext"));
		menu.add(new Separator(IWorkbenchActionConstants.CLOSE_EXT));
		menu.add(getAction(ActionFactory.QUIT.getId()));
		menu.add(new Separator(IWorkbenchActionConstants.FILE_END));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		LogManager.logDebug("End createFileMenu: returnVal-" + menu, this);
		return menu;
	}

	/**
	 * Creates and returns the Edit menu.
	 * 
	 * @return the MenuManager object created
	 */
	private MenuManager createEditMenu() {
		LogManager.logDebug("Start createEditMenu:", this);

		MenuManager menu = new MenuManager(Localization.getString("ApplicationActionBarAdvisor.Edit"), IWorkbenchActionConstants.M_EDIT);
		menu.add(new Separator(IWorkbenchActionConstants.EDIT_START));
		menu.add(new Separator(IWorkbenchActionConstants.UNDO_EXT));
		menu.add(new Separator(IWorkbenchActionConstants.CUT_EXT));
		menu.add(new Separator(IWorkbenchActionConstants.FIND_EXT));
		menu.add(new Separator(IWorkbenchActionConstants.ADD_EXT));
		menu.add(new Separator(IWorkbenchActionConstants.EDIT_END));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		LogManager.logDebug("End createEditMenu: returnVal-" + menu, this);
		return menu;
	}
	
	/**
	 * Creates and returns the Navigate menu.
	 * 
	 * @return the MenuManager object created
	 */
	private MenuManager createNavigateMenu() {
		LogManager.logDebug("Start createNavigateMenu:", this);

		MenuManager menu = new MenuManager(Localization.getString("ApplicationActionBarAdvisor.Navigate"), IWorkbenchActionConstants.M_NAVIGATE);
		menu.add(new Separator(IWorkbenchActionConstants.NAV_START));
		menu.add(getAction(ActionFactory.FORWARD_HISTORY.getId()));
		menu.add(getAction(ActionFactory.BACKWARD_HISTORY.getId()));
		menu.add(new Separator(IWorkbenchActionConstants.NAV_END));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		LogManager.logDebug("End createNavigateMenu: returnVal-" + menu, this);
		return menu;
	}
	
	/**
	 * Creates and returns the Build menu.
	 * 
	 * @return the MenuManager object created
	 */
	private MenuManager createBuildMenu() {
		LogManager.logDebug("Start createBuildMenu:", this);

		MenuManager menu = new MenuManager(Localization.getString("ApplicationActionBarAdvisor.Run"), IWorkbenchActionConstants.M_LAUNCH);
		menu.add(new Separator(IWorkbenchActionConstants.WB_START));
		menu.add(new GroupMarker(IWorkbenchActionConstants.BUILD_GROUP));
		menu.add(new GroupMarker(IWorkbenchActionConstants.BUILD_EXT));
		menu.add(new Separator("build.stop"));
		menu.add(new Separator(IWorkbenchActionConstants.WB_END));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		LogManager.logDebug("End createBuildMenu: returnVal-" + menu, this);
		return menu;
	}
	
	/**
	 * Creates and returns the Window menu.
	 * 
	 * @return the MenuManager object created
	 */
	private MenuManager createWindowMenu() {
		LogManager.logDebug("Start createWindowMenu:", this);

		MenuManager menu = new MenuManager(Localization.getString("ApplicationActionBarAdvisor.Window"), IWorkbenchActionConstants.M_WINDOW);
		menu.add(new Separator(IWorkbenchActionConstants.PROJ_START));
		menu.add(getAction(ActionFactory.OPEN_NEW_WINDOW.getId()));

		menu.add(new Separator("perspective.ext"));
		MenuManager menuPerspectives = new MenuManager(Localization.getString("ApplicationActionBarAdvisor.OpenPerspective"));
		menuPerspectives.add(perspectives);
		menu.add(menuPerspectives);

		menu.add(new GroupMarker(IWorkbenchActionConstants.VIEW_EXT));
		MenuManager menuViews = new MenuManager(Localization.getString("ApplicationActionBarAdvisor.ShowViews"));
		menuViews.add(views);
		menu.add(menuViews);

		menu.add(new Separator(IWorkbenchActionConstants.WINDOW_EXT));
		menu.add(new Separator(IWorkbenchActionConstants.PROJ_END));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(getAction(ActionFactory.PREFERENCES.getId()));

		LogManager.logDebug("End createWindowMenu: returnVal-" + menu, this);
		return menu;
	}

	/**
	 * Creates and returns the Help menu.
	 * 
	 * @return the MenuManager object created
	 */
	private MenuManager createHelpMenu() {
		LogManager.logDebug("Start createHelpMenu:", this);

		MenuManager menu = new MenuManager(Localization.getString("ApplicationActionBarAdvisor.Help"), IWorkbenchActionConstants.M_HELP + "2");
		menu.add(new Separator(IWorkbenchActionConstants.HELP_START));
		menu.add(getAction(ActionFactory.HELP_CONTENTS.getId()));
		menu.add(getAction(ActionFactory.HELP_SEARCH.getId()));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(new Separator(IWorkbenchActionConstants.HELP_END));
		menu.add(getAction(ActionFactory.ABOUT.getId()));

		LogManager.logDebug("End createHelpMenu: returnVal-" + menu, this);
		return menu;
	}

	/**
	 * Registers certain actions global.
	 * 
	 * @param window the IWorkbenchWindow object to assign 
	 */
	protected void makeActions(IWorkbenchWindow window) {
		LogManager.logDebug("Start makeActions: window-" + window, this);
		//registerAsGlobal(ActionFactory.SAVE.create(window));
		//registerAsGlobal(ActionFactory.SAVE_AS.create(window));
		//registerAsGlobal(ActionFactory.SAVE_ALL.create(window));
		//registerAsGlobal(ActionFactory.CLOSE.create(window));
		//registerAsGlobal(ActionFactory.CLOSE_ALL.create(window));
		//registerAsGlobal(ActionFactory.CLOSE_ALL_SAVED.create(window));
		//registerAsGlobal(ActionFactory.PRINT.create(window));
		//registerAsGlobal(ActionFactory.IMPORT.create(window));
		registerAsGlobal(ActionFactory.QUIT.create(window));
		//registerAsGlobal(ActionFactory.UNDO.create(window));
		//registerAsGlobal(ActionFactory.REDO.create(window));
		//registerAsGlobal(ActionFactory.CUT.create(window));
		//registerAsGlobal(ActionFactory.COPY.create(window));
		//registerAsGlobal(ActionFactory.PASTE.create(window));
		//registerAsGlobal(ActionFactory.SELECT_ALL.create(window));
		registerAsGlobal(ActionFactory.FIND.create(window));
		//registerAsGlobal(ActionFactory.REVERT.create(window));
		registerAsGlobal(ActionFactory.OPEN_NEW_WINDOW.create(window));
		registerAsGlobal(ActionFactory.PREFERENCES.create(window));
		//registerAsGlobal(ActionFactory.FORWARD.create(window));
		//registerAsGlobal(ActionFactory.BACK.create(window));
		registerAsGlobal(ActionFactory.FORWARD_HISTORY.create(window));
		registerAsGlobal(ActionFactory.BACKWARD_HISTORY.create(window));
		registerAsGlobal(ActionFactory.ABOUT.create(window));
		registerAsGlobal(ActionFactory.HELP_CONTENTS.create(window));
		registerAsGlobal(ActionFactory.HELP_SEARCH.create(window));

		views = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
		perspectives = ContributionItemFactory.PERSPECTIVES_SHORTLIST.create(window);

		LogManager.logDebug("End makeActions:", this);
	}
	
	/**
	 * Registers the action as global action and registers it for disposal.
	 * 
	 * @param action the action to register
	 */
	private void registerAsGlobal(IAction action) {
		LogManager.logDebug("Start registerAsGlobal: action-" + action, this);
		getActionBarConfigurer().registerGlobalAction(action);
		register(action);
		LogManager.logDebug("End registerAsGlobal:", this);
	}
	
	private IContributionItem views, perspectives;
}