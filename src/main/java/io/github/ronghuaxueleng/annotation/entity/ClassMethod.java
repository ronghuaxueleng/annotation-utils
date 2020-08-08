package io.github.ronghuaxueleng.annotation.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: caoqiang
 * @create: 2020/7/31 0031 下午 17:52
 **/
@Data
@NoArgsConstructor
public class ClassMethod {
    /**
     * 请求类型
     */
    protected String type;
    /**
     * 处理后的url
     */
    protected String url;
    /**
     * code
     */
    protected String code;
    /**
     * 所在类方法名
     */
    protected String method;
    /**
     * url全路径
     */
    protected String fullUrl;
    /**
     * 作用域
     */
    protected String scope = "methond";
    /**
     * 参数列表
     */
    protected List<String> params = new ArrayList<>();
    /**
     * 方法中文名
     */
    protected String methondName;
    /**
     * 所在路径名
     */
    protected String fullClassName;
    /**
     * 参数字符串
     */
    protected String paramsString;
    /**
     * 是否isPathVariable
     */
    protected boolean isPathVariable;
    /**
     * request参数列表
     */
    protected List<String> requestParamParams = new ArrayList<>();
    /**
     * pathVariable参数列表
     */
    protected List<String> pathVariableParams = new ArrayList<>();
    /**
     * 注解列表
     */
    protected List<BeanAnnotation> annotations = new ArrayList<>();
    /**
     * 返回值
     */
    protected ReturnType methodReturnType;
    /**
     * 参数列表
     */
    protected List<MethodParam> methodParams = new ArrayList<>();
}
