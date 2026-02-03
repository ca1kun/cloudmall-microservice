package edu.scau.mis.api.controller;


import edu.scau.mis.common.domain.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class IndexController {
    @GetMapping("/")
    public ApiResult<String> index() {
        return ApiResult.success("Hello SpringBoot");
    }
}