import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class MainTests {
    private WebDriver driver;

    @BeforeClass
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setupTest() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://beru.ru/");
    }

    @Test
    public void signInTest() {
        // closing pop-up window
        driver.findElement(By.className("_1ZYDKa22GJ")).click();
        // opens the sign in page
        driver.findElement(By.className("header2-nav__user")).click();

        // preforms steps to sign in
        SignInSteps.signIn(driver);

        // Test of changing button text from "Войти в аккаунт" to "Мой профиль"
        WebElement profileName = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.className("header2__nav")));
        Assert.assertEquals(profileName.getText(),"Мой профиль",
                "Button \"Войти в аккаунт\" was not changed to \"Мой профиль\".");

        
        // Mouse over the My Profile button
        Actions actor = new Actions(driver);
        actor.moveToElement(profileName).build().perform();
        // User name display test
        WebElement userName = (new WebDriverWait(driver, 2))
                .until(ExpectedConditions.presenceOfElementLocated(By.className("header2-user-menu__user-name")));
        Assert.assertEquals(userName.getText(), "Тест Авто",
                "Button \"Войти в аккаунт\" was not changed to \"Мой профиль\"." + userName.getText());
    }

    private static String newCityName = "Хвалынск";

    @Test
    public void cityChangeTest() {
        WebDriverWait waitTest = new WebDriverWait(driver, 10);
        // closing pop-up window
        driver.findElement(By.className("_1ZYDKa22GJ")).click();

        // element with city name
        WebElement cityLine = driver.findElement(By.xpath
                ("/html/body/div[1]/div/div[2]/div[2]/noindex/div/div[1]/div/div/div[1]/span/span[2]/span/span"));
        String initialCity = cityLine.getText();
        cityLine.click();


        // city input field
        WebElement loginInput = waitTest.until
                (ExpectedConditions.presenceOfElementLocated(By.className("input__control")));
        // entering new city name
        loginInput.sendKeys(newCityName);
        waitTest.until(ExpectedConditions.textToBe(By.className("input__control"), newCityName));
        loginInput.submit();

        // click on continue with new city button
        //WebElement changeCity = (new WebDriverWait(driver, 10)).until(ExpectedConditions.
        //        presenceOfElementLocated(By.xpath("/html/body/div[7]/div/div/div[1]/div[1]/form/div/button")));

        // preforms steps to sign in
        //SignInSteps.signIn(driver);
    }

    //@AfterTest()
    public void closeBrowser()
    {
        driver.close();
    }
}
