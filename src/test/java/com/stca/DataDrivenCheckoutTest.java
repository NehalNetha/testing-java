package com.stca;

// Remove ExcelUtils import
// import com.stca.utils.ExcelUtils;

// Add necessary imports for Apache POI
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook; // Or HSSFWorkbook for .xls

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

import java.io.FileInputStream; // Import for file reading
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList; // Import for List
import java.util.Iterator; // Import for Iterator
import java.util.List; // Import for List

public class DataDrivenCheckoutTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final long PAUSE_DURATION_MS = 200; // Short pause for efficiency

    // Path to your Excel file and the new sheet name
    private static final String EXCEL_FILE_PATH = "/Users/nehal/SixthSemester/SoftwareTestingTools/StartSelenium/software-testing-ca/CheckoutData.xlsx";
    private static final String SHEET_NAME = "Sheet1"; // Use the new sheet name

    private void pause() {
        try {
            Thread.sleep(PAUSE_DURATION_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Setup runs before each test case driven by the data provider
    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        // options.addArguments("--headless"); // Optional
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5)); // Adjust wait time if needed
        driver.get("https://www.saucedemo.com/");

        // Perform login (using standard user for all checkout tests)
        driver.findElement(By.xpath("//input[@id='user-name']")).sendKeys("standard_user");
        pause();
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("secret_sauce");
        pause();
        driver.findElement(By.xpath("//input[@id='login-button']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='inventory_container']")));
        pause();

        // Add a default item to the cart before starting checkout test
        driver.findElement(By.xpath("//button[@id='add-to-cart-sauce-labs-backpack']")).click();
        pause();

        // Navigate to cart and then to checkout step one
        driver.findElement(By.xpath("//a[@class='shopping_cart_link']")).click();
        wait.until(ExpectedConditions.urlContains("/cart.html"));
        pause();
        driver.findElement(By.xpath("//button[@id='checkout']")).click();
        wait.until(ExpectedConditions.urlContains("/checkout-step-one.html"));
        pause();
    }

    // --- Integrated Excel Reading Logic ---
    private Object[][] readExcelData(String filePath, String sheetName) throws IOException {
        FileInputStream fileInputStream = null;
        Workbook workbook = null;
        List<Object[]> dataList = new ArrayList<>();
        final int EXPECTED_COLUMNS = 4; // Define the number of columns expected by the test method

        try {
            fileInputStream = new FileInputStream(filePath);
            workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new IOException("Sheet '" + sheetName + "' not found in the workbook.");
            }

            Iterator<Row> rowIterator = sheet.iterator();
            DataFormatter formatter = new DataFormatter();

            // Skip header row
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Consume the header row
            } else {
                return new Object[0][0]; // Empty sheet
            }

            // We know the test method expects exactly 4 columns.
            int columnCount = EXPECTED_COLUMNS;
            System.out.println("Reading exactly " + columnCount + " columns as required by the test method.");


            // Iterate over the data rows
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                List<Object> cellData = new ArrayList<>();

                // Read cells based on the *expected* column count
                for (int i = 0; i < columnCount; i++) { // Use the fixed columnCount
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cellData.add(formatter.formatCellValue(cell).trim());
                }

                // Optional: Skip rows where all *read* cells are effectively blank
                boolean allBlank = cellData.stream().allMatch(cell -> cell == null || cell.toString().isEmpty());
                if (!allBlank) {
                     // Ensure the row data list always has 'columnCount' elements
                     // (This loop might not be strictly necessary anymore if reading exactly columnCount cells,
                     // but kept for safety in case getCell returns fewer than expected under some edge cases)
                     while (cellData.size() < columnCount) {
                         cellData.add("");
                     }
                    // Add the data, ensuring it's exactly 'columnCount' elements
                    dataList.add(cellData.subList(0, columnCount).toArray(new Object[columnCount]));
                }
            }

        } catch (Exception e) {
            System.err.println("Error reading Excel file: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to read data from Excel file: " + filePath + ", Sheet: " + sheetName, e);
        } finally {
            // Ensure resources are closed
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    System.err.println("Error closing workbook: " + e.getMessage());
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    System.err.println("Error closing file input stream: " + e.getMessage());
                }
            }
        }

        return dataList.toArray(new Object[0][]);
    }
    // --- End of Integrated Excel Reading Logic ---

    @DataProvider(name = "checkoutData")
    public Object[][] getCheckoutData() throws IOException {
        // Call the local Excel reading method
        return readExcelData(EXCEL_FILE_PATH, SHEET_NAME);
    }

    @Test(dataProvider = "checkoutData", description = "TC10: Perform data-driven checkout tests.")
    public void testCheckoutScenarios(String firstName, String lastName, String postalCode, String expectedOutcome) {
        System.out.println("Testing Checkout with FirstName: " + firstName + ", LastName: " + lastName + ", PostalCode: " + postalCode + ", Expected: " + expectedOutcome);

        WebElement firstNameField = driver.findElement(By.xpath("//input[@id='first-name']"));
        WebElement lastNameField = driver.findElement(By.xpath("//input[@id='last-name']"));
        WebElement postalCodeField = driver.findElement(By.xpath("//input[@id='postal-code']"));
        WebElement continueButton = driver.findElement(By.xpath("//input[@id='continue']"));

        // Fill the form using data from Excel
        if (firstName != null && !firstName.isEmpty()) firstNameField.sendKeys(firstName);
        pause();
        if (lastName != null && !lastName.isEmpty()) lastNameField.sendKeys(lastName);
        pause();
        if (postalCode != null && !postalCode.isEmpty()) postalCodeField.sendKeys(postalCode);
        pause();

        continueButton.click();
        pause();

        // Check outcome
        if (expectedOutcome.equalsIgnoreCase("Success")) {
            // Verify navigation to step two and then complete the checkout
            try {
                wait.until(ExpectedConditions.urlContains("/checkout-step-two.html"));
                pause();
                driver.findElement(By.xpath("//button[@id='finish']")).click();
                wait.until(ExpectedConditions.urlContains("/checkout-complete.html"));
                WebElement completeHeader = driver.findElement(By.xpath("//h2[@class='complete-header']"));
                Assert.assertEquals(completeHeader.getText(), "Thank you for your order!", "Checkout completion message is incorrect.");
                System.out.println("Checkout successful as expected.");
            } catch (Exception e) {
                Assert.fail("Checkout expected to succeed but failed. Details: " + e.getMessage());
            }
        } else { // Expected an error
            // Verify error message is displayed
            try {
                WebElement errorElement = driver.findElement(By.xpath("//h3[@data-test='error']"));
                Assert.assertTrue(errorElement.isDisplayed(), "Expected error message not displayed.");
                Assert.assertTrue(errorElement.getText().contains(expectedOutcome), // Check if the error message contains the expected text
                        "Error message text mismatch. Expected containing: '" + expectedOutcome + "', but got: '" + errorElement.getText() + "'");
                System.out.println("Checkout failed as expected with error: " + errorElement.getText());
                // Assert we are still on checkout step one page
                Assert.assertTrue(driver.getCurrentUrl().contains("/checkout-step-one.html"), "Expected to stay on checkout step one page after error.");

            } catch (NoSuchElementException e) {
                 // If no error message is found, fail the test if an error was expected.
                 // Check if we accidentally proceeded to step two
                 if (driver.getCurrentUrl().contains("/checkout-step-two.html")) {
                     Assert.fail("Checkout proceeded to step two unexpectedly when '" + expectedOutcome + "' was expected.");
                 } else {
                     Assert.fail("Expected error '" + expectedOutcome + "' but no error message element (h3 data-test='error') was found.");
                 }
            } catch (Exception e) {
                 Assert.fail("An unexpected exception occurred while verifying the error state: " + e.getMessage());
            }
        }
    }

    // Teardown runs after each test case
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}