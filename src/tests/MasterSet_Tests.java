package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import dataset.MasterSet;

/**
 
 * Noah Poczciwinski 11/21
 * Testing for the functionality of the MasterSet class.

 **/

public class MasterSet_Tests {
	
	// *** createInstanceVariable tests ***
	@Test
	public void test1() {
		MasterSet ms = new MasterSet();
		
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < 100; i++) {
			
			String s = "";
			
			// Commented out while this method is set to invisible
			//s = ms.createInstanceVariable("Alpha").getName();
			
			names.add(s);
		}
		
		String e = "_bc";
		String i = names.get(54);
		
		assertTrue("Expected: " + e + " Actual: " + i, e.equals(i));
		
	}

}
