package io.github.ronghuaxueleng.annotation.utils;

import io.github.ronghuaxueleng.annotation.entity.BeanAnnotationAttr;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.*;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import java.util.List;
import java.util.logging.Logger;

/**
 * 注解中属性修改、查看工具
 *
 * @author: caoqiang
 * @create: 2020/8/5 0005 下午 12:41
 **/
public class AnnotationUtils {

    private static final Logger logger = Logger.getAnonymousLogger();

    private final ClassUtils classUtils = new ClassUtils();

    /**
     * 在类上添加注解
     *
     * @param cc         当前类
     * @param annoName   注解名
     * @param fieldName  注解字段名
     * @param fieldValue 注解字段值
     */
    public void addClassAnnotationFieldValue(CtClass cc, String annoName, String fieldName, Object fieldValue) {
        AnnotationsAttribute attributeInfo = getClassAnnotation(cc);
        // 添加新的注解
        Annotation annotation = new Annotation(annoName, cc.getClassFile().getConstPool());
        attributeInfo.addAnnotation(setAnnotation(cc.getClassFile().getConstPool(), annotation, fieldName, fieldValue));
    }

    /**
     * 在类上添加注解
     *
     * @param cc       当前类
     * @param annoName 注解名
     * @param attrs    注解属性
     */
    public void addClassAnnotationFieldValue(CtClass cc, String annoName, List<BeanAnnotationAttr> attrs) {
        AnnotationsAttribute attributeInfo = getClassAnnotation(cc);

        // 添加新的注解
        Annotation annotation = new Annotation(annoName, cc.getClassFile().getConstPool());
        attributeInfo.addAnnotation(setAnnotation(cc.getClassFile().getConstPool(), annotation, attrs));
    }

    /**
     * 获得类上的注解
     *
     * @param cc 类
     * @return 注解对象
     */
    public AnnotationsAttribute getClassAnnotation(CtClass cc) {
        ClassFile classFile = cc.getClassFile();
        ConstPool constPool = classFile.getConstPool();

        AnnotationsAttribute attributeInfo;
        // 从这里取出原本类中的注解 建议DEBUG看下attributes中的数据
        List<AttributeInfo> attributes = classFile.getAttributes();
        if (attributes.size() > 1) {
            attributeInfo = (AnnotationsAttribute) attributes.get(1);
        } else {
            attributeInfo = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
            classFile.addAttribute(attributeInfo);
        }
        return attributeInfo;
    }

    /**
     * 修改方法注解上的属性值
     *
     * @param className  当前类名
     * @param methodName 当前方法名
     * @param annoName   方法上的注解名
     * @param fieldName  注解中的属性名
     * @param fieldValue 注解中的属性值
     * @throws NotFoundException 未找到方法异常
     */
    public void setMethodAnnotatioinFieldValue(String className, String methodName, String annoName,
                                               String fieldName, String fieldValue) throws NotFoundException {

        CtClass cc = classUtils.createClassByClassName(className);
        setMethodAnnotatioinFieldValue(cc, methodName, annoName, fieldName, fieldValue);
    }

    /**
     * 修改方法注解上的属性值
     *
     * @param cc         当前类
     * @param methodName 当前方法名
     * @param annoName   方法上的注解名
     * @param fieldName  注解中的属性名
     * @param fieldValue 注解中的属性值
     * @throws NotFoundException 未找到方法异常
     */
    public void setMethodAnnotatioinFieldValue(CtClass cc, String methodName, String annoName, String fieldName,
                                               String fieldValue) throws NotFoundException {
        classUtils.defrost(cc);
        MethodInfo methodInfo = classUtils.getMethod(cc, methodName);
        ConstPool constPool = methodInfo.getConstPool();
        AnnotationsAttribute attr = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
        Annotation annotation = attr.getAnnotation(annoName);
        if (annotation != null) {
            annotation.addMemberValue(fieldName, new StringMemberValue(fieldValue, constPool));
            attr.setAnnotation(annotation);
            methodInfo.addAttribute(attr);
        }
    }

    /**
     * 给方法添加注解
     *
     * @param className  类名
     * @param methodName 方法名
     * @param annoName   注解名
     * @param fieldName  字段名
     * @param fieldValue 字段值
     * @throws NotFoundException 未找到方法异常
     */
    public void addMethodAnnotatioinFieldValue(String className, String methodName, String annoName,
                                               String fieldName, String fieldValue) throws NotFoundException {
        CtClass cc = classUtils.createClassByClassName(className);
        addMethodAnnotatioinFieldValue(cc, methodName, annoName, fieldName, fieldValue);
    }

    /**
     * 给方法添加注解
     *
     * @param cc         当前类
     * @param methodName 方法名
     * @param annoName   注解名
     * @param fieldName  字段名
     * @param fieldValue 字段值
     * @throws NotFoundException 位置到方法异常
     */
    public void addMethodAnnotatioinFieldValue(CtClass cc, String methodName, String annoName, String fieldName,
                                               String fieldValue) throws NotFoundException {
        classUtils.defrost(cc);
        MethodInfo methodInfo = classUtils.getMethod(cc, methodName);
        ConstPool constPool = methodInfo.getConstPool();
        addMethodAnnotatioinFieldValue(cc, methodName, annoName, fieldName, new StringMemberValue(fieldValue, constPool));
    }

    public void addMethodAnnotatioinFieldValue(CtClass cc, String methodName, String annoName, List<BeanAnnotationAttr> attrs) {
        try {
            classUtils.defrost(cc);
            MethodInfo methodInfo = classUtils.getMethod(cc, methodName);
            ConstPool constPool = methodInfo.getConstPool();
            AnnotationsAttribute attr = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
            if (attr == null) {
                attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
            }
            Annotation annotation = new Annotation(annoName, constPool);
            attr.addAnnotation(setAnnotation(constPool, annotation, attrs));
            methodInfo.addAttribute(attr);
        } catch (NotFoundException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 给方法添加注解
     *
     * @param cc         当前类
     * @param methodName 方法名
     * @param annoName   注解名
     * @param fieldName  注解属性字段
     * @param fieldValue 注解属性值
     * @throws NotFoundException 未找到方法异常
     */
    public void addMethodAnnotatioinFieldValue(CtClass cc, String methodName, String annoName, String fieldName,
                                               Object fieldValue) throws NotFoundException {
        classUtils.defrost(cc);
        MethodInfo methodInfo = classUtils.getMethod(cc, methodName);
        ConstPool constPool = methodInfo.getConstPool();
        AnnotationsAttribute attr = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
        Annotation annotation = new Annotation(annoName, constPool);
        attr.addAnnotation(setAnnotation(constPool, annotation, fieldName, fieldValue));
        methodInfo.addAttribute(attr);
    }

    /**
     * 给方法添加注解
     *
     * @param cc         当前类
     * @param methodName 方法名
     * @param annoName   注解名
     * @param fieldName  字段名
     * @param fieldValue 字段值
     * @throws NotFoundException 未找到方法异常
     */
    public void addMethodAnnotatioinFieldValue(CtClass cc, String methodName, String annoName,
                                               String fieldName, MemberValue fieldValue) throws NotFoundException {
        classUtils.defrost(cc);
        MethodInfo methodInfo = classUtils.getMethod(cc, methodName);
        ConstPool constPool = methodInfo.getConstPool();
        AnnotationsAttribute attr = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
        Annotation annotation = new Annotation(annoName, constPool);
        annotation.addMemberValue(fieldName, fieldValue);
        attr.addAnnotation(annotation);
        methodInfo.addAttribute(attr);
    }

    /**
     * 设置注解
     *
     * @param constPool  类
     * @param annotation 注解
     * @param attrs      属性
     * @return 注解
     */
    public Annotation setAnnotation(ConstPool constPool, Annotation annotation, List<BeanAnnotationAttr> attrs) {
        for (BeanAnnotationAttr attr : attrs) {
            String fieldName = attr.getAttrName();
            Object fieldValue = attr.getAttrValue();
            setAnnotation(constPool, annotation, fieldName, fieldValue);
        }
        return annotation;
    }

    /**
     * @param constPool  类
     * @param annotation 注解
     * @param fieldName  字段名
     * @param fieldValue 字段值
     * @return 注解
     */
    public Annotation setAnnotation(ConstPool constPool, Annotation annotation, String fieldName, Object fieldValue) {
        if (fieldValue instanceof String) {
            annotation.addMemberValue(fieldName, new StringMemberValue(fieldValue.toString(), constPool));
        } else if (fieldValue instanceof String[]) {
            String[] objects = (String[]) fieldValue;
            ArrayMemberValue memberValue = new ArrayMemberValue(constPool);
            StringMemberValue[] stringMemberValues = new StringMemberValue[objects.length];
            for (int i = 0; i < objects.length; i++) {
                stringMemberValues[i] = new StringMemberValue(objects[i], constPool);
            }
            memberValue.setValue(stringMemberValues);
            annotation.addMemberValue(fieldName, memberValue);
        } else if (fieldValue instanceof List) {
            List<String> objects = (List<String>) fieldValue;
            ArrayMemberValue memberValue = new ArrayMemberValue(constPool);
            StringMemberValue[] stringMemberValues = new StringMemberValue[objects.size()];
            for (int i = 0; i < objects.size(); i++) {
                stringMemberValues[i] = new StringMemberValue(objects.get(i), constPool);
            }
            memberValue.setValue(stringMemberValues);
            annotation.addMemberValue(fieldName, memberValue);
        }
        return annotation;
    }

    /**
     * 获取方法注解中的属性值
     *
     * @param className  当前类名
     * @param methodName 当前方法名
     * @param annoName   方法上的注解名
     * @param fieldName  注解中的属性名
     * @return 注解属性值
     * @throws NotFoundException 未找到方法异常
     */
    public String getMethodAnnotatioinFieldValue(String className, String methodName, String annoName, String fieldName) throws NotFoundException {
        CtClass cc = classUtils.createClassByClassName(className);
        return getMethodAnnotatioinFieldValue(cc, methodName, annoName, fieldName);
    }

    /**
     * 获取方法注解中的属性值
     *
     * @param cc         当前类
     * @param methodName 当前方法名
     * @param annoName   方法上的注解名
     * @param fieldName  注解中的属性名
     * @return 注解属性值
     * @throws NotFoundException 未找到方法异常
     */
    public String getMethodAnnotatioinFieldValue(CtClass cc, String methodName, String annoName, String fieldName) throws NotFoundException {
        classUtils.defrost(cc);
        MethodInfo methodInfo = classUtils.getMethod(cc, methodName);
        AnnotationsAttribute attr = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
        String value = "";
        if (attr != null) {
            Annotation an = attr.getAnnotation(annoName);
            if (an != null) {
                value = ((StringMemberValue) an.getMemberValue(fieldName)).getValue();
            }
        }
        return value;
    }
}
