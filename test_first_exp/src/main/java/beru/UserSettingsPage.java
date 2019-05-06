package beru;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class UserSettingsPage {
    private WebDriver driver;

    private By cityNameLine = By.cssSelector(".region-form-opener .link__inner");
    private By cityNameSettings = By.cssSelector(".settings-list__title .link__inner");


    public UserSettingsPage(WebDriver driver){
        this.driver = driver;
    }

    public void checkCityName(String newCityName){
        // test of city name changes
        Assert.assertEquals(newCityName, driver.findElement(cityNameSettings).getText(),
                "City name on settings page wasn't changed.");
        Assert.assertEquals(newCityName, driver.findElement(cityNameLine).getText(),
                "City name (in top left corner) on settings page wasn't changed.");
    }
}
