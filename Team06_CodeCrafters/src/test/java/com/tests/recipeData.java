package com.tests;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class recipeData {
    private static ThreadLocal<WebDriver> driverThreadLocal = ThreadLocal.withInitial(() -> {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-popup-blocking");
        options.addArguments("-–disable-notifications");
        options.addArguments("-–disable-extensions");
        options.addArguments("--blink-settings=imagesEnabled=false");
        // options.addArguments("--headless");
        return new ChromeDriver(options);
    });

    private WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    @BeforeClass
    public void setUp() {
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

    @DataProvider(name = "categoryDataProvider", parallel = false)
    public Object[][] categoryDataProvider() {
        return new Object[][] { { "Let your kids cook" } };
    }

    @Test(dataProvider = "categoryDataProvider")
    public void extractRecipes(String categoryName) {
        FluentWait<WebDriver> wait = new FluentWait<>(getDriver()).withTimeout(Duration.ofSeconds(60))
                .pollingEvery(Duration.ofSeconds(5)).ignoring(NoSuchElementException.class);

        // Navigate to the category page
        WebElement categoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText(categoryName)));
        categoryLink.click();
        System.out.println("Clicked on category: " + categoryName);
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
                parseDataOnPage();
            } catch (IOException e) {
                System.out.println("Error while parsing data on page: " + e.getMessage());
                break;
            }
            try {
                WebElement nextPageIndex = getDriver()
                        .findElement(By.xpath("//*[@class='rescurrpg']/following-sibling::a"));
                nextPageIndex.click();
            } catch (Exception e) {
                System.out.println("No more pages for this category");
                break;
            }
        }
    }

    public void parseDataOnPage() throws IOException {
        FluentWait<WebDriver> wait = new FluentWait<>(getDriver()).withTimeout(Duration.ofSeconds(60))
                .pollingEvery(Duration.ofSeconds(5)).ignoring(NoSuchElementException.class);

        List<WebElement> raw_recipes = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("rcc_recipename")));
        ArrayList<String> links = new ArrayList<>(14);

        for (WebElement recipeElement : raw_recipes) {
            String recipeLink = recipeElement.findElement(By.tagName("a")).getAttribute("href");
            links.add(recipeLink);
        }

        for (String link : links) {
            getDriver().get(link);

 Document doc = Jsoup.parse(getDriver().getPageSource());
            
            // Extracting detailed information using Jsoup
            String recipeID = extractNumberFromURL(link);//working
            
            String recipeName = extractText(doc, "#ctl00_cntrightpanel_lblRecipeName");//working
            
            String recipeTag = extractText(doc, "[id=recipe_tags]");
            String recipeCategory = extractText(doc, "[itemprop=recipeCategory]");
            String prepTime = extractText(doc, "[itemprop=prepTime]");//working
            String cookTime = extractText(doc, "[itemprop=cookTime]");//working
            //String tag = extractText(doc, "#ctl00_cntrightpanel_lblTags");
            String servings = extractText(doc, "[itemprop=recipeYield]");
            //recipe description logic printing below
            Element recipeDescription = doc.selectFirst("span#ctl00_cntrightpanel_lblDesc");
            String recipeDesc = "";
            if (recipeDescription != null) {
                recipeDesc = recipeDescription.text();
                if (recipeDesc.length() > 100) {
                    recipeDesc = recipeDesc.substring(0, 100);
                }
            }
            //ingredients logic-- printing below
            Elements ingredients = doc.select("span[itemprop='recipeIngredient'] > a");
			 StringBuilder ingreList = new StringBuilder();
	            for (Element ingredient : ingredients) {
	                ingreList.append(",").append(ingredient.text().toLowerCase());
	            }
	            if (ingreList.length() > 0) {
	                ingreList.deleteCharAt(0);
	            }

            String preparationMethod = extractText(doc, "#ctl00_cntrightpanel_pnlRcpMethod");//working
            String nutrientValues = extractText(doc, "#rcpnutrients"); //working

            
            System.out.println("Recipe ID: " + recipeID);
            System.out.println("Recipe Name: " + recipeName);
            
           
            String categorizedRecipe = categorizeRecipe(recipeCategory, doc);
            System.out.println("Recipe Category: " + categorizedRecipe);
           
			String foodCategory = categorizeFoodType(recipeName, recipeTag);
			String cuisineCategory = categoryCuisine(recipeName,recipeTag );
			System.out.println("Food Category: " + foodCategory);
          
			
	        System.out.println("Ingredients: " + ingreList.toString());
            System.out.println("Preparation Time: " + prepTime);
            System.out.println("Cooking Time: " + cookTime);
            System.out.println("Tag: " + recipeTag);
            System.out.println("No of Servings: " + servings);
            System.out.println("Cuisine Category: " + cuisineCategory);
            System.out.println("Recipe Description is: " + recipeDesc);
            System.out.println("Preparation Method: " + preparationMethod);
            System.out.println("Nutrient Values: " + nutrientValues);
            System.out.println("Recipe URL: " + link);//working

            // Go back to the previous page-- loop through recipes
            getDriver().navigate().back();
            raw_recipes = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("rcc_recipename")));
        }
    }
    

    private String extractText(Document doc, String cssQuery) {
        Element element = doc.selectFirst(cssQuery);
        return element != null ? element.text().trim() : "";
    }
 
    
    private String extractIngredients(Document doc) {
        //Thread.sleep(500);
        Elements ingredientsElements = doc.select("#rcpinglist .rcpinglist");
        List<String> ingredients = new ArrayList<>();
        for (Element ingredient : ingredientsElements) {
            ingredients.add(ingredient.text().trim());
        }
        return String.join(", ", ingredients);
    }
    private static String extractNumberFromURL(String url) {
        // Define the regex pattern to match numbers
        Pattern pattern = Pattern.compile("\\d+");
        
        // Create a matcher for the URL
        Matcher matcher = pattern.matcher(url);
        
        // Find and return the first match (number) found
        if (matcher.find()) {
            return matcher.group();
        } else {
            return ""; // Return an empty string if no number is found
        }
    }
    private static String categorizeRecipe(String recipeCategory, Document doc) {
        // Convert the entire document text to lowercase for keyword checking
        String docText = doc.text().toLowerCase();
        
        // Check for keywords in both recipeCategory and the entire document text
        if (recipeCategory.toLowerCase().contains("lunch") || docText.contains("lunch")) {
            return "Lunch";
        } else if (recipeCategory.toLowerCase().contains("breakfast") || docText.contains("breakfast")) {
            return "Breakfast";
        } else if (recipeCategory.toLowerCase().contains("dinner") || docText.contains("dinner")) {
            return "Dinner";
        } else {
            return "Snack";
        }
    }
    private static String categorizeFoodType(String recipeName, String tag) {
        if (recipeName.contains("Vegan") || tag.contains("Vegan")) {
            return "Vegan";
        } else if (recipeName.contains("Jain") || tag.contains("Jain")) {
            return "Jain";
        } else if (recipeName.contains("Egg ") || tag.contains("Egg ")) {
            return "Eggitarian";
        } else if (recipeName.contains("NonVeg") || tag.contains("NonVeg")) {
            return "Non-veg";
        } else {
            return "Vegetarian";
        }
    }
    private static String categoryCuisine(String recipeName, String tag) {
        if (recipeName.contains("Indian") || tag.contains("Indian")) {
            return "Indian";
        } else if (recipeName.contains("South Indian") || tag.contains("South Indian")) {
            return "South Indian";
        } else if (recipeName.contains("Rajathani ") || tag.contains("Rajathani ")) {
            return "Rajathani";
        } else if (recipeName.contains("Punjabi") || tag.contains("Punjabi")) {
            return "Punjabi";
        } else if (recipeName.contains("Bengali") || tag.contains("Bengali")) {
            return "Bengali";
        }else if (recipeName.contains("Orissa") || tag.contains("Orissa")) {
            return "Orissa";
        }else if (recipeName.contains("Gujarati") || tag.contains("Gujarati")) {
            return "Gujarati";
        }else if (recipeName.contains("Maharashtrian") || tag.contains("Maharashtrian")) {
            return "Maharashtrian";
        }else if (recipeName.contains("Andhra") || tag.contains("Andhra")) {
            return "Andhra";
        }else if (recipeName.contains("Kerala") || tag.contains("Kerala")) {
            return "Kerala";
        }else if (recipeName.contains("Goan") || tag.contains("Goan")) {
            return "Goan";
        }else if (recipeName.contains("Kashmiri") || tag.contains("Kashmiri")) {
            return "Kashmiri";
        }else if (recipeName.contains("Himachali") || tag.contains("Himachali")) {
            return "Himachali";
        }else if (recipeName.contains("Tamil nadu") || tag.contains("Tamil nadu")) {
            return "Tamil nadu";
        }else if (recipeName.contains("Karnataka") || tag.contains("Karnataka")) {
            return "Karnataka";
        }else if (recipeName.contains("Sindhi") || tag.contains("Sindhi")) {
            return "Sindhi";
        }else if (recipeName.contains("Chhattisgarhi") || tag.contains("Chhattisgarhi")) {
            return "Chhattisgarhi";
        }else if (recipeName.contains("Madhya pradesh") || tag.contains("Madhya pradesh")) {
            return "Madhya pradesh";
        }else if (recipeName.contains("Assamese") || tag.contains("Assamese")) {
            return "Assamese";
        }else if (recipeName.contains("Manipuri") || tag.contains("Manipuri")) {
            return "Manipuri";
        }else if (recipeName.contains("Tripuri") || tag.contains("Tripuri")) {
            return "Tripuri";
        }else if (recipeName.contains("Sikkimese") || tag.contains("Sikkimese")) {
            return "Sikkimese";
        }else if (recipeName.contains("Mizo") || tag.contains("Mizo")) {
            return "Mizo";
        }else if (recipeName.contains("Arunachali") || tag.contains("Arunachali")) {
        	return "Arunachali";
        }else if (recipeName.contains("uttarakhand") || tag.contains("uttarakhand"))
		{
			return "uttarakhand";
		}
		else if (recipeName.contains("Haryanvi") || tag.contains("Haryanvi"))
		{
			return "Haryanvi";
		}
		else if (recipeName.contains("Awadhi") || tag.contains("Awadhi"))
		{
			return "Awadhi";
		}
		else if (recipeName.contains("Bihari") || tag.contains("Bihari"))
		{
			return "Bihari";
		}
		else if (recipeName.contains("Uttar pradesh") || tag.contains("Uttar pradesh"))
		{
			return"Uttar pradesh";
		}
		else if (recipeName.contains("Delhi") || tag.contains("Delhi"))
		{
			return "Delhi";
		}
		else
			return "North Indian";
	    
        
        }
    }

    