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

public class CartTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final long PAUSE_DURATION_MS = 1500; // Pause for 1.5 seconds

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
        // Consider adding headless mode for faster execution if needed:
        // options.addArguments("--headless"); 

        driver = new ChromeDriver(options);
        // Initialize WebDriverWait (e.g., wait up to 10 seconds) //*[@id='login-button']
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); 
    }

    @Test(description = "TC04: Verify adding a single item (Sauce Labs Backpack) to the cart.")
    public void testAddSingleItemToCart() {
        // 1. Navigate & Login
        driver.get("https://www.saucedemo.com/");
        pause(); // Pause after navigation

        driver.findElement(By.xpath("//*[@id='user-name']")).sendKeys("standard_user");
        pause(); // Pause after entering username

        driver.findElement(By.xpath("//*[@id='password']")).sendKeys("secret_sauce");
        pause(); // Pause after entering password

        driver.findElement(By.xpath("//*[@id='login-button']")).click();

        // Wait for inventory page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='inventory_container']")));
        System.out.println("Successfully logged in and inventory page loaded.");
        pause(); // Pause after login and page load

        // 2. Locate the specific item (Sauce Labs Backpack) and its add button
        // Using XPath to find the specific item container and then the button within it
        String backpackItemXPath = "//*[@id='inventory_container']/div/div[1]";
        WebElement backpackContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(backpackItemXPath)));
        WebElement addButton = backpackContainer.findElement(By.xpath("//*[@id='add-to-cart-sauce-labs-backpack']"));
        
        System.out.println("Located Sauce Labs Backpack Add to Cart button.");
        pause(); // Pause after locating item

        // 3. Click the "Add to cart" button
        addButton.click();
        System.out.println("Clicked Add to Cart for Sauce Labs Backpack.");
        pause(); // Pause after clicking add to cart

        // 4. Verify button text changes to "Remove"
        WebElement removeButton = backpackContainer.findElement(By.xpath(".//button[starts-with(@id, 'remove-')]"));
        wait.until(ExpectedConditions.visibilityOf(removeButton)); // Ensure remove button is visible
        Assert.assertEquals(removeButton.getText().toUpperCase(), "REMOVE", "Button text did not change to Remove.");
        System.out.println("Verified button text changed to Remove.");
        pause(); // Pause after verifying button text

        // 5. Verify cart badge updates to "1"
        WebElement cartBadge = driver.findElement(By.xpath("//*[@id='shopping_cart_container']/a/span"));
        wait.until(ExpectedConditions.textToBePresentInElement(cartBadge, "1")); // Wait for text to be '1'
        Assert.assertEquals(cartBadge.getText(), "1", "Cart badge count is not 1.");
        System.out.println("Verified cart badge shows 1.");
        pause(); // Pause after verifying cart badge
    }

    @AfterTest
    public void tearDown() {
        pause(); // Optional: Pause before closing
        if (driver != null) {
            driver.quit();
        }
        System.out.println("Browser closed.");
    }
}