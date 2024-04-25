
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class FindWebsitePagesWithUnusedResources {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "lib/webdrivers/chromedriver");
        WebDriver driver = new ChromeDriver();
        String url = "https://www.ghanaweb.com";
        driver.get(url);
        Set<String> visitedPages = new HashSet<>();
        explorePages(driver, visitedPages, url);
        driver.quit();
        System.out.println("Found " + visitedPages.size() + " unique pages:");
        for (String page : visitedPages) {
            System.out.println(page);
        }
    }

    private static void explorePages(WebDriver driver, Set<String> visitedPages, String currentPageUrl) {
        if (visitedPages.contains(currentPageUrl)) {
            return;
        }
        visitedPages.add(currentPageUrl);

        // Get the page source
        String pageSource = driver.getPageSource();

        List<WebElement> links = driver.findElements(By.tagName("a"));
        for (WebElement link : links) {
            try {
                String linkUrl = link.getAttribute("href");
                if (linkUrl != null && linkUrl.startsWith("https://www.history.navy.mil/")
                        && !linkUrl.equals("https://www.history.navy.mil/")) {
                    System.out.println("found a link in the current domain " + linkUrl);

                    // Analyze unused resources for the current page
                    List<String> potentiallyUnusedResources = analyzeUnusedResources(pageSource);
                    if (potentiallyUnusedResources.isEmpty()) {
                        System.out.println("No potentially unused resources found for " + currentPageUrl);
                    } else {
                        System.out.println("Potentially unused resources for " + currentPageUrl + ":");
                        for (String resource : potentiallyUnusedResources) {
                            System.out.println(resource);
                        }
                    }

                    explorePages(driver, visitedPages, linkUrl);
                }
            } catch (StaleElementReferenceException e) {
                System.out.println("StaleElementReferenceException. Re-finding the elements.");
                links = driver.findElements(By.tagName("a"));
            }
        }
    }

    private static List<String> analyzeUnusedResources(String pageSource) {
        Document doc = Jsoup.parse(pageSource);
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

        return potentiallyUnusedResources;
    }
}