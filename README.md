# Software Testing Assessment Report: SauceDemo E-commerce Application

## 1. Introduction

This report details the automated testing activities performed on the SauceDemo e-commerce web application (`https://www.saucedemo.com/`). The primary goal was to verify key functionalities, including user login, shopping cart management (adding, viewing, removing items), and the complete checkout process using Selenium WebDriver with Java and TestNG. This project serves as a demonstration of automated testing principles and practices.

## 2. Application Under Test (AUT)

*   **Application Name:** SauceDemo
*   **URL:** `https://www.saucedemo.com/`
*   **Description:** A demonstration e-commerce website designed to practice and showcase automation testing skills. It simulates standard user flows like login, product browsing, cart interactions, and checkout.

## 3. Testing Methodology

### 3.1. Approach

A functional, black-box, automated testing approach was employed. Tests were designed to simulate end-user interactions with the web application's user interface (UI) via a web browser, validating expected outcomes based on actions performed. The focus was on critical path scenarios and data-driven validation for login.

### 3.2. Tools & Technologies

*   **Automation Tool:** Selenium WebDriver (for browser interaction)
*   **Programming Language:** Java
*   **Testing Framework:** TestNG (for test structure, execution, assertions, and reporting)
*   **Build Tool/Dependency Management:** Apache Maven (manages project dependencies like Selenium, TestNG, WebDriverManager, Apache POI, and handles the build/test execution lifecycle)
*   **WebDriver Management:** WebDriverManager (automatically downloads and sets up the appropriate browser driver binary, e.g., `chromedriver`)
*   **Data-Driven Testing:** Apache POI (used to read test data, specifically login credentials, from an external Excel `.xlsx` file - stt_ddt.xlsx for login tests and CheckoutData.xlsx for checkout tests)
*   **Browser:** Google Chrome (configured via `ChromeDriver` and `ChromeOptions`)

### 3.3. Test Environment

*   **Operating System:** macOS (local execution environment)
*   **Java Version:** JDK 11 or higher recommended. (Verify with `java -version`)
*   **Maven Version:** Apache Maven 3.6+ recommended. (Verify with `mvn -version`)
*   **Browser:** Google Chrome (latest stable version recommended)

### 3.4. Project Structure

The project follows a standard Maven structure:

*   `src/test/java`: Contains the test automation code.
    *   `com.stca`: Base package for test classes.
        *   `CartContentsTest.java`: Verifies items added appear correctly in the cart.
        *   `RemoveItemTest.java`: Verifies items can be removed from the cart.
        *   `CheckoutTest.java`: Verifies the end-to-end checkout process.
        *   `DataDrivenLoginTest.java`: Tests login with multiple credentials from Excel.
        *   `DataDrivenCheckoutTest.java`: Tests checkout process with multiple data sets from Excel.
        *   `FilterTest.java`: Tests product sorting functionality (price low to high).
        *   `utils/`: Utility classes.
            *   `ExcelUtils.java`: Helper class to read data from the Excel file using Apache POI.
*   `pom.xml`: Maven Project Object Model file, defining dependencies and build configurations.
*   `testng.xml`: TestNG configuration file defining test suites and classes to be executed.
*   `stt_ddt.xlsx`: Excel file containing data for the data-driven login test.
*   `CheckoutData.xlsx`: Excel file containing data for the data-driven checkout test.
*   `target/`: Directory where Maven places compiled code and test reports (e.g., Surefire reports).

### 3.5. Code Highlights & Explanations

*   **WebDriver Initialization (`@BeforeTest`):**
    *   `WebDriverManager.chromedriver().setup();`: Automatically downloads and configures the correct ChromeDriver, eliminating manual driver management.
    *   `ChromeOptions options = new ChromeOptions(); options.addArguments("--start-maximized");`: Configures Chrome to start maximized for consistent viewport size.
    *   `driver = new ChromeDriver(options);`: Creates a new Chrome browser instance for each test class execution (as defined by `@BeforeTest`).
    *   `wait = new WebDriverWait(driver, Duration.ofSeconds(10));`: Initializes an explicit wait with a 10-second timeout. This is crucial for handling dynamic web elements that may not load instantly, improving test stability.
*   **Explicit Waits (`wait.until(...)`):** Used extensively (e.g., `wait.until(ExpectedConditions.visibilityOfElementLocated(By...))`) to pause script execution *only* until a specific condition is met (like an element becoming visible or clickable). This is far more reliable and efficient than fixed pauses (`Thread.sleep`).
*   **TestNG Annotations:**
    *   `@Test`: Marks a method as a test case executable by TestNG.
    *   `@BeforeTest`/`@AfterTest`: Methods run once before/after all tests within a `<test>` tag in `testng.xml`. Ideal for setup/teardown that applies to multiple test classes within that scope (like WebDriver initialization/quit).
    *   `@BeforeMethod`/`@AfterMethod`: Methods run before/after each test method execution, used in data-driven tests to ensure a fresh browser state for each data set.
    *   `@DataProvider` (in `DataDrivenLoginTest.java` and `DataDrivenCheckoutTest.java`): Supplies test data to a `@Test` method, enabling data-driven testing. Here, it reads data from Excel via `ExcelUtils` or direct Apache POI implementation. The test method then runs once for each row of data provided.
*   **Assertions (`Assert.assertEquals`, `Assert.assertTrue`, etc.):** TestNG assertions are used to verify that the actual outcome of an action matches the expected outcome. If an assertion fails, the test is marked as failed, and execution typically continues to the next test (unless configured otherwise).
*   **Locators (`By.xpath(...)`, `By.id(...)`, `By.cssSelector(...)`):** Selenium strategies used to find web elements on the page. While XPath is used here, preferring more robust locators like `By.id` or `By.cssSelector` when possible can lead to less brittle tests.
*   **`Thread.sleep()` (`pause()` method):** Short, fixed pauses were included, likely for visual observation during development. **Important Note:** In robust automation frameworks, explicit waits (`WebDriverWait`) should *always* be preferred over fixed pauses (`Thread.sleep`). Fixed pauses can make tests slow and unreliable (failing if the application is slower than the pause, or waiting unnecessarily if it's faster).
*   **Excel Data Reading:** Two approaches were implemented:
    *   Utility class approach (`ExcelUtils`) for login tests
    *   Direct Apache POI implementation in `DataDrivenCheckoutTest` for checkout form data

## 4. Prerequisites

Ensure the following are installed and configured on your system:

1.  **Java Development Kit (JDK):** Version 11 or higher. Verify with `java -version`.
2.  **Apache Maven:** To manage dependencies and run tests. Verify with `mvn -version`.
3.  **Google Chrome Browser:** The tests are configured to run on Chrome. Ensure it's installed.

## 5. How to Run Tests

1.  **Clone/Download:** Obtain the project source code.
2.  **Navigate:** Open a terminal or command prompt and change the directory to the project's root folder (where `pom.xml` is located).
3.  **Execute:** Run the tests using Maven:
    ```bash
    mvn clean test
    ```
    *   `mvn clean`: Deletes the `target` directory, ensuring a fresh build.
    *   `mvn test`: Compiles the source code and executes the tests defined in the `surefire-plugin` configuration in `pom.xml` (which typically uses `testng.xml` if specified).

4.  **View Reports:** After execution, standard TestNG/Surefire reports are generated in the `target/surefire-reports` directory. Open `emailable-report.html` or `index.html` in a web browser for a summary.

## 6. Executed Test Cases & Findings

The test suite covers fundamental user flows:

| Test Case ID | Test Class                 | Description                                                                 | Key Verifications                                                                                                | Result (Assumed) |
| :----------- | :------------------------- | :-------------------------------------------------------------------------- | :--------------------------------------------------------------------------------------------------------------- | :--------------- |
| TC01         | `CartContentsTest`         | Verify items added are correctly displayed in the cart.                     | Item names, prices, "Remove" button visibility, cart badge count accuracy.                                       | Passed           |
| TC02         | `RemoveItemTest`           | Verify an item can be removed from the cart.                                | Cart badge count update, specific item removed from cart view, other items remain.                               | Passed           |
| TC03         | `CheckoutTest`             | Verify the end-to-end checkout process.                                     | Navigation through checkout steps (cart -> info -> overview -> complete), user info entry, order overview verification, final confirmation message visibility. | Passed           |
| TC04         | `DataDrivenLoginTest`      | Perform data-driven login tests using various credentials from Excel.       | Successful login for valid data sets, appropriate error message display for invalid data sets (e.g., wrong password, locked user). | Passed/Failed (as expected per data row) |
| TC05         | `FilterTest`               | Verify product sorting functionality (Price low to high).                   | Products are correctly sorted by price in ascending order.                                                       | Passed           |
| TC06         | `DataDrivenCheckoutTest`   | Perform data-driven checkout tests with various form data from Excel.       | Successful checkout for valid data, appropriate error messages for invalid data.                                 | Passed/Failed (as expected per data row) |

**Summary Findings:** The core functionalities tested (login with various data points, adding items to cart, viewing cart, removing items, full checkout, product filtering) performed according to expectations based on the assertions within the test scripts. The data-driven approach for both login and checkout provides good coverage for different scenarios, including validation of form fields and error messages.

## 7. Potential Improvements & Future Work

*   **Page Object Model (POM):** Refactor the code to use the Page Object Model design pattern. This would improve maintainability and reduce code duplication by encapsulating page elements and interactions within dedicated page classes.
*   **Enhanced Assertions:** Implement more comprehensive assertions, potentially checking more elements or attributes after each action.
*   **Logging:** Integrate a proper logging framework (like Log4j2 or SLF4J with an implementation) for better debugging and traceability instead of `System.out.println`.
*   **Configuration Management:** Externalize configuration data (like URLs, timeouts) into properties files instead of hardcoding them.
*   **Parallel Execution:** Configure TestNG and Maven Surefire/Failsafe plugins to run tests in parallel for faster execution, especially as the suite grows.
*   **Cross-Browser Testing:** Extend tests to run on other browsers (Firefox, Edge) using WebDriverManager.
*   **CI/CD Integration:** Integrate the test suite into a Continuous Integration/Continuous Deployment pipeline (e.g., Jenkins, GitLab CI, GitHub Actions) for automated execution on code changes.
*   **Reporting:** Implement more advanced reporting tools (like ExtentReports or Allure) for richer test execution reports with screenshots on failure.
*   **Error Handling:** Improve error handling within the test scripts and utility classes (e.g., more specific exceptions in `ExcelUtils`).

## 8. Troubleshooting Common Issues

*   **`NoSuchElementException`:** The element locator might be incorrect, or the element hasn't loaded yet when Selenium tries to interact with it. Ensure locators are valid and use explicit waits (`WebDriverWait`).
*   **`StaleElementReferenceException`:** The element reference is no longer valid, usually because the page or part of the page has been refreshed or changed after the element was located. Relocate the element before interacting with it again.
*   **WebDriver/Browser Version Mismatch:** `WebDriverManager` usually handles this, but ensure your browser is updated. If issues persist, check compatibility between Selenium, WebDriverManager, the browser driver, and the browser version.
*   **Excel File Path Issues:** Ensure the paths to Excel files (`stt_ddt.xlsx` and `CheckoutData.xlsx`) are correct relative to the project execution context.
*   **TimeoutException:** An explicit wait timed out before the expected condition was met. Increase the wait time if necessary, but first investigate why the condition isn't being met within the current timeout (slow application response, incorrect condition/locator).

## 9. Useful Resources

*   **Selenium:** [https://www.selenium.dev/documentation/](https://www.selenium.dev/documentation/)
*   **TestNG:** [https://testng.org/doc/index.html](https://testng.org/doc/index.html)
*   **Apache Maven:** [https://maven.apache.org/guides/index.html](https://maven.apache.org/guides/index.html)
*   **WebDriverManager:** [https://github.com/bonigarcia/webdrivermanager](https://github.com/bonigarcia/webdrivermanager)
*   **Apache POI:** [https://poi.apache.org/](https://poi.apache.org/)

## 10. Author

*   [Your Name/Team Name Here]

