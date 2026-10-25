package com.PageObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.Utility.Library;

public class uploadFilePom extends Library{
	
public uploadFilePom() {
	PageFactory.initElements(driver, this);
}

@FindBy(id="singleFileInput") private WebElement chooseSingleFile;
@FindBy(xpath="//button[text()='Upload Single File']") private WebElement uploadSingleFile;
@FindBy(xpath="//input[@id='multipleFilesInput']") private WebElement chooseMultipleFiles;
@FindBy(xpath="//button[text()='Upload Multiple Files']") private WebElement uploadMultiplrFiles;

public void uploadFiles() {
	
	Library.uploadFile(chooseSingleFile, "C:\\Users\\Vishal Chavan\\Downloads\\VishalChavan_Resume.pdf","Resume File");
	Library.click(uploadSingleFile, "single file uploaded");
	Library.uploadMultipleFiles(chooseMultipleFiles, new String[]{"C:\\Users\\Vishal Chavan\\Downloads\\Resume.pdf","C:\\Users\\Vishal Chavan\\Downloads\\Vishal Resume.pdf"}, "Resume");
	Library.click(uploadMultiplrFiles, "multiple file uploaded");
	
	Library.captureStep("both file uploaded");
	
	
}



}
