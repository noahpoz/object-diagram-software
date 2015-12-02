package dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MasterSet {

	public static String[] classNames = {"Alpha", "Beta", "Gamma", "Delta", 
		"Epsilson", "Zeta,", "Eta", "Theta", 
		"Iota", "Kappa", "Lamba"};

	private ArrayList<VirtualClass> _classes;
	private ArrayList<VirtualObject> _instances;

	int _instanceVariableCount;
	int _currentObjectID;

	public MasterSet() {
		_instanceVariableCount = 0;
		_currentObjectID = 0;
	}

	public void randomize(int numClasses, int numObjects, int maxInstanceVariables) {

		if (numObjects > 52) {
			System.err.println("Error: you have exceeded the 52 object limit.");
			System.exit(0);
		}

		_classes = generateClasses(numClasses, maxInstanceVariables);
		_instances = generateObjects(numClasses, numObjects);
		
		//We want some instance variables to contain a null reference
		_instances.add(null);
		assignReferences();
	}

	private ArrayList<VirtualClass> generateClasses(int numClasses, int maxInstanceVariables) {

		if (numClasses < 1 || numClasses > 5) {
			System.err.println("Error: you have exceeded the 5 class limit");
			return null;
		}

		ArrayList<VirtualClass> list = new ArrayList<VirtualClass>();
		Random r = new Random();

		for (int i = 0; i < numClasses; i++) {
			VirtualClass vc = new VirtualClass(classNames[i]);
			int x = r.nextInt(maxInstanceVariables + 1);
			for (int j = 0; j < x; j++) {
				int z = r.nextInt(numClasses);
				vc.addInstanceVariable(createInstanceVariable(classNames[z]));
			}
			list.add(vc);
		}

		return list;
	}

	private ArrayList<VirtualObject> generateObjects(int numClasses, int numObjects) {

		if (numObjects < numClasses) {
			System.err.println("nope");
			return null;
		}

		ArrayList<VirtualObject> list = new ArrayList<VirtualObject>();

		//here we guarantee that there will be at least one object generated for each class
		for (VirtualClass c : _classes) {
			list.add(assignInstanceVariables());
		}

		//now, we generate a random number of objects for each existing class
		numObjects = numObjects - list.size();
		for (int i = 0; i < numObjects; i++) {
			Collections.shuffle(_classes);
			list.add(assignInstanceVariables());
		}

		return list;
	}
	
	private VirtualObject assignInstanceVariables() {
		VirtualObject o = new VirtualObject(_classes.get(0), _currentObjectID);
		_currentObjectID++;
		for (VirtualInstanceVariable v : _classes.get(0).getInstanceVars()) {
			VirtualInstanceVariable newVariable = new VirtualInstanceVariable(v.getName(), v.getType(), o);
			o.addInstanceVariable(newVariable);
		}
		return o;
	}

	private void assignReferences() {
		Random r = new Random();
		for (VirtualObject o : _instances) {
			if (o != null){
				for (VirtualInstanceVariable vi : o.getInstanceVariables()) {
					boolean checker = false;
					while(checker == false){
						int x = r.nextInt(_instances.size());
						String s = vi.getType();
						
						if(_instances.get(x) == null || _instances.get(x).getTypeName().equals(s)) {
							vi.setTarget(_instances.get(x));
							checker = true;
						}
					}
				}
			}
		}
	}
	
	//able to generate up to 676 unique two-letter variable names (26 * 26)
	private VirtualInstanceVariable createInstanceVariable(String type) {
		
		int wraps = _instanceVariableCount / 26;
		int remainder = _instanceVariableCount % 26;
		
		char c;
		String s = "_";
		
		if (wraps > 0) {
			c = (char) ('a' + (wraps - 1));
			s = s + c + "";
		}
		
		c = (char) ('a' + remainder);
		s = s + c;
		
		_instanceVariableCount++;
		
		return new VirtualInstanceVariable(s, type);
	}

	public ArrayList<VirtualClass> getClasses(){
		return _classes;
	}

	public ArrayList<VirtualObject> getObjects(){
		return _instances;
	}
	
	public void output() {
		// Show classes with their instance variables
		System.out.println("****** Classes ******");
		for (VirtualClass c : _classes) {
			System.out.println("- " + c.getName() + " " + c.getInstanceVars());
		}
		System.out.println("");
		
		//Show objects with their instance variables
		System.out.println("****** Objects ******");
		for (VirtualObject o : _instances) {
			if (o != null) {
				System.out.println("- " + o.getType() + " [" + o.getID() + "]");
				for (VirtualInstanceVariable vi : o.getInstanceVariables()) {
					String viTarget = "";
					int viID = 0;
					if (vi.getTarget() == null) {
						viTarget = "[null]";
						viID = -1;
					} else {
						viTarget = vi.getTarget().toString();
						viID = vi.getTarget().getID();
					}
					System.out.println("   (" + vi.getType() + ") "+ 
					vi.getName() + " --> " + viTarget + " [" + viID + "]");
				}
			}
		}
	}
}
