package io.github.ronghuaxueleng.annotation.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: caoqiang
 * @create: 2020/7/26 0026 下午 19:31
 **/
@Data
@NoArgsConstructor
public class Controller {
    /**
     * 类名全路径
     */
    protected String fullClassName;
    /**
     * 类名
     */
    protected String controllerName;
    /**
     * 作用域
     */
    protected String scope = "controller";
    /**
     * 方法列表
     */
    protected List<ClassMethod> methodList = new ArrayList<>();
    /**
     * 字段列表
     */
    protected List<Field> fieldList = new ArrayList<>();
    /**
     * 注解列表
     */
    protected Set<BeanAnnotation> annotations = new HashSet<>();
}