/* StackBehavior - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir;

public final class StackBehavior extends Enum
{
    public static final StackBehavior Pop0 = new StackBehavior("Pop0", 0);
    public static final StackBehavior Pop1 = new StackBehavior("Pop1", 1);
    public static final StackBehavior Pop2 = new StackBehavior("Pop2", 2);
    public static final StackBehavior Pop1_Pop1
	= new StackBehavior("Pop1_Pop1", 3);
    public static final StackBehavior Pop1_Pop2
	= new StackBehavior("Pop1_Pop2", 4);
    public static final StackBehavior Pop1_PopA
	= new StackBehavior("Pop1_PopA", 5);
    public static final StackBehavior Pop2_Pop1
	= new StackBehavior("Pop2_Pop1", 6);
    public static final StackBehavior Pop2_Pop2
	= new StackBehavior("Pop2_Pop2", 7);
    public static final StackBehavior PopI4 = new StackBehavior("PopI4", 8);
    public static final StackBehavior PopI8 = new StackBehavior("PopI8", 9);
    public static final StackBehavior PopR4 = new StackBehavior("PopR4", 10);
    public static final StackBehavior PopR8 = new StackBehavior("PopR8", 11);
    public static final StackBehavior PopA = new StackBehavior("PopA", 12);
    public static final StackBehavior PopI4_PopI4
	= new StackBehavior("PopI4_PopI4", 13);
    public static final StackBehavior PopI4_PopI8
	= new StackBehavior("PopI4_PopI8", 14);
    public static final StackBehavior PopI8_PopI8
	= new StackBehavior("PopI8_PopI8", 15);
    public static final StackBehavior PopR4_PopR4
	= new StackBehavior("PopR4_PopR4", 16);
    public static final StackBehavior PopR8_PopR8
	= new StackBehavior("PopR8_PopR8", 17);
    public static final StackBehavior PopI4_PopA
	= new StackBehavior("PopI4_PopA", 18);
    public static final StackBehavior PopI4_PopI4_PopA
	= new StackBehavior("PopI4_PopI4_PopA", 19);
    public static final StackBehavior PopI8_PopI4_PopA
	= new StackBehavior("PopI8_PopI4_PopA", 20);
    public static final StackBehavior PopR4_PopI4_PopA
	= new StackBehavior("PopR4_PopI4_PopA", 21);
    public static final StackBehavior PopR8_PopI4_PopA
	= new StackBehavior("PopR8_PopI4_PopA", 22);
    public static final StackBehavior PopA_PopI4_PopA
	= new StackBehavior("PopA_PopI4_PopA", 23);
    public static final StackBehavior PopA_PopA
	= new StackBehavior("PopA_PopA", 24);
    public static final StackBehavior Push0 = new StackBehavior("Push0", 25);
    public static final StackBehavior Push1 = new StackBehavior("Push1", 26);
    public static final StackBehavior Push1_Push1
	= new StackBehavior("Push1_Push1", 27);
    public static final StackBehavior Push1_Push1_Push1
	= new StackBehavior("Push1_Push1_Push1", 28);
    public static final StackBehavior Push1_Push2_Push1
	= new StackBehavior("Push1_Push2_Push1", 29);
    public static final StackBehavior Push2 = new StackBehavior("Push2", 30);
    public static final StackBehavior Push2_Push2
	= new StackBehavior("Push2_Push2", 31);
    public static final StackBehavior Push2_Push1_Push2
	= new StackBehavior("Push2_Push1_Push2", 32);
    public static final StackBehavior Push2_Push2_Push2
	= new StackBehavior("Push2_Push2_Push2", 33);
    public static final StackBehavior PushI4 = new StackBehavior("PushI4", 34);
    public static final StackBehavior PushI8 = new StackBehavior("PushI8", 35);
    public static final StackBehavior PushR4 = new StackBehavior("PushR4", 36);
    public static final StackBehavior PushR8 = new StackBehavior("PushR8", 37);
    public static final StackBehavior PushA = new StackBehavior("PushA", 38);
    public static final StackBehavior PushAddress
	= new StackBehavior("PushAddress", 39);
    public static final StackBehavior VarPop = new StackBehavior("VarPop", 40);
    public static final StackBehavior VarPush
	= new StackBehavior("VarPush", 41);
    /*synthetic*/ private static final StackBehavior[] $VALUES
		      = { Pop0, Pop1, Pop2, Pop1_Pop1, Pop1_Pop2, Pop1_PopA,
			  Pop2_Pop1, Pop2_Pop2, PopI4, PopI8, PopR4, PopR8,
			  PopA, PopI4_PopI4, PopI4_PopI8, PopI8_PopI8,
			  PopR4_PopR4, PopR8_PopR8, PopI4_PopA,
			  PopI4_PopI4_PopA, PopI8_PopI4_PopA, PopR4_PopI4_PopA,
			  PopR8_PopI4_PopA, PopA_PopI4_PopA, PopA_PopA, Push0,
			  Push1, Push1_Push1, Push1_Push1_Push1,
			  Push1_Push2_Push1, Push2, Push2_Push2,
			  Push2_Push1_Push2, Push2_Push2_Push2, PushI4, PushI8,
			  PushR4, PushR8, PushA, PushAddress, VarPop,
			  VarPush };
    
    public static StackBehavior[] values() {
	return (StackBehavior[]) $VALUES.clone();
    }
    
    public static StackBehavior valueOf(String name) {
	return (StackBehavior) Enum.valueOf(StackBehavior.class, name);
    }
    
    private StackBehavior(String string, int i) {
	super(string, i);
    }
}
