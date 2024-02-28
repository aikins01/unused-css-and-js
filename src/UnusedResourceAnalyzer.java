import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class UnusedResourceAnalyzer {

    private static final String WEB_DRIVER_PATH = "lib/web_driver/chromedriver";

    private static final String TARGET_WEBSITE = "https://www.ghanaweb.com";

    public static void main(String[] args) throws Exception {
        System.setProperty("webdriver.chrome.driver", WEB_DRIVER_PATH);
        WebDriver driver = new ChromeDriver();

        driver.get(TARGET_WEBSITE);
        String html = driver.getPageSource();
        driver.quit();

        Document doc = Jsoup.parse(html);

        // collect all resources
        Set<String> referencedResources = new HashSet<>();
        Elements scriptTags = doc.getElementsByTag("script");
        for (Element scriptTag : scriptTags) {
            String src = scriptTag.attr("src");
            if (!src.isEmpty()) {
                referencedResources.add(src);
            }
        }
        Elements linkTags = doc.getElementsByTag("link");
        for (Element linkTag : linkTags) {
            String href = linkTag.attr("href");
            if (!href.isEmpty() && linkTag.attr("rel").equals("stylesheet")) {
                referencedResources.add(href);
            }
        }

        System.out.println("Referenced resources:");
        for (String resource : referencedResources) {
            System.out.println(resource);
        }

        List<String> potentiallyUnusedResources = new ArrayList<>();
        Elements allElements = doc.getAllElements();
        for (String resource : referencedResources) {
            boolean found = false;
            for (Element element : allElements) {
                if (element.attr("src").equals(resource) || element.attr("href").equals(resource)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                potentiallyUnusedResources.add(resource);
            }
        }

        System.out.println("\nPotentially unused resources:");
        for (String resource : potentiallyUnusedResources) {
            System.out.println(resource);
        }
    }
}
