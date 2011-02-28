package org.eclipse.linuxtools.lttng.jni_v2_6;

import org.eclipse.linuxtools.lttng.jni.JniTrace;
import org.eclipse.linuxtools.lttng.jni.JniTracefile;
import org.eclipse.linuxtools.lttng.jni.common.Jni_C_Pointer;
import org.eclipse.linuxtools.lttng.jni.exception.JniException;

public class JniTrace_v2_6 extends JniTrace {
	
	private static final String LIBRARY_NAME = "liblttvtraceread-2.6.so";
	
	protected JniTrace_v2_6() {
		super();
    }
    
	public JniTrace_v2_6(String newpath) throws JniException {
		super(newpath);
	}
	
    public JniTrace_v2_6(String newpath, boolean newPrintDebug) throws JniException {
    	super(newpath, newPrintDebug);
    }
    
    
    public JniTrace_v2_6(JniTrace_v2_6 oldTrace) {
    	super(oldTrace);
    }        
    
    public JniTrace_v2_6(Jni_C_Pointer newPtr, boolean newPrintDebug) throws JniException {
    	super(newPtr, newPrintDebug);
    }
    
    
    @Override
	public boolean initializeLibrary() {
    	return ltt_initializeHandle(LIBRARY_NAME);
    }
    
    @Override
	public JniTracefile allocateNewJniTracefile(Jni_C_Pointer newPtr, JniTrace newParentTrace) throws JniException {
    	return new JniTracefile_v2_6(newPtr, newParentTrace);
    }
    
    
    
}