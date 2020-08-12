package io.github.ronghuaxueleng.annotation.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: caoqiang
 * @create: 2020/8/12 0012 下午 15:27
 **/
@Data
@NoArgsConstructor
public class Field {
    /**
     * 字段名
     */
    protected String field;
    /**
     * 注解列表
     */
    protected List<BeanAnnotation> annotations = new ArrayList<>();
}
