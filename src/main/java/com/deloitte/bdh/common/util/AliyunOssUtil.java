package com.deloitte.bdh.common.util;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.*;
import com.deloitte.bdh.common.properties.OssProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 阿里云OSS工具类
 *
 * @author pengdh
 * @date 2018/05/24
 */
@Slf4j
@Component
public class AliyunOssUtil {


	@Resource
	private OssProperties ossProperties;

	private SnowflakeIdWorker idWorker = new SnowflakeIdWorker();

	/**
	 * 上传图片
	 */
	public void uploadImg2Oss(String filePath, String url) throws Exception {
		File fileOnServer = new File(url);
		FileInputStream fin;
		try {
			fin = new FileInputStream(fileOnServer);
			String[] split = url.split("/");
			this.uploadFile2OSS(fin, filePath, split[split.length - 1]);
		} catch (FileNotFoundException e) {
			throw new Exception("图片上传失败01");
		}
	}

	/**
	 * 上传文件到OSS
	 *
	 * @param filePath 文件目录
	 * @param file 文件
	 */
	public String uploadImg2Oss(String filePath, MultipartFile file) throws Exception {
		if (file.getSize() > 1024 * 1024) {
			throw new Exception("上传图片大小不能超过1M！");
		}
		String originalFilename = file.getOriginalFilename();
		String substring = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
		String name = idWorker.nextId() + substring;
		try {
			InputStream inputStream = file.getInputStream();
			this.uploadFile2OSS(inputStream, filePath, name);
			return name;
		} catch (Exception e) {
			throw new Exception("图片上传失败02");
		}
	}

	/**
	 * 上传文件到OSS
	 *
	 * @param filePath 文件目录
	 * @param file 文件
	 */
	public String uploadFile2Oss(String filePath, MultipartFile file) throws Exception {
		if (file.getSize() > 1024 * 1024 * 10) {
			throw new Exception("上传文件大小不能超过10M！");
		}
		String originalFilename = file.getOriginalFilename();
		String substring = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
		String name = idWorker.nextId() + substring;
		try {
			InputStream inputStream = file.getInputStream();
			this.uploadFile2OSS(inputStream, filePath, name);
			return name;
		} catch (Exception e) {
			throw new Exception("文件上传失败!");
		}
	}

	/**
	 * 上传文件到OSS 无大小限制 且保留源文件名
	 *
	 * @param filePath 文件目录
	 * @param file 文件
	 */
	public String uploadFile2OssMax(String filePath, MultipartFile file) throws Exception {

		String originalFilename = file.getOriginalFilename();
		String substring = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
		String name = idWorker.nextId() + substring;
		try {
			InputStream inputStream = file.getInputStream();
			this.uploadFile2OSS(inputStream, filePath, originalFilename);
			return name;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new Exception("文件上传失败!");
		}
	}

	/**
	 * 上传到OSS服务器  如果同名文件会覆盖服务器上的
	 *
	 * @param inputStream 文件流
	 * @param filePath 文件所在目录
	 * @param fileName 文件名称 包括后缀名
	 * @return 出错返回"" ,唯一MD5数字签名
	 */
	public String uploadFile2OSS(InputStream inputStream, String filePath, String fileName) {
//		String endpoint = ossProperties.getOssEndpoint();
		String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
		String accessKeyId = ossProperties.getOssAccesskeyId();
		String accessKeySecret = ossProperties.getOssAccesskeySecret();
		String bucketName = ossProperties.getOssBucketName();
		CredentialsProvider credentialsProvider = new DefaultCredentialProvider(accessKeyId,
				accessKeySecret);
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		OSSClient ossClient = new OSSClient(endpoint, credentialsProvider, clientConfiguration);
		String ret = "";
		try {
			//创建上传Object的Metadata
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(inputStream.available());
			objectMetadata.setCacheControl("no-cache");
			objectMetadata.setHeader("Pragma", "no-cache");
			objectMetadata.setContentType(getContentType(fileName));
			objectMetadata.setContentDisposition("inline;filename=" + fileName);
			objectMetadata.setObjectAcl(CannedAccessControlList.PublicRead);
			//上传文件
			PutObjectResult putResult = ossClient
					.putObject(bucketName, filePath + fileName, inputStream, objectMetadata);
			ret = putResult.getETag();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	/**
	 * 获得图片路径
	 */
	public String getImgUrl(String filePath, String fileUrl) {
		if (!StringUtils.isEmpty(fileUrl)) {
			String[] split = fileUrl.split("/");
			return this.getUrl(filePath + split[split.length - 1]);
		}
		return null;
	}

	/**
	 * 获得文件路径
	 */
	public String getFileUrl(String filePath, String fileUrl) {
		if (!StringUtils.isEmpty(fileUrl)) {
			String[] split = fileUrl.split("/");
			return this.getUrl(filePath + split[split.length - 1]);
		}
		return null;
	}

	/**
	 * 获得url链接
	 */
	public String getUrl(String key) {
		String endpoint = ossProperties.getOssEndpoint();
		String accessKeyId = ossProperties.getOssAccesskeyId();
		String accessKeySecret = ossProperties.getOssAccesskeySecret();
		String bucketName = ossProperties.getOssBucketName();
		CredentialsProvider credentialsProvider = new DefaultCredentialProvider(accessKeyId,
				accessKeySecret);
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		OSSClient ossClient = new OSSClient(endpoint, credentialsProvider, clientConfiguration);
		// 设置URL过期时间
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2050);
		c.set(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		Date expiration = new Date(c.getTimeInMillis() + 3600L * 1000 * 24 * 365 * 10);
		// 生成URL
		URL url = ossClient.generatePresignedUrl(bucketName, key, expiration);
		if (url != null) {
			// 对于在内网上传的文件需要把内网地址换为外网地址
			String urlStr = url.toString();
			if (urlStr.contains(ossProperties.getTargetEndpoint())) {
				urlStr = urlStr.replace(ossProperties.getTargetEndpoint(),
						ossProperties.getReplacementEndpoint());
			}
			return urlStr;
		}
		return null;
	}

	/**
	 * 查询文件名列表
	 */
	public List<String> getObjectList(String filePath) {
		String endpoint = ossProperties.getOssEndpoint();
		String accessKeyId = ossProperties.getOssAccesskeyId();
		String accessKeySecret = ossProperties.getOssAccesskeySecret();
		String bucketName = ossProperties.getOssBucketName();
		CredentialsProvider credentialsProvider = new DefaultCredentialProvider(accessKeyId,
				accessKeySecret);
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		OSSClient ossClient = new OSSClient(endpoint, credentialsProvider, clientConfiguration);
		List<String> listRe = new ArrayList<>();
		try {
			log.info("===========>查询文件名列表");
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
			listObjectsRequest.setPrefix(filePath);
			ObjectListing list = ossClient.listObjects(listObjectsRequest);
			for (OSSObjectSummary objectSummary : list.getObjectSummaries()) {
				String key = objectSummary.getKey();
				if (!StringUtils.equals(key, filePath)) {
					String url = this.getImgUrl(filePath, objectSummary.getKey());
					// 对于在内网上传的文件需要把内网地址换为外网地址
					if (url.contains(ossProperties.getTargetEndpoint())) {
						url = url.replace(ossProperties.getTargetEndpoint(),
								ossProperties.getReplacementEndpoint());
					}
					listRe.add(url);
				}
			}
			return listRe;
		} catch (Exception ex) {
			log.info("==========>查询列表失败", ex);
			return new ArrayList<>();
		}
	}

	/**
	 * Description: 判断OSS服务文件上传时文件的contentType
	 *
	 * @param fileName 文件名
	 * @return String
	 */
	public String getContentType(String fileName) {
		String postfix = "";
		postfix = fileName.substring(fileName.lastIndexOf("."),
				fileName.length());
		String contentType = "application/xmsdownload";
		if (postfix.equalsIgnoreCase(".XLS") || postfix.equalsIgnoreCase(".XLSX")
				|| postfix.equalsIgnoreCase(".XLT") || postfix.equalsIgnoreCase(".XLW")
				|| postfix.equalsIgnoreCase(".CSV")) {
			contentType = "application/vnd.ms-excel";
		} else if (postfix.equalsIgnoreCase(".DOC")) {
			contentType = "application/msword";
		} else if (postfix.equalsIgnoreCase(".RTF")) {
			contentType = "application/rtf";
		} else if (postfix.equalsIgnoreCase(".TEXT")) {
			contentType = "text/plain";
		} else if (postfix.equalsIgnoreCase(".XML") || postfix.equalsIgnoreCase(".TXT")) {
			contentType = "";
		} else if (postfix.equalsIgnoreCase(".BMP")) {
			contentType = "image/bmp";
		} else if (postfix.equalsIgnoreCase(".JPG") || postfix.equalsIgnoreCase(".JPEG")) {
			contentType = "image/jpeg";
		} else if (postfix.equalsIgnoreCase(".GIF")) {
			contentType = "image/gif";
		} else if (postfix.equalsIgnoreCase(".AVI")) {
			contentType = "video/x-msvideo";
		} else if (postfix.equalsIgnoreCase(".MP3")) {
			contentType = "audio/mpeg";
		} else if (postfix.equalsIgnoreCase(".MPA") || postfix.equalsIgnoreCase(".MPE")
				|| postfix.equalsIgnoreCase(".MPEG") || postfix.equalsIgnoreCase(".MPG")) {
			contentType = "video/mpeg";
		} else if (postfix.equalsIgnoreCase(".PPT") || postfix.equalsIgnoreCase(".PPS")) {
			contentType = "application/vnd.ms-powerpoint";
		} else if (postfix.equalsIgnoreCase(".PDF")) {
			contentType = "application/pdf";
		} else if (postfix.equalsIgnoreCase(".ZIP") || postfix.equalsIgnoreCase(".RAR")) {
			contentType = "application/zip";
		}
		return contentType;
	}

}
