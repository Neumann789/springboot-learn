/* DecompilerContext - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.strobel.assembler.Collection;
import com.strobel.assembler.metadata.MethodDefinition;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.componentmodel.UserDataStoreBase;
import com.strobel.core.BooleanBox;

public final class DecompilerContext extends UserDataStoreBase
{
    private final List _reservedVariableNames;
    private final Set _forcedVisibleMembers;
    private DecompilerSettings _settings;
    private BooleanBox _isCanceled;
    private TypeDefinition _currentType;
    private MethodDefinition _currentMethod;
    
    public DecompilerContext() {
	_reservedVariableNames = new Collection();
	_forcedVisibleMembers = new LinkedHashSet();
	_settings = new DecompilerSettings();
    }
    
    public DecompilerContext(DecompilerSettings settings) {
	_reservedVariableNames = new Collection();
	_forcedVisibleMembers = new LinkedHashSet();
	_settings = new DecompilerSettings();
	_settings = settings;
    }
    
    public DecompilerSettings getSettings() {
	return _settings;
    }
    
    public void setSettings(DecompilerSettings settings) {
	_settings = settings;
    }
    
    public BooleanBox getCanceled() {
	return _isCanceled;
    }
    
    public void setCanceled(BooleanBox canceled) {
	_isCanceled = canceled;
    }
    
    public TypeDefinition getCurrentType() {
	return _currentType;
    }
    
    public void setCurrentType(TypeDefinition currentType) {
	_currentType = currentType;
    }
    
    public MethodDefinition getCurrentMethod() {
	return _currentMethod;
    }
    
    public void setCurrentMethod(MethodDefinition currentMethod) {
	_currentMethod = currentMethod;
    }
    
    public List getReservedVariableNames() {
	return _reservedVariableNames;
    }
    
    public Set getForcedVisibleMembers() {
	return _forcedVisibleMembers;
    }
}
