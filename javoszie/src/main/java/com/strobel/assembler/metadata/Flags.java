 package com.strobel.assembler.metadata;
 
 import com.strobel.util.ContractUtils;
 import java.util.Collections;
 import java.util.EnumSet;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 import javax.lang.model.element.Modifier;
 
 
 
 public class Flags
 {
   public static final int PUBLIC = 1;
   public static final int PRIVATE = 2;
   public static final int PROTECTED = 4;
   public static final int STATIC = 8;
   public static final int FINAL = 16;
   public static final int SYNCHRONIZED = 32;
   public static final int VOLATILE = 64;
   public static final int TRANSIENT = 128;
   public static final int NATIVE = 256;
   public static final int INTERFACE = 512;
   public static final int ABSTRACT = 1024;
   public static final int STRICTFP = 2048;
   public static final int SYNTHETIC = 4096;
   public static final int ANNOTATION = 8192;
   public static final int ENUM = 16384;
   
   private Flags()
   {
     throw ContractUtils.unreachable();
   }
   
   public static String toString(long flags) {
     StringBuilder buf = new StringBuilder();
     String sep = "";
     for (Flag s : asFlagSet(flags)) {
       buf.append(sep);
       buf.append(s.name());
       sep = ", ";
     }
     return buf.toString();
   }
   
   public static String toString(long flags, Kind kind) {
     StringBuilder buf = new StringBuilder();
     String sep = "";
     for (Flag s : asFlagSet(flags, kind)) {
       buf.append(sep);
       buf.append(s.name());
       sep = ", ";
     }
     return buf.toString();
   }
   
   public static EnumSet<Flag> asFlagSet(long mask)
   {
     EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);
     
     if ((mask & 1L) != 0L) {
       flags.add(Flag.PUBLIC);
     }
     if ((mask & 0x2) != 0L) {
       flags.add(Flag.PRIVATE);
     }
     if ((mask & 0x4) != 0L) {
       flags.add(Flag.PROTECTED);
     }
     if ((mask & 0x8) != 0L) {
       flags.add(Flag.STATIC);
     }
     if ((mask & 0x10) != 0L) {
       flags.add(Flag.FINAL);
     }
     if ((mask & 0x20) != 0L) {
       flags.add(Flag.SYNCHRONIZED);
     }
     if ((mask & 0x40) != 0L) {
       flags.add(Flag.VOLATILE);
     }
     if ((mask & 0x80) != 0L) {
       flags.add(Flag.TRANSIENT);
     }
     if ((mask & 0x100) != 0L) {
       flags.add(Flag.NATIVE);
     }
     if ((mask & 0x200) != 0L) {
       flags.add(Flag.INTERFACE);
     }
     if ((mask & 0x400) != 0L) {
       flags.add(Flag.ABSTRACT);
     }
     if ((mask & 0x80000000000L) != 0L) {
       flags.add(Flag.DEFAULT);
     }
     if ((mask & 0x80000000000L) != 0L) {
       flags.add(Flag.DEFAULT);
     }
     if ((mask & 0x800) != 0L) {
       flags.add(Flag.STRICTFP);
     }
     if ((mask & 0x200000000000L) != 0L) {
       flags.add(Flag.SUPER);
     }
     if ((mask & 0x80000000) != 0L) {
       flags.add(Flag.BRIDGE);
     }
     if ((mask & 0x1000) != 0L) {
       flags.add(Flag.SYNTHETIC);
     }
     if ((mask & 0x20000) != 0L) {
       flags.add(Flag.DEPRECATED);
     }
     if ((mask & 0x40000) != 0L) {
       flags.add(Flag.HASINIT);
     }
     if ((mask & 0x4000) != 0L) {
       flags.add(Flag.ENUM);
     }
     if ((mask & 0x8000) != 0L) {
       flags.add(Flag.MANDATED);
     }
     if ((mask & 0x200000) != 0L) {
       flags.add(Flag.IPROXY);
     }
     if ((mask & 0x400000) != 0L) {
       flags.add(Flag.NOOUTERTHIS);
     }
     if ((mask & 0x800000) != 0L) {
       flags.add(Flag.EXISTS);
     }
     if ((mask & 0x1000000) != 0L) {
       flags.add(Flag.COMPOUND);
     }
     if ((mask & 0x2000000) != 0L) {
       flags.add(Flag.CLASS_SEEN);
     }
     if ((mask & 0x4000000) != 0L) {
       flags.add(Flag.SOURCE_SEEN);
     }
     if ((mask & 0x8000000) != 0L) {
       flags.add(Flag.LOCKED);
     }
     if ((mask & 0x10000000) != 0L) {
       flags.add(Flag.UNATTRIBUTED);
     }
     if ((mask & 0x20000000) != 0L) {
       flags.add(Flag.ANONCONSTR);
     }
     if ((mask & 0x40000000) != 0L) {
       flags.add(Flag.ACYCLIC);
     }
     if ((mask & 0x200000000L) != 0L) {
       flags.add(Flag.PARAMETER);
     }
     if ((mask & 0x400000000L) != 0L) {
       flags.add(Flag.VARARGS);
     }
     
     return flags;
   }
   
   public static EnumSet<Flag> asFlagSet(long mask, Kind kind)
   {
     EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);
     
     if ((mask & 1L) != 0L) {
       flags.add(Flag.PUBLIC);
     }
     if ((mask & 0x2) != 0L) {
       flags.add(Flag.PRIVATE);
     }
     if ((mask & 0x4) != 0L) {
       flags.add(Flag.PROTECTED);
     }
     if ((mask & 0x8) != 0L) {
       flags.add(Flag.STATIC);
     }
     if ((mask & 0x10) != 0L) {
       flags.add(Flag.FINAL);
     }
     if ((mask & 0x20) != 0L) {
       flags.add((kind == Kind.Class) || (kind == Kind.InnerClass) ? Flag.SUPER : Flag.SYNCHRONIZED);
     }
     if ((mask & 0x40) != 0L) {
       flags.add(kind == Kind.Method ? Flag.BRIDGE : Flag.VOLATILE);
     }
     if ((mask & 0x80) != 0L) {
       flags.add(kind == Kind.Method ? Flag.VARARGS : Flag.TRANSIENT);
     }
     if ((mask & 0x100) != 0L) {
       flags.add(Flag.NATIVE);
     }
     if ((mask & 0x200) != 0L) {
       flags.add(Flag.INTERFACE);
     }
     if ((mask & 0x400) != 0L) {
       flags.add(Flag.ABSTRACT);
     }
     if ((mask & 0x80000000000L) != 0L) {
       flags.add(Flag.DEFAULT);
     }
     if ((mask & 0x800) != 0L) {
       flags.add(Flag.STRICTFP);
     }
     if ((mask & 0x200000000000L) != 0L) {
       flags.add(Flag.SUPER);
     }
     if ((mask & 0x80000000) != 0L) {
       flags.add(Flag.BRIDGE);
     }
     if ((mask & 0x1000) != 0L) {
       flags.add(Flag.SYNTHETIC);
     }
     if ((mask & 0x20000) != 0L) {
       flags.add(Flag.DEPRECATED);
     }
     if ((mask & 0x40000) != 0L) {
       flags.add(Flag.HASINIT);
     }
     if ((mask & 0x4000) != 0L) {
       flags.add(Flag.ENUM);
     }
     if ((mask & 0x200000) != 0L) {
       flags.add(Flag.IPROXY);
     }
     if ((mask & 0x400000) != 0L) {
       flags.add(Flag.NOOUTERTHIS);
     }
     if ((mask & 0x800000) != 0L) {
       flags.add(Flag.EXISTS);
     }
     if ((mask & 0x1000000) != 0L) {
       flags.add(Flag.COMPOUND);
     }
     if ((mask & 0x2000000) != 0L) {
       flags.add(Flag.CLASS_SEEN);
     }
     if ((mask & 0x4000000) != 0L) {
       flags.add(Flag.SOURCE_SEEN);
     }
     if ((mask & 0x8000000) != 0L) {
       flags.add(Flag.LOCKED);
     }
     if ((mask & 0x10000000) != 0L) {
       flags.add(Flag.UNATTRIBUTED);
     }
     if ((mask & 0x20000000) != 0L) {
       flags.add(Flag.ANONCONSTR);
     }
     if ((mask & 0x40000000) != 0L) {
       flags.add(Flag.ACYCLIC);
     }
     if ((mask & 0x200000000L) != 0L) {
       flags.add(Flag.PARAMETER);
     }
     if ((mask & 0x400000000L) != 0L) {
       flags.add(Flag.VARARGS);
     }
     
     return flags;
   }
   
   public static enum Kind {
     Class, 
     InnerClass, 
     Field, 
     Method;
     
 
 
 
 
     private Kind() {}
   }
   
 
 
 
 
   public static final int MANDATED = 32768;
   
 
 
 
   public static final int StandardFlags = 4095;
   
 
 
 
   public static final int ModifierFlags = 3583;
   
 
 
 
   public static final int ACC_SUPER = 32;
   
 
 
 
   public static final int ACC_BRIDGE = 64;
   
 
 
 
   public static final int ACC_VARARGS = 128;
   
 
 
 
   public static final int DEPRECATED = 131072;
   
 
 
 
   public static final int HASINIT = 262144;
   
 
 
 
   public static final int BLOCK = 1048576;
   
 
 
 
   public static final int IPROXY = 2097152;
   
 
 
 
   public static final int NOOUTERTHIS = 4194304;
   
 
 
 
   public static final int EXISTS = 8388608;
   
 
 
 
   public static final int COMPOUND = 16777216;
   
 
 
 
   public static final int CLASS_SEEN = 33554432;
   
 
 
 
   public static final int SOURCE_SEEN = 67108864;
   
 
 
 
   public static final int LOCKED = 134217728;
   
 
 
 
   public static final int UNATTRIBUTED = 268435456;
   
 
 
 
   public static final int ANONCONSTR = 536870912;
   
 
 
 
   public static final int ACYCLIC = 1073741824;
   
 
 
 
   public static final long BRIDGE = 2147483648L;
   
 
 
 
   public static final long PARAMETER = 8589934592L;
   
 
 
 
   public static final long VARARGS = 17179869184L;
   
 
 
 
   public static final long ACYCLIC_ANN = 34359738368L;
   
 
 
 
   public static final long GENERATEDCONSTR = 68719476736L;
   
 
 
 
   public static final long HYPOTHETICAL = 137438953472L;
   
 
 
 
   public static final long PROPRIETARY = 274877906944L;
   
 
 
 
   public static final long UNION = 549755813888L;
   
 
 
 
   public static final long OVERRIDE_BRIDGE = 1099511627776L;
   
 
 
 
   public static final long EFFECTIVELY_FINAL = 2199023255552L;
   
 
 
 
   public static final long CLASH = 4398046511104L;
   
 
 
 
   public static final long DEFAULT = 8796093022208L;
   
 
 
 
   public static final long ANONYMOUS = 17592186044416L;
   
 
 
 
   public static final long SUPER = 35184372088832L;
   
 
 
 
   public static final long LOAD_BODY_FAILED = 70368744177664L;
   
 
 
 
   public static final long DEOBFUSCATED = 140737488355328L;
   
 
 
 
   public static final int AccessFlags = 7;
   
 
 
 
   public static final int LocalClassFlags = 23568;
   
 
 
 
   public static final int MemberClassFlags = 24087;
   
 
 
 
   public static final int ClassFlags = 32273;
   
 
 
 
   public static final int InterfaceVarFlags = 25;
   
 
 
 
   public static final int VarFlags = 16607;
   
 
 
 
   public static final int ConstructorFlags = 7;
   
 
 
 
   public static final int InterfaceMethodFlags = 1025;
   
 
 
 
   public static final int MethodFlags = 3391;
   
 
 
 
   public static final long LocalVarFlags = 8589934608L;
   
 
 
 
   public static Set<Modifier> asModifierSet(long flags)
   {
     Set<Modifier> modifiers = (Set)modifierSets.get(Long.valueOf(flags));
     
     if (modifiers == null) {
       modifiers = EnumSet.noneOf(Modifier.class);
       
       if (0L != (flags & 1L)) {
         modifiers.add(Modifier.PUBLIC);
       }
       if (0L != (flags & 0x4)) {
         modifiers.add(Modifier.PROTECTED);
       }
       if (0L != (flags & 0x2)) {
         modifiers.add(Modifier.PRIVATE);
       }
       if (0L != (flags & 0x400)) {
         modifiers.add(Modifier.ABSTRACT);
       }
       if (0L != (flags & 0x8)) {
         modifiers.add(Modifier.STATIC);
       }
       if (0L != (flags & 0x10)) {
         modifiers.add(Modifier.FINAL);
       }
       if (0L != (flags & 0x80)) {
         modifiers.add(Modifier.TRANSIENT);
       }
       if (0L != (flags & 0x40)) {
         modifiers.add(Modifier.VOLATILE);
       }
       if (0L != (flags & 0x20)) {
         modifiers.add(Modifier.SYNCHRONIZED);
       }
       if (0L != (flags & 0x100)) {
         modifiers.add(Modifier.NATIVE);
       }
       if (0L != (flags & 0x800)) {
         modifiers.add(Modifier.STRICTFP);
       }
       
       modifiers = Collections.unmodifiableSet(modifiers);
       modifierSets.put(Long.valueOf(flags), modifiers);
     }
     
     return modifiers;
   }
   
   public static int toModifiers(long flags) {
     int modifiers = 0;
     
     if ((flags & 1L) != 0L) {
       modifiers |= 0x1;
     }
     if ((flags & 0x4) != 0L) {
       modifiers |= 0x4;
     }
     if ((flags & 0x2) != 0L) {
       modifiers |= 0x2;
     }
     if ((flags & 0x400) != 0L) {
       modifiers |= 0x400;
     }
     if ((flags & 0x8) != 0L) {
       modifiers |= 0x8;
     }
     if ((flags & 0x10) != 0L) {
       modifiers |= 0x10;
     }
     if ((flags & 0x80) != 0L) {
       modifiers |= 0x80;
     }
     if ((flags & 0x40) != 0L) {
       modifiers |= 0x40;
     }
     if ((flags & 0x20) != 0L) {
       modifiers |= 0x20;
     }
     if ((flags & 0x100) != 0L) {
       modifiers |= 0x100;
     }
     if ((flags & 0x800) != 0L) {
       modifiers |= 0x800;
     }
     
     return modifiers;
   }
   
   public static boolean testAny(int value, int flags) {
     return (value & flags) != 0;
   }
   
   public static boolean testAll(int value, int flags) {
     return (value & flags) == flags;
   }
   
   public static boolean testAny(long value, long flags) {
     return (value & flags) != 0L;
   }
   
   public static boolean testAll(long value, long flags) {
     return (value & flags) == flags;
   }
   
 
   private static final Map<Long, Set<Modifier>> modifierSets = new ConcurrentHashMap(64);
   
   public static boolean isEnum(TypeDefinition symbol)
   {
     return (symbol.getModifiers() & 0x4000) != 0;
   }
   
   public static long fromStandardFlags(long accessFlags, Kind kind) {
     long flags = accessFlags;
     
     if (testAny(accessFlags, 32L)) {
       flags |= ((kind == Kind.Class) || (kind == Kind.InnerClass) ? 35184372088832L : 32L);
     }
     
     if (testAny(accessFlags, 64L)) {
       flags |= (kind == Kind.Field ? 64L : 2147483648L);
     }
     
     if (testAny(accessFlags, 128L)) {
       flags |= (kind == Kind.Field ? 128L : 17179869184L);
     }
     
     return flags;
   }
   
   public static enum Flag {
     PUBLIC("public"), 
     PRIVATE("private"), 
     PROTECTED("protected"), 
     STATIC("static"), 
     FINAL("final"), 
     SYNCHRONIZED("synchronized"), 
     VOLATILE("volatile"), 
     TRANSIENT("transient"), 
     NATIVE("native"), 
     INTERFACE("interface"), 
     ABSTRACT("abstract"), 
     DEFAULT("default"), 
     STRICTFP("strictfp"), 
     SUPER("super"), 
     BRIDGE("bridge"), 
     SYNTHETIC("synthetic"), 
     DEPRECATED("deprecated"), 
     HASINIT("hasinit"), 
     ENUM("enum"), 
     MANDATED("mandated"), 
     IPROXY("iproxy"), 
     NOOUTERTHIS("noouterthis"), 
     EXISTS("exists"), 
     COMPOUND("compound"), 
     CLASS_SEEN("class_seen"), 
     SOURCE_SEEN("source_seen"), 
     LOCKED("locked"), 
     UNATTRIBUTED("unattributed"), 
     ANONCONSTR("anonconstr"), 
     ACYCLIC("acyclic"), 
     PARAMETER("parameter"), 
     VARARGS("varargs"), 
     PACKAGE("package");
     
     public final String name;
     
     private Flag(String name) {
       this.name = name;
     }
     
     public String toString() {
       return this.name;
     }
   }
 }


