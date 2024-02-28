import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class UnusedResourceAnalyzer {

    private static final String WEB_DRIVER_PATH = "lib/web_driver/chromedriver";

    private static final String TARGET_WEBSITE = "https://www.google.com";

    public static void main(String[] args) throws Exception {
        System.setProperty("webdriver.chrome.driver", WEB_DRIVER_PATH);
        WebDriver driver = new ChromeDriver();

        driver.get(TARGET_WEBSITE);
        driver.quit();
        System.out.println("Hello, World!");
    }
}
