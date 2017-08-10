 package com.strobel.core;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class Error
 {
   static IllegalStateException unmodifiableCollection()
   {
     return new IllegalStateException("Collection is read only.");
   }
   
   static IllegalArgumentException sequenceHasNoElements() {
     return new IllegalArgumentException("Sequence has no elements.");
   }
   
   static IllegalArgumentException sequenceHasMultipleElements() {
     return new IllegalArgumentException("Sequence contains more than one element.");
   }
   
   static IllegalArgumentException couldNotConvertFromNull() {
     return new IllegalArgumentException("Could not convert from 'null'.");
   }
   
   static IllegalArgumentException couldNotConvertFromType(Class<?> sourceType) {
     return new IllegalArgumentException(String.format("Could not convert from type '%s'.", new Object[] { sourceType.getName() }));
   }
   
 
   static IllegalArgumentException couldNotConvertNullValue(Class<?> targetType)
   {
     return new IllegalArgumentException(String.format("Could not convert 'null' to an instance of '%s'.", new Object[] { targetType.getName() }));
   }
   
 
 
 
 
   static IllegalArgumentException couldNotConvertValue(Class<?> sourceType, Class<?> targetType)
   {
     return new IllegalArgumentException(String.format("Could not convert a value of type '%s' to an instance of '%s'.", new Object[] { sourceType.getName(), targetType.getName() }));
   }
   
 
 
 
 
 
   static IndexOutOfBoundsException indexOutOfRange(int index)
   {
     return new IndexOutOfBoundsException(String.format("Index is out of range: %d", new Object[] { Integer.valueOf(index) }));
   }
 }


