package com.wx.admin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/log")
public class OperationLogController {
//    @PostMapping("/query")
//    public PageResponse<OperationLog> queryLogs(
//        @RequestBody LogQueryRequest request
//    ) {
//        QueryWrapper<OperationLog> wrapper = new QueryWrapper<>();
//        if (StringUtils.isNotBlank(request.getOperatorName())) {
//            wrapper.like("operator_name", request.getOperatorName());
//        }
//        Page<OperationLog> page = new Page<>(request.getPage(), request.getPageSize());
//        return ApiResponse.page(logService.page(page, wrapper));
//    }
}