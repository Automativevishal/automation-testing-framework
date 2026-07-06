package com.Test;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.PageObject.GoogleSearchPOM;
import com.Utility.BaseClass;

@Listeners(com.Utility.listeners.class)
public class googleSearch extends BaseClass{

	@Test (description="Verify Google Search")
	
	public void TC_googleSearch() {
		
		GoogleSearchPOM pom=new GoogleSearchPOM();
		pom.searchGoogle(exel.getstringdata("Sheet1", 8, 0));
		
	}
	
	
	
}
