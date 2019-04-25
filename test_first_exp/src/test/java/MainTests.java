import com.google.gson.JsonObject;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainTests {
    private WebDriver driver;
    private WebDriverWait waitTest;
    private static int productsOnThePage = 24;
    private static int priceFrom = 999, priceTo = 1999;

    @BeforeClass
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setupTest() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://beru.ru/");
        waitTest = new WebDriverWait(driver, 10);
        //driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
    }

    @Test
    public void signInTest() {
        // preforms steps to sign in
        SignInSteps.signIn(driver);

        // Test of changing button text from "Войти в аккаунт" to "Мой профиль"
        WebElement profileName = waitTest.until
                (ExpectedConditions.presenceOfElementLocated(By.className("header2__nav")));
        Assert.assertEquals(profileName.getText(),"Мой профиль",
                "Button \"Войти в аккаунт\" was not changed to \"Мой профиль\".");

        
        // Mouse over the My Profile button
        Actions actor = new Actions(driver);
        actor.moveToElement(profileName).build().perform();
        // User name display test
        WebElement userName = waitTest.until
                (ExpectedConditions.presenceOfElementLocated(By.className("header2-user-menu__user-name")));
        Assert.assertEquals(userName.getText(), "Тест Авто",
                "Button \"Войти в аккаунт\" was not changed to \"Мой профиль\"." + userName.getText());
    }

    private static String newCityName = "Хвалынск";

    @Test
    public void cityChangeTest() {
        // element with city name on the top of the main page
        WebElement cityLine = driver.findElement(By.className("region-form-opener"))
                .findElement(By.className("link__inner"));
        cityLine.click();

        // city input popup
        WebElement cityPopup = waitTest.until
                (ExpectedConditions.presenceOfElementLocated(By.className("region-select-form")));
        // city input field
        WebElement cityField = cityPopup.findElement(By.className("input__control"));
        cityField.sendKeys(newCityName);
        // wait until entered text is displayed on popup
        waitTest.until(ExpectedConditions.textToBePresentInElement(cityPopup, newCityName));
        // submit changes
        cityField.sendKeys(Keys.ENTER);
        cityPopup.submit();

        // wait until page loading is complete (footer social media elements are always loaded last)
        waitTest.until(ExpectedConditions.visibilityOfElementLocated(By.className("footer__social-media")));
        // city name line on the top of the main page
        cityLine = driver.findElement(By.className("region-form-opener")).findElement(By.className("link__inner"));
        // test of city name changes
        Assert.assertEquals(newCityName, cityLine.getText(),
                "City name on main page wasn't changed to" + newCityName);

        // preforms steps to sign in
        SignInSteps.signIn(driver);

        // goes to account settings
        Actions actor = new Actions(driver);
        WebElement myProfile = waitTest.until
                (ExpectedConditions.presenceOfElementLocated(By.className("header2__nav")));
        actor.moveToElement(myProfile).build().perform();
        waitTest.until(ExpectedConditions.presenceOfElementLocated(By.className("header2-user-menu")));
        driver.findElement(By.className("header2-user-menu")).findElement(By.linkText("Настройки")).click();
        // city name on settings page
        WebElement myCity = waitTest.until(ExpectedConditions.presenceOfElementLocated
                (By.className("settings-list__title"))).findElement(By.className("link__inner"));
        cityLine = driver.findElement(By.className("region-form-opener")).findElement(By.className("link__inner"));

        Assert.assertEquals(cityLine.getText(), myCity.getText(),
                "City name on the top of the page is not equal to the city name in settings");


    }

    @Test
    public void shoppingTest() {
        // click on catalog button
        driver.findElement(By.className("header2__navigation")).click();

        // waiting for opening the list of categories
        WebElement categoryList = waitTest.until
                (ExpectedConditions.presenceOfElementLocated(By.className("popup2__content")));
        // Mouse over beauty and hygiene category
        Actions actor = new Actions(driver);
        actor.moveToElement(categoryList.findElement(By.linkText("Красота и гигиена"))).build().perform();
        // click on electric toothbrushes subcategory
        driver.findElement(By.linkText("Электрические зубные щетки")).click();

        // entering price limits
        driver.findElement(By.id("glpricefrom")).sendKeys(String.valueOf(priceFrom));
        driver.findElement(By.id("glpriceto")).sendKeys(String.valueOf(priceTo));

        // waiting for displaying search results
        WebElement searchResult = waitTest.until(ExpectedConditions.visibilityOfElementLocated
                (By.className("_1PQIIOelRL")));
        int goodsWasFound = getResultNumFromPopup(searchResult.getText());
        int pageIterations = (int)Math.ceil((double)goodsWasFound / productsOnThePage);
        int goodsRealQuantity = 0;

        boolean wasIncorrectPrice = false;
        // going through all the goods and checking their price
        for (int i = 1; i <= pageIterations; i++){
            // waiting for loading of the page of goods
            waitTest.until(ExpectedConditions.attributeToBe(By.xpath
                   ("/html/body/div[1]/div[2]/div[2]/div[2]/div[4]/div"), "style", "height: auto;"));

            // collecting all the goods
            List<WebElement> goods;
            goods = driver.findElements(By.className("grid-snippet_react"));
            goodsRealQuantity += goods.size();

            // checking all the goods on the current page
            for (int j = 0; j < goods.size(); j++) {
                // Reads json info about the product
                String bem_str = goods.get(j).getAttribute("data-bem");
                try {
                    JSONObject obj = new JSONObject(bem_str);
                    int price = obj.getJSONObject("grid-snippet").getInt("price");
                    if (price > priceTo || price <  priceFrom)
                        wasIncorrectPrice = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // click on "next page" button if the page is not last
            if (i < pageIterations) {
                WebElement nextPageGoods = driver.findElement(By.xpath
                        ("/html/body/div[1]/div[2]/div[2]/div[2]/div[4]/div/div[3]/a[2]"));
                nextPageGoods.click();
            }
        }

        // fail test if there were any goods with incorrect price
        Assert.assertFalse(wasIncorrectPrice, "There was a good with price that doesn't belong to the price range");
        // fail test if expected number of goods doesn't match actual number
        Assert.assertEquals(goodsRealQuantity, goodsWasFound,
                "Number of goods in the popup info doesn't match the number of goods found on the result pages");

        // need to buy penultimate product in the list (if there is only 1 product on the last page)
        if (goodsWasFound % productsOnThePage == 1){
            WebElement prevPageGoods = driver.findElement(By.xpath
                    ("/html/body/div[1]/div[2]/div[2]/div[2]/div[4]/div/div[3]/a"));
            prevPageGoods.click();
        }

        
    }

    private int getResultNumFromPopup(String s)
    {
        s = s.substring(8);
        s = s.substring(0, s.indexOf(' '));
        return Integer.valueOf(s);
    }
    //@AfterTest()
    public void closeBrowser()
    {
        driver.close();
    }
}
