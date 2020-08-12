package io.github.ronghuaxueleng.annotation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: caoqiang
 * @create: 2020/8/1 0001 下午 14:32
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MethodParam {
    /**
     * 参数名称
     */
    protected String name;
    /**
     * 中文名
     */
    protected String cnName;
    /**
     * 参数类型
     */
    protected String type;

    /**
     * 是否是java基本类型
     */
    protected boolean isBaseType;

    public void setBaseType() {
        this.isBaseType = "java.lang.Integer".equalsIgnoreCase(this.type)
                || "java.lang.Byte".equalsIgnoreCase(this.type)
                || "java.lang.Long".equalsIgnoreCase(this.type)
                || "java.lang.Double".equalsIgnoreCase(this.type)
                || "java.lang.Float".equalsIgnoreCase(this.type)
                || "java.lang.Character".equalsIgnoreCase(this.type)
                || "java.lang.Short".equalsIgnoreCase(this.type)
                || "java.lang.Boolean".equalsIgnoreCase(this.type)
                || "java.lang.String".equalsIgnoreCase(this.type);
    }

    /**
     * 注解类型
     */
    protected String annoType;
    /**
     * 注解
     */
    protected BeanAnnotation annotation;
    /**
     * 参数字段
     */
    protected List<ReturnTypeField> fields;
}
