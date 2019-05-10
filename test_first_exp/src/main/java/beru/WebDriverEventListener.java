package beru;

import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

public class WebDriverEventListener extends AbstractWebDriverEventListener {
    @Attachment(value = "Page screenshot", type = "image/png")
    private byte[] getScreenshot() {
        return ((TakesScreenshot) DriverSettings.getDriver()).getScreenshotAs(OutputType.BYTES);
    }

    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        getScreenshot();
    }

    @Override
    public void afterClickOn(WebElement element, WebDriver driver) {
        getScreenshot();
    }

    @Override
    public void afterChangeValueOf(WebElement element, WebDriver driver, java.lang.CharSequence[] keysToSend){
        getScreenshot();
    }
}
