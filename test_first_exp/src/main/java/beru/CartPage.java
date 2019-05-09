package beru;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class CartPage {
    private WebDriver driver;
    private WebDriverWait waitTest;

    private By totalAmountWidget = By.className("_1n63a5bOO8");
    private By deliveryText = By.className("_3EX9adn_xp");
    private By deliveryRemainFreeMessage = By.className("voCFmXKfcL");
    private By goodsCost = By.cssSelector("[data-auto=\"total-items\"]");
    private By deliveryCost = By.cssSelector("[data-auto=\"total-delivery\"]");
    private By totalSumValue = By.cssSelector("[data-auto=\"total-price\"]");

    private int freeDeliverySum = 2499;

    public CartPage(WebDriver driver, WebDriverWait wait){
        this.driver = driver;
        this.waitTest = wait;
    }

    public void checkDeliveryMessage(){
        waitTest.until(ExpectedConditions.textToBePresentInElementLocated(totalAmountWidget, "Итого"));
        Assert.assertTrue(driver.findElement(deliveryText).getText().contains("До бесплатной доставки"),
                "There is no message about free delivery");
    }

    public void checkMoneySums(){
        int deliveryRemainSum = getMoneyValue(driver.findElement(deliveryRemainFreeMessage).getText());
        int goodsSum = getMoneyValue(driver.findElement(goodsCost).getText());
        int deliverySum = getMoneyValue(driver.findElement(deliveryCost).getText());
        int totalSum = getMoneyValue(driver.findElement(totalSumValue).getText());

        Assert.assertEquals(totalSum, goodsSum + deliverySum,
                "Total sum doesn't equal product price + delivery price.");
        Assert.assertEquals(deliveryRemainSum, freeDeliverySum - goodsSum,
                "\"Until free delivery left\" value doesn't equal " + freeDeliverySum + " - goods price.");
    }

    public void addProductForFreeDelivery(){
        int currentGoodsCost = getMoneyValue(driver.findElement(goodsCost).getText());
        int oneProductCost = currentGoodsCost;
        // add products until free delivery is achieved
        while (currentGoodsCost < freeDeliverySum){
            driver.findElement(By.className("_3hWhO4rvmA")).click();
            currentGoodsCost += oneProductCost;
        }
    }

    public void checkFreeDelivery(){
        // check that there is a message about free delivery
        WebElement deliveryMessage = driver.findElement(deliveryText);
        waitTest.until(ExpectedConditions.textToBePresentInElement(deliveryMessage, "Поздравляем"));
        Assert.assertTrue(deliveryMessage.getText().contains("Вы получили бесплатную доставку"),
                "There is no message about free delivery");
        int goodsSum = getMoneyValue(driver.findElement(goodsCost).getText());
        int totalSum = getMoneyValue(driver.findElement(totalSumValue).getText());

        Assert.assertEquals(totalSum, goodsSum, "Total sum doesn't equal products sum.");
    }

    private int getMoneyValue(String s){
        s = s.replaceAll("\\(.*\\)|\\D", "");
        return Integer.valueOf(s);
    }
}
