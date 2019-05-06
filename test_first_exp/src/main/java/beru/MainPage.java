package beru;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class MainPage{
    private WebDriver driver;
    private WebDriverWait waitTest;

    private By profileHeader = By.className("header2-nav__user");
    private By userEmail = By.className("header2-user-menu__email");
    private By cityNameLine = By.cssSelector(".region-form-opener .link__inner");
    private By changeCityPopup = By.className("region-select-form");
    private By cityInputField = By.cssSelector(".header2-region-popup .input__control");
    private By citySuggestList = By.className("region-suggest__list-item");
    private By profileMenuPopup = By.className("header2-user-menu");

    public MainPage(WebDriver driver, WebDriverWait wait){
        this.driver = driver;
        this.waitTest = wait;
    }

    public void clickLoginButton(){
        // opens the sign in page
        driver.findElement(profileHeader).click();
    }

    public void checkMyProfile(){
        Assert.assertEquals(driver.findElement(profileHeader).getText(),"Мой профиль",
                "Button \"Войти в аккаунт\" was not changed to \"Мой профиль\".");
    }

    public void checkLoginName(){
        // Mouse over the My Profile button
        Actions actor = new Actions(driver);
        actor.moveToElement(driver.findElement(profileHeader)).build().perform();
        // User login display test
        WebElement userNameElement = waitTest.until(ExpectedConditions.visibilityOfElementLocated(userEmail));
        Assert.assertEquals(userNameElement.getText(), "auto-test-acc@yandex.ru",
                "Incorrect user login is displayed.");
    }

    public void clickCityName(){
        // element with city name on the top of the main page
        WebElement cityLine = driver.findElement(cityNameLine);
        cityLine.click();
    }

    public void enterCityName(String newCityName){
        // city input popup
        WebElement cityPopup = waitTest.until(ExpectedConditions.visibilityOfElementLocated(changeCityPopup));
        WebElement cityField = cityPopup.findElement(cityInputField);
        cityField.sendKeys(newCityName);
        waitTest.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(citySuggestList));
        // submit changes
        cityField.sendKeys(Keys.ENTER);
        cityPopup.submit();
    }

    public void checkCityName(String newCityName){
        // wait until page loading is complete (footer social media elements are always loaded last)
        waitTest.until(ExpectedConditions.visibilityOfElementLocated(By.className("footer__social-media")));
        // test of city name changes
        Assert.assertEquals(newCityName, driver.findElement(cityNameLine).getText(),
                "City name on main page wasn't changed.");
    }

    public void clickSettingsButton(){
        // goes to account settings
        Actions actor = new Actions(driver);
        WebElement myProfile = waitTest.until(ExpectedConditions.visibilityOfElementLocated(profileHeader));
        actor.moveToElement(myProfile).build().perform();
        waitTest.until(ExpectedConditions.visibilityOfElementLocated(userEmail));
        driver.findElement(profileMenuPopup).findElement(By.linkText("Настройки")).click();
    }
}
