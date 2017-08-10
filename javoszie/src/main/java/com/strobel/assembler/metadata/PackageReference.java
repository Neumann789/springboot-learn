 package com.strobel.assembler.metadata;
 
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class PackageReference
 {
   public static final PackageReference GLOBAL = new PackageReference();
   
   private final PackageReference _parent;
   private final String _name;
   private String _fullName;
   
   private PackageReference()
   {
     this._parent = null;
     this._name = "";
   }
   
   public PackageReference(String name) {
     this._parent = null;
     this._name = ((String)VerifyArgument.notNull(name, "name"));
   }
   
   public PackageReference(PackageReference parent, String name) {
     this._parent = parent;
     this._name = ((String)VerifyArgument.notNull(name, "name"));
   }
   
   public final boolean isGlobal() {
     return this._name.length() == 0;
   }
   
   public final String getName() {
     return this._name;
   }
   
   public final String getFullName() {
     if (this._fullName == null) {
       if ((this._parent == null) || (this._parent.equals(GLOBAL))) {
         this._fullName = getName();
       }
       else {
         this._fullName = (this._parent.getFullName() + "." + getName());
       }
     }
     return this._fullName;
   }
   
   public final PackageReference getParent() {
     return this._parent;
   }
   
   public boolean equals(Object o)
   {
     if (this == o) {
       return true;
     }
     
     if ((o instanceof PackageReference)) {
       PackageReference that = (PackageReference)o;
       
       return (this._name.equals(that._name)) && (this._parent == null ? that._parent == null : this._parent.equals(that._parent));
     }
     
 
     return false;
   }
   
   public int hashCode()
   {
     int result = this._parent != null ? this._parent.hashCode() : 0;
     result = 31 * result + this._name.hashCode();
     return result;
   }
   
   public static PackageReference parse(String qualifiedName) {
     VerifyArgument.notNull(qualifiedName, "qualifiedName");
     
     List<String> parts = StringUtilities.split(qualifiedName, '.', new char[] { '/' });
     
     if (parts.isEmpty()) {
       return GLOBAL;
     }
     
     PackageReference current = new PackageReference((String)parts.get(0));
     
     for (int i = 1; i < parts.size(); i++) {
       current = new PackageReference(current, (String)parts.get(i));
     }
     
     return current;
   }
 }


