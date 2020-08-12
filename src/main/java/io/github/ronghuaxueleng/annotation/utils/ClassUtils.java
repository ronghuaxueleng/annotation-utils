package io.github.ronghuaxueleng.annotation.utils;

import io.github.ronghuaxueleng.annotation.entity.ReturnTypeField;
import javassist.*;
import javassist.bytecode.MethodInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 类操作工具
 *
 * @author: caoqiang
 * @create: 2020/8/5 0005 下午 12:41
 **/
public class ClassUtils {

    private static final Logger logger = Logger.getAnonymousLogger();

    /**
     * 解冻
     *
     * @param cc 当前类
     */
    public void defrost(CtClass cc) {
        if (cc.isFrozen()) {
            cc.defrost();
        }
    }

    /**
     * 通过类名生成class
     *
     * @param className 类名
     * @return 类对象
     * @throws NotFoundException 类没有找到异常
     */
    public CtClass createClassByClassName(String className) throws NotFoundException {
        ClassPool classPool = ClassPool.getDefault();
        return classPool.get(className);
    }

    /**
     * 通过方法名获得方法
     *
     * @param cc         当前类
     * @param methodName 方法名
     * @return 方法对象
     * @throws NotFoundException 方法没有找到
     */
    public MethodInfo getMethod(CtClass cc, String methodName) throws NotFoundException {
        CtMethod ctMethod = cc.getDeclaredMethod(methodName);
        return ctMethod.getMethodInfo();
    }

    /**
     * 获得类字段
     *
     * @param className 类名
     * @return 字段列表
     */
    public List<ReturnTypeField> getClassFields(String className) {
        List<ReturnTypeField> returnTypeFieldList = new ArrayList<>();
        try {
            // 创建类
            CtClass cc = createClassByClassName(className);
            //获得所有属性
            CtField[] declaredFields = cc.getDeclaredFields();
            for (CtField declaredField : declaredFields) {
                // 获取属性的名字
                String name = declaredField.getName();
                // 将属性的首字符大写，方便构造get，set方法
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                try {
                    CtMethod declaredMethod = cc.getDeclaredMethod("get" + name);
                    if (declaredMethod != null) {
                        ReturnTypeField returnTypeField = new ReturnTypeField();
                        returnTypeField.setGenericType(declaredField.getType().getName());
                        returnTypeField.setName(declaredField.getName());
                        returnTypeFieldList.add(returnTypeField);
                    }
                } catch (NotFoundException ignored) {
                }
            }
        } catch (NotFoundException e) {
            logger.info(e.getMessage());
        }
        return returnTypeFieldList;
    }

}
