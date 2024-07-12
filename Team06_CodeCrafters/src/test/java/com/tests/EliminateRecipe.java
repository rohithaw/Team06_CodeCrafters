package com.tests;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.List;

public class EliminateRecipe {

	public static void main(String[] args) throws IOException {

		ChromeDriver driver= new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("https://www.tarladalal.com/RecipeCategories.aspx?focus=cuisine");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		WebElement Continentalrecipes= driver.findElement(By.xpath("//a[@title='Click here to see all recipes under Continental food']"));
		Continentalrecipes.click();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		WebElement RedSaucePasta= driver.findElement(By.xpath("//strong[text()='Red Sauce Pasta']"));
		RedSaucePasta.click();
		//RedSaucePasta.click();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		WebElement Ingredients=driver.findElement(By.xpath("//span[contains(text(),'Ingredients')]"));
		Ingredients.click();
		//driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

		// Extract the list of ingredients
		List<WebElement> ingredientlist =driver.findElements(By.xpath("//span[@itemprop='recipeIngredient']"));
		ArrayList<String> rec = new ArrayList<>();
		// Print each ingredient
		for (WebElement recipe_ingredient : ingredientlist) {
			System.out.println(recipe_ingredient.getText());
			//Store recipe ingredient list in array
			rec.add(recipe_ingredient.getText());
		}

		System.out.println(rec);
		readExcel();
	}

	public static void readExcel() throws IOException {
		ArrayList<String> eliminatelist = new ArrayList<>();
		FileInputStream file=new FileInputStream("C:\\Users\\nikhi\\git\\Team06_CodeCrafters\\Team06_CodeCrafters\\src\\test\\resources\\input data\\IngredientsAndComorbidities-ScrapperHackathon.xlsx");
		XSSFWorkbook workbook =new XSSFWorkbook(file);
		XSSFSheet sheet=workbook.getSheet("Final list for LFV Elimination ");
		int totalrows=sheet.getLastRowNum();
		for (int i=1;i<totalrows;i++) {
			int totalcells=sheet.getRow(i).getLastCellNum();
			for(int j=0;j<totalcells;j++) {	
				System.out.println(sheet.getRow(i).getCell(j));

			}
		}

		workbook.close();
		file.close();


	}

}









