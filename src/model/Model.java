package model;

import ui.shapes.Shape;

import java.util.ArrayList;
import java.util.Observable;

import javax.swing.SwingUtilities;

import codeGeneration.*;
import dataset.*;
import ui.UI;

public class Model extends Observable {

	private UI _ui;
	private CodeGenerator _codeGenerator;
	
	public Model(int[] args, UI ui) {
		
		_ui = ui;
		createNewCode(args[0], args[1], args[2]);

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
			return SolutionChecker.checkSolution(shapes, _codeGenerator.getMasterSet(), 
					_codeGenerator.getLocalVars(), _ui.getEditPane());
		}
	}
}
