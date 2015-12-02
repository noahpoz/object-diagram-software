package codeGeneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import dataset.*;

public class CodeGenerator {

	private int _numClasses;
	private int _numObjects;
	private int _numInstanceVars;

	private ArrayList<VirtualClass> _classes;
	private ArrayList<VirtualObject> _instances;

	private MasterSet _masterSet;

	/* we must use a hash set, because a list keeping track of all the cases present in this particular generation
	  must not contain duplicates */ 
	private HashSet<String> _cases;
	
	private HashMap<String, String> _caseMapping;
	private HashMap<String, Integer> _caseOrder;
	
	//keeps track of what generate local variables have what instance variables
	private HashMap<String, ArrayList<String[]>> _localVars;

	public CodeGenerator(int numClasses, int numObjects, int numInstanceVars){
		
		_numClasses = numClasses;
		_numObjects = numObjects;
		_numInstanceVars = numInstanceVars;
		
		//initialize these for later use
		_cases = new HashSet<String>();
		_masterSet = new MasterSet();
		_caseMapping = new HashMap<String, String>();
		_localVars = new HashMap<String, ArrayList<String[]>>();

		//made each case with the order the assignment statements are to appear in the main
		_caseOrder = new HashMap<String, Integer>();
		_caseOrder.put("Free Case", 0);
		_caseOrder.put("First Order Singular", 1);
		_caseOrder.put("Dual Restrictive Case", 2);
		_caseOrder.put("Last Order Singular", 3);
	}

	//recursive method (base case = all class case conditions are met)
	public ArrayList<String> generate() {
		
		ArrayList<String> code = new ArrayList<String>();
		
		_masterSet.randomize(_numClasses, _numObjects, _numInstanceVars);
		
		_classes = _masterSet.getClasses();
		_instances = _masterSet.getObjects();
		//remove the null reference put in at the end
		_instances.remove(_instances.size()-1);
		
		ArrayList<String> classes = createClasses(_classes);
		for(int i = 0; i< _classes.size(); i++){
			code.add(classes.get(i));
		}
		code.add(generateMain());
		
		boolean first = false;
		boolean last = false;
		boolean dual = false;
		
		for (String s : getCases()) {
			if (s.equals("First Order Singular")) first = true;
			if (s.equals("Dual Restrictive Case")) dual = true;
			if (s.equals("Last Order Singular")) last = true;
		}
		
		if (first && dual && last) {
			
			//base case
			_masterSet.output();
			outputLocalVars();
			System.out.println(_caseMapping);
			return code;
		
		} else {
			
			//recursive case
			CodeGenerator cg = new CodeGenerator(_numClasses, _numObjects, _numInstanceVars);
			ArrayList<String> result = cg.generate();
			
			//we need to set the top level instances vars to those of the base instance, which have the correct info 
			_masterSet = cg.getMasterSet();
			_localVars = cg.getLocalVars();
			_caseMapping = cg.getCaseMapping();
			
			return result;
			
		}
	}

	public ArrayList<String> createClasses(ArrayList<VirtualClass> classes){
		ArrayList<String> code = new ArrayList<String>();
		for(int i = 0; i < _classes.size(); i ++){
			String classFile = "";
			classFile += "public class " + _classes.get(i).getName() + " {\n\n";
			ArrayList<VirtualInstanceVariable> instances = _classes.get(i).getInstanceVars();
			for(VirtualInstanceVariable vi: instances){
				classFile += "\tprivate " + vi.getType() + " " + vi.getName() + ";\n"; 
			}

			String finalCode = createFinalCode(_classes.get(i));
			classFile += finalCode;

			code.add(classFile);
		}
		return code;
	}

	public String createFinalCode(VirtualClass virtualClass){
		String finalCode = "";
		String cased = determineCase(virtualClass);

		_cases.add(cased);
		_caseMapping.put(virtualClass.getName(), cased);

		ArrayList<VirtualInstanceVariable> vars = virtualClass.getInstanceVars();
		if(cased.equals("Dual Restrictive Case")){
			finalCode = "\n\tpublic " + virtualClass.getName() + "() {\n\n\t}\n\n";
			for(int i = 0; i < vars.size(); i ++){
				finalCode += "\tpublic void set" + vars.get(i).getName() + "(" + vars.get(i).getType() + " " + vars.get(i).getName().substring(1, vars.get(i).getName().length()) + ") {\n\t\t" + vars.get(i).getName() + " = " + vars.get(i).getName().substring(1, vars.get(i).getName().length()) + ";\n	}\n";
			}
			finalCode += "}";
		}
		if(cased.equals("Free Case")||cased.equals("First Order Singular")){
			finalCode = "\n\tpublic " + virtualClass.getName() + "() {\n\n\t}\n}";
		}
		if(cased.equals("Last Order Singular")){
			finalCode = "\n\tpublic " + virtualClass.getName() + "( ";
			for(int i = 0; i < vars.size(); i ++){
				if(i == vars.size()-1){
					finalCode += vars.get(i).getType() + " " + vars.get(i).getName().substring(1, vars.get(i).getName().length()) + ") {\n";
				}
				else{
					finalCode += vars.get(i).getType() + " " + vars.get(i).getName().substring(1, vars.get(i).getName().length()) + " , ";
				}
			}
			for(int i = 0; i < vars.size(); i ++){
				finalCode += "\t\t" + vars.get(i).getName() + " = " + vars.get(i).getName().substring(1, vars.get(i).getName().length()) + ";\n";
			}
			finalCode += "\t}\n}";

		}


		return finalCode;
	}

	public String determineCase(VirtualClass virtualClass){
		String cased = "";
		int decider = 0;

		for(VirtualObject vo: _instances){
			if(vo.getTypeName().equals(virtualClass.getName())){
				ArrayList<VirtualInstanceVariable> vars = vo.getInstanceVariables();

				//check to see if an object is referring to itself
				for(VirtualInstanceVariable vi: vars){
					VirtualObject tempVirtualObject = vi.getTarget();
					if(vo.equals(tempVirtualObject)){
						decider = 2;
					}
				}

				//check to see if anything is referring to this object
				for (VirtualObject otherVO : _instances) {
					for (VirtualInstanceVariable viv : otherVO.getInstanceVariables()) {
						if (viv.getTarget() != null) {
							if(viv.getTarget().equals(vo)){
								vo.addToTargetedBy(otherVO);
								if (decider == 0) {
									decider = 1;
								} else {
									decider = 2;
								}
							}
						}
					}
				}

				//check to see if it is referring to anything
				for (VirtualInstanceVariable viv : vo.getInstanceVariables()){
					if (viv.getTarget() != null){
						
						//if it was 2 before, it has to stay 2
						if (decider == 1 || decider == 2){
							decider = 2;
						} else {
							decider = 3;
						}
					}
				}
			}
		}

		switch(decider){
		case 0: 
			cased = "Free Case";
			break;
		case 1: 
			cased = "First Order Singular";
			break;
		case 2:
			cased =  "Dual Restrictive Case";
			break;
		case 3:
			cased =  "Last Order Singular";
			break;
		}
		return cased;
	}

	public String generateMain(){

		String header = "public class Driver {\n\n\t public static void main(String[] args) {\n\n ";
		
		//a hash set does not maintain order, must switch to array list
		ArrayList<String> orderedCases = new ArrayList<String>();
		for (String s : _cases) {
			orderedCases.add(s);
		}

		//reorder cases
		int smallestSoFar;
		for (int i = 0; i < orderedCases.size(); i++) {
			smallestSoFar = i;
			for (int j = i; j < orderedCases.size(); j++) {
				if (_caseOrder.get(orderedCases.get(j)) < _caseOrder.get(orderedCases.get(smallestSoFar))) {
					smallestSoFar = j;
				}
			}
			Collections.swap(orderedCases, i, smallestSoFar);
		}

		String beginning = "";
		String ending = "";
		String dualCleanup = "";
		
		// ***** GENERATING VARIABLE INSTANTIATIONS *****	
		
		int varCount = 0;
		
		// <object, varName> Maps the local variable created below to the object to which it refers
		HashMap<VirtualObject, String> objectMap = new HashMap<VirtualObject, String>();
		
		//to keep track of things that need to get done out of the initial loop through orderedCases
		ArrayList<VirtualObject> dualSetters = new ArrayList<VirtualObject>();
		ArrayList<VirtualObject> lastOrderAssignments = new ArrayList<VirtualObject>();
		
		//print assignment statements in their proper case order
		for (String currentCase : orderedCases) {
			
			//create the proper assignment statement for each object whose class conforms to the current case
			for (VirtualObject currentObject : _instances) {
				
				//check class conformity to the current case
				if (_caseMapping.get(currentObject.getTypeName()).equals(currentCase)) {
					
					String type = currentObject.getTypeName();
					
					if (currentCase.equals("First Order Singular") || currentCase.equals("Free Case")) {
						
						beginning += "\t\t" + type + " " + getChar(varCount) + " = new " + type + "();\n";
						objectMap.put(currentObject, getChar(varCount));
						currentObject.setLocalVariable(getChar(varCount));
						varCount++;
						
					}
					
					if (currentCase.equals("Dual Restrictive Case")) {
						
						beginning += "\t\t" + type + " " + getChar(varCount) + " = new " + type + "();\n"; 
						
						//these can only be safely written after all firsts and duals have been instantiated and assigned
						dualSetters.add(currentObject);
						objectMap.put(currentObject, getChar(varCount));
						currentObject.setLocalVariable(getChar(varCount));
						varCount++;
						
					}
					
					if (currentCase.equals("Last Order Singular")) {
						
						//these must be written after all firsts have been instantiated and assigned
						lastOrderAssignments.add(currentObject);
						
					}
				}
			}
		}
		
		//set all the variables for each dual restrictive object
		for (VirtualObject o : dualSetters) {
			
			String currentName = objectMap.get(o);
			
			//set each individual variable
			for (VirtualInstanceVariable v : o.getInstanceVariables()) {
				
				dualCleanup += "\t\t" + currentName + ".set" + v.getName() + "(" + objectMap.get(v.getTarget()) + ")\n";
				
			}
		}
		
		//instantiate all the last orders and pass in what their instance variables hold
		for (VirtualObject o : lastOrderAssignments) {
			
			ending += "\t\t" + o.getTypeName() + " " + getChar(varCount) + " = new " + o.getTypeName() + "(";
			objectMap.put(o, getChar(varCount));
			
			//now we have to iterate for each instance variable that needs its corresponding argument
			for (int i = 0; i < o.getInstanceVariables().size(); i++) {
				
				VirtualInstanceVariable v = o.getInstanceVariables().get(i);
				
				ending += objectMap.get(v.getTarget());
				if (i == o.getInstanceVariables().size() - 1) {
					ending += ");\n";
				} else {
					ending += ", ";
				}
			}
			o.setLocalVariable(getChar(varCount));
			varCount++;
		}
		
		for (int i = 0; i < varCount; i++) {
			trackLocalVariable(i, objectMap);
		}
		
		return header + beginning + dualCleanup + ending + "\n\t}\n}";
	}
	
	/* FORMAT:
	 * 
	 *  a --> | _a --> c |
	 * 		  | _b --> d |
	 * 
	 *  c --> | _e --> null |
	 *  	  | _g --> a    |
	 * 
	 */
	
	private void trackLocalVariable(int varCount, HashMap<VirtualObject, String> objectMap) {
		
		ArrayList<String[]> instanceVars = new ArrayList<String[]>();
		
		VirtualObject result = null;
		for (VirtualObject o : _instances) {
			
			//find the object that corresponds to this char
			if (objectMap.get(o).equals(getChar(varCount))) {
				result = o;
				break;
			}
		}
		
		boolean added = false;
		for (VirtualInstanceVariable v : result.getInstanceVariables()) {
			String[] s = new String[2];
			s[0] = v.getName();
			s[1] = objectMap.get(v.getTarget());
			instanceVars.add(s);
			added = true;
		}
		
		if (!added) {
			String[] s = {"", ""};
			instanceVars.add(s);
		}
		
		_localVars.put(getChar(varCount), instanceVars);
	}

	public String getChar(int charNumber){
		return Character.toString((char)('a' + charNumber));
	}

	public HashSet<String> getCases() {
		return _cases;
	}
	
	private HashMap<String, String> getCaseMapping() {
		return _caseMapping;
	}
	
	//should probably return a deep copy, just to be safe
	public MasterSet getMasterSet() {
		return _masterSet;
	}
	
	public HashMap<String, ArrayList<String[]>> getLocalVars() {
		return _localVars;
	}
	
	public void outputLocalVars() {
		
		for (int i = 0; i < _localVars.size(); i++) {
			
			char c = (char) (i + 'a');
			String s = c + "";
			
			System.out.println("--------------------");
			System.out.println(s + ":");
			
			for (String[] longString : _localVars.get(s)) {
				if (longString[0].equals("")) {
					System.out.println("[no instance variables]");
				} else {
					System.out.println("\t" + longString[0] + " --> " + longString[1]);
				}
			}
		}
	}
}
