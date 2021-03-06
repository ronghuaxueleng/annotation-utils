package io.github.ronghuaxueleng.annotation.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.ronghuaxueleng.annotation.entity.*;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.*;
import javassist.bytecode.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * 注解中属性修改、查看工具
 *
 * @author: caoqiang
 * @create: 2020/8/5 0005 下午 12:41
 **/
public class AnnotationUtils {

    private static final Logger logger = Logger.getAnonymousLogger();
    private final Gson gson = new Gson();
    private final ClassUtils classUtils = new ClassUtils();

    /**
     * 在类上添加注解
     *
     * @param module            注解对象
     * @param classfileBuffer   类字节码
     * @param savedClassDirPath 保存class文件夹路径
     * @param debug             是否debug
     * @return 字节码
     */
    public byte[] addClassAnnotation(Controller module, byte[] classfileBuffer, String savedClassDirPath, boolean debug) {
        // 获取一个 class 池。
        ClassPool classPool = ClassPool.getDefault();

        try {
            // 创建一个新的 class 类。classfileBuffer 就是当前class的字节码
            CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
            Set<BeanAnnotation> classAnnotations = module.getAnnotations();
            for (BeanAnnotation annotation : classAnnotations) {
                String name = annotation.getName();
                List<BeanAnnotationAttr> attrs = annotation.getAttrs();
                addClassAnnotationFieldValue(ctClass, name, attrs);
            }

            List<ClassMethod> methodList = module.getMethodList();
            for (ClassMethod method : methodList) {
                List<BeanAnnotation> methodAnnotations = method.getAnnotations();
                for (BeanAnnotation annotation : methodAnnotations) {
                    String name = annotation.getName();
                    List<BeanAnnotationAttr> attrs = annotation.getAttrs();
                    addMethodAnnotatioinFieldValue(ctClass, method.getMethod(), name, attrs);
                }
            }

            List<Field> fieldList = module.getFieldList();
            for (Field field : fieldList) {
                List<BeanAnnotation> fieldAnnotations = field.getAnnotations();
                for (BeanAnnotation annotation : fieldAnnotations) {
                    String name = annotation.getName();
                    List<BeanAnnotationAttr> attrs = annotation.getAttrs();
                    addFieldAnnotatioinFieldValue(ctClass, field.getField(), name, attrs);
                }
            }

            if (savedClassDirPath != null) {
                ctClass.writeFile(savedClassDirPath);
            }
            // 返回新的字节码
            return ctClass.toBytecode();
        } catch (Exception e) {
            if (debug) {
                e.printStackTrace();
            }
            logger.info(e.getMessage());
        }
        return new byte[0];
    }

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

    public void addFieldAnnotatioinFieldValue(String classNmae, String fieldName, String annoName, List<BeanAnnotationAttr> attrs) throws NotFoundException {
        CtClass cc = classUtils.createClassByClassName(classNmae);
        addFieldAnnotatioinFieldValue(cc, fieldName, annoName, attrs);
    }

    /**
     * 给属性添加注解
     *
     * @param cc
     * @param fieldName
     * @param annoName
     * @param attrs
     */
    public void addFieldAnnotatioinFieldValue(CtClass cc, String fieldName, String annoName, List<BeanAnnotationAttr> attrs) {
        try {
            classUtils.defrost(cc);
            CtField declaredField = cc.getDeclaredField(fieldName);
            FieldInfo fieldInfo = declaredField.getFieldInfo();
            ConstPool constPool = fieldInfo.getConstPool();
            AnnotationsAttribute attr = (AnnotationsAttribute) fieldInfo.getAttribute(AnnotationsAttribute.visibleTag);
            if (attr == null) {
                attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
            }
            Annotation annotation = new Annotation(annoName, constPool);
            attr.addAnnotation(setAnnotation(constPool, annotation, attrs));
            fieldInfo.addAttribute(attr);
        } catch (NotFoundException e) {
            logger.info(e.getMessage());
        }
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

    /**
     * 给方法添加注解
     *
     * @param cc         当前类
     * @param methodName 方法名
     * @param annoName   注解名
     * @param attrs      属性
     */
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
            List<Object> objects = (List<Object>) fieldValue;
            if (objects.size() > 0) {
                ArrayMemberValue memberValue = new ArrayMemberValue(constPool);
                if (objects.get(0) instanceof String) {
                    List<String> stringObjects = (List<String>) fieldValue;
                    StringMemberValue[] stringMemberValues = new StringMemberValue[stringObjects.size()];
                    for (int i = 0; i < stringObjects.size(); i++) {
                        stringMemberValues[i] = new StringMemberValue(stringObjects.get(i), constPool);
                    }
                    memberValue.setValue(stringMemberValues);
                } else if (objects.get(0) instanceof Map) {
                    String toJson = gson.toJson(fieldValue);
                    List<BeanAnnotation> subAnnotations = gson.fromJson(toJson, new TypeToken<List<BeanAnnotation>>() {
                    }.getType());
                    List<AnnotationMemberValue> annotationMemberValues = new ArrayList<>();
                    for (BeanAnnotation subAnnotation : subAnnotations) {
                        String subAnnotationName = subAnnotation.getName();
                        List<BeanAnnotationAttr> subAnnotationAttrs = subAnnotation.getAttrs();
                        Annotation anno = setAnnotation(constPool, new Annotation(subAnnotationName, constPool), subAnnotationAttrs);
                        AnnotationMemberValue annotationMemberValue = new AnnotationMemberValue(constPool);
                        annotationMemberValue.setValue(anno);
                        annotationMemberValues.add(annotationMemberValue);
                    }
                    memberValue.setValue(annotationMemberValues.toArray(new MemberValue[0]));
                }
                annotation.addMemberValue(fieldName, memberValue);
            }
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
