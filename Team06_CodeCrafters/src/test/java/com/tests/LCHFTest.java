package com.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

public class LCHFTest {
	
	
	WebDriver driver;

	JavascriptExecutor je = (JavascriptExecutor) driver;

	

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

		WebElement clickthelink = driver.findElement(By.xpath("//a[@href='recipes-for-low-carb-veg-indian-recipes-757']"));
		clickthelink.click();

		je.executeScript("window.scrollBy(0,5000)");
		
		
		//Pagination
		List<WebElement> Page = driver.findElements(By.xpath("//div[@id='pagination']/a"));

		int pagesize = Page.size();
		for (int j = 1; j <= pagesize; j++) {

			WebElement pagei = driver.findElement(By.xpath("//div[@id='pagination']/a[" + j + "]"));
			pagei.click();

			List<WebElement> noofitems = driver.findElements(By.xpath("//div[@class='recipelist']/article"));
			int count = noofitems.size();

			System.out.println(count);
			
			for (int i = 1; i <= count; i++) {



				WebElement receipe = driver.findElement(By.xpath("//div[@class='recipelist']/article[" + i + "]"));
				String receipeId = receipe.getText().substring(0, 13);
				System.out.println(receipeId);
				

				WebElement receipename = driver.findElement(By.xpath("//div[@class='recipelist']/article[" + i + "]/div[3]"));
				String receipeName = receipename.getText().substring(0, 30);
				System.out.println(receipeName);
				
				
					WebElement Ingredients = driver.findElement(By.xpath("//div[@id='rcpinglist']"));
					if (Ingredients.isDisplayed()) {
						java.lang.String Ingredientstext = Ingredients.getText();
						if (Ingredientstext != null) {
							System.out.println(Ingredientstext);
							je.executeScript("window.scrollBy(0,200)");
						} 
						
					}
						File file = new File("src/test/resources/input data/IngredientsAndComorbidities-ScrapperHackathon.xlsx");
						FileInputStream inputStream1 = new FileInputStream(file);
						XSSFWorkbook wbs = new XSSFWorkbook(inputStream1);
						XSSFSheet sheet = wbs.getSheet("Sheet2");
						int LastRowNum = sheet.getLastRowNum();
						for (int k = 1; k <= LastRowNum; k++) {
							Row row = sheet.getRow(i);

							if (row.getCell(4).toString().toLowerCase().contains("Ham")
									|| row.getCell(4).toString().toLowerCase().contains("sausage")
									|| row.getCell(4).toString().toLowerCase().contains("tinned fish")
									|| row.getCell(4).toString().toLowerCase().contains("corn")
									|| row.getCell(4).toString().toLowerCase().toLowerCase().contains("white bread")
									|| row.getCell(4).toString().toLowerCase().contains("pasta")
									|| row.getCell(4).toString().toLowerCase().contains("processed grains")
									|| row.getCell(4).toString().toLowerCase().contains("cream of rice")
									|| row.getCell(4).toString().toLowerCase().contains("refined flour")
									|| row.getCell(4).toString().toLowerCase().contains("soda")
									|| row.getCell(4).toString().toLowerCase().contains("flavoured water")
									|| row.getCell(4).toString().toLowerCase().contains("gatorade")
									|| row.getCell(4).toString().toLowerCase().contains("apple juice")
									|| row.getCell(4).toString().toLowerCase().contains("orange juice")
									|| row.getCell(4).toString().toLowerCase().contains("pomegranate juice")
									|| row.getCell(4).toString().toLowerCase().contains("peanut butter")
									|| row.getCell(4).toString().toLowerCase().contains("spreads")
									|| row.getCell(4).toString().toLowerCase().contains("frozen foods")
									|| row.getCell(4).toString().toLowerCase().contains("Flavoured curd")
									|| row.getCell(4).toString().toLowerCase().contains("Flavoured yogurt")
									|| row.getCell(4).toString().toLowerCase().contains("flakes")
									|| row.getCell(4).toString().toLowerCase().contains("honey")
									|| row.getCell(4).toString().toLowerCase().contains("maple syrup")
									|| row.getCell(4).toString().toLowerCase().contains("jaggery")
									|| row.getCell(4).toString().toLowerCase().contains("sweets")
									|| row.getCell(4).toString().toLowerCase().contains("candies")
									|| row.getCell(4).toString().toLowerCase().contains("chocolates")
									|| row.getCell(4).toString().toLowerCase().contains("all purpose flour")
									|| row.getCell(4).toString().toLowerCase().contains("alcohol")
									|| row.getCell(4).toString().toLowerCase().contains("jams")
									|| row.getCell(4).toString().toLowerCase().contains("jelly")
									|| row.getCell(4).toString().toLowerCase().contains("mango")
									|| row.getCell(4).toString().toLowerCase().contains("banana")
									|| row.getCell(4).toString().toLowerCase().contains("butter")
									|| row.getCell(4).toString().toLowerCase().contains("cheese")
									|| row.getCell(4).toString().toLowerCase().contains("pineapple")
									|| row.getCell(4).toString().toLowerCase().contains("pickled")
									|| row.getCell(4).toString().toLowerCase().contains("maida")
									||row.getCell(4).toString().toLowerCase().contains("mayonnise")
									||row.getCell(4).toString().toLowerCase().contains("palmolien oil")
									||row.getCell(4).toString().toLowerCase().contains("dried food")
									||row.getCell(4).toString().toLowerCase().contains("maida")
									||row.getCell(4).toString().toLowerCase().contains("refined flour")
									||row.getCell(4).toString().toLowerCase().contains("maida")
									||row.getCell(4).toString().toLowerCase().contains("canned fruits")
									||row.getCell(4).toString().toLowerCase().contains("canned vegatables")
									||row.getCell(4).toString().toLowerCase().contains("chips")
									||row.getCell(4).toString().toLowerCase().contains("mayonnaise")
									||row.getCell(4).toString().toLowerCase().contains("palmolein oil")
									||row.getCell(4).toString().toLowerCase().contains("dried food")
									||row.getCell(4).toString().toLowerCase().contains("baked food")
									||row.getCell(4).toString().toLowerCase().contains("sweetend milk")
									||row.getCell(4).toString().toLowerCase().contains("sweetend tea")
									||row.getCell(4).toString().toLowerCase().contains("packaged snacks")
									||row.getCell(4).toString().toLowerCase().contains("soft drinks")
									||row.getCell(4).toString().toLowerCase().contains("banana")
									||row.getCell(4).toString().toLowerCase().contains("melon")
									||row.getCell(4).toString().toLowerCase().contains("milk"))

							{

								sheet.removeRow(row);
								FileOutputStream outputStream = new FileOutputStream(
										"/Users/ark/git/Team06_CodeCrafters/Team06_CodeCrafters/src/test/resources/Output");
								wbs.write(outputStream);

				}
			}

		}
		}
	}
}
	

