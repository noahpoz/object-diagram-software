package model;

import ui.shapes.Shape;

import java.util.ArrayList;
import java.util.Observable;

import javax.swing.SwingUtilities;

import codeGeneration.*;
import dataset.*;
import ui.UI;

/* We should be able to call generateNewCode on the model, which will:
   		- put a MasterSet object in _masterSet
   		- put a CodeGenerator object in _codeGenerator
   		
   I'm thinking if we could define a getCode() method which, when called on _codeGenerator,
   will return the generated code in this format:
   
   		ArrayList<String[]> such that each String contains:
   		
   				{ CLASSNAME, CLASS CODE }
  
 */


public class Model extends Observable {

	private CodeGenerator _codeGenerator;
	
	public Model() {
		
		createNewCode(3, 5, 3);

	}
	
	public void createNewCode(int numClasses, int numObjects, int numInstanceVars) {
		_codeGenerator = new CodeGenerator(numClasses, numObjects, numInstanceVars);
	}
	
	public ArrayList<String> getCode() {
		return _codeGenerator.generate();
	}
	
	/* essentially a wrapper method for the sake of encapsulating model functionality 
	(besides, only the model should have access to the masterset) */
	public boolean checkSolution(ArrayList<Shape> shapes) {
		System.out.println("Checking Solution...");
		
		if (_codeGenerator == null) {
			System.err.println("Error: code has not yet been generated. Call getNewCode(...) to "
					+ "generate a MasterSet.");
			return false;
		} else {
			return SolutionChecker.checkSolution(shapes, _codeGenerator.getMasterSet(), _codeGenerator.getLocalVars());
		}
	}
}
