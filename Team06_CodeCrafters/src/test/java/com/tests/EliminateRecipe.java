package com.tests;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;


public class EliminateRecipe {
	
	public static void main(String[] args) {
		
		ChromeDriver driver= new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("https://www.tarladalal.com/RecipeCategories.aspx?focus=cuisine");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		WebElement Continentalrecipes= driver.findElement(By.xpath("//a[@title='Click here to see all recipes under Continental food']"));
		Continentalrecipes.click();
		
	}

	}


