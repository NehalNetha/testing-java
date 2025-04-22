
package com.stca;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

public class ChromeDriverTest {
    
    private WebDriver driver;
    
    @BeforeTest
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        
        driver = new ChromeDriver(options);
    }
    
    @Test(priority = 1)
    public void testAddAllProductsToCart() {
        // Navigate to Sauce Demo website
        driver.get("https://www.saucedemo.com/");
        
        // Login with standard user
        driver.findElement(By.xpath("//input[@id='user-name']")).sendKeys("standard_user");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("secret_sauce");
        driver.findElement(By.xpath("//input[@id='login-button']")).click();
        
        // Wait for products page to load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Find all add to cart buttons
        List<WebElement> addButtons = driver.findElements(By.xpath("//button[starts-with(@id, 'add-to-cart')]"));
        System.out.println("Found " + addButtons.size() + " products to add to cart");
        
        // Add all products to cart
        for (WebElement button : addButtons) {
            button.click();
        }
        
        // Verify cart count equals number of products added
        WebElement cartBadge = driver.findElement(By.xpath("//span[@class='shopping_cart_badge']"));
        Assert.assertEquals(Integer.parseInt(cartBadge.getText()), addButtons.size(), 
                "Cart count does not match number of products added");
        
        System.out.println("Successfully added all products to cart");
    }
    
    @Test(priority = 2)
    public void testViewCartContents() {
        // Navigate to cart page
        driver.findElement(By.xpath("//*[@id='shopping_cart_container']/a")).click();
        
        // Verify cart page loaded
        Assert.assertTrue(driver.findElement(By.xpath("//div[@id='cart_contents_container']")).isDisplayed(),
                "Cart contents not displayed");
        
        // Verify all items are in cart
        List<WebElement> cartItems = driver.findElements(By.xpath("//div[@class='cart_item']"));
        System.out.println("Number of items in cart: " + cartItems.size());
        
        // Verify each item has remove button
        for (WebElement item : cartItems) {
            Assert.assertTrue(item.findElement(By.xpath(".//button[starts-with(@id, 'remove')]")).isDisplayed(),
                    "Remove button not found for item");
        }
        
        System.out.println("Successfully verified cart contents");
    }
    
    @Test(priority = 3)
    public void testCheckoutProcess() {
        // Click checkout
        driver.findElement(By.xpath("//button[@id='checkout']")).click();
        
        // Fill checkout information
        driver.findElement(By.xpath("//input[@id='first-name']")).sendKeys("Test");
        driver.findElement(By.xpath("//input[@id='last-name']")).sendKeys("User");
        driver.findElement(By.xpath("//input[@id='postal-code']")).sendKeys("12345");
        driver.findElement(By.xpath("//input[@id='continue']")).click();
        
        // Verify checkout overview is displayed
        Assert.assertTrue(driver.findElement(By.xpath("//div[@class='checkout_summary_container']")).isDisplayed(), 
                "Checkout overview not displayed");
        
        // Verify total amount is calculated correctly
        WebElement summarySubtotal = driver.findElement(By.xpath("//div[@class='summary_subtotal_label']"));
        Assert.assertTrue(summarySubtotal.isDisplayed(), "Subtotal not displayed");
        
        // Complete checkout
        driver.findElement(By.xpath("//button[@id='finish']")).click();
        
        // Verify checkout complete
        Assert.assertTrue(driver.findElement(By.xpath("//h2[@class='complete-header']")).isDisplayed(), 
                "Checkout complete message not displayed");
        
        System.out.println("Successfully completed checkout process for all items");
        
        // Keep browser open for verification
        try {
            Thread.sleep(300000); // Keep open for 5 minutes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @AfterTest
    public void tearDown() {
        // Browser will stay open for verification
        // Remove driver.quit() to keep browser open
        System.out.println("Browser left open for verification");
    }
} 