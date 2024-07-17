package com.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.tests.A_ZScrapedRecipesLFV;
import com.utilities.ConfigReader;
import com.utilities.ExcelRead;
import com.utilities.ExcelValueCheck;
import com.utilities.ExcelWrite;
import com.utilities.LoggerLoad;

public class Recipes_LFVPage extends A_ZScrapedRecipesLFV {

	private WebDriver driver;
	private List<String> excelVeganIngredients;
	private List<String> excelNotFullyVeganIngredients;
<<<<<<< HEAD
	private List<String> excelEliminateIngredients;
	private List<String> excelRecipeToAvoidList;
=======
	private List<String> excelEliminateIngredients = new ArrayList<>();
>>>>>>> 43041c1675fb3c010b261d6c0fccec85619c4db7
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
	private List<String> excelRecipeToAvoidList;
	private static final Object lock = new Object();

	List<String> columnNamesVegan = Collections.singletonList("Add");
	List<String> columnNamesNotFullyVegan = Collections.singletonList("To Add ( if not fully vegan)");
	List<String> columnNamesEliminate = Collections.singletonList("Eliminate");
	List<String> columnNamesRecipeToAvoid = Collections.singletonList("Recipes to avoid");

<<<<<<< HEAD
=======
	
>>>>>>> 43041c1675fb3c010b261d6c0fccec85619c4db7
	@BeforeClass
	public void readExcel() throws Throwable {
		String userDir = System.getProperty("user.dir");
		String getPathread = ConfigReader.getGlobalValue("inputExcelPath");
		String inputDataPath = userDir + getPathread;

		try {
<<<<<<< HEAD
      synchronized (lock) {
			excelVeganIngredients = ExcelRead.getDataFromExcel("Final list for LFV Elimination ", columnNamesVegan, inputDataPath);
			excelNotFullyVeganIngredients = ExcelRead.getDataFromExcel("Final list for LFV Elimination ", columnNamesNotFullyVegan, inputDataPath);
			excelEliminateIngredients = ExcelRead.getDataFromExcel("Final list for LFV Elimination ",
					columnNamesEliminate, inputDataPath);
			excelRecipeToAvoidList = ExcelRead.getDataFromExcel("Final list for LFV Elimination ",columnNamesRecipeToAvoid, inputDataPath);
			System.out.println("Add Ingredients List: " + excelVeganIngredients);
			System.out.println("Not Fully Vegan Ingredients List: " + excelNotFullyVeganIngredients);
			System.out.println("Recipe to Avoid List: " + excelRecipeToAvoidList);
      }
    } catch (IOException e) {
=======
			synchronized (lock) {
				excelVeganIngredients = ExcelRead.getDataFromExcel("Final list for LFV Elimination ", columnNamesVegan,
						inputDataPath);
				excelNotFullyVeganIngredients = ExcelRead.getDataFromExcel("Final list for LFV Elimination ",
						columnNamesNotFullyVegan, inputDataPath);
				excelEliminateIngredients = ExcelRead.getDataFromExcel("Final list for LFV Elimination ",
						columnNamesEliminate, inputDataPath);
				excelRecipeToAvoidList = ExcelRead.getDataFromExcel("Final list for LFV Elimination ",
						columnNamesRecipeToAvoid, inputDataPath);
				LoggerLoad.info("Recipe to Avoid List: " + excelRecipeToAvoidList);

				LoggerLoad.info("Add Ingredients List: " + excelVeganIngredients);
				LoggerLoad.info("Not Fully Vegan Ingredients List: " + excelNotFullyVeganIngredients);
				LoggerLoad.info("Eliminate Ingredients List: " + excelEliminateIngredients);
			}
		} catch (IOException e) {
>>>>>>> 43041c1675fb3c010b261d6c0fccec85619c4db7
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
			LoggerLoad.info("Page Number: " + pageIndex);

			try {
				List<WebElement> recipeCards = driver.findElements(By.className("rcc_recipecard"));
				LoggerLoad.info("No_of_recipes: " + recipeCards.size());

				for (int j = 0; j < recipeCards.size(); j++) {
					processRecipe(j);
				}
			} catch (Exception e) {
				LoggerLoad.info("Error while extracting data: " + e.getMessage());
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
<<<<<<< HEAD
				System.out.println("Recipe Id: " + id);

=======
				LoggerLoad.info("Recipe Id: " + id);
>>>>>>> 43041c1675fb3c010b261d6c0fccec85619c4db7

				// Getting recipe name
				WebElement recipeNameElement = recipeCard.findElement(By.xpath(".//span[@class='rcc_recipename']/a"));
				recipeName = recipeNameElement.getText();
				LoggerLoad.info("Recipe Name: " + recipeName);

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
				List<String> matchedVeganIngredients = matchIngredientsWithExcel(excelVeganIngredients, webIngredients);
<<<<<<< HEAD
				List<String> matchedNotFullyVeganIngredients = matchIngredientsWithExcel(excelNotFullyVeganIngredients, webIngredients);
				List<String> unmatchedLFVIngredients = matchIngredientsWithEliminateListforLFV(excelEliminateIngredients);
				List<String> RecipeToAvoidFood = matchwithRecipeToAvoid(excelRecipeToAvoidList);
=======
				List<String> matchedNotFullyVeganIngredients = matchIngredientsWithExcel(excelNotFullyVeganIngredients,
						webIngredients);
				List<String> unmatchedLFVIngredients = getUnmatchedIngredients(excelEliminateIngredients,
						webIngredients);
				unmatchedLFVIngredients = eliminateRedundantUnmatchedIngredients(unmatchedLFVIngredients);
>>>>>>> 43041c1675fb3c010b261d6c0fccec85619c4db7

				List<String> RecipeToAvoidFood = matchwithRecipeToAvoid(excelRecipeToAvoidList);
				String userDir = System.getProperty("user.dir");
				String getPathread = ConfigReader.getGlobalValue("outputExcelPath");
				String outputDataPath = userDir + getPathread;

				boolean recipeExistsinAddVeganConditions = ExcelValueCheck.recipeExistsInExcelCheck("LFVAdd", recipeID,
						outputDataPath);
				boolean recipeExistsinAddNotVeganConditions = ExcelValueCheck
						.recipeExistsInExcelCheck("LFVAddNotFullyVegan", recipeID, outputDataPath);
				boolean recipeNotExistsInEliminateConditions = ExcelValueCheck
						.recipeExistsInExcelCheck("LFVEliminate", recipeID, outputDataPath);
				boolean recipeExistsInRecipeToAvoidConditions = ExcelValueCheck
						.recipeExistsInExcelCheck("LFVRecipesToAvoid", recipeID, outputDataPath);
				
				if (recipeExistsinAddVeganConditions || recipeExistsinAddNotVeganConditions || recipeNotExistsInEliminateConditions || recipeExistsInRecipeToAvoidConditions ) {
					LoggerLoad.info("Recipe already exists in excel: " + recipeID);
					return; // Exit the method to avoid writing duplicate recipes
				}

				if (recipeName.contains("Vegan") || recipeTags.contains("Vegan")) {
					if (!matchedVeganIngredients.isEmpty()) {
						try {
							synchronized (lock) {
								ExcelWrite.writeToExcel("LFVAdd", id, recipeName, recipeCategory, foodCategory,
										String.join(", ", matchedVeganIngredients), preparationTime, cookingTime,
										recipeTags, noOfServings, cuisineCategory, recipeDescription, preparationMethod,
										nutrientValues, driver.getCurrentUrl(), outputDataPath);
							}
						} catch (IOException e) {
							LoggerLoad.info("Error writing to Excel: " + e.getMessage());
						}
					}
				}
				if (!matchedNotFullyVeganIngredients.isEmpty()) {
					try {
						synchronized (lock) {
							ExcelWrite.writeToExcel("LFVAddNotFullyVegan", id, recipeName, recipeCategory, foodCategory,
									String.join(", ", matchedNotFullyVeganIngredients), preparationTime, cookingTime,
									recipeTags, noOfServings, cuisineCategory, recipeDescription, preparationMethod,
									nutrientValues, driver.getCurrentUrl(), outputDataPath);
						}
					} catch (IOException e) {
						LoggerLoad.info("Error writing to Excel: " + e.getMessage());
					}
				}
				if (!unmatchedLFVIngredients.isEmpty()) {
					try {
						synchronized (lock) {
							ExcelWrite.writeToExcel("LFVEliminate", id, recipeName, recipeCategory, foodCategory,
									String.join(", ", unmatchedLFVIngredients), preparationTime, cookingTime,
									recipeTags, noOfServings, cuisineCategory, recipeDescription, preparationMethod,
									nutrientValues, driver.getCurrentUrl(), outputDataPath);
						}
					} catch (IOException e) {
						LoggerLoad.info("Error writing to Excel: " + e.getMessage());
					}
				}
				
				if (!RecipeToAvoidFood.isEmpty()) {
					try {
						ExcelWrite.writeToExcel("LFVRecipesToAvoid", id, recipeName, recipeCategory, foodCategory,
								String.join(", ", webIngredients), preparationTime, cookingTime, recipeTags,
								noOfServings, cuisineCategory, recipeDescription, preparationMethod, nutrientValues,
								driver.getCurrentUrl(), outputDataPath);
					} catch (IOException e) {
						System.out.println("Error writing to Excel: " + e.getMessage());
					}
				}

				if (!RecipeToAvoidFood.isEmpty()) {
					try {
						synchronized (lock) {
							ExcelWrite.writeToExcel("LFVRecipesToAvoid", id, recipeName, recipeCategory, foodCategory,
									String.join(", ", webIngredients), preparationTime, cookingTime, recipeTags,
									noOfServings, cuisineCategory, recipeDescription, preparationMethod, nutrientValues,
									driver.getCurrentUrl(), outputDataPath);
						}
					} catch (IOException e) {
						LoggerLoad.info("Error writing to Excel: " + e.getMessage());
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
						LoggerLoad.info("Element not found, retrying...");
						retryCount++;
					}
				}
			} else {
				LoggerLoad.info("Index " + index + " out of bounds for recipe cards");
			}
		} catch (IndexOutOfBoundsException e) {
			LoggerLoad.info("Index " + index + " out of bounds for recipe cards");
		} catch (Exception e) {
			LoggerLoad.info("Error in processRecipe: " + e.getMessage());
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
		LoggerLoad.info("Ingredients: " + webIngredients);
		return webIngredients;
	}

	private List<String> matchIngredientsWithExcel(List<String> excelIngredients, List<String> webIngredients) {
		List<String> matchedIngredients = new ArrayList<>();

		// Match ingredients with Excel ingredients list (partial matches allowed)
		for (String webIngredient : webIngredients) {
			for (String excelIngredient : excelIngredients) {
				if (webIngredient.contains(excelIngredient.toLowerCase())
						|| excelIngredient.toLowerCase().contains(webIngredient)) {
					LoggerLoad.info("Ingredient match found: Web Ingredient - " + webIngredient
							+ ", Excel Ingredient - " + excelIngredient);
					matchedIngredients.add(webIngredient);
				}
			}
		}
		return matchedIngredients;

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
	
	//Recipe To Avoid Logic 
	
		public List<String> matchwithRecipeToAvoid(List<String> excelIngredients) {
			String receipeName = driver.findElement(By.xpath("//span[@id='ctl00_cntrightpanel_lblRecipeName']")).getText();
			String tag = driver.findElement(By.id("recipe_tags")).getText();
			String taglower = tag.toLowerCase();
			String receipeNamelower = receipeName.toLowerCase();
			List<String> recipeToAvoid = new ArrayList<>();
			
			for (String excelIngredient : excelIngredients) {
	            // excel ingredient for case-insensitive matching
	            String excelIngredientLower = excelIngredient.toLowerCase();
	            // Check if the tags contains excel values
	            //&& receipeName.toLowerCase().contains(excelIngredientLower)
	            if (taglower.contains(excelIngredientLower)|| excelIngredient.toLowerCase().contains(taglower) && receipeName.contains(excelIngredientLower) || 
	            		excelIngredient.toLowerCase().contains(receipeNamelower)) {
	                System.out.println("Match found: " + excelIngredient + " in Tag/Name.");
	                recipeToAvoid.add(excelIngredient); // Add the matched ingredient, not the whole preparation method text
	            }  
	        
			}
			return recipeToAvoid;
		}
		

	private List<String> eliminateRedundantUnmatchedIngredients(List<String> unmatchedIngredients) {
		return new ArrayList<>(new HashSet<>(unmatchedIngredients));
	}

	public List<String> matchwithRecipeToAvoid(List<String> excelIngredients) {
		List<String> matchedIngredients = new ArrayList<>();
		// Extract tags from the web page
		String tagText = driver.findElement(By.id("recipe_tags")).getText().toLowerCase();
		String[] tagArray = tagText.split(",\\s*"); // Split by comma and trim whitespace
		List<String> tags = Arrays.asList(tagArray);
		// Match tags with Excel ingredients list (partial matches allowed)
		for (String tag : tags) {
			for (String excelIngredient : excelIngredients) {
				if (normalize(tag).contains(normalize(excelIngredient))
						|| normalize(excelIngredient).contains(normalize(tag))) {
					LoggerLoad.info("Match found: " + excelIngredient + " in tags.");
					matchedIngredients.add(excelIngredient);
					// Assuming you want to add all matching ingredients
				}
			}
		}
		return matchedIngredients;
	}

	private String normalize(String text) {
		return text.toLowerCase().trim();
	}

	private boolean navigateToNextPage() {
		try {
			WebElement nextPageIndex = driver.findElement(By.xpath("//*[@class='rescurrpg']/following-sibling::a"));
			nextPageIndex.click();
			return true;
		} catch (Exception e) {
			LoggerLoad.info("No more pages for this alphabet");
			return false;
		}
	}

	private void getRecipeCategory() {

		try {
			// je.executeScript("window.scrollBy(0,200)");
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

			LoggerLoad.info("Recipe Category is :" + recipeCategory);
		} catch (NoSuchElementException e) {
			LoggerLoad.info("Recipe category element not found for recipe: " + recipeName);
			recipeCategory = "Unknown";
		}
	}

	private void getTags() {
		try {
			recipeTags = driver.findElement(By.id("recipe_tags")).getText();
			LoggerLoad.info("Tags are : " + recipeTags);
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

			LoggerLoad.info("Recipe Category is :" + foodCategory);

		} catch (NoSuchElementException e) {
			LoggerLoad.info("Food category element not found for recipe: " + recipeName);
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
			LoggerLoad.info("Cuisine Category is :" + cuisineCategory);
		} catch (NoSuchElementException e) {
			LoggerLoad.info("Cuisine category element not found for recipe: " + recipeName);
			cuisineCategory = "Unknown";

		}

	}

	private void getPreparationTime() {
		try {
			preparationTime = driver.findElement(By.xpath("//time[@itemprop='prepTime']")).getText();
			LoggerLoad.info("Preperation Time is :" + preparationTime);
			// je.executeScript("window.scrollBy(0,200)");
		} catch (NoSuchElementException e) {
			preparationTime = "Unknown";
		}
	}

	private void getCookingTime() {
		try {
			cookingTime = driver.findElement(By.xpath("//time[@itemprop='cookTime']")).getText();
			LoggerLoad.info("Cooking Time is :" + cookingTime);
		} catch (NoSuchElementException e) {
			cookingTime = "Unknown";
		}
	}

	private void getRecipeDescription() {
		try {
			recipeDescription = driver.findElement(By.xpath("//span[@id='ctl00_cntrightpanel_lblDesc']")).getText();
			LoggerLoad.info("Recipe Description: " + recipeDescription);
		} catch (NoSuchElementException e) {
			recipeDescription = "Unknown";
		}

	}

	private void getPreparationMethod() {
		try {
			preparationMethod = driver.findElement(By.xpath("//div[@id='ctl00_cntrightpanel_pnlRcpMethod']")).getText();
			LoggerLoad.info("Preparation Method : " + preparationMethod);

		} catch (NoSuchElementException e) {
			preparationMethod = "Unknown";
		}

	}

	private void getNutrientValues() {
		try {
			nutrientValues = driver.findElement(By.xpath("//table[@id='rcpnutrients']/tbody")).getText();
			LoggerLoad.info("Nutrient Values: " + nutrientValues);
		} catch (NoSuchElementException e) {
			nutrientValues = "Unknown";
		}
	}

	private void getNoOfServings() {
		try {
			noOfServings = driver.findElement(By.id("ctl00_cntrightpanel_lblServes")).getText();
			LoggerLoad.info("No of Servings: " + noOfServings);
		} catch (NoSuchElementException e) {
			noOfServings = "Unknown";
		}
	}
}