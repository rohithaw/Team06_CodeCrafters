package com.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.utilities.ConfigReader;
import com.utilities.ExcelRead;
import com.utilities.ExcelWrite;

public class Recipes_AllergyPage {

	private WebDriver driver;
	private List<String> excelAllergyIngredients = new ArrayList<>();;
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
	String alphabetPageTitle = "";
	private static final Object lock = new Object();

	List<String> columnNamesAllergy = Collections.singletonList("Allergies (Bonus points)");

	@BeforeClass
	public void readExcel() throws Throwable {
		String userDir = System.getProperty("user.dir");
		String getPathread = ConfigReader.getGlobalValue("inputExcelPath");
		String inputDataPath = userDir + getPathread;

		try {
			excelAllergyIngredients = ExcelRead.getDataFromExcel("Filter -1 Allergies - Bonus Poi", columnNamesAllergy,
					inputDataPath);
			System.out.println("Allergy Ingredients List: " + excelAllergyIngredients);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void extractDataFromPages(WebDriver driver, String alphabetPageTitle) throws Throwable {
		this.driver = driver;
		extractRecipes();
	}

	private void extractRecipes() throws Throwable {
		int pageIndex = 0;

		while (true) {
			pageIndex++;
			System.out.println("Page Number: " + pageIndex);

			try {
				List<WebElement> recipeCards = driver.findElements(By.className("rcc_recipecard"));
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
			List<WebElement> recipeCards = driver.findElements(By.className("rcc_recipecard"));
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

				List<String> webIngredients = extractIngredients();
				List<String> unmatchedAllergyIngredients = getUnmatchedIngredients(excelAllergyIngredients,
						webIngredients);
				unmatchedAllergyIngredients = eliminateRedundantUnmatchedIngredients(unmatchedAllergyIngredients);
				String userDir = System.getProperty("user.dir");
				String getPathread = ConfigReader.getGlobalValue("outputExcelPath");
				String outputDataPath = userDir + getPathread;
				if (!unmatchedAllergyIngredients.isEmpty()) {
					try {
						synchronized (lock) {

							ExcelWrite.writeToExcel("Allergy", id, recipeName, recipeCategory, foodCategory,
									String.join(", ", unmatchedAllergyIngredients), preparationTime, cookingTime,
									recipeTags, noOfServings, cuisineCategory, recipeDescription, preparationMethod,
									nutrientValues, driver.getCurrentUrl(), outputDataPath);
						}
					} catch (IOException e) {
						System.out.println("Error writing to Excel: " + e.getMessage());
					}
				}

			}

			int maxRetries = 3;
			int retryCount = 0;
			while (retryCount < maxRetries) {
				try {
					driver.navigate().back();
					driver.findElement(By.className("rcc_recipecard")).isDisplayed();
					return; // Navigation successful, exit retry loop
				} catch (NoSuchElementException e) {
					System.out.println("Element not found, retrying...");
					retryCount++;
				}
			}
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Index " + index + " out of bounds for recipe cards");
		} catch (Exception e) {
			System.out.println("Error in processRecipe: " + e.getMessage());
		}
	}

	private List<String> extractIngredients() {
		List<WebElement> ingredientsList = driver
				.findElements(By.xpath("//div[@id='rcpinglist']//span[@itemprop='recipeIngredient']//a/span"));
		List<String> webIngredients = new ArrayList<>();

		for (WebElement ingredient : ingredientsList) {
			String ingredientName = ingredient.getText().trim().toLowerCase();
			webIngredients.add(ingredientName);
		}
		System.out.println("Ingredients: " + webIngredients);
		return webIngredients;
	}

	private List<String> getUnmatchedIngredients(List<String> excelIngredients, List<String> webIngredients) {
		Set<String> excelSet = new HashSet<>(excelIngredients);
		List<String> unmatchedIngredients = new ArrayList<>();

		for (String webIngredient : webIngredients) {
			boolean found = false;
			for (String excelIngredient : excelSet) {
				if (webIngredient.toLowerCase().contains(excelIngredient.toLowerCase())) {
					found = true;
					break;
				}
			}
			if (!found) {
				unmatchedIngredients.add(webIngredient);
			}
		}
		return unmatchedIngredients;
	}

	private List<String> eliminateRedundantUnmatchedIngredients(List<String> unmatchedIngredients) {
		return new ArrayList<>(new HashSet<>(unmatchedIngredients));
	}

	private boolean navigateToNextPage() {
		try {
			WebElement nextPageIndex = driver.findElement(By.xpath("//*[@class='rescurrpg']/following-sibling::a"));
			nextPageIndex.click();
			return true;
		} catch (Exception e) {
			System.out.println("No more pages for this alphabet");
			return false;
		}
	}

	private void getRecipeCategory() {

		try {
			recipeCategory = driver.findElement(By.xpath("//a[@itemprop='recipeCategory'][1]")).getText();
			if (recipeCategory.toLowerCase().contains("lunch") || recipeName.toLowerCase().contains("lunch")) {
				recipeCategory = "Lunch";
			} else if (recipeCategory.toLowerCase().contains("breakfast")
					|| recipeName.toLowerCase().contains("breakfast")) {
				recipeCategory = "Breakfast";
			} else if (recipeCategory.toLowerCase().contains("dinner") || recipeName.toLowerCase().contains("dinner")) {
				recipeCategory = "Dinner";
			} else if (recipeCategory.toLowerCase().contains("snack") || recipeName.toLowerCase().contains("snack")) {
				recipeCategory = "Snack";
			} else {
				recipeCategory = "NA";
			}

			System.out.println("Recipe Category is :" + recipeCategory);
		} catch (NoSuchElementException e) {
			System.out.println("Recipe category element not found for recipe: " + recipeName);
			recipeCategory = "Unknown";
		}
	}

	private void getTags() {
		try {
			recipeTags = driver.findElement(By.id("recipe_tags")).getText();
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
			} else if (recipeName.contains("Egg") || recipeTags.contains("Egg")) {
				foodCategory = "EGGITARIAN";
			} else if (recipeName.contains("NonVeg") || recipeTags.contains("NonVeg")) {
				foodCategory = "NONVEGETARIAN";
			} else if (recipeName.contains("Vegetarian") || recipeTags.contains("Vegetarian")) {
				foodCategory = "VEGETARIAN";
			} else {
				foodCategory = "NA";
			}

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
			} else if (recipeName.contains("North Indian") || recipeTags.contains("North Indian")) {
				cuisineCategory = "North Indian";
			} else {
				cuisineCategory = "NA";
			}
			System.out.println("Cuisine Category is :" + cuisineCategory);
		} catch (NoSuchElementException e) {
			System.out.println("Cuisine category element not found for recipe: " + recipeName);
			cuisineCategory = "Unknown";

		}

	}

	private void getPreparationTime() {
		try {
			preparationTime = driver.findElement(By.xpath("//time[@itemprop='prepTime']")).getText();
			System.out.println("Preperation Time is :" + preparationTime);
			// je.executeScript("window.scrollBy(0,200)");
		} catch (NoSuchElementException e) {
			preparationTime = "Unknown";
		}
	}

	private void getCookingTime() {
		try {
			cookingTime = driver.findElement(By.xpath("//time[@itemprop='cookTime']")).getText();
			System.out.println("Cooking Time is :" + cookingTime);
		} catch (NoSuchElementException e) {
			cookingTime = "Unknown";
		}
	}

	private void getRecipeDescription() {
		try {
			recipeDescription = driver.findElement(By.xpath("//span[@id='ctl00_cntrightpanel_lblDesc']")).getText();
			System.out.println("Recipe Description: " + recipeDescription);
		} catch (NoSuchElementException e) {
			recipeDescription = "Unknown";
		}

	}

	private void getPreparationMethod() {
		try {
			preparationMethod = driver.findElement(By.xpath("//div[@id='ctl00_cntrightpanel_pnlRcpMethod']")).getText();
			System.out.println("Preparation Method : " + preparationMethod);

		} catch (NoSuchElementException e) {
			preparationMethod = "Unknown";
		}

	}

	private void getNutrientValues() {
		try {
			nutrientValues = driver.findElement(By.xpath("//table[@id='rcpnutrients']/tbody")).getText();
			System.out.println("Nutrient Values: " + nutrientValues);
		} catch (NoSuchElementException e) {
			nutrientValues = "Unknown";
		}
	}

	private void getNoOfServings() {
		try {
			noOfServings = driver.findElement(By.id("ctl00_cntrightpanel_lblServes")).getText();
			System.out.println("No of Servings: " + noOfServings);
		} catch (NoSuchElementException e) {
			noOfServings = "Unknown";
		}
	}

}
