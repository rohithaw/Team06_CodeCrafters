
package com.tests;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

public class MezzePlatter {
	private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

	public static void browserSetUp() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("disable-gpu");
		options.addArguments("blink-settings=imageEnabled=false");// Disable images
		options.addArguments("--disable-popup-blocking");
		options.addArguments("-–disable-notifications");
		options.addArguments("-–disable-extensions");
		options.addArguments("--blink-settings=imagesEnabled=false");
		WebDriver driver = new ChromeDriver(options);
		tlDriver.set(driver);
	};

	private WebDriver getDriver() {
		if (tlDriver.get() == null) {
			browserSetUp();
		}
		return tlDriver.get();

	}

	public void landingPage() {
		WebDriver driver = getDriver();
		getDriver().get("https://www.tarladalal.com/");
		getDriver().manage().window().maximize();
		getDriver().manage().deleteAllCookies();

	}

	public void navigateToRecipe() throws InterruptedException {
	        WebDriver driver = getDriver();
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	        Actions actions = new Actions(driver);

	        Thread.sleep(2000);
	        WebElement recipe = driver.findElement(By.xpath("//div[@id='menucontainer']//div//div//ul[@id='nav']//li//a//div[text()='RECIPES']"));
	        recipe.click();
	        Thread.sleep(2000);
	        JavascriptExecutor je = (JavascriptExecutor) driver;
			je.executeScript("window.scrollBy(0,200)");
			Thread.sleep(2000);
			getDriver().get("https://www.tarladalal.com/recipes-for-lebanese-vegetarian-lebanese-recipes-115");
			
			WebElement recipe1 = driver.findElement(By.xpath("//div[@class='rcc_rcpcore']//span[@class='rcc_recipename']//a[@href='mezze-platter-22584r']"));
			recipe1.click();
			Thread.sleep(5000);
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("Sheet1");
			XSSFRow headingrow = sheet.createRow(0);
				headingrow.createCell(0).setCellValue("RecipeId");
				headingrow.createCell(1).setCellValue("Recipe Name");
				headingrow.createCell(4).setCellValue("Ingredients");
				//Name
				String receipeName = driver.findElement(By.xpath("//div//span[@id='ctl00_cntrightpanel_lblrecipeNameH2']")).getText();
				System.out.println("Recipe Name is :" +receipeName);
				Thread.sleep(5000);
				// Ingredients
				String ingredients = driver.findElement(By.xpath("//div[@id='recipe_ingredients']//section//div[@id='rcpinglist']")).getText();
				
				System.out.println("Ingredients are :" +ingredients);
				 // Elimination List Check
		        List<String> eliminationList = Arrays.asList("milk", "soy", "egg", "Tabbouleh", "peanuts", "walnut", "almond", "hazelnut", "lettuce");
		        String lowerCaseIngredients = ingredients.toLowerCase();
		        for (String item : eliminationList) {
		            if (lowerCaseIngredients.contains(item.toLowerCase().trim())) {
		                System.out.println("The ingredient list contains: " + item);
		            }
		        }

		        je.executeScript("window.scrollBy(0,200)");
	}

	}

