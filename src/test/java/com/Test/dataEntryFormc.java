package com.Test;

import org.testng.annotations.Test;

import com.PageObject.dataEntryForm;
import com.Utility.Library;

public class dataEntryFormc extends Library {
	
	@Test
	
	public void filldata() {
		
		dataEntryForm pom= new dataEntryForm();
		
		pom.dayEntry(exel.getstringdata("DataEntryForm", 0, 0), exel.getstringdata("DataEntryForm", 1, 0), exel.getstringdata("DataEntryForm", 2, 0), exel.getstringdata("DataEntryForm", 3, 0));
		
		
	}
	
	

}
