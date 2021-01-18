package com.deloitte.bdh.common.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class FtpUtil {
    /**
     * 日志对象
     **/
    private static final Logger logger = LoggerFactory.getLogger(FtpUtil.class);

    /**
     * FTP地址
     **/
    private String host;

    /**
     * FTP端口
     **/
    private int port;

    /**
     * FTP用户名
     **/
    private String username;

    /**
     * FTP密码
     **/
    private String password;

    /**
     * FTP基础目录
     **/
    private static final String BASE_PATH = "/";

    /**
     * 本地字符编码
     **/
    private static String localCharset = "GBK";

    /**
     * FTP协议里面，规定文件名编码为iso-8859-1
     **/
    private static String serverCharset = "ISO-8859-1";

    /**
     * UTF-8字符编码
     **/
    private static final String CHARSET_UTF8 = "UTF-8";

    /**
     * OPTS UTF8字符串常量
     **/
    private static final String OPTS_UTF8 = "OPTS UTF8";

    /**
     * 设置缓冲区大小4M
     **/
    private static final int BUFFER_SIZE = 1024 * 1024 * 10;

    /**
     * FTPClient对象
     **/
    private static FTPClient ftpClient = null;

    public FtpUtil(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * 上传文件到FTP服务器
     *
     * @param ftpPath  FTP服务器文件相对路径，例如：test/123
     * @param fileName 上传到FTP服务的文件名，例如：666.txt
     * @param is       文件输入流
     * @return boolean 成功返回true，否则返回false
     */
    public boolean uploadFile(String ftpPath, String fileName, InputStream is) {
        // 登录
        login();
        boolean flag = false;
        if (ftpClient != null) {
            try {
                ftpClient.setBufferSize(BUFFER_SIZE);
                // 设置编码：开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）
                if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(OPTS_UTF8, "ON"))) {
                    localCharset = CHARSET_UTF8;
                }
                ftpClient.setControlEncoding(localCharset);
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                String path = initPath(ftpPath);
                // 目录不存在，则递归创建
                if (!ftpClient.changeWorkingDirectory(path)) {
                    this.createDirectories(path);
                }
                // 设置被动模式，开通一个端口来传输数据
//                ftpClient.enterLocalPassiveMode();
                // 上传文件
                flag = ftpClient.storeFile(new String(fileName.getBytes(localCharset), serverCharset), is);
            } catch (Exception e) {
                logger.error("本地文件上传FTP失败", e);
            } finally {
                closeConnect();
            }
        }
        return flag;
    }

    /**
     * 本地文件上传到FTP服务器
     *
     * @param ftpPath   FTP服务器文件相对路径，例如：test/123
     * @param localPath 本地文件路径，例如：D:/test/123/test.txt
     * @param fileName  上传到FTP服务的文件名，例如：666.txt
     * @return boolean 成功返回true，否则返回false
     */
    public boolean uploadLocalFile(String ftpPath, String localPath, String fileName) {
        // 登录
        login();
        boolean flag = false;
        if (ftpClient != null) {
            File file = new File(localPath);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                ftpClient.setBufferSize(BUFFER_SIZE);
                // 设置编码：开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）
                if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(OPTS_UTF8, "ON"))) {
                    localCharset = CHARSET_UTF8;
                }
                ftpClient.setControlEncoding(localCharset);
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

                String path = initPath(ftpPath);
                // 目录不存在，则递归创建
                if (!ftpClient.changeWorkingDirectory(path)) {
                    this.createDirectories(path);
                }
                // 设置被动模式，开通一个端口来传输数据
//                ftpClient.enterLocalPassiveMode();
                // 上传文件
                flag = ftpClient.storeFile(new String(fileName.getBytes(localCharset), serverCharset), fis);
            } catch (Exception e) {
                logger.error("local file upload error", e);
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    fis = null;
                }
                closeConnect();
            }
        }
        return flag;
    }

    /**
     * 远程文件上传到FTP服务器
     *
     * @param ftpPath    FTP服务器文件相对路径，例如：test/123
     * @param remotePath 远程文件路径，例如：http://www.baidu.com/xxx/xxx.jpg
     * @param fileName   上传到FTP服务的文件名，例如：test.jpg
     * @return boolean 成功返回true，否则返回false
     */
    public boolean uploadRemoteFile(String ftpPath, String remotePath, String fileName) {
        // 登录
        login();
        boolean flag = false;
        if (ftpClient != null) {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = null;
            try {
                // 远程获取文件输入流
                HttpGet httpget = new HttpGet(remotePath);
                response = httpClient.execute(httpget);
                HttpEntity entity = response.getEntity();
                InputStream input = entity.getContent();
                ftpClient.setBufferSize(BUFFER_SIZE);
                // 设置编码：开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）
                if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(OPTS_UTF8, "ON"))) {
                    localCharset = CHARSET_UTF8;
                }
                ftpClient.setControlEncoding(localCharset);
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                String path = initPath(ftpPath);
                // 目录不存在，则递归创建
                if (!ftpClient.changeWorkingDirectory(path)) {
                    this.createDirectories(path);
                }
                // 设置被动模式，开通一个端口来传输数据
//                ftpClient.enterLocalPassiveMode();
                // 上传文件
                flag = ftpClient.storeFile(new String(fileName.getBytes(localCharset), serverCharset), input);
            } catch (Exception e) {
                logger.error("remote file upload error", e);
            } finally {
                closeConnect();
                try {
                    httpClient.close();
                } catch (IOException e) {
                    httpClient = null;
                }
                if (response != null) {
                    try {
                        response.close();
                    } catch (IOException e) {
                        response = null;
                    }
                }
            }
        }
        return flag;
    }


    /**
     * 下载指定文件到本地
     *
     * @param ftpPath  FTP服务器文件相对路径，例如：test/123
     * @param fileName 要下载的文件名，例如：test.txt
     * @param savePath 保存文件到本地的路径，例如：D:/test
     * @return 成功返回true，否则返回false
     */
    public boolean downloadFile(String ftpPath, String fileName, String savePath) {
        // 登录
        login();
        boolean flag = false;
        if (ftpClient != null) {
            try {
                String path = initPath(ftpPath);
                // 判断是否存在该目录
                if (!ftpClient.changeWorkingDirectory(path)) {
                    logger.error(BASE_PATH + ftpPath + " not exists");
                    return flag;
                }
//                ftpClient.enterLocalPassiveMode();  // 设置被动模式，开通一个端口来传输数据
                String[] listNames = ftpClient.listNames();
                // 判断该目录下是否有文件
                if (listNames == null || listNames.length == 0) {
                    logger.error(BASE_PATH + ftpPath + " no file found");
                    return flag;
                }
                for (String ff : listNames) {
                    String ftpName = new String(ff.getBytes(serverCharset), localCharset);
                    if (ftpName.equals(fileName)) {
                        File file = new File(savePath + '/' + ftpName);
                        try (OutputStream os = new FileOutputStream(file)) {
                            flag = ftpClient.retrieveFile(ff, os);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                logger.error("file download error", e);
            } finally {
                closeConnect();
            }
        }
        return flag;
    }

    /**
     * 下载该目录下所有文件到本地
     *
     * @param ftpPath  FTP服务器上的相对路径，例如：test/123
     * @param savePath 保存文件到本地的路径，例如：D:/test
     * @return 成功返回true，否则返回false
     */
    public boolean downloadFiles(String ftpPath, String savePath) {
        // 登录
        login();
        boolean flag = false;
        if (ftpClient != null) {
            try {
                String path = initPath(ftpPath);
                // 判断是否存在该目录
                if (!ftpClient.changeWorkingDirectory(path)) {
                    logger.error(BASE_PATH + ftpPath + " not exists");
                    return flag;
                }
//                ftpClient.enterLocalPassiveMode();  // 设置被动模式，开通一个端口来传输数据
                String[] fs = ftpClient.listNames();
                // 判断该目录下是否有文件
                if (fs == null || fs.length == 0) {
                    logger.error(BASE_PATH + ftpPath + " no file found");
                    return flag;
                }
                for (String ff : fs) {
                    String ftpName = new String(ff.getBytes(serverCharset), localCharset);
                    File file = new File(savePath + '/' + ftpName);
                    try (OutputStream os = new FileOutputStream(file)) {
                        ftpClient.retrieveFile(ff, os);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                flag = true;
            } catch (IOException e) {
                logger.error("download file error", e);
            } finally {
                closeConnect();
            }
        }
        return flag;
    }

    /**
     * 获取该目录下所有文件,以字节数组返回
     *
     * @param ftpPath FTP服务器上文件所在相对路径，例如：test/123
     * @return Map<String, Object> 其中key为文件名，value为字节数组对象
     */
    public Map<String, byte[]> getFileBytes(String ftpPath) {
        // 登录
        login();
        Map<String, byte[]> map = new HashMap<>();
        if (ftpClient != null) {
            try {
                String path = initPath(ftpPath);
                // 判断是否存在该目录
                if (!ftpClient.changeWorkingDirectory(path)) {
                    logger.error(BASE_PATH + ftpPath + " not exists");
                    return map;
                }
//                ftpClient.enterLocalPassiveMode();  // 设置被动模式，开通一个端口来传输数据
                String[] fs = ftpClient.listNames();
                // 判断该目录下是否有文件
                if (fs == null || fs.length == 0) {
                    logger.error(BASE_PATH + ftpPath + " no file found");
                    return map;
                }
                for (String ff : fs) {
                    try (InputStream is = ftpClient.retrieveFileStream(ff)) {
                        String ftpName = new String(ff.getBytes(serverCharset), localCharset);
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int readLength = 0;
                        while ((readLength = is.read(buffer, 0, BUFFER_SIZE)) > 0) {
                            byteStream.write(buffer, 0, readLength);
                        }
                        map.put(ftpName, byteStream.toByteArray());
                        ftpClient.completePendingCommand(); // 处理多个文件
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            } catch (IOException e) {
                logger.error("get file error", e);
            } finally {
                closeConnect();
            }
        }
        return map;
    }

    /**
     * 根据名称获取文件，以字节数组返回
     *
     * @param ftpPath  FTP服务器文件相对路径，例如：test/123
     * @param fileName 文件名，例如：test.xls
     * @return byte[] 字节数组对象
     */
    public byte[] getFileBytesByName(String ftpPath, String fileName) {
        // 登录
        login();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        if (ftpClient != null) {
            try {
                String path = initPath(ftpPath);
                // 判断是否存在该目录
                if (!ftpClient.changeWorkingDirectory(path)) {
                    logger.error(BASE_PATH + ftpPath + " not exists");
                    return byteStream.toByteArray();
                }
//                ftpClient.enterLocalPassiveMode();  // 设置被动模式，开通一个端口来传输数据
                String[] fs = ftpClient.listNames();
                // 判断该目录下是否有文件
                if (fs == null || fs.length == 0) {
                    logger.error(BASE_PATH + ftpPath + " no file found");
                    return byteStream.toByteArray();
                }
                for (String ff : fs) {
                    String ftpName = new String(ff.getBytes(serverCharset), localCharset);
                    if (ftpName.equals(fileName)) {
                        try (InputStream is = ftpClient.retrieveFileStream(ff);) {
                            byte[] buffer = new byte[BUFFER_SIZE];
                            int len = -1;
                            while ((len = is.read(buffer, 0, BUFFER_SIZE)) != -1) {
                                byteStream.write(buffer, 0, len);
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                logger.error("get file error", e);
            } finally {
                closeConnect();
            }
        }
        return byteStream.toByteArray();
    }

    /**
     * 下载指定文件到本地
     *
     * @param ftpPath  FTP服务器文件相对路径，例如：test/123
     * @param savePath 保存文件到本地的路径，例如：D:/test
     * @return 成功返回true，否则返回false
     */
    public boolean downloadLatestFile(String ftpPath, String savePath) {
        // 登录
        login();
        boolean flag = false;
        if (ftpClient != null) {
            try {
                String path = initPath(ftpPath);
                // 判断是否存在该目录
                if (!ftpClient.changeWorkingDirectory(path)) {
                    logger.error(BASE_PATH + ftpPath + " not exists");
                    return flag;
                }
//                ftpClient.enterLocalPassiveMode();  // 设置被动模式，开通一个端口来传输数据
                FTPFile[] ftpFiles = ftpClient.listFiles();
                if (ftpFiles == null || ftpFiles.length == 0) {
                    logger.error(BASE_PATH + ftpPath + " no file found");
                    return flag;
                }
                Arrays.sort(ftpFiles, new Comparator<FTPFile>() {
                    @Override
                    public int compare(FTPFile file1, FTPFile file2) {
                        return file2.getTimestamp().getTime().compareTo(file1.getTimestamp().getTime());
                    }
                });
                File file = new File(savePath + '/' + ftpFiles[0].getName());
                try (OutputStream os = new FileOutputStream(file)) {
                    flag = ftpClient.retrieveFile(ftpFiles[0].getName(), os);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            } catch (IOException e) {
                logger.error("file download error", e);
            } finally {
                closeConnect();
            }
        }
        return flag;
    }

    /**
     * 根据名称获取文件，以字节数组返回
     *
     * @param ftpPath FTP服务器文件相对路径，例如：test/123
     * @return byte[] 字节数组对象
     */
    public byte[] getLatestFileBytes(String ftpPath) {
        // 登录
        login();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        if (ftpClient != null) {
            try {
                String path = initPath(ftpPath);
                // 判断是否存在该目录
                if (!ftpClient.changeWorkingDirectory(path)) {
                    logger.error(BASE_PATH + ftpPath + " not exists");
                    return byteStream.toByteArray();
                }
//                ftpClient.enterLocalPassiveMode();  // 设置被动模式，开通一个端口来传输数据
                FTPFile[] ftpFiles = ftpClient.listFiles();
                if (ftpFiles == null || ftpFiles.length == 0) {
                    logger.error(BASE_PATH + ftpPath + " no file found");
                    return byteStream.toByteArray();
                }
                Arrays.sort(ftpFiles, new Comparator<FTPFile>() {
                    @Override
                    public int compare(FTPFile file1, FTPFile file2) {
                        return file2.getTimestamp().getTime().compareTo(file1.getTimestamp().getTime());
                    }
                });

                try (InputStream is = ftpClient.retrieveFileStream(ftpFiles[0].getName())) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len = -1;
                    while ((len = is.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        byteStream.write(buffer, 0, len);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            } catch (IOException e) {
                logger.error("get file error", e);
            } finally {
                closeConnect();
            }
        }
        return byteStream.toByteArray();
    }

    /**
     * 获取该目录下所有文件,以输入流返回
     *
     * @param ftpPath FTP服务器上文件相对路径，例如：test/123
     * @return Map<String, InputStream> 其中key为文件名，value为输入流对象
     */
    public Map<String, InputStream> getFileInputStream(String ftpPath) {
        // 登录
        login();
        Map<String, InputStream> map = new HashMap<>();
        if (ftpClient != null) {
            try {
                String path = initPath(ftpPath);
                // 判断是否存在该目录
                if (!ftpClient.changeWorkingDirectory(path)) {
                    logger.error(BASE_PATH + ftpPath + " not exists");
                    return map;
                }
//                ftpClient.enterLocalPassiveMode();  // 设置被动模式，开通一个端口来传输数据
                String[] fs = ftpClient.listNames();
                // 判断该目录下是否有文件
                if (fs == null || fs.length == 0) {
                    logger.error(BASE_PATH + ftpPath + " no file found");
                    return map;
                }
                for (String ff : fs) {
                    String ftpName = new String(ff.getBytes(serverCharset), localCharset);
                    InputStream is = ftpClient.retrieveFileStream(ff);
                    map.put(ftpName, is);
                    ftpClient.completePendingCommand(); // 处理多个文件
                }
            } catch (IOException e) {
                logger.error("get file error", e);
            } finally {
                closeConnect();
            }
        }
        return map;
    }

    /**
     * 根据名称获取文件，以输入流返回
     *
     * @param ftpPath  FTP服务器上文件相对路径，例如：test/123
     * @param fileName 文件名，例如：test.txt
     * @return InputStream 输入流对象
     */
    public InputStream getInputStreamByName(String ftpPath, String fileName) {
        // 登录
        login();
        InputStream input = null;
        if (ftpClient != null) {
            try {
                String path = initPath(ftpPath);
                // 判断是否存在该目录
                if (!ftpClient.changeWorkingDirectory(path)) {
                    logger.error(BASE_PATH + ftpPath + " not exists");
                    return input;
                }
//                ftpClient.enterLocalPassiveMode();  // 设置被动模式，开通一个端口来传输数据
                String[] fs = ftpClient.listNames();
                // 判断该目录下是否有文件
                if (fs == null || fs.length == 0) {
                    logger.error(BASE_PATH + ftpPath + " no file found");
                    return input;
                }
                for (String ff : fs) {
                    String ftpName = new String(ff.getBytes(serverCharset), localCharset);
                    if (ftpName.equals(fileName)) {
                        input = ftpClient.retrieveFileStream(ff);
                        break;
                    }
                }
            } catch (IOException e) {
                logger.error("get file error", e);
            } finally {
                closeConnect();
            }
        }
        return input;
    }

    /**
     * 删除指定文件
     *
     * @param filePath 文件相对路径，例如：test/123/test.txt
     * @return 成功返回true，否则返回false
     */
    public boolean deleteFile(String filePath) {
        // 登录
        login();
        boolean flag = false;
        if (ftpClient != null) {
            try {
                String path = initPath(filePath);
                flag = ftpClient.deleteFile(path);
            } catch (IOException e) {
                logger.error("delete file error", e);
            } finally {
                closeConnect();
            }
        }
        return flag;
    }

    /**
     * 删除目录下所有文件
     *
     * @param dirPath 文件相对路径，例如：test/123
     * @return 成功返回true，否则返回false
     */
    public boolean deleteFiles(String dirPath) {
        // 登录
        login();
        boolean flag = false;
        if (ftpClient != null) {
            try {
//                ftpClient.enterLocalPassiveMode();  // 设置被动模式，开通一个端口来传输数据
                String path = initPath(dirPath);
                String[] fs = ftpClient.listNames(path);
                // 判断该目录下是否有文件
                if (fs == null || fs.length == 0) {
                    logger.error(BASE_PATH + dirPath + ": no file found");
                    return flag;
                }
                for (String ftpFile : fs) {
                    ftpClient.deleteFile(ftpFile);
                }
                flag = true;
            } catch (IOException e) {
                logger.error("delete file error", e);
            } finally {
                closeConnect();
            }
        }
        return flag;
    }

    /**
     * 连接FTP服务器
     */
    private void login() {
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(host, port);
            ftpClient.login(username, password);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                closeConnect();
                logger.error("ftp server connect error");
            }
        } catch (Exception e) {
            logger.error("ftp server login error", e);
        }
    }

    /**
     * 关闭FTP连接
     */
    private void closeConnect() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                logger.error("ftp server close error", e);
            }
        }
    }

    private String initPath(String dirPath) {
        if (dirPath.startsWith(BASE_PATH)) {
            return changeEncoding(dirPath);
        } else {
            return changeEncoding(BASE_PATH + dirPath);
        }
    }

    /**
     * FTP服务器路径编码转换
     *
     * @param ftpPath FTP服务器路径
     * @return String
     */
    private String changeEncoding(String ftpPath) {
        String directory = null;
        try {
            if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(OPTS_UTF8, "ON"))) {
                localCharset = CHARSET_UTF8;
            }
            directory = new String(ftpPath.getBytes(localCharset), serverCharset);
        } catch (Exception e) {
            logger.error("Encoding switch error", e);
        }
        return directory;
    }

    /**
     * 在服务器上递归创建目录
     *
     * @param dirPath 上传目录路径
     * @return     
     */
    private void createDirectories(String dirPath) {
        try {
            if (!dirPath.endsWith("/")) {
                dirPath += "/";
            }
            String directory = dirPath.substring(0, dirPath.lastIndexOf("/") + 1);
            ftpClient.makeDirectory("/");
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            while (true) {
                String subDirectory = new String(dirPath.substring(start, end));
                if (!ftpClient.changeWorkingDirectory(subDirectory)) {
                    if (ftpClient.makeDirectory(subDirectory)) {
                        ftpClient.changeWorkingDirectory(subDirectory);
                    } else {
                        logger.info("create directory error");
                        return;
                    }
                }
                start = end + 1;
                end = directory.indexOf("/", start);
                //检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("upload directory create error", e);
        }
    }
}
