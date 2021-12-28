package bmstu.iu7m.osipov.utils;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class ClassObjectBuilder {

    private static Map<String, Class<?>> primitives;

    private static Map<String, Class<?>> boxedTypes;

    static {
        primitives = new HashMap<>();
        primitives.put("boolean", boolean.class);
        primitives.put("byte", byte.class);
        primitives.put("char",char.class);
        primitives.put("short",short.class);
        primitives.put("int", int.class);
        primitives.put("long", long.class);
        primitives.put("float", float.class);
        primitives.put("double", double.class);

        boxedTypes = new HashMap<>();
        boxedTypes.put("boolean", Boolean.class);
        boxedTypes.put("byte", Byte.class);
        boxedTypes.put("char",Character.class);
        boxedTypes.put("short",Short.class);
        boxedTypes.put("int", Integer.class);
        boxedTypes.put("long", Long.class);
        boxedTypes.put("float", Float.class);
        boxedTypes.put("double", Double.class);
    }

    public static Map<String, Class<?>> getPrimitiveTypes(){
        return primitives;
    }
    public static Map<String, Class<?>> getBoxedTypes(){
        return boxedTypes;
    }

    public static Object createInstance(String className, Class<?>[] paramTypes, Object[] paramValues){
        Class type = null;
        Object obj = null;
        try{
            type = Class.forName(className);
        }
        catch (ClassNotFoundException e) { }

        if(type == null)
            return obj;

        Constructor<?> ctr = null;
        try {
            ctr = type.getDeclaredConstructor(paramTypes);
        } catch (NoSuchMethodException | SecurityException e){}
        if(ctr == null)
            return obj;

        try {
            for(int i = 0; i < paramValues.length; i++){
                paramValues[i] = PrimitiveTypeConverter.castTo(paramTypes[i], paramValues[i].toString());
            }
            obj = ctr.newInstance(paramValues);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e){}

        return obj;
    }

    public static Constructor<?> getDeclaredConstructor(String className, Class<?>[] paramTypes) {
        Class<?> type = null;
        Constructor<?> ctr = null;
        try {
            type = Class.forName(className);
        } catch (ClassNotFoundException e) { }

        if (type == null)
            return ctr;

        try {
            ctr = type.getDeclaredConstructor(paramTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            System.out.println(e);
        }
        return ctr;
    }

    public static Object createInstance(Constructor<?> constructor, Object[] paramValues){
        Object obj = null;
        if(constructor == null)
            return obj;
        try {
            obj = constructor.newInstance(paramValues);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e){}

        return obj;
    }

    public static Object createInstance(Constructor<?> constructor, Object[] paramValues,
                                        Function2<Constructor<?>, Object[], Object[]> converter
    )
    {
        return createInstance(constructor, converter.call(constructor, paramValues));
    }

    public static Object createInstance(Constructor<?> constructor, Object[] paramValues,
                                        Function3<Constructor<?>, Object[], Integer, Object[]> converter,
                                        int offset
    )
    {
        return createInstance(constructor, converter.call(constructor, paramValues, offset));
    }

    public static Method getDeclaredMethod(Object obj, String name){
        Method m = null;
        if(obj == null || name == null || name.length() == 0)
            return m;
         m = Arrays.stream(obj.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        return m;
    }

    public static Method getMethod(Object obj, String name){
        Method m = null;
        if(obj == null || name == null || name.length() == 0)
            return m;
        m = Arrays.stream(obj.getClass().getMethods())
                .filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        return m;
    }

    public static Method getClassMethod(Class<?> clazz, String name){
        Method m = null;
        if(clazz == null || name == null || name.length() == 0)
            return m;
        m = Arrays.stream(clazz.getMethods()).filter(x -> x.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
        return m;
    }

    public static Object copy(Object entity) throws IllegalAccessException, InstantiationException {
        Class<?> clazz = entity.getClass();
        Object newEntity = entity.getClass().newInstance();

        while (clazz != null) {
            copyFields(entity, newEntity, clazz);
            clazz = clazz.getSuperclass();
        }
        return newEntity;
    }

    private static void copyFields(Object entity, Object newEntity, Class<?> clazz) throws IllegalAccessException, InstantiationException {
        List<Field> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            fields.add(field);
        }

        Field modifiersField = null;
        try{
            modifiersField = Field.class.getDeclaredField("modifiers");
        } catch (NoSuchFieldException e){}
        modifiersField.setAccessible(true);

        for (Field field : fields) {
            field.setAccessible(true);

            //Skip static final fields.
            if((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC
                //&& ((field.getModifiers() & Modifier.FINAL) == Modifier.FINAL)
            )
                continue;

            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(newEntity, field.get(entity));
            modifiersField.setInt(field, field.getModifiers() | Modifier.FINAL);
        }
        fields.clear();
    }

    public static Object getClone(Object original)  {
        Method m = null;
        Class<?> clazz = original.getClass();
        Object res = null;
        while(clazz != null && m == null) {
            try {
                m = clazz.getDeclaredMethod("clone");
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }
        m.setAccessible(true);
        try{
            res = m.invoke(original);
        }
        catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e){
            System.out.println(e.getCause());
        }
        return res;
    }
}