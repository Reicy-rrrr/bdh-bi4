package com.deloitte.bdh.data.model.resp;

import com.deloitte.bdh.data.model.BiEtlDbFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ftp文件上传结果
 *
 * @author chenghzhang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FtpUploadResult {

    @ApiModelProperty(value = "ip地址", example = "127.0.0.1", required = true)
    private String host;

    /**
     * ftp端口
     **/
    @ApiModelProperty(value = "端口", example = "21", required = true)
    private String port;

    /**
     * ftp用户名
     **/
    @ApiModelProperty(value = "ftp用户名", example = "root", required = true)
    private String username;

    /**
     * ftp密码
     **/
    @ApiModelProperty(value = "ftp密码", example = "123456", required = true)
    private String password;

    /**
     * ftp服务器文件存储路径
     */
    @ApiModelProperty(value = "文件路径", example = "/bi_resources/x/20200101/", required = true)
    private String filePath;

    /**
     * 上传后的文件名
     */
    @ApiModelProperty(value = "文件名称", example = "测试.csv", required = true)
    private String fileName;

    /**
     * 文件转化json模板
     */
/*    @ApiModelProperty(value = "文件转化json模板", required = true)
    private JsonTemplate jsonTemplate;*/

    /**
     * 文件信息
     */
    @ApiModelProperty(value = "文件信息", required = true)
    private BiEtlDbFile fileInfo;
}
