package dataset;

import java.util.ArrayList;

public class VirtualClass {
	
	private String _className;
	private ArrayList<VirtualInstanceVariable> _instanceVars;
	
	public VirtualClass(String name) {
		_className = name;
		_instanceVars = new ArrayList<VirtualInstanceVariable>();
	}
	
	public void addInstanceVariable(VirtualInstanceVariable var) {
		_instanceVars.add(var);
	}
	
	public String getName() {
		return _className;
	}
	
	public ArrayList<VirtualInstanceVariable> getInstanceVars() {
		return _instanceVars;
	}
	
	@Override
	public String toString() {
		return _className;
	}

}
