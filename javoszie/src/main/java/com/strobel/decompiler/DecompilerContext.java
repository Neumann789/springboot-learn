 package com.strobel.decompiler;
 
 import com.strobel.assembler.Collection;
 import com.strobel.assembler.metadata.IMemberDefinition;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.componentmodel.UserDataStoreBase;
 import com.strobel.core.BooleanBox;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Set;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class DecompilerContext
   extends UserDataStoreBase
 {
   private final List<String> _reservedVariableNames = new Collection();
   private final Set<IMemberDefinition> _forcedVisibleMembers = new LinkedHashSet();
   private DecompilerSettings _settings = new DecompilerSettings();
   private BooleanBox _isCanceled;
   private TypeDefinition _currentType;
   private MethodDefinition _currentMethod;
   
   public DecompilerContext() {}
   
   public DecompilerContext(DecompilerSettings settings)
   {
     this._settings = settings;
   }
   
   public DecompilerSettings getSettings() {
     return this._settings;
   }
   
   public void setSettings(DecompilerSettings settings) {
     this._settings = settings;
   }
   
   public BooleanBox getCanceled() {
     return this._isCanceled;
   }
   
   public void setCanceled(BooleanBox canceled) {
     this._isCanceled = canceled;
   }
   
   public TypeDefinition getCurrentType() {
     return this._currentType;
   }
   
   public void setCurrentType(TypeDefinition currentType) {
     this._currentType = currentType;
   }
   
   public MethodDefinition getCurrentMethod() {
     return this._currentMethod;
   }
   
   public void setCurrentMethod(MethodDefinition currentMethod) {
     this._currentMethod = currentMethod;
   }
   
   public List<String> getReservedVariableNames() {
     return this._reservedVariableNames;
   }
   
   public Set<IMemberDefinition> getForcedVisibleMembers() {
     return this._forcedVisibleMembers;
   }
 }


