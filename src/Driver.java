import javax.swing.SwingUtilities;

import ui.UI;

public class Driver {
	
	// I am making some changes.
	
	//require 3 args: <numObjects, numClasses, maxInstanceVars>
    public static void main(String [ ] args){
        
    	//ensure correct number of args
        if (args.length != 3) {
        	System.err.println("Error: Incorrect number of arguments.");
        	showFormat();
        }
        
        //ensure args are ints
        int[] formattedArgs = new int[3];
        for (int i = 0; i < 3; i++) {
        	try {
        		formattedArgs[i] = Integer.parseInt(args[i]);
        	} catch (NumberFormatException e) {
        		System.err.println("Error: Invalid argument format.");
        		showFormat();
        	}
        }
        
        //ensure args fall within proper range
        if (formattedArgs[0] < 0 || formattedArgs[1] < 0 || formattedArgs[2] < 0) {
        	System.err.println("Error: Arguments cannot be less than zero.");
        	showFormat();
        }
        
        if (formattedArgs[0] > 5) {
        	System.err.println("Error: You have exceeded the five class limit.");
        	showFormat();
        }
        
        if (formattedArgs[1] > 20) {
        	System.err.println("Error: You have exceeded the twenty object limit.");
        	showFormat();
        }
        
        if (formattedArgs[2] > 5) {
        	System.err.println("Error: You have exceeded the five instance variable limit.");
        }
        
        SwingUtilities.invokeLater(new UI(formattedArgs));
    }
    
    private static void showFormat() {
    	System.out.println("Format: <number of objects, number of classes, max instance variables>");
    	System.out.println("Example: 3 12 4");
    	System.exit(0);
    }
}
