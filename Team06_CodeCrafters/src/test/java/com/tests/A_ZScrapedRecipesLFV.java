package com.tests;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
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
import org.testng.annotations.Test;

import com.utilities.ConfigReader;
import com.utilities.CreateExcel;
import com.utilities.ExcelRead;
import com.utilities.ExcelWrite;

public class A_ZScrapedRecipesLFV {
	private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

	private List<String> excelIngredients;
	private String recipeName;
	private String recipeCategory;
	private String recipeTags;
	private String foodCategory;
	private String cuisineCategory;
	private String preparationTime;
	private String cookingTime;
	private String recipeDescription;
	private String preparationMethod;
	private String nutrientValues;
	private String noOfServings;

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

	JavascriptExecutor je = (JavascriptExecutor) getDriver();

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
			excelIngredients = ExcelRead.getDataFromExcel("Final list for LFV Elimination ", "Eliminate", inputDataPath);
			System.out.println("To Eliminate Ingredients List: " + excelIngredients.toString());
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
				recipeName = recipeNameElement.getText();
				System.out.println("Recipe Name: " + recipeName);



				// Clicking into the recipe link
				recipeNameElement.click();
				getRecipeCategory();
				getTags();
				getFoodCategory();
				getcuisineCategory();
				getPreparationTime();
				getPreparationMethod();
				getCookingTime();
				getNutrientValues();
				getNoOfServings();
				getRecipeDescription();

				extractIngredients();
				List<String> matchedIngredients = matchIngredientsWithExcel();
				matchIngredientsWithEliminateListforLFV();

				if (!matchedIngredients.isEmpty()) {
					try {
						String userDir = System.getProperty("user.dir");
						String getPathread = ConfigReader.getGlobalValue("outputExcelPath");
						String outputDataPath = userDir + getPathread;
						ExcelWrite.writeToExcel("LFVEliminate","LFVAdd",id, recipeName, recipeCategory, foodCategory,
								String.join(", ", matchedIngredients), preparationTime, cookingTime, recipeTags,
								noOfServings, cuisineCategory, recipeDescription, preparationMethod, nutrientValues,
								getDriver().getCurrentUrl(), outputDataPath);
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

	private void getRecipeCategory() {

		try {
			// je.executeScript("window.scrollBy(0,200)");
			String receipeCategory = getDriver().findElement(By.xpath("//a[@itemprop='recipeCategory'][1]")).getText();
			if (receipeCategory.toLowerCase().contains("lunch") || recipeName.toLowerCase().contains("lunch")) {
				recipeCategory = "Lunch";
			} else if (receipeCategory.toLowerCase().contains("breakfast")
					|| recipeName.toLowerCase().contains("breakfast")) {
				recipeCategory = "Breakfast";
			} else if (receipeCategory.toLowerCase().contains("dinner")
					|| recipeName.toLowerCase().contains("dinner")) {
				recipeCategory = "Dinner";
			} else {
				recipeCategory = "Snack";
			}

			System.out.println("Recipe Category is :" + recipeCategory);
		} catch (NoSuchElementException e) {
			System.out.println("Recipe category element not found for recipe: " + recipeName);
			recipeCategory = "Unknown";
		}
	}

	private void getTags() {
		try {
			recipeTags = getDriver().findElement(By.id("recipe_tags")).getText();
			System.out.println("Tags are : " + recipeTags);
		} catch (NoSuchElementException e) {
			recipeTags = "Unknown";
		}
	}

	// Get the Food Category(Veg/non-veg/vegan/Jain)
	private void getFoodCategory() {
		try {

			if (recipeName.contains("Vegan") || recipeTags.contains("Vegan")) {
				foodCategory = "VEGAN";
			} else if (recipeName.contains("Jain") || recipeTags.contains("Jain")) {
				foodCategory = "JAIN";
			} else if (recipeName.contains("Egg ") || recipeTags.contains("Egg ")) {
				foodCategory = "EGGITARIAN";
			} else if (recipeName.contains("NonVeg") || recipeTags.contains("NonVeg")) {
				foodCategory = "NONVEGETARIAN";
			} else
				foodCategory = "VEGETARIAN";

			System.out.println("Recipe Category is :" + foodCategory);

		} catch (NoSuchElementException e) {
			System.out.println("Food category element not found for recipe: " + recipeName);
			foodCategory = "Unknown";
		}
	}

	private void getcuisineCategory() {
		try {

			if (recipeName.contains("Indian") || recipeTags.contains("Indian")) {
				cuisineCategory = "Indian";
			} else if (recipeName.contains("South Indian") || recipeTags.contains("South Indian")) {
				cuisineCategory = "South Indian";
			} else if (recipeName.contains("Rajasthani") || recipeTags.contains("Rajasthani")) {
				cuisineCategory = "Rajasthani";
			} else if (recipeName.contains("Punjabi") || recipeTags.contains("Punjabi")) {
				cuisineCategory = "Punjabi";
			} else if (recipeName.contains("Bengali") || recipeTags.contains("Bengali")) {
				cuisineCategory = "Bengali";
			} else if (recipeName.contains("Orissa") || recipeTags.contains("orissa")) {
				cuisineCategory = "Orissa";
			} else if (recipeName.contains("Gujarati") || recipeTags.contains("Gujarati")) {
				cuisineCategory = "Gujarati";
			} else if (recipeName.contains("Maharashtrian") || recipeTags.contains("Maharashtrian")) {
				cuisineCategory = "Maharashtrian";
			} else if (recipeName.contains("Andhra") || recipeTags.contains("Andhra")) {
				cuisineCategory = "Andhra";
			} else if (recipeName.contains("Kerala") || recipeTags.contains("Kerala")) {
				cuisineCategory = "Kerala";
			} else if (recipeName.contains("Goan") || recipeTags.contains("Goan")) {
				cuisineCategory = "Goan";
			} else if (recipeName.contains("Kashmiri") || recipeTags.contains("Kashmiri")) {
				cuisineCategory = "Kashmiri";
			} else if (recipeName.contains("Himachali") || recipeTags.contains("Himachali")) {
				cuisineCategory = "Himachali";
			} else if (recipeName.contains("Tamil nadu") || recipeTags.contains("Tamil nadu")) {
				cuisineCategory = "Tamil nadu";
			} else if (recipeName.contains("Karnataka") || recipeTags.contains("Karnataka")) {
				cuisineCategory = "Karnataka";
			} else if (recipeName.contains("Sindhi") || recipeTags.contains("Sindhi")) {
				cuisineCategory = "Sindhi";
			} else if (recipeName.contains("Chhattisgarhi") || recipeTags.contains("Chhattisgarhi")) {
				cuisineCategory = "Chhattisgarhi";
			} else if (recipeName.contains("Madhya pradesh") || recipeTags.contains("Madhya pradesh")) {
				cuisineCategory = "Madhya pradesh";
			} else if (recipeName.contains("Assamese") || recipeTags.contains("Assamese")) {
				cuisineCategory = "Assamese";
			} else if (recipeName.contains("Manipuri") || recipeTags.contains("Manipuri")) {
				cuisineCategory = "Manipuri";
			} else if (recipeName.contains("Tripuri") || recipeTags.contains("Tripuri")) {
				cuisineCategory = "Tripuri";
			} else if (recipeName.contains("Sikkimese") || recipeTags.contains("Sikkimese")) {
				cuisineCategory = "Sikkimese";
			} else if (recipeName.contains("Mizo") || recipeTags.contains("Mizo")) {
				cuisineCategory = "Mizo";
			} else if (recipeName.contains("Arunachali") || recipeTags.contains("Arunachali")) {
				cuisineCategory = "Arunachali";
			} else if (recipeName.contains("uttarakhand") || recipeTags.contains("uttarakhand")) {
				cuisineCategory = "uttarakhand";
			} else if (recipeName.contains("Haryanvi") || recipeTags.contains("Haryanvi")) {
				cuisineCategory = "Haryanvi";
			} else if (recipeName.contains("Awadhi") || recipeTags.contains("Awadhi")) {
				cuisineCategory = "Awadhi";
			} else if (recipeName.contains("Bihari") || recipeTags.contains("Bihari")) {
				cuisineCategory = "Bihari";
			} else if (recipeName.contains("Uttar pradesh") || recipeTags.contains("Uttar pradesh")) {
				cuisineCategory = "Uttar pradesh";
			} else if (recipeName.contains("Delhi") || recipeTags.contains("Delhi")) {
				cuisineCategory = "Delhi";
			} else
				cuisineCategory = "North Indian";
			System.out.println("Cuisine Category is :" + cuisineCategory);
		} catch (NoSuchElementException e) {
			System.out.println("Cuisine category element not found for recipe: " + recipeName);
			cuisineCategory = "Unknown";

		}

	}

	private void getPreparationTime() {
		try {
			preparationTime = getDriver().findElement(By.xpath("//time[@itemprop='prepTime']")).getText();
			System.out.println("Preperation Time is :" + preparationTime);
			// je.executeScript("window.scrollBy(0,200)");
		} catch (NoSuchElementException e) {
			preparationTime = "Unknown";
		}
	}

	private void getCookingTime() {
		try {
			cookingTime = getDriver().findElement(By.xpath("//time[@itemprop='cookTime']")).getText();
			System.out.println("Cooking Time is :" + cookingTime);
		} catch (NoSuchElementException e) {
			cookingTime = "Unknown";
		}
	}

	private void getRecipeDescription() {
		try {
			recipeDescription = getDriver().findElement(By.xpath("//span[@id='ctl00_cntrightpanel_lblDesc']"))
					.getText();
			System.out.println("Recipe Description: " + recipeDescription);
		} catch (NoSuchElementException e) {
			recipeDescription = "Unknown";
		}

	}

	private void getPreparationMethod() {
		try {
			preparationMethod = getDriver().findElement(By.xpath("//div[@id='ctl00_cntrightpanel_pnlRcpMethod']"))
					.getText();
			System.out.println("Preparation Method : " + preparationMethod);

		} catch (NoSuchElementException e) {
			preparationMethod = "Unknown";
		}

	}

	private void getNutrientValues() {
		try {
			nutrientValues = getDriver().findElement(By.xpath("//table[@id='rcpnutrients']/tbody")).getText();
			System.out.println("Nutrient Values: " + nutrientValues);
		} catch (NoSuchElementException e) {
			nutrientValues = "Unknown";
		}
	}

	private void getNoOfServings() {
		try {
			noOfServings = getDriver().findElement(By.id("ctl00_cntrightpanel_lblServes")).getText();
			System.out.println("No of Servings: " + noOfServings);
		} catch (NoSuchElementException e) {
			noOfServings = "Unknown";
		}
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
		//Add ingredients
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

	//Eliminate logic
	private List<String> matchIngredientsWithEliminateListforLFV() {
		List<WebElement> ingredientsList = getDriver()
				.findElements(By.xpath("//div[@id='rcpinglist']//span[@itemprop='recipeIngredient']//a/span"));
		List<String> webIngredients = new ArrayList<>();
		List<String> matchedIngredients = new ArrayList<>();

		for (WebElement ingredient : ingredientsList) {
			String ingredientName = ingredient.getText().trim().toLowerCase();
			webIngredients.add(ingredientName);
		}
		for (String webIngredient : webIngredients) {
			for (String excelIngredient : excelIngredients) {
				if (!webIngredient.contains(excelIngredient.toLowerCase())
						|| !excelIngredient.toLowerCase().contains(webIngredient)) {
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