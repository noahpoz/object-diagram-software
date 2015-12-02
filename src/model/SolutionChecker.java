package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import dataset.MasterSet;
import dataset.VirtualInstanceVariable;
import dataset.VirtualObject;
import ui.shapes.ObjectShape;
import ui.shapes.Shape;
import ui.shapes.VariableShape;

/*
 	I see no need for instantiating this class, because it need not store any information. 
 	All it really needs is to be able to do is take in information and spit out a result. That is,
 	compare the information in shapes with the information in set, and state whether they agree.

 */

public class SolutionChecker {

	public static boolean checkSolution(ArrayList<Shape> shapes, MasterSet set, HashMap<String, ArrayList<String[]>> locals) {
		JFrame frame = new JFrame();

		ArrayList<VirtualObject> instances = set.getObjects();

		boolean checkClassName = false;
		boolean checkObjectNum = false;
		boolean checkVarNum = false;
		boolean checkVarNames = false;
		boolean checkVarNamesInObjects = false;
		boolean checkReferences = false;

		//checking Object number
		int ObjectNum = 0;
		for(int i = 0; i <shapes.size(); i ++){
			if(shapes.get(i).getClass().equals(ObjectShape.class)){
				ObjectNum ++;
			}
		}

		if(ObjectNum == instances.size()){
			checkObjectNum = true;
		}
		else{
			JOptionPane.showMessageDialog(frame, "Check your number of Objects");
			return false;
		}

		//checking class names
		HashSet<String> classCount = new HashSet<String>();
		Set<String> currentlocalVars =  locals.keySet();


		for(int i = 0 ; i <shapes.size(); i ++){
			if(shapes.get(i).getClass().equals(ObjectShape.class)){
				classCount.add(shapes.get(i).getName());
			}		
		}

		if(classCount.equals(currentlocalVars)){
			checkClassName = true;
		}
		else{
			JOptionPane.showMessageDialog(frame,"Check your Class names");
			return false;
		}

		//checking number of instance variables
		int varNum = 0;
		int expectedVarNum = 0;

		for(int i = 0; i < instances.size(); i ++){
			expectedVarNum += instances.get(i).getInstanceVariables().size();
		}

		for(int i = 0; i < shapes.size(); i ++){
			if(shapes.get(i).getClass().equals(VariableShape.class)){
				varNum ++;
			}
		}

		if(varNum == expectedVarNum){
			checkVarNum = true;
		}
		else{
			JOptionPane.showMessageDialog(frame, "Check your number of Instance Variables");
			return false;
		}

		//checking instance Variable Names
		HashSet<String> expectedVarNames = new HashSet<String>();
		for(int i= 0; i < instances.size(); i ++){
			ArrayList<VirtualInstanceVariable> iv = instances.get(i).getInstanceVariables();
			for(int y = 0; y < iv.size(); y++){
				expectedVarNames.add(iv.get(y).getName());
			}
		}

		HashSet<String> varNames = new HashSet<String>();
		for(int i = 0; i < shapes.size(); i ++){
			if(shapes.get(i).getClass().equals(VariableShape.class)){
				varNames.add(shapes.get(i).getName());
			}
		}

		if(varNames.size() == expectedVarNames.size()){
			for(String s : varNames){
				if(expectedVarNames.contains(s)){
					checkVarNames = true;
				}
				else{
					JOptionPane.showMessageDialog(frame, "Check your Instance Variable Names");
					return false;
				}
			}
		}
		else{
			JOptionPane.showMessageDialog(frame, "Check your Instance Variable Names");
			return false;
		}

		//check variable names in each object
		ArrayList<ObjectShape> currentObjects = new ArrayList<ObjectShape>();
		for(int i = 0; i < shapes.size(); i ++){
			if(shapes.get(i).getClass().equals(ObjectShape.class)){
				currentObjects.add((ObjectShape) shapes.get(i));
			}
		}

		for(ObjectShape o: currentObjects){
			ArrayList<VariableShape> iv = o.getInstanceVariables();
			String s = o.getName();
			for(VirtualObject vo : instances){
				if(vo.getLocalVariable().equals(s)){
					ArrayList<VirtualInstanceVariable> iv2 = vo.getInstanceVariables();
					ArrayList<String> iv2Strings = new ArrayList<String>();
					for(VirtualInstanceVariable viv: iv2){
						iv2Strings.add(viv.getName());
					}
					for(VariableShape vs : iv){
						if(iv2Strings.contains(vs.getName())){
							checkVarNamesInObjects = true;
						}
						else{
							JOptionPane.showMessageDialog(frame, "Check your Variable Names in each Object");
							return false;
						}
					}

				}
			}
		}
		
		//Check References
		int correctReferences = 0;
		int wantedReferences = 0;

		for(VirtualObject o : instances){
			ArrayList<VirtualInstanceVariable> iv =  o.getInstanceVariables();
			for(VirtualInstanceVariable v : iv){
				if(v.getTarget() == null){
					wantedReferences += 1;
				}
				else{
					wantedReferences += 2;
				}
			}
		}

		for(ObjectShape os: currentObjects){
			ArrayList<VariableShape> varso = os.getInstanceVariables();
			for(VariableShape vs: varso){
				String s = vs.getName();
				for(VirtualObject o: instances){
					ArrayList<VirtualInstanceVariable> iv = o.getInstanceVariables();
					for(VirtualInstanceVariable v: iv){
						if(v.getName().equals(s) && v.getOrigin().getLocalVariable().equals(os.getName())){
							if(vs.getReference() != null && v.getTarget() != null){
								String target = vs.getReference().getName();
								if(target.equals(v.getTarget().getLocalVariable())){
									correctReferences +=2;
								}
							}
							if(vs.getReference() == null && v.getTarget() == null){
								correctReferences +=1;
							}
						}
					}
				}	
			}
		}

		if(correctReferences == wantedReferences){
			checkReferences = true;
		}
		else{
			JOptionPane.showMessageDialog(frame, "Check your References");
			return false;	
		}

		if(checkClassName && checkObjectNum && checkVarNum && checkVarNames && checkVarNamesInObjects && checkReferences){
			JOptionPane.showMessageDialog(frame, "Good Job!");
			return true;
		}

		return false;

	}

}
