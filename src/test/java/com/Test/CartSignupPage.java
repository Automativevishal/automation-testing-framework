package com.Test;


import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.PageObject.eCartSignupPage;
import com.Utility.BaseClass;
import com.Utility.Library;

@Listeners(com.Utility.listeners.class)
public class CartSignupPage  extends BaseClass{
	
	@Test (description="Veryfy user can signup on ecart website")
	
	public void hv(){
		
		 System.out.println("Name  : " + exel.getstringdata("eCart", 1, 1));
		 System.out.println("Email : " + exel.getstringdata("eCart", 2, 1));
		    
		eCartSignupPage pom=new eCartSignupPage();
		
		pom.ecartSignUP(exel.getstringdata("eCart", 1, 1),
				exel.getstringdata("eCart", 2, 1));
		pom.filAccoutInfo1(exel.getstringdata("eCart", 5, 1),
				
				exel.getstringdata("eCart", 7, 1), 
				exel.getstringdata("eCart", 8, 1),
				exel.getstringdata("eCart", 9, 1),
				exel.getstringdata("eCart", 10, 1));
		
		pom.fillAddressInfo2(
		        exel.getstringdata("eCart", 12, 1),
		        exel.getstringdata("eCart", 13, 1),
		        exel.getstringdata("eCart", 14, 1),
		        exel.getstringdata("eCart", 15, 1),
		        exel.getstringdata("eCart", 16, 1),
		        exel.getstringdata("eCart", 17, 1),
		        exel.getstringdata("eCart", 18, 1),
		        exel.getstringdata("eCart", 19, 1), // City
		        exel.getstringdata("eCart", 20, 1),
		        exel.getstringdata("eCart", 21, 1));
		
		if (pom.verifyAccountCreated()) {
		    Library.captureStep("Account Created Confirmation");
		}
	
		pom.clickContinue();
		Library.waitForPageToLoad();

		if (pom.verifyHomePage()) {
		    Library.captureStep("Ecart Website Home Page Opened");
		}
		
		
		
		
	}}
	


