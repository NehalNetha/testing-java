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
import java.util.List;

public class AddAllItemsTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final long PAUSE_DURATION_MS = 500; 

    private void pause() {
        try {
            Thread.sleep(PAUSE_DURATION_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); 
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

    @Test(description = "TC05: Verify adding all available items to the cart.")
    public void testAddAllItemsToCart() {
        driver.get("https://www.saucedemo.com/");
        pause(); 

        driver.findElement(By.xpath("//input[@id='user-name']")).sendKeys("standard_user");
        pause(); 

        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("secret_sauce");
        pause(); 

        driver.findElement(By.xpath("//input[@id='login-button']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='inventory_container']")));
        System.out.println("Successfully logged in and inventory page loaded.");
        pause(); 

        List<WebElement> addButtons = driver.findElements(By.xpath("//button[starts-with(@id, 'add-to-cart')]"));
        int numberOfItemsToAdd = addButtons.size();
        System.out.println("Found " + numberOfItemsToAdd + " items to add to the cart.");
        Assert.assertTrue(numberOfItemsToAdd > 0, "No 'Add to cart' buttons found on the inventory page.");
        pause();

        for (WebElement button : addButtons) {
            wait.until(ExpectedConditions.elementToBeClickable(button)).click();
            System.out.println("Clicked 'Add to cart' for an item.");
        }
        System.out.println("Clicked all 'Add to cart' buttons.");
        pause();

        List<WebElement> removeButtons = driver.findElements(By.xpath("//button[starts-with(@id, 'remove-')]"));
        int numberOfRemoveButtons = removeButtons.size();
        System.out.println("Found " + numberOfRemoveButtons + " 'Remove' buttons.");
        Assert.assertEquals(numberOfRemoveButtons, numberOfItemsToAdd, "Number of 'Remove' buttons does not match number of items added.");
        System.out.println("Verified all 'Add to cart' buttons changed to 'Remove'.");
        pause();

        WebElement cartBadge = driver.findElement(By.xpath("//span[@class='shopping_cart_badge']"));
        String expectedBadgeCount = String.valueOf(numberOfItemsToAdd);
        wait.until(ExpectedConditions.textToBePresentInElement(cartBadge, expectedBadgeCount)); 
        Assert.assertEquals(cartBadge.getText(), expectedBadgeCount, "Cart badge count does not match the number of items added.");
        System.out.println("Verified cart badge shows: " + expectedBadgeCount);
        pause();
    }

    @AfterTest
    public void tearDown() {
        pause(); 
        if (driver != null) {
            driver.quit();
        }
        System.out.println("Browser closed.");
    }
}