package com.mampod.track.sdk.tool;

import android.text.TextUtils;

import com.mampod.track.sdk.annotation.Transient;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TreeMap;

/**
 * @package： com.mampod.ergedd.statistics
 * @Des: 反射对象属性操作类
 * @author: Jack-Lu
 * @time: 2018/9/26 下午6:36
 * @change:
 * @changtime:
 * @changelog:
 */
public class PropertyUtil {

    /**
     * 通过反射获取各个属性名称和属性值封装成类
     *
     * @param sensorDataDto
     * @return
     */
    public static TreeMap<String, Object> sensorDataList(Object sensorDataDto) {
        TreeMap<String, Object> map = new TreeMap<>();
        Class<?> clazz = sensorDataDto.getClass();
        try {
            exceClass(sensorDataDto, map, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private static void exceClass(Object sensorDataDto, TreeMap<String, Object> sensorDatas, Class<?> clazz) throws Exception {
        if (clazz != Object.class) {
            returnclassF(sensorDataDto, sensorDatas, clazz);
            Class<?> clazzs = clazz.getSuperclass();
            exceClass(sensorDataDto, sensorDatas, clazzs);
        }
    }

    private static void returnclassF(Object sensorDataDto, TreeMap<String, Object> sensorDatas, Class<?> clazz) throws Exception {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //验签公有参数
            Transient property = field.getAnnotation(Transient.class);
            if (property == null) {
                filterProperties(sensorDataDto, sensorDatas, field);
            }
        }
    }

    /**
     * 筛选属性
     *
     * @param sensorDataDto
     * @param sensorDatas
     * @param field
     * @throws IllegalAccessException
     */
    private static void filterProperties(Object sensorDataDto, TreeMap<String, Object> sensorDatas, Field field) throws IllegalAccessException {
        // 对于每个属性，获取属性名
        String key = field.getName();
        if (!"serialVersionUID".equals(key)) {
            field.setAccessible(true);
            Object o = field.get(sensorDataDto);
            if (o != null) {
                String value = "";
                if (o instanceof String || o instanceof Long
                        || o instanceof Double || o instanceof Integer || o instanceof Float || o instanceof Short) {
                    value = o.toString();
                } else if (o instanceof Boolean) {
                    value = (Boolean) o ? "1" : "0";
                } else if (o instanceof Enum) {
                    Class<?> class_data = o.getClass();
                    if (class_data != null) {
                        value = String.valueOf(o);
                    }
                } else if (o instanceof Object) {
                    Class<?> class_data = o.getClass();
                    if (class_data != null) {
                        Field[] data_fields = class_data.getDeclaredFields();
                        for (Field f : data_fields) {
                            Transient p = f.getAnnotation(Transient.class);
                            if (p == null) {
                                filterProperties(o, sensorDatas, f);
                            }
                        }
                    }
                }
                if (!TextUtils.isEmpty(value)) {
                    sensorDatas.put(field.getName().toString(), value);
                }
            }
        }
    }

    /**
     * 获取当前对象对应字段的属性（对象）
     *
     * @param obj   当前对象
     * @param field 需要获取的属性名
     * @return Object 当前对象指定属性值
     */
    public static Object getFieldValue(Object obj, String field) {
        Class<?> claz = obj.getClass();
        Field f;
        Object fieldValue = null;
        try {
            f = claz.getDeclaredField(field);
            f.setAccessible(true);
            fieldValue = f.get(obj);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return fieldValue;
    }


    /**
     * 获取当前对象对应方法的值（对象）
     *
     * @param obj 当前对象
     * @return Object 当前对象方法名
     */
    public static Object getMethodValue(Object obj, String methodName) {

        Class<?> claz = obj.getClass();
        Method method;
        Object methodValue = null;
        try {
            method = claz.getMethod(methodName);
            if (method != null) {
                methodValue = method.invoke(obj);
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return methodValue;
    }

}
