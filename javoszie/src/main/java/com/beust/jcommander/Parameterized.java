 package com.beust.jcommander;
 
 import com.beust.jcommander.internal.Lists;
 import java.lang.annotation.Annotation;
 import java.lang.reflect.Field;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.ParameterizedType;
 import java.lang.reflect.Type;
 import java.util.List;
 
 
 
 
 
 
 
 
 public class Parameterized
 {
   private Field m_field;
   private Method m_method;
   private Method m_getter;
   private WrappedParameter m_wrappedParameter;
   private ParametersDelegate m_parametersDelegate;
   
   public Parameterized(WrappedParameter wp, ParametersDelegate pd, Field field, Method method)
   {
     this.m_wrappedParameter = wp;
     this.m_method = method;
     this.m_field = field;
     if (this.m_field != null) {
       this.m_field.setAccessible(true);
     }
     this.m_parametersDelegate = pd;
   }
   
   public static List<Parameterized> parseArg(Object arg) {
     List<Parameterized> result = Lists.newArrayList();
     
     Class<? extends Object> cls = arg.getClass();
     while (!Object.class.equals(cls)) {
       for (Field f : cls.getDeclaredFields()) {
         Annotation annotation = f.getAnnotation(Parameter.class);
         Annotation delegateAnnotation = f.getAnnotation(ParametersDelegate.class);
         Annotation dynamicParameter = f.getAnnotation(DynamicParameter.class);
         if (annotation != null) {
           result.add(new Parameterized(new WrappedParameter((Parameter)annotation), null, f, null));
         }
         else if (dynamicParameter != null) {
           result.add(new Parameterized(new WrappedParameter((DynamicParameter)dynamicParameter), null, f, null));
         }
         else if (delegateAnnotation != null) {
           result.add(new Parameterized(null, (ParametersDelegate)delegateAnnotation, f, null));
         }
       }
       
       cls = cls.getSuperclass();
     }
     
 
     cls = arg.getClass();
     while (!Object.class.equals(cls)) {
       for (Method m : cls.getDeclaredMethods()) {
         Annotation annotation = m.getAnnotation(Parameter.class);
         Annotation delegateAnnotation = m.getAnnotation(ParametersDelegate.class);
         Annotation dynamicParameter = m.getAnnotation(DynamicParameter.class);
         if (annotation != null) {
           result.add(new Parameterized(new WrappedParameter((Parameter)annotation), null, null, m));
         }
         else if (dynamicParameter != null) {
           result.add(new Parameterized(new WrappedParameter((DynamicParameter)annotation), null, null, m));
         }
         else if (delegateAnnotation != null) {
           result.add(new Parameterized(null, (ParametersDelegate)delegateAnnotation, null, m));
         }
       }
       
       cls = cls.getSuperclass();
     }
     
     return result;
   }
   
   public WrappedParameter getWrappedParameter() {
     return this.m_wrappedParameter;
   }
   
   public Class<?> getType() {
     if (this.m_method != null) {
       return this.m_method.getParameterTypes()[0];
     }
     return this.m_field.getType();
   }
   
   public String getName()
   {
     if (this.m_method != null) {
       return this.m_method.getName();
     }
     return this.m_field.getName();
   }
   
   public Object get(Object object)
   {
     try {
       if (this.m_method != null) {
         if (this.m_getter == null) {
           this.m_getter = this.m_method.getDeclaringClass().getMethod("g" + this.m_method.getName().substring(1), new Class[0]);
         }
         
 
         return this.m_getter.invoke(object, new Object[0]);
       }
       return this.m_field.get(object);
     }
     catch (SecurityException e) {
       throw new ParameterException(e);
     }
     catch (NoSuchMethodException e) {
       String name = this.m_method.getName();
       String fieldName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
       Object result = null;
       try {
         Field field = this.m_method.getDeclaringClass().getDeclaredField(fieldName);
         if (field != null) {
           field.setAccessible(true);
           result = field.get(object);
         }
       }
       catch (NoSuchFieldException ex) {}catch (IllegalAccessException ex) {}
       
 
 
       return result;
     } catch (IllegalArgumentException e) {
       throw new ParameterException(e);
     } catch (IllegalAccessException e) {
       throw new ParameterException(e);
     } catch (InvocationTargetException e) {
       throw new ParameterException(e);
     }
   }
   
   public int hashCode()
   {
     int prime = 31;
     int result = 1;
     result = 31 * result + (this.m_field == null ? 0 : this.m_field.hashCode());
     result = 31 * result + (this.m_method == null ? 0 : this.m_method.hashCode());
     return result;
   }
   
   public boolean equals(Object obj)
   {
     if (this == obj)
       return true;
     if (obj == null)
       return false;
     if (getClass() != obj.getClass())
       return false;
     Parameterized other = (Parameterized)obj;
     if (this.m_field == null) {
       if (other.m_field != null)
         return false;
     } else if (!this.m_field.equals(other.m_field))
       return false;
     if (this.m_method == null) {
       if (other.m_method != null)
         return false;
     } else if (!this.m_method.equals(other.m_method))
       return false;
     return true;
   }
   
   public boolean isDynamicParameter(Field field) {
     if (this.m_method != null) {
       return this.m_method.getAnnotation(DynamicParameter.class) != null;
     }
     return this.m_field.getAnnotation(DynamicParameter.class) != null;
   }
   
   public void set(Object object, Object value)
   {
     try {
       if (this.m_method != null) {
         this.m_method.invoke(object, new Object[] { value });
       } else {
         this.m_field.set(object, value);
       }
     } catch (IllegalArgumentException ex) {
       throw new ParameterException(ex);
     } catch (IllegalAccessException ex) {
       throw new ParameterException(ex);
     }
     catch (InvocationTargetException ex) {
       if ((ex.getTargetException() instanceof ParameterException)) {
         throw ((ParameterException)ex.getTargetException());
       }
       throw new ParameterException(ex);
     }
   }
   
   public ParametersDelegate getDelegateAnnotation()
   {
     return this.m_parametersDelegate;
   }
   
   public Type getGenericType() {
     if (this.m_method != null) {
       return this.m_method.getGenericParameterTypes()[0];
     }
     return this.m_field.getGenericType();
   }
   
   public Parameter getParameter()
   {
     return this.m_wrappedParameter.getParameter();
   }
   
 
 
   public Type findFieldGenericType()
   {
     if (this.m_method != null) {
       return null;
     }
     if ((this.m_field.getGenericType() instanceof ParameterizedType)) {
       ParameterizedType p = (ParameterizedType)this.m_field.getGenericType();
       Type cls = p.getActualTypeArguments()[0];
       if ((cls instanceof Class)) {
         return cls;
       }
     }
     
 
     return null;
   }
   
   public boolean isDynamicParameter() {
     return this.m_wrappedParameter.getDynamicParameter() != null;
   }
 }


