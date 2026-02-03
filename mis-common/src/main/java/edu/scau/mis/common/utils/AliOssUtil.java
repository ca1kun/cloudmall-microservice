package edu.scau.mis.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import edu.scau.mis.common.config.OssProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
public class AliOssUtil {

    @Autowired
    private OssProperties ossProperties;

    /**
     * 上传文件
     * @param file 前端传来的文件
     * @return 图片的完整访问 URL
     */
    public String upload(MultipartFile file) throws IOException {
        // 1. 获取配置
        String endpoint = ossProperties.getEndpoint();
        String accessKeyId = ossProperties.getAccessKeyId();
        String accessKeySecret = ossProperties.getAccessKeySecret();
        String bucketName = ossProperties.getBucketName();

        // 2. 获取文件输入流
        InputStream inputStream = file.getInputStream();

        // 3. 生成唯一文件名 (防止覆盖)
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = UUID.randomUUID().toString() + extension;

        // 4. 创建 OSSClient 实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 5. 上传
            ossClient.putObject(bucketName, objectName, inputStream);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        // 6. 拼接返回 URL (例如 https://your-bucket.oss-cn-shenzhen.aliyuncs.com/uuid.jpg)
        // 注意：endpoint 可能带 http://，这里需要处理一下，或者手动拼接
        // 这里假设 endpoint 是 http://oss-cn-shenzhen.aliyuncs.com
        String url = endpoint.replaceFirst("http://", "https://")
                             .replaceFirst("https://", "https://" + bucketName + ".") 
                             + "/" + objectName;
        
        // 如果上面拼接逻辑复杂，可以直接用下面这种通用格式：
        // https://bucket-name.endpoint/object-name
        return "https://" + bucketName + "." + endpoint.replace("http://", "").replace("https://", "") + "/" + objectName;
    }
    /**
     * 删除 OSS 文件
     * @param fileUrl 完整的图片 URL
     */
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        // 1. 获取配置信息
        String bucketName = ossProperties.getBucketName(); // scau-mis-images
        String endpoint = ossProperties.getEndpoint();     // oss-cn-shenzhen.aliyuncs.com
        String accessKeyId = ossProperties.getAccessKeyId();
        String accessKeySecret = ossProperties.getAccessKeySecret();

        // 2. 构造域名前缀（https://bucket.endpoint/）
        // 你的 URL 是: https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/xxx.png
        String domain = "https://" + bucketName + "." + endpoint + "/";

        // 3. 解析出 ObjectName (文件名)
        // 逻辑：把 URL 里的 "https://.../" 替换为空，剩下的就是文件名
        String objectName = fileUrl.replace(domain, "");

        // 容错处理：万一数据库存的是 http 开头的
        String httpDomain = "http://" + bucketName + "." + endpoint + "/";
        objectName = objectName.replace(httpDomain, "");

        System.out.println("准备删除 OSS 文件，解析出的文件名: " + objectName);

        // 4. 创建 OSS 客户端并删除
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            // 关键步骤：传入 bucketName 和 解析出来的 objectName
            ossClient.deleteObject(bucketName, objectName);
            System.out.println("✅ OSS 文件删除成功: " + objectName);
        } catch (Exception e) {
            System.err.println("❌ OSS 删除失败: " + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}