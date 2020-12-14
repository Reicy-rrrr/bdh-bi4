package com.deloitte.bdh.data.analyse.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Author:LIJUN
 * Date:14/12/2020
 * Description:
 */
public class ScreenshotUtil {

    private static String screenshotPath = System.getProperty("user.dir") + File.separator + "target" + File.separator + "screenshot-output";

    private static String driverPath = "C:\\Users\\junlicq\\Documents\\driver\\chromedriver.exe";

    public static void fullScreen(String url, String screenshotName) {
        WebDriver driver = getDriver();
        driver.get(url);
        driver.findElement(By.id("body"));
        try {
            Thread.sleep(2000);
            // 截图目录
            File screenshotFile = new File(screenshotPath);
            // 若文件夹不存在就创建该文件夹
            if (!screenshotFile.exists() && !screenshotFile.isDirectory()) {
                screenshotFile.mkdirs();
            }
            // 截图格式
            String screenshotFormat = ".png";
            // 截图名称
            File storeFile = new File(screenshotPath + File.separator + screenshotName + screenshotFormat);
            BufferedImage bi = new AShot()
                    .shootingStrategy(ShootingStrategies.viewportPasting(100))
                    .takeScreenshot(driver).getImage();
            // 截图存储
            ImageIO.write(bi, "png", storeFile);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            driver.close();
        }
    }

    public static void viewScreen(String url, String screenshotName) {
        WebDriver driver = getDriver();
        driver.get(url);
        driver.findElement(By.id("body"));
        try {
            Thread.sleep(2000);

            String title = driver.getTitle();
            System.out.println(title);

            // 截图目录
            File screenshotFile = new File(screenshotPath);
            // 若文件夹不存在就创建该文件夹
            if (!screenshotFile.exists() && !screenshotFile.isDirectory()) {
                screenshotFile.mkdirs();
            }
            // 截图格式
            String screenshotFormat = ".png";
            // 截图名称
            File storeFile = new File(screenshotPath + File.separator + screenshotName + screenshotFormat);

            // 截图操作（可见内容）
            File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(sourceFile, storeFile);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            driver.close();
        }
    }

    private static WebDriver getDriver() {
        System.setProperty("webdriver.chrome.driver", driverPath);
        ChromeOptions chromeOptions = new ChromeOptions();
        //设置为 headless 模式
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--disable-infobars");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--hide-scrollbars");
        //全屏启动
        chromeOptions.addArguments("start-fullscreen");
        //全屏启动，无地址栏
        chromeOptions.addArguments("kiosk");
        return new ChromeDriver(chromeOptions);
    }
}
