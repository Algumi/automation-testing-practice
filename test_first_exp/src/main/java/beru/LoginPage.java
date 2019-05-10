package beru;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {
    private WebDriver driver;

    private By loginInput = By.id("passp-field-login");
    private By passwordInput = By.id("passp-field-passwd");
    private By continueButton = By.className("passp-sign-in-button");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    @Step("Enter user login")
    public void enterLogin() {
        // opens the sign in page
        driver.findElement(loginInput).sendKeys("auto-test-acc");
    }

    @Step("Enter user password")
    public void enterPassword() {
        // opens the sign in page
        driver.findElement(passwordInput).sendKeys("testAUTOacc");
    }

    @Step("Click on submit button")
    public void clickContinueButton() {
        // opens the sign in page
        driver.findElement(continueButton).click();
    }
}
