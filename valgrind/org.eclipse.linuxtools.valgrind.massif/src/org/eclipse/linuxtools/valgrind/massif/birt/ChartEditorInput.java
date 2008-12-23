package org.eclipse.linuxtools.valgrind.massif.birt;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.linuxtools.valgrind.massif.MassifPlugin;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class ChartEditorInput implements IEditorInput {
	
	protected HeapChart chart;
	protected String name;

	public ChartEditorInput(HeapChart chart, String name) {
		this.chart = chart;
		this.name = name;
	}

	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return MassifPlugin.imageDescriptorFromPlugin(MassifPlugin.PLUGIN_ID, "icons/linecharticon.gif"); //$NON-NLS-1$
	}

	public String getName() {		
		return name;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return NLS.bind(Messages.getString("ChartEditorInput.Heap_allocation_chart_for"), name); //$NON-NLS-1$
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	public HeapChart getChart() {
		return chart;
	}

}
