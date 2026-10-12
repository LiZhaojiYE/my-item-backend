package com.lzj.util;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/** @author 李兆基 */
@Slf4j
public class MinioUtil {
    /** MinIO 客户端 */
    private static MinioClient minioClient;
    /** MinIO 端点 */
    private final static String END_POINT = "http://192.168.211.77:9001";
    /** MinIO 访钥 */
    private final static String ACCESS_KEY = "GpWikFdCqDqOWTkDYGWO";
    /** MinIO 秘钥 */
    private final static String SECRET_KEY = "T0QKEy8W4W6lKzVU8wxQ3DMBqVmD7ElO489rwJ4W";

    static {
        try {
            minioClient = new MinioClient(END_POINT, ACCESS_KEY, SECRET_KEY);
        } catch (Exception e) {
            log.error("MinioClient 实例化失败: {}", e.getMessage());
        }
    }

    /**
     * 将文件上传到 MinIO 对象存储服务中
     *
     * @param file       文件对象
     * @param dir        文件存储目录
     * @param bucketName MinIO 桶名
     * @return 上传后的文件名
     */
    @SneakyThrows
    public static String upload(MultipartFile file, String dir, String bucketName) {
        checkMultipartFile(file);
        checkDir(dir);
        checkBucket(bucketName);
        String fileName = randomFilename(file);
        // 上传文件
        minioClient.putObject(bucketName, dir + "/" + fileName, file.getInputStream(), file.getContentType());
        return fileName;
    }

    /**
     * 从 MinIO 中删除指定文件
     *
     * @param fileName   文件名
     * @param dir        文件存储目录
     * @param bucketName MinIO 桶名
     */
    @SneakyThrows
    public static void delete(String fileName, String dir, String bucketName) {
        checkFileName(fileName);
        checkDir(dir);
        checkBucket(bucketName);
        minioClient.removeObject(bucketName, dir + "/" + fileName);
    }

    /**
     * 根据文件对象获取随机文件名称
     *
     * @param file 文件对象
     * @return 随机文件名称，格式为 "UUID-时间戳.原始文件后缀"
     */
    private static String randomFilename(MultipartFile file) {
        // 获取原始文件名
        String fileName = file.getOriginalFilename();
        checkFileName(fileName);
        // 第一部分：获取 UUID 后 10 位
        String part01 = UUID.randomUUID().toString().substring(26);
        // 第二部分：时间戳
        String part02 = String.valueOf(System.currentTimeMillis());
        // 第三部分：原始文件后缀
        String part03 = fileName.substring(fileName.lastIndexOf("."));
        // 组合并返回最终文件名
        return part01 + "-" + part02 + part03;
    }


    /**
     * 检查文件对象是否为空，若为空则抛出异常
     *
     * @param multipartFile 文件对象
     */
    private static void checkMultipartFile(MultipartFile multipartFile) {
        if (ObjectUtil.isNull(multipartFile)) {
            throw new RuntimeException("MINIO文件对象为空");
        }
    }

    /**
     * 检查文件名称是否为空或空串，是则抛出异常
     *
     * @param fileName 文件名称
     */
    private static void checkFileName(String fileName) {
        if (StrUtil.isEmpty(fileName)) {
            throw new RuntimeException("MINIO文件名为空或空串");
        }
    }

    /**
     * 检查文件上传目录名称是否为空或空串，是则抛出异常
     *
     * @param dir 文件上传目录名称
     */
    private static void checkDir(String dir) {
        if (StrUtil.isEmpty(dir)) {
            throw new RuntimeException("MINIO文件上传目录名称为空或空串");
        }
    }

    /**
     * 检查 MINIO 桶名是否为空或空串以及指定的 MINIO 桶是否存在，是则抛出异常
     *
     * @param bucketName MINIO 桶名
     */
    @SneakyThrows
    private static void checkBucket(String bucketName) {

		// 校验数据桶名称是否为空
        if (StrUtil.isEmpty(bucketName)) {
            throw new RuntimeException("MINIO桶名为空或空串");
        }

        // 校验数据桶是否存在
        if (!minioClient.bucketExists(bucketName)) {
            throw new RuntimeException("MinIO桶不存在");
        }
    }
}
