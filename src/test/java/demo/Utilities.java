package demo;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import demo.wrappers.SeleniumWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utilities {

    public static void scrape(String year, WebDriver driver) {
        try {
            System.out.println("Starting data extraction for year: " + year);

            WebElement yearLink = driver.findElement(By.id(year));
            String yearLinkText = yearLink.getText();

            SeleniumWrapper.clickOnElement(yearLink, driver);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@class='table']")));

            ArrayList<HashMap<String, String>> movieList = new ArrayList<>();
            List<WebElement> filmRows = driver.findElements(By.xpath("//tr[@class='film']"));

            for (WebElement filmRow : filmRows) {
                try {
                    String filmTitle = filmRow.findElement(By.xpath("./td[contains(@class,'title')]")).getText();
                    String nomination = filmRow.findElement(By.xpath("./td[contains(@class,'nominations')]")).getText();
                    String awards = filmRow.findElement(By.xpath("./td[contains(@class,'awards')]")).getText();
                    boolean isWinner = filmRow.getAttribute("class").contains("best-picture");
                    long epoch = System.currentTimeMillis() / 1000;

                    HashMap<String, String> movieMap = new HashMap<>();
                    movieMap.put("epochTime", String.valueOf(epoch));
                    movieMap.put("year", yearLinkText);
                    movieMap.put("title", filmTitle);
                    movieMap.put("nomination", nomination);
                    movieMap.put("awards", awards);
                    movieMap.put("isWinner", String.valueOf(isWinner));

                    movieList.add(movieMap);

                } catch (Exception e) {
                    System.err.println("Error extracting data for a film row: " + e.getMessage());
                }
            }

            if (movieList.isEmpty()) {
                System.out.println("No movie data found for year: " + year);
            } else {
                System.out.println("Collected " + movieList.size() + " movies for year: " + year);
            }

            // Save data to JSON
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            File jsonFile = new File(outputDir, year + "-oscar-winner-data.json");
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(jsonFile, movieList);

            System.out.println("JSON data written to: " + jsonFile.getAbsolutePath());
            Assert.assertTrue(jsonFile.length() > 0, "Generated JSON file is empty.");

        } catch (Exception e) {
            System.err.println("Web scraping for movies failed for year " + year + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
