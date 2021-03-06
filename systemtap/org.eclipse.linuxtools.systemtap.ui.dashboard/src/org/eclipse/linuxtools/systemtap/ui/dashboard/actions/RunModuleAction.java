/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Jeff Briggs, Henry Hughes, Ryan Morse, Anithra P J
 *******************************************************************************/

package org.eclipse.linuxtools.systemtap.ui.dashboard.actions;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import org.eclipse.linuxtools.systemtap.ui.consolelog.ClientSession;
import org.eclipse.linuxtools.systemtap.ui.consolelog.ScpClient;
import org.eclipse.linuxtools.systemtap.ui.consolelog.Subscription;
import org.eclipse.linuxtools.systemtap.ui.consolelog.dialogs.SelectServerDialog;
import org.eclipse.linuxtools.systemtap.ui.consolelog.internal.ConsoleLogPlugin;
import org.eclipse.linuxtools.systemtap.ui.consolelog.preferences.ConsoleLogPreferenceConstants;
import org.eclipse.linuxtools.systemtap.ui.consolelog.structures.ScriptConsole;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.nonui.datasets.IDataSet;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.wizards.dataset.DataSetFactory;
import org.eclipse.linuxtools.systemtap.ui.structures.TreeNode;
import org.eclipse.linuxtools.systemtap.ui.structures.listeners.IActionListener;
import org.eclipse.linuxtools.systemtap.ui.dashboard.actions.hidden.GetSelectedModule;
import org.eclipse.linuxtools.systemtap.ui.dashboard.structures.ActiveModuleData;
import org.eclipse.linuxtools.systemtap.ui.dashboard.structures.DashboardModule;
import org.eclipse.linuxtools.systemtap.ui.dashboard.views.ActiveModuleBrowserView;
import org.eclipse.linuxtools.systemtap.ui.dashboard.views.DashboardModuleBrowserView;
import org.eclipse.linuxtools.systemtap.ui.graphicalrun.structures.ChartStreamDaemon2;


/**
 * This action is used to run the selected dashboard module.  
 * The command is setup and started.  Any graphs associated with the module
 * are also added to the main window the the user to monitor.
 * @author Ryan Morse
 */

@SuppressWarnings("deprecation")
public class RunModuleAction extends Action implements IViewActionDelegate, IWorkbenchWindowActionDelegate {
//	public RunModuleAction(){
	//	buildEnablementChecks();
	//}
	
	public void init(IViewPart view) {
		this.view = view;
	}
	
	public void init(IWorkbenchWindow window) {
		fWindow= window;
	}
	
	public void run(IAction act) {
		run();
	}
	
	/**
	 * This is the main method of the class. It handles running of the module.  
	 * The command is setup and started.  Any graphs associated with the module
	 * are also added to the main window the the user to monitor.
	 */
	public void run() {
		//Get the treeViewer
//		BusyIndicator.showWhile(Display.getCurrent(), this.);
		 Display disp = PlatformUI.getWorkbench().getDisplay();
		  Cursor cursor = new Cursor(disp, SWT.CURSOR_WAIT);
		  PlatformUI.getWorkbench().getDisplay().getActiveShell().setCursor(cursor);

		DashboardModule module = null;
		TreeNode node = GetSelectedModule.getNode(view);
		if (node.getChildCount() == 0)
		{
			module = (DashboardModule)node.getData(); 
		if(null != module) {
			runScript(module);
				setEnablement(false);
				buildEnablementChecks();
			fireActionEvent();
			
		} }
		else { 
			for(int j=0; j<node.getChildCount(); j++) {
			module = (DashboardModule)node.getChildAt(j).getData();
			runScript(module);
			setEnablement(false);
			buildEnablementChecks();
			fireActionEvent();
			}
			}
		  PlatformUI.getWorkbench().getDisplay().getActiveShell().setCursor(null);
		  cursor.dispose();

	}
	
	/*public void run() {
		//Get the treeViewer
		IViewPart ivp;
		//DashboardModule module = GetSelectedModule.getModule(view);
		TreeNode node = GetSelectedModule.getModule(view);
		if(null != node) {	
			ivp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ActiveModuleBrowserView.ID);
			ActiveModuleBrowserView ambv = (ActiveModuleBrowserView)ivp;
			boolean paused = ambv.isPaused(module);
					
			//TODO: Check for prebuilt module first
			String[] cmd = new String[] {
					"stap",
					"-g",
					module.script.getAbsolutePath()};
			
			IDataSet ds;
			if(paused)
				ds = ambv.pause(module).data;
			else
				ds = DataSetFactory.createDataSet(module.dataSetID, module.labels);
			//LoggedCommand command = new LoggedCommand(cmd, getEnvironmentVariables(), new PasswordPrompt(DashboardSessionSettings.password), 100);
			//command.addInputStreamListener(new ChartStreamDaemon(ds, module.parser));
			//command.start();
			  fileName = module.script.getAbsolutePath();
            
			createClientSession();
			//if(paused)
				//ambv.pause(module).paused = false;
			//else {
				ActiveModuleData amd = new ActiveModuleData();
				amd.module = module;
				amd.cmd = console;
				amd.data = ds;
				amd.paused = false;
				addActive(module.category + "." + module.display, amd);
			//}
				setEnablement(false);
				buildEnablementChecks();
			fireActionEvent();
			
		} else { buildEnablementChecks(); }
	}*/
	
	/**
	 * Adds the newly run module to the ActiveModuleBrowserView.
	 * @param display The name used to display this module
	 * @param amd The data used in the running module.
	 */
	private void addActive(String display, ActiveModuleData amd) {
		IViewPart ivp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ActiveModuleBrowserView.ID);
		((ActiveModuleBrowserView)ivp).add(display, amd);
	}
	
	/*private String[] getEnvironmentVariables() {
		return EnvironmentVariablesPreferencePage.getEnvironmentVariables();
	}*/
	
	private void runScript(DashboardModule module)
	{
		IViewPart ivp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ActiveModuleBrowserView.ID);
		ActiveModuleBrowserView ambv = (ActiveModuleBrowserView)ivp;
		boolean paused = ambv.isPaused(module);
		fileName = module.script.getAbsolutePath();
		String tmpfileName = fileName;
		String serverfileName = fileName.substring(fileName.lastIndexOf('/')+1);
		if(ConsoleLogPlugin.getDefault().getPluginPreferences().getBoolean(ConsoleLogPreferenceConstants.REMEMBER_SERVER)!=true)
        	
        {
			new SelectServerDialog(fWindow.getShell()).open();
		}
	  if (module.location.equalsIgnoreCase("local")) {
		 try{
				ScpClient scpclient = new ScpClient();
				tmpfileName=null;
				tmpfileName="/tmp/"+ serverfileName;
				 scpclient.transfer(fileName,tmpfileName);
		    }catch(Exception e){e.printStackTrace();}
	  }
		String modname = serverfileName.substring(0, serverfileName.indexOf('.'));
		if (modname.indexOf('-') != -1)
			modname = modname.substring(0, modname.indexOf('-'));
	
		//TODO: Check for prebuilt module first
		String[] cmd = new String[] {
				"stap",
				"-g",
				"-m",
				modname,
				tmpfileName};
		
			   	ScriptConsole console = ScriptConsole.getInstance(serverfileName);
            console.run(cmd, null, null, null);
		IDataSet ds;
		if(paused)
			ds = ambv.pause(module).data;
		else
			ds = DataSetFactory.createDataSet(module.dataSetID, module.labels);
		  
		console.getCommand().addInputStreamListener(new ChartStreamDaemon2(console, ds, module.parser));
		  
		 
		  
	//	createClientSession();
	//	subscription.addInputStreamListener(new ChartStreamDaemon2(console, ds, module.parser));

		//if(paused)
			//ambv.pause(module).paused = false;
		//else {
			ActiveModuleData amd = new ActiveModuleData();
			amd.module = module;
			amd.cmd = console;
			amd.data = ds;
			amd.paused = false;
			addActive(module.category + "." + module.display, amd);
		//}
	
	}
	
	/**
	 * This updates the enablement of the action based on the newly selected item
	 * @param act The action that called this method
	 * @param select The newly selected item.
	 */
	public void selectionChanged(IAction act, ISelection select) {
		this.act = act;
		setEnablement(false);
		buildEnablementChecks();
	}
	
	/**
	 * This method handles creating the checks that are used to determine if
	 * the action should be enabled for use.
	 */
	private void buildEnablementChecks() {
		IViewPart ivp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(DashboardModuleBrowserView.ID);
		if(null != ivp) {
			final DashboardModuleBrowserView dmbv = (DashboardModuleBrowserView)ivp;
			dmbv.getViewer().addSelectionChangedListener(moduleListener);
			
			ivp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ActiveModuleBrowserView.ID);
			final ActiveModuleBrowserView ambv = (ActiveModuleBrowserView)ivp;
			ambv.getViewer().addSelectionChangedListener(activeModuleListener);
			
			StopModuleAction.addActionListener(stopListener);
		PauseModuleAction.addActionListener(pauseListener);
		}
	}
	
	/**
	 * Toggles whether or not the action is enabled
	 * @param enabled boolean flag representing whether the action is enabled or not
	 */
	private void setEnablement(boolean enabled) {
		act.setEnabled(enabled);
	}
	
	/**
	 * Removes all internal references provided by this action.  Nothing should be
	 * called or referenced after the dispose method.
	 */
	public void dispose() {
		try {
			IViewPart ivp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(DashboardModuleBrowserView.ID);
			final DashboardModuleBrowserView dmbv = (DashboardModuleBrowserView)ivp;
			dmbv.getViewer().removeSelectionChangedListener(moduleListener);
			
			ivp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ActiveModuleBrowserView.ID);
			final ActiveModuleBrowserView ambv = (ActiveModuleBrowserView)ivp;
			ambv.getViewer().removeSelectionChangedListener(activeModuleListener);
			
			StopModuleAction.removeActionListener(stopListener);
			PauseModuleAction.removeActionListener(pauseListener);
		} catch(Exception e) {
			
		}
		view = null;
		act = null;
		fWindow = null;
	}
	
	/**
	 * Adds a new listener to the button to inform others when the run button
	 * is pressed.
	 * @param listener The class interested in knowing when scripts are run
	 */
	@SuppressWarnings("unchecked")
	public static void addActionListener(IActionListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes the listener from getting run events.
	 * @param listener The class that no longer should receive run notices
	 */
	public static void removeActionListener(IActionListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * This method fires the event when a module is run to every listener
	 * that is registered.
	 */
	private static void fireActionEvent() {
		for(int i=0; i<listeners.size(); i++)
			((IActionListener)listeners.get(i)).handleActionEvent();
	}
	
	/**
	 * Enables this action everytime a module is stopped.
	 */
	private final IActionListener stopListener = new IActionListener() {
		public void handleActionEvent() {
			setEnablement(true);
		}
	};
	
	/**
	 * Enables this action everytime a module is paused.
	 */
	private final IActionListener pauseListener = new IActionListener() {
		public void handleActionEvent() {
			setEnablement(true);
		}
	};
	
	/**
	 * This method checks to see if the newly selected item in the 
	 * ActiveModuleBrowserView is paused or not.  It will then set the
	 * enablement based on whether or not it is already paused.
	 */
	private final ISelectionChangedListener activeModuleListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent e) {
			try {
				TreeNode node = (TreeNode)((StructuredSelection)(e.getSelection())).getFirstElement();
				if(((ActiveModuleData)node.getData()).paused)
					setEnablement(true);
				else
					setEnablement(false);
			} catch(Exception ex) {}
		}
	};
	
	/**
	 * This method checks to see if the newly selected item in the 
	 * DashboardModuleBrowserView is running or not.  It will then set the
	 * enablement based on whether or not it is running.
	 */
	private final ISelectionChangedListener moduleListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent e) {
			try {
				TreeNode node = (TreeNode)((StructuredSelection)(e.getSelection())).getFirstElement();
				IViewPart ivp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ActiveModuleBrowserView.ID);
				ActiveModuleBrowserView amdv = (ActiveModuleBrowserView)ivp;
				DashboardModule module = (node.getData() instanceof DashboardModule) ? (DashboardModule)node.getData() : null;
				int childcount = node.getChildCount();
				if(0 == childcount && !amdv.isActive(module))
					setEnablement(true);
				else if(amdv.isActive(module) && amdv.isPaused(module))
					setEnablement(true);
				else if(childcount > 0)
				{   	
					boolean active = false;
					
					
						for(int j=0; j<childcount; j++) {
							if(amdv.isActive((DashboardModule)node.getChildAt(j).getData())) {
								active = true;
								break; }
						}
						if (active == false ) setEnablement(true);
					
				}
				else
				 setEnablement(false);
			} catch(Exception ex) {}
		}
	};
	
	protected boolean createClientSession()
	{
		
		if (!ClientSession.isConnected())
		{
				new SelectServerDialog(fWindow.getShell()).open();
		}
		if((ConsoleLogPlugin.getDefault().getPluginPreferences().getBoolean(ConsoleLogPreferenceConstants.CANCELLED))!=true)
		{
		subscription = new Subscription(fileName,false);
		if (ClientSession.isConnected())		
		{
	//	console = ScriptConsole.getInstance(fileName, subscription);
     //   console.run();
		}
		}		
		return true;
	}
	
	private IViewPart view;
	@SuppressWarnings("unchecked")
	private static ArrayList listeners = new ArrayList();
	private String fileName = null;
	protected IWorkbenchWindow fWindow = null;
	private IAction act;
	protected Subscription subscription;
	protected int SCRIPT_ID;
	protected ScriptConsole console;
}
