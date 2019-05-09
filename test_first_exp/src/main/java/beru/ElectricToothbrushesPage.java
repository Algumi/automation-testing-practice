package beru;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.util.List;

public class ElectricToothbrushesPage {
    private WebDriver driver;
    private WebDriverWait waitTest;

    private By priceFromInput = By.id("glpricefrom");
    private By priceToInput = By.id("glpriceto");
    private By searchResultPopup = By.className("_1PQIIOelRL");
    private By nextPageButton = By.className("n-pager__button-next");
    private By previousPageButton = By.className("n-pager__button-prev");
    private By tableOfGoods = By.className("n-filter-applied-results__content");
    private By productInTheTable = By.className("grid-snippet_react");
    private By addToCartButton = By.className("_2w0qPDYwej");
    private By productAddedToCartPopup = By.className("_3UjOWy-LbN");
    private By goToCartButton = By.linkText("Перейти в корзину");

    private static int priceFrom = 999, priceTo = 1999;

    public ElectricToothbrushesPage(WebDriver driver, WebDriverWait wait){
        this.driver = driver;
        this.waitTest = wait;
    }

    public void enterPriceLimits(){
        driver.findElement(priceFromInput).sendKeys(String.valueOf(priceFrom));
        driver.findElement(priceToInput).sendKeys(String.valueOf(priceTo));
    }

    public int getBrushesQuantity(){
        // waiting for displaying search results
        WebElement searchResult = waitTest.until(ExpectedConditions.visibilityOfElementLocated(searchResultPopup));
        return getResultNumFromPopup(searchResult.getText());
    }

    public void checkAllBrushes(int expectedQuantity){
        boolean nextBtnIsPresent = true;
        int actualGoodsQuantity = 0;

        // going through all the goods and checking their price
        while(nextBtnIsPresent){
            // waiting for page of goods loading
            waitTest.until(ExpectedConditions.attributeToBe(tableOfGoods, "style", "height: auto;"));

            // collecting all the goods
            List<WebElement> goods;
            goods = driver.findElements(productInTheTable);
            actualGoodsQuantity += goods.size();

            // checking all the goods on the current page
            for (WebElement product : goods) {
                // Reads json info about the product
                String bem_str = product.getAttribute("data-bem");
                try {
                    JSONObject obj = new JSONObject(bem_str);
                    int price = obj.getJSONObject("grid-snippet").getInt("price");
                    // fail test if there were any goods with incorrect price
                    Assert.assertTrue(price >= priceFrom && price <= priceTo,
                            "There was a good with price that doesn't belong to the price range.");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            nextBtnIsPresent = driver.findElements(nextPageButton).size() > 0;
            if (nextBtnIsPresent) driver.findElement(nextPageButton).click();
        }
        // fail test if expected number of goods doesn't match actual number
        Assert.assertEquals(actualGoodsQuantity, expectedQuantity,
                "Number of goods in the popup info doesn't match the number of goods found on the result pages.");
    }

    public void addPenultimateBrush(){
        // need to buy penultimate product in the list (if there is only 1 product on the last page)
        if (driver.findElements(productInTheTable).size() == 1){
            WebElement prevPageGoods = driver.findElement(previousPageButton);
            prevPageGoods.click();
        }

        // add penultimate product to the cart
        waitTest.until(ExpectedConditions.attributeToBe(tableOfGoods, "style", "height: auto;"));
        List<WebElement> goods = driver.findElements(addToCartButton);
        goods.get(goods.size() - 2).click();
    }

    public void clickCartButton(){
        waitTest.until(ExpectedConditions.textToBePresentInElementLocated
                        (productAddedToCartPopup, "Товар добавлен в корзину!"));
        driver.findElement(goToCartButton).click();
    }

    private int getResultNumFromPopup(String s)
    {
        s = s.replaceAll("\\D", "");
        return Integer.valueOf(s);
    }
}
