package com.deloitte.bdh.data.analyse.utils;

import com.deloitte.bdh.common.properties.OssProperties;
import com.deloitte.bdh.common.util.AliyunOssUtil;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.common.util.UUIDUtil;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Author:LIJUN
 * Date:14/12/2020
 * Description:
 */
@Slf4j
@Component
public class ScreenshotUtil {

    @Resource
    private AliyunOssUtil aliyunOssUtil;

    @Autowired
    private OssProperties ossProperties;

    @Value("${bi.analyse.driverPath}")
    private String driverPath;

//    private static String screenshotPath = System.getProperty("user.dir") + File.separator + "target" + File.separator + "screenshot-output";

    /**
     * 全屏截图
     * @param url
     * @return
     * @throws Exception
     */
    public String fullScreen(String url) throws Exception {
//        url = "https://news.qq.com/";
        WebDriver driver = getDriver();
        driver.get(url);
//        driver.findElement(By.className("ant-layout"));
        try {
            Thread.sleep(2000);
            // 截图格式
            String screenshotFormat = ".png";
            // 截图名称
            String screenshotName = GenerateCodeUtil.genShot();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            BufferedImage bi = new AShot()
                    .shootingStrategy(ShootingStrategies.viewportPasting(100))
                    .takeScreenshot(driver).getImage();
            // 截图存储
            ImageIO.write(bi, "png", os);

            InputStream input = new ByteArrayInputStream(os.toByteArray());
            String filePath = AnalyseConstants.DOCUMENT_DIR + ThreadLocalHolder.getTenantCode() + "/bi/subscribe/";
            String fileName = screenshotName + screenshotFormat;
            aliyunOssUtil.uploadFile2OSS(input, filePath, fileName);
            String imageUrl = aliyunOssUtil.getImgUrl(filePath, fileName);
            // 对于在内网上传的文件需要把内网地址换为外网地址
            if (imageUrl.contains(ossProperties.getTargetEndpoint())) {
                imageUrl = imageUrl.replace(ossProperties.getTargetEndpoint(),
                        ossProperties.getReplacementEndpoint());
            }
            return imageUrl;
        } catch (IOException | InterruptedException e) {
            log.error("截图失败：" + e.getMessage());
            throw e;
        } finally {
            driver.close();
        }
    }

    /**
     * 可视区域截图
     * @param url
     * @return
     * @throws Exception
     */
    public String viewScreen(String url, String screenshotName) throws Exception {
        WebDriver driver = getDriver();
        driver.get(url);
        driver.findElement(By.id("body"));
        try {
            Thread.sleep(2000);

            // 截图格式
            String screenshotFormat = ".png";
            // 截图操作（可见内容）
            File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            InputStream input = new FileInputStream(sourceFile);
            String filePath = AnalyseConstants.DOCUMENT_DIR + ThreadLocalHolder.getTenantCode() + "/bi/subscribe/";
            String fileName = screenshotName + screenshotFormat;
            aliyunOssUtil.uploadFile2OSS(input, filePath, fileName);
            String imageUrl = aliyunOssUtil.getImgUrl(filePath, fileName);
            // 对于在内网上传的文件需要把内网地址换为外网地址
            if (imageUrl.contains(ossProperties.getTargetEndpoint())) {
                imageUrl = imageUrl.replace(ossProperties.getTargetEndpoint(),
                        ossProperties.getReplacementEndpoint());
            }
            return imageUrl;
        } catch (IOException | InterruptedException e) {
            log.error("截图失败：" + e.getMessage());
            throw e;
        } finally {
            driver.close();
        }
    }

    private WebDriver getDriver() {
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
