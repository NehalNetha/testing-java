package com.stca;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FilterTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final long PAUSE_DURATION_MS = 500; // Pause for 0.5 seconds

    // Helper method for pausing (use explicit waits where possible)
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
        // options.addArguments("--headless"); // Optional: run headless
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test(description = "TC10: Verify product sorting functionality (Price low to high).")
    public void testSortPriceLowToHigh() {
        // 1. Navigate & Login
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

        WebElement sortDropdownElement = driver.findElement(By.className("product_sort_container"));
        Select sortDropdown = new Select(sortDropdownElement);
        sortDropdown.selectByValue("lohi"); // Value for "Price (low to high)"
        System.out.println("Selected sort option: Price (low to high)");
        pause(); // Allow time for potential DOM update after sorting

        List<WebElement> priceElements = driver.findElements(By.className("inventory_item_price"));
        List<Double> actualPrices = new ArrayList<>();
        for (WebElement priceElement : priceElements) {
            // Remove '$' and convert to double
            actualPrices.add(Double.parseDouble(priceElement.getText().replace("$", "")));
        }
        System.out.println("Actual prices after sorting: " + actualPrices);

        List<Double> expectedSortedPrices = new ArrayList<>(actualPrices);
        Collections.sort(expectedSortedPrices); // Sort the collected prices numerically

        System.out.println("Expected prices after sorting: " + expectedSortedPrices);
        Assert.assertEquals(actualPrices, expectedSortedPrices, "Items are not sorted correctly by price (low to high).");
        System.out.println("Verification successful: Items are sorted by price (low to high).");
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