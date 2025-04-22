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

public class CheckoutTest {

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

    @Test(description = "TC08: Verify that a user can successfully complete the checkout process.")
    public void testCompleteCheckoutProcess() {
        driver.get("https://www.saucedemo.com/");
        pause();

        driver.findElement(By.xpath("//input[@id='user-name']")).sendKeys("standard_user");
        pause();
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("secret_sauce");
        pause();
        driver.findElement(By.xpath("//input[@id='login-button']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='inventory_container']")));
        pause();

        String itemToAddName = "Sauce Labs Backpack";
        String itemToAddButtonId = "add-to-cart-sauce-labs-backpack";

        driver.findElement(By.xpath("//button[@id='" + itemToAddButtonId + "']")).click();
        pause();

        driver.findElement(By.xpath("//a[@class='shopping_cart_link']")).click();
        wait.until(ExpectedConditions.urlContains("/cart.html"));
        pause();

        driver.findElement(By.xpath("//button[@id='checkout']")).click();
        wait.until(ExpectedConditions.urlContains("/checkout-step-one.html"));
        pause();

        driver.findElement(By.xpath("//input[@id='first-name']")).sendKeys("Test");
        pause();
        driver.findElement(By.xpath("//input[@id='last-name']")).sendKeys("User");
        pause();
        driver.findElement(By.xpath("//input[@id='postal-code']")).sendKeys("12345");
        pause();

        driver.findElement(By.xpath("//input[@id='continue']")).click();
        wait.until(ExpectedConditions.urlContains("/checkout-step-two.html"));
        pause();

        WebElement overviewItemName = driver.findElement(By.xpath("//div[@class='inventory_item_name']"));
        Assert.assertEquals(overviewItemName.getText(), itemToAddName, "Item name on checkout overview does not match.");

        // Optional: Verify total price if needed, requires parsing and calculation
        // WebElement totalPriceElement = driver.findElement(By.xpath("//div[@class='summary_total_label']"));
        // Assert.assertTrue(totalPriceElement.getText().contains("Total:"), "Total price label not found.");
        pause();

        driver.findElement(By.xpath("//button[@id='finish']")).click();
        wait.until(ExpectedConditions.urlContains("/checkout-complete.html"));
        pause();

        WebElement completeHeader = driver.findElement(By.xpath("//h2[@class='complete-header']"));
        Assert.assertEquals(completeHeader.getText(), "Thank you for your order!", "Checkout completion message is incorrect.");

        Assert.assertTrue(driver.getCurrentUrl().endsWith("/checkout-complete.html"), "Not redirected to the checkout complete page.");
        pause();
    }

    @AfterTest
    public void tearDown() {
        pause();
        if (driver != null) {
            driver.quit();
        }
    }
}