package com.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

public class simpleLoginTest {
	
	@Test
	public void loginTest() {
	WebDriver driver=new ChromeDriver();
	driver.get("https://www.facebook.com/login/?next=https%3A%2F%2Fwww.facebook.com%2F");
	driver.manage().window().maximize();
	
	WebElement login=driver.findElement(By.xpath("//input[@id='_R_c9l6neappb6amH1_']"));
	login.sendKeys("8788053005");
	
	WebElement password=driver.findElement(By.xpath("//input[@id='_R_cdl6neappb6amH1_']"));
	password.sendKeys("Vishal");
	
	WebElement submit=driver.findElement(By.xpath("//span[text()='Log in']"	));
	submit.click();	
	
	driver.quit();
	
	}

}
