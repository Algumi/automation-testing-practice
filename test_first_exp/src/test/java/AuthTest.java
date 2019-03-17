import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AuthTest {
    private WebDriver driver;

    @BeforeClass
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setupTest() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void loginOnBeru() {
        driver.get("https://beru.ru/");

        // closing pop-up window
        driver.findElement(By.className("_1ZYDKa22GJ")).click();
        // opens the sign in page
        driver.findElement(By.className("header2-nav__user")).click();

        // Test account data: testAUTOacc - password; auto-test-acc - login

        // login input field
        WebElement loginInput = driver.findElement(By.id("passp-field-login"));
        // entering user login
        loginInput.sendKeys("auto-test-acc");
        // click on sign-in button
        driver.findElement(By.className("passp-sign-in-button")).click();

        // password input field (with explicit wait)
        WebElement passwordInput = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("passp-field-passwd")));
        // entering user password
        passwordInput.sendKeys("testAUTOacc");
        // click on sign-in button
        driver.findElement(By.className("passp-sign-in-button")).click();

        // Test of changing button text from "Войти в аккаунт" to "Мой профиль"
        WebElement profileName = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.className("header2__nav")));
        Assert.assertEquals(profileName.getText(),"Мой профиль",
                "Button \"Войти в аккаунт\" was not changed to \"Мой профиль\".");
    }
}
