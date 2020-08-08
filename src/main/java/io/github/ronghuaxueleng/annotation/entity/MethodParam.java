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
