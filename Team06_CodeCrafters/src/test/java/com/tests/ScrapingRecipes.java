package com.tests;

import java.time.Duration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.utilities.CreateExcel;

public class ScrapingRecipes {
	private static ThreadLocal<WebDriver> driverThreadLocal = ThreadLocal.withInitial(() -> {
		ChromeOptions options = new ChromeOptions();
		 //options.addArguments("--headless");
		return new ChromeDriver(options);
	});

	private WebDriver getDriver() {
		return driverThreadLocal.get();
	}

	
	@BeforeClass
	public void setUp() {
		CreateExcel.createExcelWorkBook();
		getDriver().get("https://www.tarladalal.com/");
		getDriver().manage().window().maximize();
	}

	@AfterClass
	public void tearDown() {
		if (getDriver() != null) {
			getDriver().quit();
			driverThreadLocal.remove();
		}
	}

	@DataProvider(name = "alphabetDataProvider", parallel = false)
	public Object[][] alphabetDataProvider() {
		return new Object[][] { { "A" }, { "B" }, { "C" }, { "D" }, { "E" }, { "F" }, { "G" }, { "H" }, { "I" },
				{ "J" }, { "K" }, { "L" }, { "M" }, { "N" }, { "O" }, { "P" }, { "Q" }, { "R" }, { "S" }, { "T" },
				{ "U" }, { "V" }, { "W" }, { "X" }, { "Y" }, { "Z" } };
	}

	@Test(dataProvider = "alphabetDataProvider")
	public void clickAlphabetLink(String alphabet) {
		FluentWait<WebDriver> wait = new FluentWait<>(getDriver()).withTimeout(Duration.ofSeconds(60))
				.pollingEvery(Duration.ofSeconds(5)).ignoring(NoSuchElementException.class);

		if (alphabet.equals("A")) {
			getDriver().findElement(By.xpath("//a[text()='Recipe A To Z']")).click();
		}

		WebElement alphabetLink = wait.until(driver -> driver.findElement(By.xpath("//a[text()='" + alphabet + "']")));
		alphabetLink.click();
		System.out.println("Clicked on alphabet: " + alphabet);

		extractDataFromPages();
	}

	public void extractDataFromPages() {
		FluentWait<WebDriver> wait = new FluentWait<>(getDriver()).withTimeout(Duration.ofSeconds(60))
				.pollingEvery(Duration.ofSeconds(5)).ignoring(NoSuchElementException.class);

		int pageIndex = 0;

		while (true) {
			pageIndex++;
			System.out.println("Page Number: " + pageIndex);
			try {
				String pageSource = getDriver().getPageSource();
				Document doc = Jsoup.parse(pageSource);

				Elements recipeCards = doc.select(".rcc_recipecard");
				System.out.println("No_of_recipes: " + recipeCards.size());

				for (Element recipeCard : recipeCards) {
					String recipeID = recipeCard.attr("id").replaceAll("[^0-9]", "");
					System.out.println("Recipe Id: " + recipeID);

					Element recipeNameElement = recipeCard.selectFirst(".rcc_recipename a");
					String recipeName = recipeNameElement.text();
					System.out.println("Recipe Name: " + recipeName);

					WebElement recipeLink = getDriver()
							.findElement(By.xpath("//span[@class='rcc_recipename']/a[text()='" + recipeName + "']"));
					((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", recipeLink);
					wait.until(ExpectedConditions.elementToBeClickable(recipeLink)).click();
                    getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(2000));
                    
                    //
					
					getDriver().navigate().back();
					wait.until(ExpectedConditions.presenceOfElementLocated(
							By.xpath("//span[@class='rcc_recipename']/a[text()='" + recipeName + "']")));
				}

			} catch (Exception e) {
				System.out.println("Error while extracting data: " + e.getMessage());
				break;
			}

			try {
				WebElement nextPageIndex = getDriver()
						.findElement(By.xpath("//*[@class='rescurrpg']/following-sibling::a"));
				nextPageIndex.click();
			} catch (Exception e) {
				System.out.println("No more pages for this alphabet");
				break;
			}
		}
	}
}
