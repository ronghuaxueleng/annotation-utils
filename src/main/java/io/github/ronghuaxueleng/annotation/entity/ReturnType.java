package io.github.ronghuaxueleng.annotation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: caoqiang
 * @create: 2020/7/27 0027 下午 21:00
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnType {

  /**
   * 返回类型类名
   */
  protected String returnTypeName;

  /**
   * 字段列表
   */
  protected List<ReturnTypeField> returnTypeFields;

}
