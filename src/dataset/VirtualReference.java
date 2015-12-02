package dataset;

public class VirtualReference {
	
	private VirtualInstanceVariable _origin;
	private VirtualObject _target;
	
	public VirtualReference(VirtualInstanceVariable origin, VirtualObject target){
		_origin = origin;
		_target = target;
	}
	
	public VirtualInstanceVariable getOrigin() {
		return _origin;
	}
	
	public VirtualObject getTarget() {
		return _target;
	}

}
