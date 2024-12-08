package demo.wrappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class SeleniumWrapper {

    public static boolean clickOnElement(WebElement element, WebDriver driver) {
        if (element.isDisplayed()) {
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].scrollIntoView(true)", element);
                element.click();
                Thread.sleep(2000);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static boolean enterText(WebElement element, WebDriver driver, String text) {
        try {
            element.click();
            element.clear();
            element.sendKeys(text);
            Thread.sleep(1000);
            return true;
        } catch (Exception e) {
            return false;

        }
    }

}