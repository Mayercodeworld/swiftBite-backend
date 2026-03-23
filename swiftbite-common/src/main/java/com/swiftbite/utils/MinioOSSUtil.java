package com.swiftbite.utils;

import com.swiftbite.properties.MinioOSSProperties;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component // 交给IOC容器管理
public class MinioOSSUtil { // 视为第三方类

    private final MinioOSSProperties minioOSSProperties;

    public MinioOSSUtil(MinioOSSProperties minioOSSProperties) {
        this.minioOSSProperties = minioOSSProperties;
    }

    /**
     * 上传文件到 MinIO
     *
     * @param content           文件字节数组
     * @param originalFilename  原始文件名（用于获取扩展名）
     * @return 可访问的文件 URL
     * @throws Exception 上传失败时抛出异常
     */
    public String upload(byte[] content, String originalFilename) throws Exception {
        String endpoint = minioOSSProperties.getEndpoint();
        String bucketName = minioOSSProperties.getBucketName();
        String accessKey = minioOSSProperties.getAccessKey();
        String secretKey = minioOSSProperties.getSecretKey();

        // 构建目录：yyyy/MM/
        String dir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        // 生成唯一文件名
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + ext;
        String objectName = dir + "/" + newFileName;

        // 创建 MinIO 客户端
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        //// 确保 bucket 存在（可选，生产环境建议提前创建）
        //if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
        //    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        //}

        // 上传对象
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(new ByteArrayInputStream(content), content.length, -1)
                            .contentType(detectContentType(ext))
                            .build()
            );
        } catch (MinioException e) {
            throw new RuntimeException("MinIO 上传失败: " + e.getMessage(), e);
        }

        // 构建可访问的 URL（注意：MinIO 默认不带 HTTPS，且无虚拟主机风格）
        // 格式：http://192.168.5.9:9000/bucket-name/yyyy/MM/uuid.png
        return endpoint + "/" + bucketName + "/" + objectName;
    }

    /**
     * 简单根据扩展名设置 Content-Type（可扩展）
     */
    private String detectContentType(String ext) {
        ext = ext.toLowerCase();
        switch (ext) {
            case ".png": return "image/png";
            case ".jpg":
            case ".jpeg": return "image/jpeg";
            case ".gif": return "image/gif";
            case ".pdf": return "application/pdf";
            case ".txt": return "text/plain";
            default: return "application/octet-stream";
        }
    }
}