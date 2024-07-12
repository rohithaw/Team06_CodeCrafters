package com.tests;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.utilities.ConfigReader;
import com.utilities.CreateExcel;
import com.utilities.ExcelRead;
import com.utilities.ExcelWrite;

public class DinnerRecipes {
	private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();
	private List<String> excelIngredients;

	public static void browsersetup() {
		ChromeOptions options = new ChromeOptions();
		 options.addArguments("--headless"); // Uncomment if you want to run in
		// headless mode
		options.addArguments("--disable-popup-blocking");
		options.addArguments("--disable-notifications");
		options.addArguments("--disable-extensions");
		options.addArguments("disable-gpu"); // Applicable to Windows OS only
		options.addArguments("blink-settings=imagesEnabled=false"); // Disable images
		tlDriver.set(new ChromeDriver(options));
	};

	private WebDriver getDriver() {
		if (tlDriver.get() == null) {
			browsersetup();
		}
		return tlDriver.get();
	}

	@BeforeClass
	public void setUp() throws Throwable {
		CreateExcel.createExcelWorkBook();
		getDriver().get("https://www.tarladalal.com/");
		getDriver().manage().window().maximize();
		getDriver().manage().deleteAllCookies();

		try {
			String userDir = System.getProperty("user.dir");
			String getPathread = ConfigReader.getGlobalValue("inputExcelPath");
			String inputDataPath = userDir + getPathread;
			excelIngredients = ExcelRead.getDataFromExcel("Final list for LFV Elimination ", "Add", inputDataPath);
			System.out.println("To Add Ingredients List: " + excelIngredients.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public void tearDown() {
		if (tlDriver.get() != null) {
			tlDriver.get().quit();
			tlDriver.remove();
		}
	}

	@Test
	public void extractDataFromPages() throws Throwable {
		navigateToDinnerRecipes();
		extractRecipes();
	}

	private void navigateToDinnerRecipes() {
		getDriver().findElement(By.xpath("(//a[@href='RecipeCategories.aspx'])[1]")).click();

		FluentWait<WebDriver> wait = new FluentWait<>(getDriver()).withTimeout(Duration.ofSeconds(60))
				.pollingEvery(Duration.ofSeconds(5)).ignoring(NoSuchElementException.class);

		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href='recipes-for-dinner-menus-1116']")))
				.click();
	}

	private void extractRecipes() throws Throwable {
		int pageIndex = 0;

		while (true) {
			pageIndex++;
			System.out.println("Page Number: " + pageIndex);

			try {
				List<WebElement> recipeCards = getDriver().findElements(By.className("rcc_recipecard"));
				System.out.println("No_of_recipes: " + recipeCards.size());

				for (int j = 0; j < recipeCards.size(); j++) {
					processRecipe(j);
				}
			} catch (Exception e) {
				System.out.println("Error while extracting data: " + e.getMessage());
				break;
			}

			if (!navigateToNextPage()) {
				break;
			}
		}
	}

	private void processRecipe(int index) throws Throwable {
		try {
			List<WebElement> recipeCards = getDriver().findElements(By.className("rcc_recipecard"));
			if (index < recipeCards.size()) {
				WebElement recipeCard = recipeCards.get(index);

				// Getting recipe id
				String recipeID = recipeCard.getAttribute("id");
				String id = recipeID.replaceAll("[^0-9]", "");
				System.out.println("Recipe Id: " + id);

				// Getting recipe name
				WebElement recipeNameElement = recipeCard.findElement(By.xpath(".//span[@class='rcc_recipename']/a"));
				String recipeName = recipeNameElement.getText();
				System.out.println("Recipe Name: " + recipeName);

				// Clicking into the recipe link
				recipeNameElement.click();

				extractIngredients();
				List<String> matchedIngredients = matchIngredientsWithExcel();

				if (!matchedIngredients.isEmpty()) {
					try {
						String userDir = System.getProperty("user.dir");
						String getPathread = ConfigReader.getGlobalValue("outputExcelPath");
						String outputDataPath = userDir + getPathread;
						ExcelWrite.writeToExcel("LFVAdd", id, recipeName, "Dinner", "FoodCategory",
								String.join(", ", matchedIngredients), "PreparationTime", "CookingTime", "Tag",
								"NoOfServings", "CuisineCategory", "RecipeDescription", "PreparationMethod",
								"NutrientValues", getDriver().getCurrentUrl(), outputDataPath);
					} catch (IOException e) {
						System.out.println("Error writing to Excel: " + e.getMessage());
					}
				}

				int maxRetries = 3;
				int retryCount = 0;
				while (retryCount < maxRetries) {
					try {
						getDriver().navigate().back();
						getDriver().findElement(By.className("rcc_recipecard")).isDisplayed();
						return; // Navigation successful, exit retry loop
					} catch (NoSuchElementException e) {
						System.out.println("Element not found, retrying...");
						retryCount++;
					}
				}
			} else {
				System.out.println("Index " + index + " out of bounds for recipe cards");
			}
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Index " + index + " out of bounds for recipe cards");
		} catch (Exception e) {
			System.out.println("Error in processRecipe: " + e.getMessage());
		}
	}

	private void extractIngredients() {
		List<WebElement> ingredientsList = getDriver()
				.findElements(By.xpath("//div[@id='rcpinglist']//span[@itemprop='recipeIngredient']//a/span"));
		List<String> webIngredients = new ArrayList<>();

		for (WebElement ingredient : ingredientsList) {
			String ingredientName = ingredient.getText().trim().toLowerCase();
			webIngredients.add(ingredientName);
		}
		System.out.println("*****************" + webIngredients.toString());
	}

	private List<String> matchIngredientsWithExcel() {
		List<WebElement> ingredientsList = getDriver()
				.findElements(By.xpath("//div[@id='rcpinglist']//span[@itemprop='recipeIngredient']//a/span"));
		List<String> webIngredients = new ArrayList<>();
		List<String> matchedIngredients = new ArrayList<>();

		for (WebElement ingredient : ingredientsList) {
			String ingredientName = ingredient.getText().trim().toLowerCase();
			webIngredients.add(ingredientName);
		}

		// Match ingredients with Excel ingredients list (partial matches allowed)
		for (String webIngredient : webIngredients) {
			for (String excelIngredient : excelIngredients) {
				if (webIngredient.contains(excelIngredient.toLowerCase())
						|| excelIngredient.toLowerCase().contains(webIngredient)) {
					System.out.println("Ingredient match found: Web Ingredient - " + webIngredient
							+ ", Excel Ingredient - " + excelIngredient);
					matchedIngredients.add(webIngredient);
				}
			}
		}
		return matchedIngredients;
	}

	private boolean navigateToNextPage() {
		try {
			WebElement nextPageIndex = getDriver()
					.findElement(By.xpath("//*[@class='rescurrpg']/following-sibling::a"));
			nextPageIndex.click();
			return true;
		} catch (Exception e) {
			System.out.println("No more pages for this alphabet");
			return false;
		}
	}
}
