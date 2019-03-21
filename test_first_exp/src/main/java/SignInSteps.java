import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SignInSteps {
    private static String login = "auto-test-acc";
    private static String password = "testAUTOacc";

    public static void signIn(WebDriver driver){
        // opens the sign in page
        driver.findElement(By.className("header2-nav__user")).click();

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
    }
}
