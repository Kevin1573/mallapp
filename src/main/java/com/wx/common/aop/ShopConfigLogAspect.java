package com.wx.common.aop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wx.common.model.ApiResponse;
import com.wx.orm.entity.OperationLog;
import com.wx.orm.entity.ShopConfigDO;
import com.wx.orm.entity.UserProfileDO;
import com.wx.service.TokenCarrier;
import com.wx.service.TokenService;
import com.wx.service.OperationLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Aspect
@Component
public class ShopConfigLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(ShopConfigLogAspect.class);


    private final OperationLogService logService;

    private final TokenService tokenService;

    public ShopConfigLogAspect(OperationLogService logService, TokenService tokenService) {
        this.logService = logService;
        this.tokenService = tokenService;
    }

    @Around("execution(* com.wx.admin.controller.*Controller.*(..))")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        OperationLog log = new OperationLog();
        long startTime = System.currentTimeMillis();

        try {
            // 1. 获取基础信息
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String methodName = signature.getMethod().getName();
            log.setOperationType(parseOperationType(methodName));
            log.setClassName(joinPoint.getTarget().getClass().getName());
            log.setMethodName(methodName);

            ServletRequestAttributes attributes = (ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes();
            String token = null;
            if (attributes != null) {
                //log.setIp(attributes.getRequest().getRemoteAddr());
                HttpServletRequest request = attributes.getRequest();
                token = request.getHeader("Authorization");
            }

            if (token == null) {
                // 从请求体参数中获取 token
                token = extractTokenFromRequestBody(joinPoint.getArgs());
            }
            UserProfileDO operator = tokenService.getUserByToken(token);
            if (operator != null) {
                log.setOperatorId(operator.getId());
                log.setOperatorName(operator.getPhone());
            }

            // 3. 记录请求参数（排除敏感信息）
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                log.setRequestParams(cleanSensitiveData(args));
            }

            // 4. 执行原方法
            Object result = joinPoint.proceed();

            // 5. 记录响应结果
            log.setSuccess(true);
            log.setResponseData(result instanceof ApiResponse ?
                    ((ApiResponse<?>) result).getMessage() : "success");

            return result;
        } catch (Exception e) {
            log.setSuccess(false);
            log.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            // 6. 保存日志
            try {
                log.setCostTime(System.currentTimeMillis() - startTime);
                log.setCreateTime(new Date());
                logService.save(log);
            } catch (Exception e) {
                logger.error("操作日志保存失败", e);
            }
        }
    }

    /**
     * 从 @RequestBody 注解的参数中提取 token
     */
    private String extractTokenFromRequestBody2(Object[] args) {
        try {
            for (Object arg : args) {
                // 通过反射获取字段值（需处理不同请求对象）
                Field tokenField = arg.getClass().getDeclaredField("token");
                tokenField.setAccessible(true);
                Object tokenValue = tokenField.get(arg);
                if (tokenValue instanceof String) {
                    return (String) tokenValue;
                }
            }
        } catch (Exception e) {
            logger.debug("Token 字段不存在或无法访问");
        }
        return null;
    }

    // 修改提取方法
    private String extractTokenFromRequestBody(Object[] args) {
        return Arrays.stream(args)
                .filter(arg -> arg instanceof TokenCarrier)
                .map(arg -> ((TokenCarrier) arg).getToken())
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }


    private String parseOperationType(String methodName) {
        if (methodName.startsWith("add")) return "CREATE";
        if (methodName.startsWith("update")) return "UPDATE";
        if (methodName.startsWith("delete")) return "DELETE";
        return "QUERY";
    }

    private String cleanSensitiveData(Object[] args) {
        return Arrays.stream(args)
                .map(arg -> {
                    if (arg instanceof ShopConfigDO) {
                        ShopConfigDO config = (ShopConfigDO) arg;
                        config.setContactPhone(null); // 隐藏联系电话
                        return config.toString();
                    }
                    return arg.toString();
                })
                .collect(Collectors.joining(", "));
    }

    // 在切面类中添加过滤规则
    private static final Set<String> SENSITIVE_KEYS = new HashSet<String>() {{
        add("password");
        add("token");
        add("contactPhone");
    }};

    private String filterSensitive(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(json);
            node.fields().forEachRemaining(entry -> {
                if (SENSITIVE_KEYS.contains(entry.getKey())) {
                    ((ObjectNode) node).put(entry.getKey(), "***");
                }
            });
            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            return json;
        }
    }
}
