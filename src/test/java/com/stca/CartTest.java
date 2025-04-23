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
 

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); 
    }

    @Test(description = "TC04: Verify adding a single item (Sauce Labs Backpack) to the cart.")
    public void testAddSingleItemToCart() {
        driver.get("https://www.saucedemo.com/");
        pause(); // Pause after navigation

        driver.findElement(By.xpath("//*[@id='user-name']")).sendKeys("standard_user");
        pause(); // Pause after entering username

        driver.findElement(By.xpath("//*[@id='password']")).sendKeys("secret_sauce");
        pause(); // Pause after entering password

        driver.findElement(By.xpath("//*[@id='login-button']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='inventory_container']")));
        System.out.println("Successfully logged in and inventory page loaded.");
        pause();

        String backpackItemXPath = "//*[@id='inventory_container']/div/div[1]";
        WebElement backpackContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(backpackItemXPath)));
        WebElement addButton = backpackContainer.findElement(By.xpath("//*[@id='add-to-cart-sauce-labs-backpack']"));
        
        System.out.println("Located Sauce Labs Backpack Add to Cart button.");
        pause(); 

        addButton.click();
        System.out.println("Clicked Add to Cart for Sauce Labs Backpack.");
        pause(); 

        WebElement removeButton = backpackContainer.findElement(By.xpath(".//button[starts-with(@id, 'remove-')]"));
        wait.until(ExpectedConditions.visibilityOf(removeButton)); // Ensure remove button is visible
        Assert.assertEquals(removeButton.getText().toUpperCase(), "REMOVE", "Button text did not change to Remove.");
        System.out.println("Verified button text changed to Remove.");
        pause(); // Pause after verifying button text

        WebElement cartBadge = driver.findElement(By.xpath("//*[@id='shopping_cart_container']/a/span"));
        wait.until(ExpectedConditions.textToBePresentInElement(cartBadge, "1")); // Wait for text to be '1'
        Assert.assertEquals(cartBadge.getText(), "1", "Cart badge count is not 1.");
        System.out.println("Verified cart badge shows 1.");
        pause(); 
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