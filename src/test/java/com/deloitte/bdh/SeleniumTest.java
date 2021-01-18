package com.deloitte.bdh;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.internal.WrapsDriver;
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
public class SeleniumTest {

    private static String screenshotPath = System.getProperty("user.dir") + File.separator + "target" + File.separator + "screenshot-output";

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\junlicq\\Documents\\driver\\chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        //设置为 headless 模式
//        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--disable-infobars");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--hide-scrollbars");
        //全屏启动
        chromeOptions.addArguments("start-fullscreen");
        //全屏启动，无地址栏
        chromeOptions.addArguments("kiosk");
        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.get("https://news.qq.com/");
//        driver.findElement(By.className("ant-layout"));
        try {
            Thread.sleep(200);

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
            String screenshotName = "test";
            File storeFile = new File(screenshotPath + File.separator + screenshotName + screenshotFormat);

//            // 截图操作（可见内容）
//            File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//            FileUtils.copyFile(sourceFile, storeFile);

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


}
