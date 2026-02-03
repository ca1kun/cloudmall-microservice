package edu.scau.mis.api.controller;

import edu.scau.mis.common.utils.AliOssUtil;
import edu.scau.mis.common.domain.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传接口
     */
    @PostMapping("/upload")
    public ApiResult<String> upload(MultipartFile file) {
        try {
            String url = aliOssUtil.upload(file);
            return ApiResult.success("上传成功", url);
        } catch (IOException e) {
            return ApiResult.error("文件上传失败: " + e.getMessage());
        }
    }
}