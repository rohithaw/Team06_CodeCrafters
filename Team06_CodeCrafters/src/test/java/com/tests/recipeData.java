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
            //String recipeID = extractRecipeID(doc);
            String recipeName = extractText(doc, "#ctl00_cntrightpanel_lblRecipeName");//working
            //String recipeCategory = extractText(doc, "#ctl00_cntrightpanel_lblCategory");
            //String foodCategory = extractText(doc, "#ctl00_cntrightpanel_lblFoodCategory");
            //String ingredients = extractIngredients(doc);
            String ingredients = extractText(doc,"[itemprop=recipeIngredient]");
           // String receipeCategory = driver.findElement(By.xpath("//a[@itemprop='recipeCategory'][1]")).getText();
            //String recipeCate= extractText(doc,"[itemprop=recipeCategory]");
            String recipeTag = extractText(doc, "[id=recipe_tags]");
            String recipeCategory = extractText(doc, "[itemprop=recipeCategory]");
            String prepTime = extractText(doc, "[itemprop=prepTime]");//working
            String cookTime = extractText(doc, "[itemprop=cookTime]");//working
            //String tag = extractText(doc, "#ctl00_cntrightpanel_lblTags");
            String servings = extractText(doc, "[itemprop=recipeYield]");
            String cuisineCategory = extractText(doc, "#ctl00_cntrightpanel_lblCuisine");
            String recipeDescription = extractText(doc, "#recipe-desc");
            String preparationMethod = extractText(doc, "#ctl00_cntrightpanel_pnlRcpMethod");//working
            String nutrientValues = extractText(doc, "#rcpnutrients"); //working

            // Print or store the extracted information
            System.out.println("Recipe ID: " + recipeID);
            System.out.println("Recipe Name: " + recipeName);
            
           // String recipeCategory = null;
			//System.out.println("Recipe Category: " + recipeCategory);
            String categorizedRecipe = categorizeRecipe(recipeCategory, doc);
            System.out.println("Recipe Category: " + categorizedRecipe);
            //String tag = null;
			String foodCategory = categorizeFoodType(recipeName, recipeTag);
			System.out.println("Food Category: " + foodCategory);
            System.out.println("Ingredients: " + ingredients);
            System.out.println("Preparation Time: " + prepTime);
            System.out.println("Cooking Time: " + cookTime);
            System.out.println("Tag: " + recipeTag);
            System.out.println("No of Servings: " + servings);
            System.out.println("Cuisine Category: " + cuisineCategory);
            System.out.println("Recipe Description: " + recipeDescription);
            System.out.println("Preparation Method: " + preparationMethod);
            System.out.println("Nutrient Values: " + nutrientValues);
            System.out.println("Recipe URL: " + link);//working

            // Go back to the previous page
            getDriver().navigate().back();
            raw_recipes = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("rcc_recipename")));
        }
    }

//    private String extractRecipeID(Document doc) {
//        Elements elements = doc.select(".rcc_rcpno");
//        System.out.println("Number of elements found for recipe ID: " + elements.size());
//        for (Element element : elements) {
//            String fullText = element.text().trim();
//            System.out.println("Extracted text for recipe ID: " + fullText);
//            Pattern pattern = Pattern.compile("\\d+");
//            Matcher matcher = pattern.matcher(fullText);
//            if (matcher.find()) {
//                return matcher.group();
//            }
//        }
//        return "";
//    }

    

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
}

