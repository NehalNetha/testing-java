package com.stca;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class CartContentsTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final long PAUSE_DURATION_MS = 1000; // Pause for 1 second

    // Helper method for pausing
    private void pause() {
        try {
            Thread.sleep(PAUSE_DURATION_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupt status
            System.err.println("Pause interrupted: " + e.getMessage());
        }
    }

    @BeforeTest
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        // options.addArguments("--headless"); 

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); 
    }

    @Test(description = "TC06: Verify that items added to the cart are correctly displayed on the cart page.")
    public void testCartContents() {
        // 1. Navigate & Login
        driver.get("https://www.saucedemo.com/");
        pause(); 

        driver.findElement(By.xpath("//input[@id='user-name']")).sendKeys("standard_user");
        pause(); 

        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("secret_sauce");
        pause(); 

        driver.findElement(By.xpath("//input[@id='login-button']")).click();

        // Wait for inventory page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='inventory_container']")));
        System.out.println("Successfully logged in and inventory page loaded.");
        pause(); 

        // 2. Store item details before adding to cart
        Map<String, String> itemsToAdd = new HashMap<>();
        
        // Use provided XPaths for inventory page items
        // Add Sauce Labs Backpack details
        String backpackName = driver.findElement(By.xpath("//*[@id=\"item_4_title_link\"]/div")).getText();
        String backpackPrice = driver.findElement(By.xpath("//*[@id=\"inventory_container\"]/div/div[1]/div[2]/div[2]/div")).getText();
        itemsToAdd.put(backpackName, backpackPrice);
        
        // Add Sauce Labs Bike Light details
        String bikeLightName = driver.findElement(By.xpath("//*[@id=\"item_0_title_link\"]/div")).getText();
        String bikeLightPrice = driver.findElement(By.xpath("//*[@id=\"inventory_container\"]/div/div[2]/div[2]/div[2]/div")).getText();
        itemsToAdd.put(bikeLightName, bikeLightPrice);
        
        System.out.println("Stored details for items to be added: " + itemsToAdd.keySet());
        pause();

        // 3. Add the items to cart
        // Using specific IDs which are generally reliable
        driver.findElement(By.xpath("//button[@id='add-to-cart-sauce-labs-backpack']")).click();
        System.out.println("Added Sauce Labs Backpack to cart.");
        pause();
        
        driver.findElement(By.xpath("//button[@id='add-to-cart-sauce-labs-bike-light']")).click();
        System.out.println("Added Sauce Labs Bike Light to cart.");
        pause();
        
        // 4. Verify cart badge shows correct count - Use provided XPath
        WebElement cartBadge = driver.findElement(By.xpath("//*[@id=\"shopping_cart_container\"]/a/span"));
        Assert.assertEquals(cartBadge.getText(), "2", "Cart badge count does not match the number of items added.");
        System.out.println("Verified cart badge shows: 2");
        pause();
        
        // 5. Click on the cart icon to navigate to cart page - Use provided XPath
        driver.findElement(By.xpath("//*[@id=\"shopping_cart_container\"]/a")).click();
        
        // Wait for cart page to load
        wait.until(ExpectedConditions.urlContains("/cart.html"));
        System.out.println("Successfully navigated to cart page.");
        pause();
        
        // 6. Verify cart contents
        // Check if each added item is in the cart with correct name and price
        for (Map.Entry<String, String> item : itemsToAdd.entrySet()) {
            String itemName = item.getKey();
            String itemPrice = item.getValue();
            
            // Define base XPath for the cart item row based on item name
            String cartItemXPath = "//div[@class='cart_item']//div[@class='inventory_item_name' and text()='" + itemName + "']/ancestor::div[@class='cart_item']";

            // Verify item name exists in cart (using the name element within the row)
            WebElement nameElement = driver.findElement(By.xpath(cartItemXPath + "//div[@class='inventory_item_name']"));
            Assert.assertEquals(nameElement.getText(), itemName, "Item name in cart does not match: " + itemName);
            
            // Verify item price is correct (relative to the cart item row)
            WebElement priceElement = driver.findElement(By.xpath(cartItemXPath + "//div[@class='inventory_item_price']"));
            Assert.assertEquals(priceElement.getText(), itemPrice, "Item price in cart does not match for: " + itemName);
            
            // Verify Remove button exists for the item (relative to the cart item row)
            WebElement removeButton = driver.findElement(By.xpath(cartItemXPath + "//button[starts-with(@id, 'remove-')]"));
            Assert.assertTrue(removeButton.isDisplayed(), "Remove button not found or not visible for: " + itemName);
            Assert.assertEquals(removeButton.getText().toUpperCase(), "REMOVE", "Remove button text is incorrect for: " + itemName);
            
            System.out.println("Verified item in cart: " + itemName + " with price: " + itemPrice);
        }
        
        System.out.println("All items verified in cart with correct details and remove buttons.");
        pause();
    }

    @AfterTest
    public void tearDown() {
        pause(); // Pause before closing
        if (driver != null) {
            driver.quit();
        }
        System.out.println("Browser closed.");
    }
}