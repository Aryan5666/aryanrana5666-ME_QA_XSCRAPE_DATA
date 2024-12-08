package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import demo.wrappers.SeleniumWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class TestCases {
    ChromeDriver driver;

    @BeforeTest
    public void startBrowser() {
        try {
            File logDir = new File("output");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "output" + File.separator + "chromedriver.log");

            ChromeOptions options = new ChromeOptions();
            LoggingPreferences logs = new LoggingPreferences();

            logs.enable(LogType.BROWSER, Level.ALL);
            logs.enable(LogType.DRIVER, Level.ALL);
            options.setCapability("goog:loggingPrefs", logs);
            options.addArguments("--remote-allow-origins=*");

            driver = new ChromeDriver(options);
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        } catch (Exception e) {
            System.err.println("Failed to initialize the browser: " + e.getMessage());
            Assert.fail("Browser initialization failed.");
        }
    }

    @Test
    public void testCase01() throws IOException {
        driver.get("https://www.scrapethissite.com/pages/");
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.scrapethissite.com/pages/", "Unverified URL");

        WebElement hockeyTeamsElement = driver.findElement(By.xpath("//a[contains(text(),'Hockey Teams')]"));
        SeleniumWrapper.clickOnElement(hockeyTeamsElement, driver);

        ArrayList<HashMap<String, Object>> dataList = new ArrayList<>();

        for (int page = 1; page <= 4; page++) {
            List<WebElement> rows = driver.findElements(By.xpath("//tr[@class='team']"));
            for (WebElement row : rows) {
                String teamName = row.findElement(By.xpath("./td[@class='name']")).getText();
                int year = Integer.parseInt(row.findElement(By.xpath("./td[@class='year']")).getText());
                double winPercentage = Double.parseDouble(row.findElement(By.xpath("./td[contains(@class,'pct')]")).getText());
                long epoch = System.currentTimeMillis() / 1000;

                if (winPercentage < 0.4) {
                    HashMap<String, Object> dataMap = new HashMap<>();
                    dataMap.put("epochTime", String.valueOf(epoch));
                    dataMap.put("teamName", teamName);
                    dataMap.put("year", year);
                    dataMap.put("winPercentage", winPercentage);
                    dataList.add(dataMap);
                }
            }
            if (page < 4) {
                try {
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@aria-label='Next']"))).click();
                } catch (Exception e) {
                    System.err.println("Failed to navigate to the next page: " + e.getMessage());
                }
            }
        }

        Assert.assertFalse(dataList.isEmpty(), "No data found matching the criteria.");

        File outputDir = new File("output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        File jsonFile = new File(outputDir, "hockey-team-data.json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(jsonFile, dataList);
        Assert.assertTrue(jsonFile.length() > 0, "JSON file is empty.");
    }

    @Test
    public void testCase02() {
        driver.get("https://www.scrapethissite.com/pages/");
        WebElement oscarWiningFilms = driver.findElement(By.xpath("//a[contains(text(),'Oscar Winning Films')]"));
        SeleniumWrapper.clickOnElement(oscarWiningFilms, driver);

        Utilities.scrape("2015", driver);
        Utilities.scrape("2014", driver);
    }

    @AfterTest
    public void endTest() {
        driver.quit();
    }
}
