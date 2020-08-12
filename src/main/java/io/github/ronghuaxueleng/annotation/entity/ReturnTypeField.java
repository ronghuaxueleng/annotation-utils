package io.github.ronghuaxueleng.annotation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回值字段
 *
 * @author: caoqiang
 * @create: 2020/7/27 0027 下午 20:57
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnTypeField {

  /**
   * 字段名
   */
  protected String name;

  /**
   * 字段中文名
   */
  protected String cnName;

  /**
   * 类型
   */
  protected String genericType;
}
