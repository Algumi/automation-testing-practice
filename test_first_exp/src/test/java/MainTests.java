import beru.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class MainTests extends DriverSettings {
    @DataProvider(name = "cityChangeTest")
    public Object[][] createData(){
        return new Object[][]{
                {"Хвалынск"},
                {"Саратов"}
        };
    }

    @Test
    public void signInTest() {
        MainPage main = new MainPage(getDriver(), getWait());
        main.clickLoginButton();

        LoginPage login = new LoginPage(getDriver());
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
        LoginPage login = new LoginPage(getDriver());
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
        MainPage main = new MainPage(getDriver(), getWait());
        main.clickCatalogButton();
        main.clickToothbrushSection();

        ElectricToothbrushesPage toothbrushes = new ElectricToothbrushesPage(getDriver(), getWait());
        toothbrushes.enterPriceLimits();
        int expectedBrushesQuantity = toothbrushes.getBrushesQuantity();
        toothbrushes.checkAllBrushes(expectedBrushesQuantity);
        toothbrushes.addPenultimateBrush();
        toothbrushes.clickCartButton();

        CartPage cart = new CartPage(getDriver(), getWait());
        cart.checkDeliveryMessage();
        cart.checkMoneySums();
        cart.addProductForFreeDelivery();
        cart.checkFreeDelivery();
    }

}
