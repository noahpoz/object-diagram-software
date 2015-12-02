package dataset;

public class VirtualInstanceVariable {

	private String _type;
	private String _name;
	
	private VirtualObject _target;
	private VirtualObject _origin;
	
	public VirtualInstanceVariable(String name, String type, VirtualObject origin) {
		_name = name;
		_type = type;
		_target = null;
		_origin = origin;
	}
	
	public VirtualInstanceVariable(String name, String type) {
		_name = name;
		_type = type;
		_target = null;
	}
	
	public void setTarget(VirtualObject target) {
		_target = target;
	}
	
	public String getType() {
		return _type;
	}
	
	public VirtualObject getTarget() {
		return _target;
	}
	
	public String getName() {
		return _name;
	}
	
	@Override
	public String toString() {
		return _name;
	}
	
	public VirtualObject getOrigin(){
		return _origin;
	}
}

