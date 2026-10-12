package com.lzj.component;
import cn.hutool.json.JSONUtil;
import com.lzj.result.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/** @author 李兆基 */
@RestControllerAdvice(basePackages = "com.lzj.controller")
public class ResultAdvice implements ResponseBodyAdvice<Object> {

	/**
     * @param methodParameter 控制方法全名
     * @param aClass          转换器类型，以下两种均为 HttpMessageConverter 的子类
     *                        字符串型数据使用 StringHttpMessageConverter 转换，
     *                        其他类型数据使用 MappingJackson2HttpMessageConverter 转换。
     * @return true 表示继续调用 beforeBodyWrite() 方法，false 表示不处理数据，直接返回。
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        // 放行到 beforeBodyWrite() 方法
        return true;
    }

    /**
     * @param o                  响应数据
     * @param methodParameter    控制方法全名
     * @param mediaType          响应类型，如 application/json 等
     * @param serverHttpRequest  请求对象
     * @param serverHttpResponse 响应对象
     * @param aClass             转换器类型，以下两种均为 HttpMessageConverter 的子类
     *                           字符串型数据使用 StringHttpMessageConverter 转换，
     *                           其他类型数据使用 MappingJackson2HttpMessageConverter 转换。
     */
    @Override
    public Object beforeBodyWrite(Object o,
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class aClass,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {

        // 若 Jackson 使用字符串转换器，使用 JSON 封装一下再直接返回字符串，避免爆发转换异常
        if (aClass == StringHttpMessageConverter.class) {
            return JSONUtil.toJsonStr(new Result<>(o));
        }

        // 若返回值已经是 Result 类型则直接返回
        if (o instanceof Result) {
            return o;
        }

        // 统一将 Controller 的返回值封装为 Result 类型
        return new Result<>(o);
    }
}
