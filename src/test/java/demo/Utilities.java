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

import com.fasterxml.jackson.databind.ObjectMapper;

import demo.wrappers.SeleniumWrapper;

public class Utilities {

    public static void scrape(String year, WebDriver driver) {
        try {

            WebElement yearLink = driver.findElement(By.id(year));
            String yearLinkText = yearLink.getText();

            SeleniumWrapper.clickOnElement(yearLink, driver);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@class='table']")));

            ArrayList<HashMap<String, String>> movieList = new ArrayList<>();

            List<WebElement> filmRows = driver.findElements(By.xpath("//tr[@class='film']"));
            // Declare int count = 0;
            int count = 0;
            for (WebElement filmRow : filmRows) {
                String filmTitle = filmRow.findElement(By.xpath("./td[contains(@class,'title')]")).getText();
                String nomination = filmRow.findElement(By.xpath("./td[contains(@class,'nominations')]")).getText();
                String awards = filmRow.findElement(By.xpath("./td[contains(@class,'awards')]")).getText();
                boolean isWinnerFlag = count == 0;
                String isWinner = String.valueOf(isWinnerFlag);

                long epoch = System.currentTimeMillis() / 1000;
                String epochTime = String.valueOf(epoch);

                HashMap<String, String> movieMap = new HashMap<>();
                movieMap.put("epochTime", epochTime);
                movieMap.put("year", yearLinkText);
                movieMap.put("title", filmTitle);
                movieMap.put("nomination", nomination);
                movieMap.put("awards", awards);
                movieMap.put("isWinner", isWinner);

                movieList.add(movieMap);

                count++;
            }

            // Print the collected data
            for (HashMap<String, String> movieData : movieList) {
                System.out.println("Epoch time: " + movieData.get("epochTime") + ", Year: " + movieData.get("year")
                        + ", Film title: " + movieData.get("title") + ", Nomination: " + movieData.get("nomination")
                        + ", Awards: " + movieData.get("awards")
                        + ", Best Picture: " + movieData.get("isWinner"));
            }

            // Store the HashMap Data in a json File
            ObjectMapper mapper = new ObjectMapper();
            try {
                File jsonFile = new File("/Users/zaynadee/Documents/selenium-starter-2/src/test/output/" + year
                        + "-oscar-winner-data.json");
                mapper.writeValue(jsonFile, movieList);
                System.out.println("JSON data written to: " + jsonFile.getAbsolutePath());
                Assert.assertTrue(jsonFile.length() != 0);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println("Web Scrap for movies is failed..");
            e.printStackTrace();
        }
    }

}