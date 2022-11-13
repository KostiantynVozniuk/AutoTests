package com.vozniuk;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class AutoTestsLab2 {

    public static final String SEARCH_INPUT_VENDOR = "/html/body/div/div[2]/div[1]/form/div[5]/div/div/div[1]/div[1]/div/div/div[2]/div/input";
    public static final String LI_VENDOR_FIRST_ELEMENT = "/html/body/div/div[2]/div[1]/form/div[5]/div/div/div[1]/div[1]/div/div/div[2]/div/ul/li[1]/a";
    public static final String SUBMIT_BUTTON = "/html/body/div/div[2]/div/form/div[6]/div/div/button";
    public static final String TOP_PRICE_ELEMENT = "/html/body/div/div[2]/div[1]/form/section[2]/div/div[1]/div[1]/div/input[2]";
    public static final String ADVANCED_SEARCH = "/html/body/div[1]/main/div[3]/div[1]/form/div[3]/a";
    public static final String ACCEPT_COOKIES = "/html/body/div/div[3]/section/div/div[1]/div[2]/label[1]";
    public static final String MAX_RUN = "/html/body/div/div[2]/div[1]/form/section[5]/div[7]/div/div/div/input[2]";
    public static final String NON_CRUSHED = "/html/body/div/div[2]/div[1]/form/section[4]/div[1]/div/select/option[2]";
    public static final String SEDAN_CHECKBOX = "/html/body/div/div[2]/div[1]/form/div[3]/div/div/label[2]";
    public static final String YEAR_2014 = "/html/body/div/div[2]/div/form/div[5]/div/div/div[2]/div/div/select[1]/option[10]";
    public static final String ADDITIONAL_SEARCH_LI_FIRST_ELEMENT = "/html/body/div/div[2]/div[1]/form/div[5]/div/div[2]/div[1]/div[1]/div/div/div[2]/div/ul/li/a";
    public static final String BMW = "BMW";
    public static final String AUDI = "AUDI";
    private WebDriver webDriver;

    private static final String url = "https://auto.ria.com/uk/";


    @BeforeClass(alwaysRun = true)
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-fullscreen");
        options.setImplicitWaitTimeout(Duration.ofSeconds(25));

        webDriver = new ChromeDriver(options);
    }

    @BeforeMethod
    public void openPage() {
        webDriver.get(url);
    }

    @AfterClass(alwaysRun = true)
    @SneakyThrows
    public void quit() {
        webDriver.quit();
    }

    @
            Test
    public void not_much_new_BMW_without_any_crash_and_low_price() {
        webDriver.findElement(By.xpath(ADVANCED_SEARCH)).click();
        webDriver.findElement(By.xpath(ACCEPT_COOKIES)).click();

        WebElement submit = webDriver.findElement(By.xpath(SUBMIT_BUTTON));

        //Look for VIN only cars
        webDriver.findElement(By.cssSelector("label[for='verifiedVIN']")).click();

        //Select BMW CARS
        webDriver.findElement(By.xpath(SEARCH_INPUT_VENDOR)).sendKeys("BMW");
        webDriver.findElement(By.xpath(LI_VENDOR_FIRST_ELEMENT)).click();

        //Select car options
        webDriver.findElement(By.xpath(TOP_PRICE_ELEMENT)).sendKeys("15000");
        webDriver.findElement(By.xpath(SEDAN_CHECKBOX)).click();
        webDriver.findElement(By.xpath(MAX_RUN)).sendKeys("180");
        webDriver.findElement(By.xpath(NON_CRUSHED)).click();
        webDriver.findElement(By.xpath(YEAR_2014)).click();

        submit.click();

        Integer results = Integer.valueOf(webDriver.findElement(By.id("staticResultsCount")).getText());

        Predicate<Integer> resultPredicate = result -> result < 30;

        Assert.assertTrue(resultPredicate.test(results));
    }

    @Test
    public void only_selected_vendors_cars_on_page() {
        webDriver.findElement(By.xpath(ADVANCED_SEARCH)).click();
        webDriver.findElement(By.xpath(ACCEPT_COOKIES)).click();

        WebElement submit = webDriver.findElement(By.xpath(SUBMIT_BUTTON));

        //Select vendors
        webDriver.findElement(By.xpath(SEARCH_INPUT_VENDOR)).sendKeys(BMW);
        webDriver.findElement(By.xpath(LI_VENDOR_FIRST_ELEMENT)).click();
        webDriver.findElement(By.cssSelector("#app > div:nth-child(2) > div.app-content.extended > form > div:nth-child(5) > div > a")).click();
        webDriver.findElement(By.id("autocompleteInput-brand-1")).sendKeys(AUDI);
        webDriver.findElement(By.xpath(ADDITIONAL_SEARCH_LI_FIRST_ELEMENT)).click();

        submit.click();

        //Read all found cars names
        List<String> names =  webDriver.findElements(By.className("ticket-item"))
                                       .stream()
                                       .map(card -> card.findElement(By.className("address")).findElement(By.cssSelector("span")).getText())
                                       .collect(Collectors.toList());

        System.out.println(names);

        Predicate<List<String>> namingPredicate = list -> list.stream().allMatch(name -> name.toUpperCase().contains(BMW) || name.toUpperCase().contains(AUDI));

        Assert.assertTrue(namingPredicate.test(names));
    }


}
