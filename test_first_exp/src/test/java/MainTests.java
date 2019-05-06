import beru.DriverSettings;
import beru.LoginPage;
import beru.MainPage;
import beru.UserSettingsPage;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.List;


public class MainTests extends DriverSettings {
    @DataProvider(name = "cityChangeTest")
    public Object[][] createData(){
        return new Object[][]{
                {"Хвалынск"},
                {"Саратов"}
        };
    }
    private static int productsOnThePage = 24;
    private static int priceFrom = 999, priceTo = 1999;
    private static int freeDeliverySum = 2499;

    @Test
    public void signInTest() {
        MainPage main = new MainPage(getDriver(), getWait());
        main.clickLoginButton();

        LoginPage login = new LoginPage(getDriver(), getWait());
        login.enterLogin();
        login.clickContinueButton();
        login.enterPassword();
        login.clickContinueButton();

        main.checkMyProfile();
        main.checkLoginName();
    }

    @Test(dataProvider = "cityChangeTest")
    public void cityChangeTest(String newCityName) {
        MainPage main = new MainPage(getDriver(), getWait());
        main.clickCityName();
        main.enterCityName(newCityName);
        main.checkCityName(newCityName);

        main.clickLoginButton();
        LoginPage login = new LoginPage(getDriver(), getWait());
        login.enterLogin();
        login.clickContinueButton();
        login.enterPassword();
        login.clickContinueButton();

        main.clickSettingsButton();
        UserSettingsPage settings = new UserSettingsPage(getDriver());
        settings.checkCityName(newCityName);
    }

    @Test
    public void shoppingTest() {
        // click on catalog button
        driver.findElement(By.className("header2__navigation")).click();

        // waiting for opening the list of categories
        WebElement categoryList = waitTest.until
                (ExpectedConditions.visibilityOfElementLocated(By.className("popup2__content")));
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
            waitTest.until(ExpectedConditions.attributeToBe(By.className
                   ("n-filter-applied-results__content"), "style", "height: auto;"));

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
            if (i < pageIterations)
                driver.findElement(By.className("n-pager__button-next")).click();
        }

        // fail test if there were any goods with incorrect price
        Assert.assertFalse(wasIncorrectPrice, "There was a good with price that doesn't belong to the price range.");
        // fail test if expected number of goods doesn't match actual number
        Assert.assertEquals(goodsRealQuantity, goodsWasFound,
                "Number of goods in the popup info doesn't match the number of goods found on the result pages.");

        // need to buy penultimate product in the list (if there is only 1 product on the last page)
        if (goodsWasFound % productsOnThePage == 1){
            WebElement prevPageGoods = driver.findElement(By.className("n-pager__button-prev"));
            prevPageGoods.click();
        }

        // add penultimate product to the cart
        waitTest.until(ExpectedConditions.attributeToBe(By.className
                ("n-filter-applied-results__content"), "style", "height: auto;"));
        List<WebElement> goods = driver.findElements(By.className("_2w0qPDYwej"));
        goods.get(goods.size() - 2).click();

        // goes to the cart
        waitTest.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Перейти в корзину")));
        driver.findElement(By.linkText("Перейти в корзину")).click();

        // check of "Until free delivery left" value
        waitTest.until(ExpectedConditions.visibilityOfElementLocated(By.className("_3EX9adn_xp")));
        WebElement deliveryMessage = driver.findElement(By.className("_3EX9adn_xp"));
        Assert.assertTrue(deliveryMessage.getText().contains("До бесплатной доставки"),
                "There is no message about free delivery");

        int deliveryRemainSum = getMoneyValue(driver.findElement(By.className("voCFmXKfcL")).getText());
        int goodsSum = getMoneyValue(driver.findElement(By.cssSelector("[data-auto=\"total-items\"]")).getText());
        int deliverySum = getMoneyValue(driver.findElement(By.cssSelector("[data-auto=\"total-delivery\"]")).getText());
        int totalSum = getMoneyValue(driver.findElement(By.cssSelector("[data-auto=\"total-price\"]")).getText());

        Assert.assertEquals(totalSum, goodsSum + deliverySum,
                "Total sum doesn't equal product price + delivery price.");
        Assert.assertEquals(deliveryRemainSum, freeDeliverySum - goodsSum,
                "\"Until free delivery left\" value doesn't equal " + freeDeliverySum + " - goods price.");

        // add products until free delivery is achieved
        while (deliveryRemainSum < freeDeliverySum){
            driver.findElement(By.className("_3hWhO4rvmA")).click();
            deliveryRemainSum += goodsSum;
        }

        // check that there is a message about free delivery
        deliveryMessage = driver.findElement(By.className("_3EX9adn_xp"));
        waitTest.until(ExpectedConditions.textToBePresentInElement(deliveryMessage, "Поздравляем"));
        Assert.assertTrue(deliveryMessage.getText().contains("Вы получили бесплатную доставку"),
                "There is no message about free delivery");
        goodsSum = getMoneyValue(driver.findElement(By.cssSelector("[data-auto=\"total-items\"]")).getText());
        totalSum = getMoneyValue(driver.findElement(By.cssSelector("[data-auto=\"total-price\"]")).getText());

        Assert.assertEquals(totalSum, goodsSum, "Total sum doesn't equal products sum.");
    }

    private int getMoneyValue(String s){
        s = s.replaceAll("\\(.*\\)|\\D", "");
        return Integer.valueOf(s);
    }

    private int getResultNumFromPopup(String s)
    {
        s = s.substring(8);
        s = s.substring(0, s.indexOf(' '));
        return Integer.valueOf(s);
    }
}
