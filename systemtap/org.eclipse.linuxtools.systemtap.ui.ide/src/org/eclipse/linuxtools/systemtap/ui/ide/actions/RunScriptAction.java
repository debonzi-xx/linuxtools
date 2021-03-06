/*******************************************************************************
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeff Briggs, Henry Hughes, Ryan Morse, Roland Grunberg, Anithra P J
 *******************************************************************************/

package org.eclipse.linuxtools.systemtap.ui.ide.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.linuxtools.systemtap.ui.consolelog.ClientSession;
import org.eclipse.linuxtools.systemtap.ui.consolelog.ScpClient;
import org.eclipse.linuxtools.systemtap.ui.consolelog.Subscription;
import org.eclipse.linuxtools.systemtap.ui.consolelog.dialogs.SelectServerDialog;
import org.eclipse.linuxtools.systemtap.ui.consolelog.internal.ConsoleLogPlugin;
import org.eclipse.linuxtools.systemtap.ui.consolelog.preferences.ConsoleLogPreferenceConstants;
import org.eclipse.linuxtools.systemtap.ui.consolelog.structures.ScriptConsole;
import org.eclipse.linuxtools.systemtap.ui.editor.PathEditorInput;
import org.eclipse.linuxtools.systemtap.ui.ide.IDESessionSettings;
import org.eclipse.linuxtools.systemtap.ui.ide.editors.stp.STPEditor;
import org.eclipse.linuxtools.systemtap.ui.ide.internal.IDEPlugin;
import org.eclipse.linuxtools.systemtap.ui.ide.internal.Localization;
import org.eclipse.linuxtools.systemtap.ui.ide.preferences.IDEPreferenceConstants;
import org.eclipse.linuxtools.systemtap.ui.ide.structures.StapErrorParser;
import org.eclipse.linuxtools.systemtap.ui.ide.structures.TapsetLibrary;
import org.eclipse.linuxtools.systemtap.ui.logging.LogManager;
import org.eclipse.linuxtools.systemtap.ui.structures.PasswordPrompt;
import org.eclipse.linuxtools.systemtap.ui.systemtapgui.preferences.EnvironmentVariablesPreferencePage;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.ResourceUtil;



/**
 * This <code>Action</code> is used to run a SystemTap script that is currently open in the editor.
 * @author Ryan Morse
 */

@SuppressWarnings("deprecation")
public class RunScriptAction extends Action implements IWorkbenchWindowActionDelegate {
	public RunScriptAction() {
		super();
	}

	public void dispose() {
		LogManager.logInfo("Disposing", this);
		fWindow= null;
	}

	public void init(IWorkbenchWindow window) {
		LogManager.logInfo("Initializing fWindow: "+ window, this);
		fWindow= window;
	}

	public void run(IAction action) {
		run();
	}

	/**
	 * The main body of this event. Starts by making sure the current editor is valid to run,
	 * then builds the command line arguments for stap and retrieves the environment variables.
	 * Finally, it gets an instance of <code>ScriptConsole</code> to run the script.
	 */
	public void run() {
		LogManager.logDebug("Start run:", this);
		continueRun = true;
		if(ConsoleLogPlugin.getDefault().getPluginPreferences().getBoolean(ConsoleLogPreferenceConstants.REMEMBER_SERVER)!=true)
        	
        {
			new SelectServerDialog(fWindow.getShell()).open();
		}
		if(isValid()) {
			 try{
				 
					ScpClient scpclient = new ScpClient();
					serverfileName = fileName.substring(fileName.lastIndexOf('/')+1);
					tmpfileName="/tmp/"+ serverfileName;
					 scpclient.transfer(fileName,tmpfileName);
			        }catch(Exception e){e.printStackTrace();}
					
			String[] script = buildScript();
			String[] envVars = getEnvironmentVariables();
            if(continueRun)
            {
            
    	
            	ScriptConsole console = ScriptConsole.getInstance(serverfileName);
                console.run(script, envVars, new PasswordPrompt(IDESessionSettings.password), new StapErrorParser());
            }
		}
		
		LogManager.logDebug("End run:", this);
	}
	
	/**
	 * Returns the path of the current editor in the window this action is associated with.
	 * @return The string representation of the path of the current file.
	 */
	protected String getFilePath() {
		IEditorPart ed = fWindow.getActivePage().getActiveEditor();
		if(ed.getEditorInput() instanceof PathEditorInput)
		 return ((PathEditorInput)ed.getEditorInput()).getPath().toString();
		else
	     return ResourceUtil.getFile(ed.getEditorInput()).getLocation().toString();
		
	}
	
	/**
	 * Checks if the current editor is operating on a file that actually exists and can be 
	 * used as an argument to stap (as opposed to an unsaved buffer).
	 * @return True if the file is valid.
	 */
	protected boolean isValid() {
		IEditorPart ed = fWindow.getActivePage().getActiveEditor();
        
		if(isValidFile(ed))
			if(isValidDirectory(((PathEditorInput)ed.getEditorInput()).getPath().toString()))
				return true;
		return true;
	}
	
	private boolean isValidFile(IEditorPart ed) {
		if(null == ed) {
			String msg = MessageFormat.format(Localization.getString("RunScriptAction.NoScriptFile"),(Object[]) null);
			LogManager.logInfo("Initializing", MessageDialog.class);
			MessageDialog.openWarning(fWindow.getShell(), Localization.getString("RunScriptAction.Problem"), msg);
			LogManager.logInfo("Disposing", MessageDialog.class);
			return false;
		}
		
		if(ed.isDirty())
			ed.doSave(new ProgressMonitorPart(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), new FillLayout()));
		
		return true;
	}
	
	private boolean isValidDirectory(String fileName) {
		this.fileName = fileName;
		if(0 == IDESessionSettings.tapsetLocation.trim().length())
			TapsetLibrary.getTapsetLocation(IDEPlugin.getDefault().getPreferenceStore());
		if(fileName.contains(IDESessionSettings.tapsetLocation)) {
			String msg = MessageFormat.format(Localization.getString("RunScriptAction.TapsetDirectoryRun"),(Object []) null);
			MessageDialog.openWarning(fWindow.getShell(), Localization.getString("RunScriptAction.Error"), msg);
			return false;
		}
		return true;
	}
	
	/**
	 * Called by <code>run(IAction)</code> to generate the command line necessary to run the script.
	 * @return The arguments to pass to <code>Runtime.exec</code> to start the stap process on this script.
	 * @see TerminalCommand
	 * @see Runtime#exec(java.lang.String[], java.lang.String[])
	 */
	protected String[] buildScript() {
		return buildStandardScript();
	}
	
	/**
	 * The command line argument generation method used by <code>RunScriptAction</code>. This generates
	 * a stap command line that includes the tapsets specified in user preferences, a guru mode flag
	 * if necessary, and the path to the script on disk.
	 * @return The command to invoke to start the script running in stap.
	 */
	protected String[] buildStandardScript() {
	//FixMe: Take care of this in the next release. For now only the guru mode is sent
		ArrayList<String> cmdList = new ArrayList<String>();
		String[] script;
		
		getImportedTapsets(cmdList);
		
		if(isGuru())
			cmdList.add("-g"); //$NON-NLS-1$
		

		script = finalizeScript(cmdList);
		
		return script;
	}
	
	/**
	 * Adds the tapsets that the user has added in preferences to the input <code>ArrayList</code>
	 * @param cmdList The list to add the user-specified tapset locations to.
	 */
	
	protected void getImportedTapsets(ArrayList<String> cmdList) {
		Preferences pref = IDEPlugin.getDefault().getPluginPreferences();
		String[] tapsets = pref.getString(IDEPreferenceConstants.P_TAPSETS).split(File.pathSeparator);

		//Get all imported tapsets
		if(null != tapsets && tapsets.length > 0 && tapsets[0].trim().length() > 0) {
	   		for(int i=0; i<tapsets.length; i++) {
	   			cmdList.add("-I"); //$NON-NLS-1$
	   			cmdList.add(tapsets[i]);
	   		}
		}
	}
	
	/**
	 * Checks the current script to determine if guru mode is required in order to run. This is determined 
	 * by the presence of embedded C.
	 * @return True if the script contains embedded C code.
	 */
	protected boolean isGuru() {
		try {
			File f = new File(fileName);
			FileReader fr = new FileReader(f);
			
			int curr = 0;
			int prev = 0;
			boolean front = false;
			boolean imbedded = false;
			boolean inLineComment = false;
			boolean inBlockComment = false;
			while(-1 != (curr = fr.read())) {
				if(!inLineComment && !inBlockComment && '%' == prev && '{' == curr)
					front = true;
				else if(!inLineComment && !inBlockComment && '%' == prev && '}' == curr && front) {
					imbedded = true;
					break;
				} else if(!inBlockComment && (('/' == prev && '/' == curr) || '#' == curr)) {
					inLineComment = true;
				} else if(!inLineComment && '/' == prev && '*' == curr) {
					inBlockComment = true;
				} else if('\n' == curr) {
					inLineComment = false;
				} else if('*' == prev && '/' == curr) {
					inBlockComment = false;
				}
				prev = curr;
			}
			if(imbedded)
				return true;
		} catch (FileNotFoundException fnfe) {
			LogManager.logCritical("FileNotFoundException run: " + fnfe.getMessage(), this);
		} catch (IOException ie) {
			LogManager.logCritical("IOException run: " + ie.getMessage(), this);
		}
		return false;
	}
	
	protected boolean createClientSession()
	{
		
	if (!ClientSession.isConnected())
		{
				new SelectServerDialog(fWindow.getShell()).open();
		}
		if((ConsoleLogPlugin.getDefault().getPluginPreferences().getBoolean(ConsoleLogPreferenceConstants.CANCELLED))!=true)
		{
		subscription = new Subscription(fileName,isGuru());
	/*	if (ClientSession.isConnected())		
		{
		console = ScriptConsole.getInstance(fileName, subscription);
        console.run();
		}*/
		}		
		return true;
	}
	
	/**
	 * Produces a <code>String[]</code> from the <code>ArrayList</code> passed in with stap inserted
	 * as the first entry, and the filename as the last entry. Used to convert the arguments generated
	 * earlier in <code>buildStandardScript</code> such as tapset locations and guru mode into an actual
	 * command line argument array that can be passed to <code>Runtime.exec</code>.
	 * @param cmdList The list of arguments for stap for this script
	 * @return An array suitable to pass to <code>Runtime.exec</code> to start stap on this file.
	 */
	protected String[] finalizeScript(ArrayList<String> cmdList) {
		
		String[] script;

		script = new String[cmdList.size() + 4];
		script[0] = "stap"; //$NON-NLS-1$
			
		script[script.length-1] = tmpfileName;
		
		for(int i=0; i< cmdList.size(); i++) {
			script[i+1] = cmdList.get(i).toString();
		}
		script[script.length-3]="-m";
		
		String modname = serverfileName.substring(0, serverfileName.indexOf('.'));
		if (modname.indexOf('-') != -1)
			modname = modname.substring(0, modname.indexOf('-'));
		script[script.length-2]=modname;
		
		return script;
	}
	
	protected String[] getEnvironmentVariables() {
		return EnvironmentVariablesPreferencePage.getEnvironmentVariables();
	}
	
	public void selectionChanged(IAction act, ISelection select) {
		this.act = act;
		setEnablement(false);
		buildEnablementChecks();
	}
	
	private void buildEnablementChecks() {
		if(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() instanceof STPEditor)
			setEnablement(true);
	}
	
	private void setEnablement(boolean enabled) {
		act.setEnabled(enabled);
	}
	
	protected Subscription getSubscription()
	{
		return subscription;
	}

	protected boolean continueRun = true;
	protected String fileName = null;
	protected String tmpfileName = null;
	protected String serverfileName = null;
	protected IWorkbenchWindow fWindow;
	private IAction act;
	protected Subscription subscription;
	protected int SCRIPT_ID;
	protected ScriptConsole console;
	
}