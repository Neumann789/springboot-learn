/* EntityType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages;

public final class EntityType extends Enum
{
    public static final EntityType NONE = new EntityType("NONE", 0);
    public static final EntityType TYPE_DEFINITION
	= new EntityType("TYPE_DEFINITION", 1);
    public static final EntityType ENUM_VALUE
	= new EntityType("ENUM_VALUE", 2);
    public static final EntityType FIELD = new EntityType("FIELD", 3);
    public static final EntityType METHOD = new EntityType("METHOD", 4);
    public static final EntityType CONSTRUCTOR
	= new EntityType("CONSTRUCTOR", 5);
    public static final EntityType PARAMETER = new EntityType("PARAMETER", 6);
    /*synthetic*/ private static final EntityType[] $VALUES
		      = { NONE, TYPE_DEFINITION, ENUM_VALUE, FIELD, METHOD,
			  CONSTRUCTOR, PARAMETER };
    
    public static EntityType[] values() {
	return (EntityType[]) $VALUES.clone();
    }
    
    public static EntityType valueOf(String name) {
	return (EntityType) Enum.valueOf(EntityType.class, name);
    }
    
    private EntityType(String string, int i) {
	super(string, i);
    }
}
