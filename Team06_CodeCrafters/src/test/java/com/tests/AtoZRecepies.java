package com.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

public class AtoZRecepies {
	
	WebDriver driver;
	JavascriptExecutor je = (JavascriptExecutor) driver;
	
	//@Test(enabled=false)
	@Test(priority=1)
	public void LaunchBrowser() throws Exception {
		
		System.setProperty("webdriver.chrome.driver","/Users/ark/Downloads/chromedriver");
		ChromeOptions opt = new ChromeOptions();
		opt.addArguments("--remote-allow-origins=*");
		//opt.addArguments("--headless");
		opt.addArguments("--disable-popup-blocking");      
    	opt.addArguments("-–disable-notifications");
		opt.addArguments("-–disable-extensions");
		opt.addArguments("--blink-settings=imagesEnabled=false");
		
		driver = new ChromeDriver(opt);
		driver.get("https://www.tarladalal.com/");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
		driver.manage().window().maximize();
		
		driver.findElement(By.xpath("//div[text()='RECIPES']")).click();
	    JavascriptExecutor je = (JavascriptExecutor) driver;
		je.executeScript("window.scrollBy(0,200)");
		driver.findElement(By.xpath("//a[@href='recipes-for-babies-1215']")).click();
		je.executeScript("window.scrollBy(0,5000)");
		
		Receipedetails();
		
	}
		
		
		public void Receipedetails() throws IOException, Exception {
			
			Thread.sleep(50);
		
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("Recipes");
		    XSSFRow headingrow = sheet.createRow(0);
		    headingrow.createCell(0).setCellValue("RecipeId");
			headingrow.createCell(1).setCellValue("Recipe Name");
			headingrow.createCell(2).setCellValue("Recipe Category(Breakfast/lunch/snack/dinner)");
			headingrow.createCell(3).setCellValue("Food Category(Veg/non-veg/vegan/Jain)");
			headingrow.createCell(4).setCellValue("Ingredients");
			headingrow.createCell(5).setCellValue("Preparation Time");
			headingrow.createCell(6).setCellValue("Cooking Time");
			headingrow.createCell(7).setCellValue("Tag");
			headingrow.createCell(8).setCellValue("No of servings");
			headingrow.createCell(9).setCellValue("Cuisine category");
			headingrow.createCell(10).setCellValue("Recipe Description");
			headingrow.createCell(11).setCellValue("Preparation method");
			headingrow.createCell(12).setCellValue("Nutrient values");
			headingrow.createCell(13).setCellValue("Recipe URL");
			
	        int rownum=1;
	        
	        List<WebElement> Page = driver.findElements(By.xpath("//div[@id='pagination']/a"));
			int pagesize = Page.size();
			for (int j = 1; j <= pagesize; j++) {


			WebElement pagei = driver.findElement(By.xpath("//div[@id='pagination']/a[" + j + "]"));
			pagei.click();
			
			List<WebElement> noofitems = driver.findElements(By.xpath("//div[@class='recipelist']/article"));
			int count = noofitems.size();
			System.out.println(count);
			
			for (int i = 1; i <= count; i++) {

				XSSFRow row = sheet.createRow(rownum++);
				
				// Get the recipe id
				WebElement receipeId = driver.findElement(By.xpath("//div[@class='recipelist']/article["+i+"]"));
				String St=receipeId.getText().split("\\n")[0].split(" ")[1];
				System.out.println("Recipe Id is: " +St);
				row.createCell(0).setCellValue(St);

				// Get the recipe name
				String receipeName = driver.findElement(By.xpath("//div[@class='recipelist']/article[\" + i + \"]/div[3]/span/a")).getText();
				row.createCell(1).setCellValue(receipeName);
				System.out.println("Recipe Name is :" +receipeName);
				
				// Click on the recipe name/link
				WebElement item1 = driver.findElement(By.xpath("//div[@class='recipelist']/article[" + i + "]/div[3]/span/a"));
				item1.click();

				JavascriptExecutor je = (JavascriptExecutor) driver;
				je.executeScript("window.scrollBy(0,200)");
				
				// Get the recipe Category
				String receipeCategory = driver.findElement(By.xpath("//a[@itemprop='recipeCategory'][1]")).getText();
				String RecipeCategory= null;
				if(receipeCategory.toLowerCase().contains("lunch") || receipeName.toLowerCase().contains("lunch"))
				 {
					 RecipeCategory = "Lunch"; 
				 }
				 else if(receipeCategory.toLowerCase().contains("breakfast")  || receipeName.toLowerCase().contains("breakfast"))
				 {
					 RecipeCategory = "Breakfast";  
				 }
				 else if(receipeCategory.toLowerCase().contains("dinner")  || receipeName.toLowerCase().contains("dinner"))
				 {
					 RecipeCategory = "Dinner";  
				 }
				 else
				 {
					 RecipeCategory = "Snack";  
				 }
				
				row.createCell(2).setCellValue(RecipeCategory);
				System.out.println("Recipe Category is :" +RecipeCategory);
				je.executeScript("window.scrollBy(0,200)");
				
				
               //Get Tags
				
				String RecipeTags = driver.findElement(By.id("recipe_tags")).getText();
				row.createCell(7).setCellValue(RecipeTags);
				System.out.println("tags are : " +RecipeTags);
				// Get the Food Category(Veg/non-veg/vegan/Jain)
				
				//String foodcategory = driver.findElement(By.xpath("//div[@id='rcpinglist']")).getText();
				String FoodCategory= null;
			    
			    if (receipeName.contains("Vegan") || RecipeTags.contains("Vegan"))
			    {
			    	FoodCategory = "VEGAN";
			    }
				else if (receipeName.contains("Jain") || RecipeTags.contains("Jain"))
				{
					 FoodCategory = "JAIN";
				}
				else if (receipeName.contains("Egg ") || RecipeTags.contains("Egg "))
				{
					 FoodCategory = "EGGITARIAN";
				}
				else if (receipeName.contains("NonVeg") || RecipeTags.contains("NonVeg"))
				{
					FoodCategory = "NONVEGETARIAN";
				}
				else
					 FoodCategory = "VEGETARIAN";
			    
			
			row.createCell(3).setCellValue(FoodCategory );
			System.out.println("Recipe Category is :" +FoodCategory );
			je.executeScript("window.scrollBy(0,200)");
				
				
				// Get the Ingredients
				String Ingredients = driver.findElement(By.xpath("//div[@id='rcpinglist']")).getText();
				row.createCell(4).setCellValue(Ingredients);
				System.out.println("Ingredients are :" +Ingredients);
				je.executeScript("window.scrollBy(0,200)");
				
				//Preperation time 
				String Preparationtime = driver.findElement(By.xpath("//time[@itemprop='prepTime']")).getText();
				row.createCell(5).setCellValue(Preparationtime);
				System.out.println("Preperation Time is :" +Preparationtime);
				je.executeScript("window.scrollBy(0,200)");
				
				//Cooking Time 
				
				String Cookingtime = driver.findElement(By.xpath("//time[@itemprop='cookTime']")).getText();
				row.createCell(6).setCellValue(Cookingtime);
				System.out.println("Cooking Time is :" +Cookingtime);
				je.executeScript("window.scrollBy(0,100)");
				
				
		

			// Get cuisinecategory 
			
			String CuisineCategory= null;
		    
		    if (receipeName.contains("Indian") || RecipeTags.contains("Indian"))
		    {
		    	CuisineCategory = "Indian";
		    }
			else if (receipeName.contains("South Indian") || RecipeTags.contains("South Indian"))
			{
				CuisineCategory = "South Indian";
			}
			else if (receipeName.contains("Rajathani") || RecipeTags.contains("Rajathani"))
			{
				CuisineCategory = "Rajathani";
			}
			else if (receipeName.contains("Punjabi") || RecipeTags.contains("Punjabi"))
			{
				CuisineCategory = "Punjabi";
			}
			else if (receipeName.contains("Bengali") || RecipeTags.contains("Bengali"))
			{
				CuisineCategory = "Bengali";
			}
			else if (receipeName.contains("Orissa") || RecipeTags.contains("orissa"))
			{
				CuisineCategory = "Orissa";
			}
			else if (receipeName.contains("Gujarati") || RecipeTags.contains("Gujarati"))
			{
				CuisineCategory = "Gujarati";
			}
			else if (receipeName.contains("Maharashtrian") || RecipeTags.contains("Maharashtrian"))
			{
				CuisineCategory = "Maharashtrian";
			}
			else if (receipeName.contains("Andhra") || RecipeTags.contains("Andhra"))
			{
				CuisineCategory = "Andhra";
			}
			else if (receipeName.contains("Kerala") || RecipeTags.contains("Kerala"))
			{
				CuisineCategory = "Kerala";
			}
			else if (receipeName.contains("Goan") || RecipeTags.contains("Goan"))
			{
				CuisineCategory = "Goan";
			}
			else if (receipeName.contains("Kashmiri") || RecipeTags.contains("Kashmiri"))
			{
				CuisineCategory = "Kashmiri";
			}
			else if (receipeName.contains("Himachali") || RecipeTags.contains("Himachali"))
			{
				CuisineCategory = "Himachali";
			}
			else if (receipeName.contains("Tamil nadu") || RecipeTags.contains("Tamil nadu"))
			{
				CuisineCategory = "Tamil nadu";
			}
			else if (receipeName.contains("Karnataka") || RecipeTags.contains("Karnataka"))
			{
				CuisineCategory = "Karnataka";
			}
			else if (receipeName.contains("Sindhi") || RecipeTags.contains("Sindhi"))
			{
				CuisineCategory = "Sindhi";
			}
			else if (receipeName.contains("Chhattisgarhi") || RecipeTags.contains("Chhattisgarhi"))
			{
				CuisineCategory = "Chhattisgarhi";
			}
			else if (receipeName.contains("Madhya pradesh") || RecipeTags.contains("Madhya pradesh"))
			{
				CuisineCategory = "Madhya pradesh";
			}
			else if (receipeName.contains("Assamese") || RecipeTags.contains("Assamese"))
			{
				CuisineCategory = "Assamese";
			}
			else if (receipeName.contains("Manipuri") || RecipeTags.contains("Manipuri"))
			{
				CuisineCategory = "Manipuri";
			}
			else if (receipeName.contains("Tripuri") || RecipeTags.contains("Tripuri"))
			{
				CuisineCategory = "Tripuri";
			}
			else if (receipeName.contains("Sikkimese") || RecipeTags.contains("Sikkimese"))
			{
				CuisineCategory = "Sikkimese";
			}
			else if (receipeName.contains("Mizo") || RecipeTags.contains("Mizo"))
			{
				CuisineCategory = "Mizo";
			}
			else if (receipeName.contains("Arunachali") || RecipeTags.contains("Arunachali"))
			{
				CuisineCategory = "Arunachali";
			}
			else if (receipeName.contains("uttarakhand") || RecipeTags.contains("uttarakhand"))
			{
				CuisineCategory = "uttarakhand";
			}
			else if (receipeName.contains("Haryanvi") || RecipeTags.contains("Haryanvi"))
			{
				CuisineCategory = "Haryanvi";
			}
			else if (receipeName.contains("Awadhi") || RecipeTags.contains("Awadhi"))
			{
				CuisineCategory = "Awadhi";
			}
			else if (receipeName.contains("Bihari") || RecipeTags.contains("Bihari"))
			{
				CuisineCategory = "Bihari";
			}
			else if (receipeName.contains("Uttar pradesh") || RecipeTags.contains("Uttar pradesh"))
			{
				CuisineCategory = "Uttar pradesh";
			}
			else if (receipeName.contains("Delhi") || RecipeTags.contains("Delhi"))
			{
				CuisineCategory = "Delhi";
			}
			else
				CuisineCategory = "North Indian";
		    
		    row.createCell(9).setCellValue(CuisineCategory);
			System.out.println("Cuisine Category is :" +CuisineCategory );
				
			
			
			//No.Of Servings
			String NoofServings = driver.findElement(By.xpath("//span[@id='ctl00_cntrightpanel_lblServes']")).getText();
			row.createCell(8).setCellValue(NoofServings);
			System.out.println("No of Servings are : " +NoofServings);
			
			
			//Preparation Method
			WebElement prepMethod = driver.findElement(By.xpath("//div[@id='ctl00_cntrightpanel_pnlRcpMethod']"));	
			String PrepMethod = prepMethod.getText();
			System.out.println("Preparation Method : " +PrepMethod);
			row.createCell(11).setCellValue(PrepMethod);
			
			//Nutrient Value 
			try {
				String NutrientsValue = driver.findElement(By.xpath("//table[@id='rcpnutrients']/tbody")).getText();
				row.createCell(12).setCellValue(NutrientsValue);
				System.out.println(NutrientsValue);
	            }
	            catch(Exception e) {
	            	System.out.println("NutrientsValue:" + "null");
	            	
	            }
				je.executeScript("window.scrollBy(0,100)");

				// Recipe Description
				
				WebElement Desc = driver.findElement(By.xpath("//span[@id='ctl00_cntrightpanel_lblDesc']"));
				String receipeDesc = Desc.getText().substring(0, 30);
				System.out.println("Receipe Description is : " +receipeDesc);
				row.createCell(10).setCellValue(receipeDesc);

			//Receipe URL
				
				String receipeurl = driver.getCurrentUrl();
				row.createCell(13).setCellValue(receipeurl);
				System.out.println(receipeurl);
				je.executeScript("window.scrollBy(0,100)");
				
			
		
			
				FileOutputStream outputStream = new FileOutputStream("ReceipeExtract.xlsx");

     			wb.write(outputStream);
				
				Thread.sleep(20);

				driver.navigate().back();

			}}

			}    
	        

	}

//	
//@AfterTest
//
//public void teardown() {
//
//	driver.close();

//}





