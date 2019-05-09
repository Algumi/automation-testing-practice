package beru;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {
    private WebDriver driver;

    private By loginInput = By.id("passp-field-login");
    private By passwordInput = By.id("passp-field-passwd");
    private By continueButton = By.className("passp-sign-in-button");

    public LoginPage(WebDriver driver){
        this.driver = driver;
    }

    public void enterLogin(){
        // opens the sign in page
        driver.findElement(loginInput).sendKeys("auto-test-acc");
    }

    public void enterPassword(){
        // opens the sign in page
        driver.findElement(passwordInput).sendKeys("testAUTOacc");
    }

    public void clickContinueButton(){
        // opens the sign in page
        driver.findElement(continueButton).click();
    }
}
