import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

public class AuthTest {

    @Test
    public void navigateToBeruAuthPage(){
        System.setProperty("webdriver.chrome.driver","drv/chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://beru.ru/");

        //driver.
    }
}
