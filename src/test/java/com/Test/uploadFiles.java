package com.Test;

import org.testng.annotations.Test;

import com.PageObject.uploadFilePom;
import com.Utility.Library;

public class uploadFiles extends Library{

	
	@Test
	
	public void uploadFile() {
		
		uploadFilePom pom=new uploadFilePom();
		pom.uploadFiles();
		
	}
}
