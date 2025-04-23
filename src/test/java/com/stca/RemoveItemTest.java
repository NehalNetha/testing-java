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
import org.openqa.selenium.NoSuchElementException;


import java.time.Duration;
import java.util.List;

public class RemoveItemTest {

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

    @Test(description = "TC07: Verify that an item can be removed from the shopping cart.")
    public void testRemoveItemFromCart() {
        driver.get("https://www.saucedemo.com/");
        pause();

        driver.findElement(By.xpath("//input[@id='user-name']")).sendKeys("standard_user");
        pause();
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("secret_sauce");
        pause();
        driver.findElement(By.xpath("//input[@id='login-button']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='inventory_container']")));
        pause();

        String itemToRemoveName = "Sauce Labs Backpack";
        String itemToKeepName = "Sauce Labs Bike Light";
        String itemToRemoveButtonId = "add-to-cart-sauce-labs-backpack";
        String itemToKeepButtonId = "add-to-cart-sauce-labs-bike-light";
        String itemToRemoveRemoveButtonId = "remove-sauce-labs-backpack";

        driver.findElement(By.xpath("//button[@id='" + itemToRemoveButtonId + "']")).click();
        pause();
        driver.findElement(By.xpath("//button[@id='" + itemToKeepButtonId + "']")).click();
        pause();

        WebElement initialCartBadge = driver.findElement(By.xpath("//span[@class='shopping_cart_badge']"));
        Assert.assertEquals(initialCartBadge.getText(), "2", "Initial cart badge count is incorrect.");
        pause();

        driver.findElement(By.xpath("//a[@class='shopping_cart_link']")).click();
        wait.until(ExpectedConditions.urlContains("/cart.html"));
        pause();

        WebElement removeButton = driver.findElement(By.xpath("//button[@id='" + itemToRemoveRemoveButtonId + "']"));
        removeButton.click();
        pause();

        WebElement updatedCartBadge = driver.findElement(By.xpath("//span[@class='shopping_cart_badge']"));
        Assert.assertEquals(updatedCartBadge.getText(), "1", "Cart badge count did not decrease after removal.");
        pause();

        List<WebElement> remainingItems = driver.findElements(By.xpath("//div[@class='inventory_item_name']"));
        boolean removedItemFound = false;
        boolean keptItemFound = false;
        for (WebElement item : remainingItems) {
            if (item.getText().equals(itemToRemoveName)) {
                removedItemFound = true;
            }
            if (item.getText().equals(itemToKeepName)) {
                keptItemFound = true;
            }
        }

        Assert.assertFalse(removedItemFound, itemToRemoveName + " was found in the cart after removal.");
        Assert.assertTrue(keptItemFound, itemToKeepName + " was not found in the cart after removal of another item.");
        pause();

        try {
            WebElement keptItemRemoveButton = driver.findElement(By.xpath("//div[@class='inventory_item_name' and text()='" + itemToKeepName + "']/ancestor::div[@class='cart_item']//button[starts-with(@id, 'remove-')]"));
            Assert.assertTrue(keptItemRemoveButton.isDisplayed(), "Remove button for the remaining item is not displayed.");
        } catch (NoSuchElementException e) {
             Assert.fail("Could not find the remaining item or its remove button: " + itemToKeepName);
        }
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