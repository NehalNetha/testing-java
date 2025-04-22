package com.stca;

import com.stca.utils.ExcelUtils; // Make sure this line is exactly like this
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.time.Duration;

public class DataDrivenLoginTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final long PAUSE_DURATION_MS = 200; // Shorter pause for data-driven tests

    // Path to your Excel file
    private static final String EXCEL_FILE_PATH = "/Users/nehal/SixthSemester/SoftwareTestingTools/StartSelenium/software-testing-ca/stt_ddt.xlsx";
    private static final String SHEET_NAME = "Sheet1"; // Or your actual sheet name

    private void pause() {
        try {
            Thread.sleep(PAUSE_DURATION_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @BeforeMethod // Run before each test method defined by the data provider
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless"); // Optional: run headless
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5)); // Shorter wait for faster feedback
        driver.get("https://www.saucedemo.com/");
    }

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() throws IOException {
        // Read data from Excel using the utility class
        return ExcelUtils.getTableArray(EXCEL_FILE_PATH, SHEET_NAME);
    }

    @Test(dataProvider = "loginData", description = "TC09: Perform data-driven login tests.")
    public void testLoginScenarios(String username, String password, String expectedOutcome) {
        System.out.println("Testing with Username: " + username + ", Password: " + password + ", Expected: " + expectedOutcome);

        WebElement userField = driver.findElement(By.xpath("//input[@id='user-name']"));
        WebElement passField = driver.findElement(By.xpath("//input[@id='password']"));
        WebElement loginButton = driver.findElement(By.xpath("//input[@id='login-button']"));

        userField.sendKeys(username);
        pause();
        passField.sendKeys(password);
        pause();
        loginButton.click();
        pause();

        boolean isLoginSuccessful = false;
        try {
            // Check if login was successful by looking for inventory container or cart icon
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='inventory_container']")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='shopping_cart_link']"))
            ));
            isLoginSuccessful = true;
        } catch (Exception e) {
            // Login likely failed, check for error message
            isLoginSuccessful = false;
        }

        if (expectedOutcome.equalsIgnoreCase("Success")) {
            Assert.assertTrue(isLoginSuccessful, "Login expected to succeed but failed for user: " + username);
            // Optional: Add further checks for successful login page elements
        } else { // Expected "Failure"
            Assert.assertFalse(isLoginSuccessful, "Login expected to fail but succeeded for user: " + username);
            // Verify error message is displayed
            try {
                WebElement errorElement = driver.findElement(By.xpath("//h3[@data-test='error']"));
                Assert.assertTrue(errorElement.isDisplayed(), "Expected error message not displayed for user: " + username);
                System.out.println("Failure verified with error: " + errorElement.getText());
            } catch (NoSuchElementException e) {
                // This case might happen if login succeeds when it should fail
                 if (!isLoginSuccessful) { // If login truly failed but no error message found (unlikely for saucedemo)
                     System.out.println("Login failed as expected, but standard error message element not found.");
                 } else { // Login succeeded unexpectedly
                     Assert.fail("Login succeeded but was expected to fail, and no error message found for user: " + username);
                 }
            }
        }
    }

    @AfterMethod // Run after each test method defined by the data provider
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}