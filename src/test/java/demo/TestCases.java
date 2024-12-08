package demo;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import demo.wrappers.SeleniumWrapper;

public class TestCases {

    ChromeDriver driver;

    @BeforeTest
    public void startBrowser() {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    }

    @Test()
    public void testCase01() throws InterruptedException {
        driver.get("https://www.scrapethissite.com/pages/");

        // Verify the current link with Assert Statements
        Assert.assertTrue(driver.getCurrentUrl().equals("https://www.scrapethissite.com/pages/"), "Unverified URL");
        System.out.println("Verified URL: https://www.scrapethissite.com/pages/");

        WebElement hockeyTeamsElement = driver.findElement(By.xpath("//a[contains(text(),'Hockey Teams')]"));
        SeleniumWrapper.clickOnElement(hockeyTeamsElement, driver);

        // Initialize and declare a HashMap ArrayList called dataList
        ArrayList<HashMap<String, Object>> dataList = new ArrayList<>();

        // Locate page 1
        WebElement clickOnPage = driver.findElement(By.xpath("(//ul[@class='pagination']/li/a)[1]"));

        // Click on page 1
        SeleniumWrapper.clickOnElement(clickOnPage, driver);

        // Iterate through 4 pages
        for (int page = 1; page <= 4; page++) {
            List<WebElement> rows = driver.findElements(By.xpath("//tr[@class='team']"));
            for (WebElement row : rows) {
                // Extract data from each row
                // Get text from TeamName locator
                String teamName = row.findElement(By.xpath("./td[@class='name']")).getText();
                // Get year from Year locator
                int year = Integer.parseInt(row.findElement(By.xpath("./td[@class='year']")).getText());
                // Get Winning percentage from Win% locator
                double winPercentage = Double
                        .parseDouble(row.findElement(By.xpath("./td[contains(@class,'pct')]")).getText());


                // Declare epochTime
                long epoch = System.currentTimeMillis() / 1000;

                // Convert epochTime into string
                String epochTime = String.valueOf(epoch);
                
                // Check if win percentage is less than 40%
                if (winPercentage < 0.4) {
                    // Create a HashMap to store the data
                    HashMap<String, Object> dataMap = new HashMap<>();
                    dataMap.put("epochTime", epochTime);
                    dataMap.put("teamName", teamName);
                    dataMap.put("year", year);
                    dataMap.put("winPercentage", winPercentage);

                    // Add the HashMap to the ArrayList
                    dataList.add(dataMap);
                }
            }
            // Navigate to the next page
            if (page < 4) {
                WebElement nextPagElement = driver.findElement(By.xpath("//a[@aria-label='Next']"));
                nextPagElement.click();
                // You might need to add some wait here to ensure the page is fully loaded
                // before scraping again
                Thread.sleep(5000);
            }
        }
        // Print the collected data
        for (HashMap<String, Object> data : dataList) {
            System.out.println("Epoch time of scrape: " + data.get("epochTime") + ", Team Name: " + data.get("teamName")
                    + ", Year: " + data.get("year") + ", win%: " + data.get("winPercentage"));
        }

        // Store the HashMap Data in a json File
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File jsonFile = new File(
                    "/Users/zaynadee/Documents/selenium-starter-2/src/test/output/hockey-team-data.json");
            objectMapper.writeValue(jsonFile, dataList);
            System.out.println("JSON data written to: " + jsonFile.getAbsolutePath());
            Assert.assertTrue(jsonFile.length() != 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testCase02() {
        driver.get("https://www.scrapethissite.com/pages/");

        WebElement oscarWiningFilms = driver.findElement(By.xpath("//a[contains(text(),'Oscar Winning Films')]"));
        SeleniumWrapper.clickOnElement(oscarWiningFilms, driver);

        Utilities.scrape("2015", driver);
        Utilities.scrape("2014", driver);
        Utilities.scrape("2013", driver);
        Utilities.scrape("2012", driver);
        Utilities.scrape("2011", driver);
        Utilities.scrape("2010", driver);

    }

    @AfterTest
    public void endTest() {
        driver.quit();
    }

}