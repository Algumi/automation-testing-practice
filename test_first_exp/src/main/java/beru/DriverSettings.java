package beru;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.util.concurrent.TimeUnit;

public class DriverSettings {
    private static EventFiringWebDriver driver;
    private static WebDriverWait waitTest;

    @BeforeClass
    public void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setupTest() {
        driver = new EventFiringWebDriver(new ChromeDriver());
        driver.register(new WebDriverEventListener());

        driver.manage().window().maximize();
        driver.get("https://beru.ru/");
        waitTest = new WebDriverWait(driver, 10);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    protected static WebDriver getDriver() {
        return driver;
    }

    protected static WebDriverWait getWait() {
        return waitTest;
    }

    @AfterMethod
    public void closeBrowser() {
        WebElement profileName = waitTest.until
                (ExpectedConditions.visibilityOfElementLocated(By.className("header2__nav")));
        if (profileName.getText().equals("Мой профиль")){
            profileName.click();
            driver.findElement(By.linkText("Выход")).click();
        }

        driver.close();
    }
}
