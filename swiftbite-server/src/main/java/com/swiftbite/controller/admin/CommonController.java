package com.swiftbite.controller.admin;

import com.swiftbite.constant.MessageConstant;
import com.swiftbite.result.Result;
import com.swiftbite.utils.MinioOSSUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "公共服务接口")
public class CommonController {

    @Autowired
    private MinioOSSUtil minioOSSUtil;
    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    public Result upload(MultipartFile file) throws Exception {
        log.info("文件上传：{}", file.getOriginalFilename());

        try {
            // 将文件传给OSS容器并返回其url
            String url = minioOSSUtil.upload(file.getBytes(), file.getOriginalFilename());
            log.info("文件url：{}", url);
            return Result.success(url);
        } catch (Exception e) {
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
