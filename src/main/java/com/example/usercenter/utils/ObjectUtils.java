package com.example.usercenter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;


/**
 * 对象工具类
 */
public class ObjectUtils {
    private static final Logger logger = LoggerFactory.getLogger(ObjectUtils.class);
    /**
     * 判断对象是否除了某些属性，其余值为空
     *
     * @param object
     * @return
     */
    public static boolean objectCheckIsNull(Object object, List<String> excludeFields) {
        boolean flag = true; //定义返回结果，默认为true

        if (Objects.isNull(object)) {
            flag = true;
        } else {
            Class clazz = (Class) object.getClass(); // 得到类对象
            Field fields[] = clazz.getDeclaredFields(); // 得到所有属性
            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldValue = null;
                try {
                    fieldValue = field.get(object); //得到属性值
                    Type fieldType = field.getGenericType();//得到属性类型
                    String fieldName = field.getName(); // 得到属性名

                    logger.info("属性类型：" + fieldType + ",属性名：" + fieldName + ",属性值：" + fieldValue);
                } catch (IllegalArgumentException e) {
                    logger.error(e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage(), e);
                }
                if (fieldValue != null && !excludeFields.contains(field.getName())) {  //只要有一个属性值不为null且这个属性不是例外属性 就返回false 表示对象不为null
                    flag = false;
                    break;
                }
            }
        }

        return flag;
    }


}
