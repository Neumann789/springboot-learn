/* AstCode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.ast;
import com.strobel.assembler.ir.OpCode;
import com.strobel.assembler.metadata.MethodBody;
import com.strobel.core.StringUtilities;
import com.strobel.core.StrongBox;

public final class AstCode extends Enum
{
    public static final AstCode Nop = new AstCode("Nop", 0);
    public static final AstCode AConstNull = new AstCode("AConstNull", 1);
    public static final AstCode __IConstM1 = new AstCode("__IConstM1", 2);
    public static final AstCode __IConst0 = new AstCode("__IConst0", 3);
    public static final AstCode __IConst1 = new AstCode("__IConst1", 4);
    public static final AstCode __IConst2 = new AstCode("__IConst2", 5);
    public static final AstCode __IConst3 = new AstCode("__IConst3", 6);
    public static final AstCode __IConst4 = new AstCode("__IConst4", 7);
    public static final AstCode __IConst5 = new AstCode("__IConst5", 8);
    public static final AstCode __LConst0 = new AstCode("__LConst0", 9);
    public static final AstCode __LConst1 = new AstCode("__LConst1", 10);
    public static final AstCode __FConst0 = new AstCode("__FConst0", 11);
    public static final AstCode __FConst1 = new AstCode("__FConst1", 12);
    public static final AstCode __FConst2 = new AstCode("__FConst2", 13);
    public static final AstCode __DConst0 = new AstCode("__DConst0", 14);
    public static final AstCode __DConst1 = new AstCode("__DConst1", 15);
    public static final AstCode __BIPush = new AstCode("__BIPush", 16);
    public static final AstCode __SIPush = new AstCode("__SIPush", 17);
    public static final AstCode LdC = new AstCode("LdC", 18);
    public static final AstCode __LdCW = new AstCode("__LdCW", 19);
    public static final AstCode __LdC2W = new AstCode("__LdC2W", 20);
    public static final AstCode __ILoad = new AstCode("__ILoad", 21);
    public static final AstCode __LLoad = new AstCode("__LLoad", 22);
    public static final AstCode __FLoad = new AstCode("__FLoad", 23);
    public static final AstCode __DLoad = new AstCode("__DLoad", 24);
    public static final AstCode __ALoad = new AstCode("__ALoad", 25);
    public static final AstCode __ILoad0 = new AstCode("__ILoad0", 26);
    public static final AstCode __ILoad1 = new AstCode("__ILoad1", 27);
    public static final AstCode __ILoad2 = new AstCode("__ILoad2", 28);
    public static final AstCode __ILoad3 = new AstCode("__ILoad3", 29);
    public static final AstCode __LLoad0 = new AstCode("__LLoad0", 30);
    public static final AstCode __LLoad1 = new AstCode("__LLoad1", 31);
    public static final AstCode __LLoad2 = new AstCode("__LLoad2", 32);
    public static final AstCode __LLoad3 = new AstCode("__LLoad3", 33);
    public static final AstCode __FLoad0 = new AstCode("__FLoad0", 34);
    public static final AstCode __FLoad1 = new AstCode("__FLoad1", 35);
    public static final AstCode __FLoad2 = new AstCode("__FLoad2", 36);
    public static final AstCode __FLoad3 = new AstCode("__FLoad3", 37);
    public static final AstCode __DLoad0 = new AstCode("__DLoad0", 38);
    public static final AstCode __DLoad1 = new AstCode("__DLoad1", 39);
    public static final AstCode __DLoad2 = new AstCode("__DLoad2", 40);
    public static final AstCode __DLoad3 = new AstCode("__DLoad3", 41);
    public static final AstCode __ALoad0 = new AstCode("__ALoad0", 42);
    public static final AstCode __ALoad1 = new AstCode("__ALoad1", 43);
    public static final AstCode __ALoad2 = new AstCode("__ALoad2", 44);
    public static final AstCode __ALoad3 = new AstCode("__ALoad3", 45);
    public static final AstCode __IALoad = new AstCode("__IALoad", 46);
    public static final AstCode __LALoad = new AstCode("__LALoad", 47);
    public static final AstCode __FALoad = new AstCode("__FALoad", 48);
    public static final AstCode __DALoad = new AstCode("__DALoad", 49);
    public static final AstCode __AALoad = new AstCode("__AALoad", 50);
    public static final AstCode __BALoad = new AstCode("__BALoad", 51);
    public static final AstCode __CALoad = new AstCode("__CALoad", 52);
    public static final AstCode __SALoad = new AstCode("__SALoad", 53);
    public static final AstCode __IStore = new AstCode("__IStore", 54);
    public static final AstCode __LStore = new AstCode("__LStore", 55);
    public static final AstCode __FStore = new AstCode("__FStore", 56);
    public static final AstCode __DStore = new AstCode("__DStore", 57);
    public static final AstCode __AStore = new AstCode("__AStore", 58);
    public static final AstCode __IStore0 = new AstCode("__IStore0", 59);
    public static final AstCode __IStore1 = new AstCode("__IStore1", 60);
    public static final AstCode __IStore2 = new AstCode("__IStore2", 61);
    public static final AstCode __IStore3 = new AstCode("__IStore3", 62);
    public static final AstCode __LStore0 = new AstCode("__LStore0", 63);
    public static final AstCode __LStore1 = new AstCode("__LStore1", 64);
    public static final AstCode __LStore2 = new AstCode("__LStore2", 65);
    public static final AstCode __LStore3 = new AstCode("__LStore3", 66);
    public static final AstCode __FStore0 = new AstCode("__FStore0", 67);
    public static final AstCode __FStore1 = new AstCode("__FStore1", 68);
    public static final AstCode __FStore2 = new AstCode("__FStore2", 69);
    public static final AstCode __FStore3 = new AstCode("__FStore3", 70);
    public static final AstCode __DStore0 = new AstCode("__DStore0", 71);
    public static final AstCode __DStore1 = new AstCode("__DStore1", 72);
    public static final AstCode __DStore2 = new AstCode("__DStore2", 73);
    public static final AstCode __DStore3 = new AstCode("__DStore3", 74);
    public static final AstCode __AStore0 = new AstCode("__AStore0", 75);
    public static final AstCode __AStore1 = new AstCode("__AStore1", 76);
    public static final AstCode __AStore2 = new AstCode("__AStore2", 77);
    public static final AstCode __AStore3 = new AstCode("__AStore3", 78);
    public static final AstCode __IAStore = new AstCode("__IAStore", 79);
    public static final AstCode __LAStore = new AstCode("__LAStore", 80);
    public static final AstCode __FAStore = new AstCode("__FAStore", 81);
    public static final AstCode __DAStore = new AstCode("__DAStore", 82);
    public static final AstCode __AAStore = new AstCode("__AAStore", 83);
    public static final AstCode __BAStore = new AstCode("__BAStore", 84);
    public static final AstCode __CAStore = new AstCode("__CAStore", 85);
    public static final AstCode __SAStore = new AstCode("__SAStore", 86);
    public static final AstCode Pop = new AstCode("Pop", 87);
    public static final AstCode Pop2 = new AstCode("Pop2", 88);
    public static final AstCode Dup = new AstCode("Dup", 89);
    public static final AstCode DupX1 = new AstCode("DupX1", 90);
    public static final AstCode DupX2 = new AstCode("DupX2", 91);
    public static final AstCode Dup2 = new AstCode("Dup2", 92);
    public static final AstCode Dup2X1 = new AstCode("Dup2X1", 93);
    public static final AstCode Dup2X2 = new AstCode("Dup2X2", 94);
    public static final AstCode Swap = new AstCode("Swap", 95);
    public static final AstCode __IAdd = new AstCode("__IAdd", 96);
    public static final AstCode __LAdd = new AstCode("__LAdd", 97);
    public static final AstCode __FAdd = new AstCode("__FAdd", 98);
    public static final AstCode __DAdd = new AstCode("__DAdd", 99);
    public static final AstCode __ISub = new AstCode("__ISub", 100);
    public static final AstCode __LSub = new AstCode("__LSub", 101);
    public static final AstCode __FSub = new AstCode("__FSub", 102);
    public static final AstCode __DSub = new AstCode("__DSub", 103);
    public static final AstCode __IMul = new AstCode("__IMul", 104);
    public static final AstCode __LMul = new AstCode("__LMul", 105);
    public static final AstCode __FMul = new AstCode("__FMul", 106);
    public static final AstCode __DMul = new AstCode("__DMul", 107);
    public static final AstCode __IDiv = new AstCode("__IDiv", 108);
    public static final AstCode __LDiv = new AstCode("__LDiv", 109);
    public static final AstCode __FDiv = new AstCode("__FDiv", 110);
    public static final AstCode __DDiv = new AstCode("__DDiv", 111);
    public static final AstCode __IRem = new AstCode("__IRem", 112);
    public static final AstCode __LRem = new AstCode("__LRem", 113);
    public static final AstCode __FRem = new AstCode("__FRem", 114);
    public static final AstCode __DRem = new AstCode("__DRem", 115);
    public static final AstCode __INeg = new AstCode("__INeg", 116);
    public static final AstCode __LNeg = new AstCode("__LNeg", 117);
    public static final AstCode __FNeg = new AstCode("__FNeg", 118);
    public static final AstCode __DNeg = new AstCode("__DNeg", 119);
    public static final AstCode __IShl = new AstCode("__IShl", 120);
    public static final AstCode __LShl = new AstCode("__LShl", 121);
    public static final AstCode __IShr = new AstCode("__IShr", 122);
    public static final AstCode __LShr = new AstCode("__LShr", 123);
    public static final AstCode __IUShr = new AstCode("__IUShr", 124);
    public static final AstCode __LUShr = new AstCode("__LUShr", 125);
    public static final AstCode __IAnd = new AstCode("__IAnd", 126);
    public static final AstCode __LAnd = new AstCode("__LAnd", 127);
    public static final AstCode __IOr = new AstCode("__IOr", 128);
    public static final AstCode __LOr = new AstCode("__LOr", 129);
    public static final AstCode __IXor = new AstCode("__IXor", 130);
    public static final AstCode __LXor = new AstCode("__LXor", 131);
    public static final AstCode __IInc = new AstCode("__IInc", 132);
    public static final AstCode I2L = new AstCode("I2L", 133);
    public static final AstCode I2F = new AstCode("I2F", 134);
    public static final AstCode I2D = new AstCode("I2D", 135);
    public static final AstCode L2I = new AstCode("L2I", 136);
    public static final AstCode L2F = new AstCode("L2F", 137);
    public static final AstCode L2D = new AstCode("L2D", 138);
    public static final AstCode F2I = new AstCode("F2I", 139);
    public static final AstCode F2L = new AstCode("F2L", 140);
    public static final AstCode F2D = new AstCode("F2D", 141);
    public static final AstCode D2I = new AstCode("D2I", 142);
    public static final AstCode D2L = new AstCode("D2L", 143);
    public static final AstCode D2F = new AstCode("D2F", 144);
    public static final AstCode I2B = new AstCode("I2B", 145);
    public static final AstCode I2C = new AstCode("I2C", 146);
    public static final AstCode I2S = new AstCode("I2S", 147);
    public static final AstCode __LCmp = new AstCode("__LCmp", 148);
    public static final AstCode __FCmpL = new AstCode("__FCmpL", 149);
    public static final AstCode __FCmpG = new AstCode("__FCmpG", 150);
    public static final AstCode __DCmpL = new AstCode("__DCmpL", 151);
    public static final AstCode __DCmpG = new AstCode("__DCmpG", 152);
    public static final AstCode __IfEq = new AstCode("__IfEq", 153);
    public static final AstCode __IfNe = new AstCode("__IfNe", 154);
    public static final AstCode __IfLt = new AstCode("__IfLt", 155);
    public static final AstCode __IfGe = new AstCode("__IfGe", 156);
    public static final AstCode __IfGt = new AstCode("__IfGt", 157);
    public static final AstCode __IfLe = new AstCode("__IfLe", 158);
    public static final AstCode __IfICmpEq = new AstCode("__IfICmpEq", 159);
    public static final AstCode __IfICmpNe = new AstCode("__IfICmpNe", 160);
    public static final AstCode __IfICmpLt = new AstCode("__IfICmpLt", 161);
    public static final AstCode __IfICmpGe = new AstCode("__IfICmpGe", 162);
    public static final AstCode __IfICmpGt = new AstCode("__IfICmpGt", 163);
    public static final AstCode __IfICmpLe = new AstCode("__IfICmpLe", 164);
    public static final AstCode __IfACmpEq = new AstCode("__IfACmpEq", 165);
    public static final AstCode __IfACmpNe = new AstCode("__IfACmpNe", 166);
    public static final AstCode Goto = new AstCode("Goto", 167);
    public static final AstCode Jsr = new AstCode("Jsr", 168);
    public static final AstCode Ret = new AstCode("Ret", 169);
    public static final AstCode __TableSwitch
	= new AstCode("__TableSwitch", 170);
    public static final AstCode __LookupSwitch
	= new AstCode("__LookupSwitch", 171);
    public static final AstCode __IReturn = new AstCode("__IReturn", 172);
    public static final AstCode __LReturn = new AstCode("__LReturn", 173);
    public static final AstCode __FReturn = new AstCode("__FReturn", 174);
    public static final AstCode __DReturn = new AstCode("__DReturn", 175);
    public static final AstCode __AReturn = new AstCode("__AReturn", 176);
    public static final AstCode __Return = new AstCode("__Return", 177);
    public static final AstCode GetStatic = new AstCode("GetStatic", 178);
    public static final AstCode PutStatic = new AstCode("PutStatic", 179);
    public static final AstCode GetField = new AstCode("GetField", 180);
    public static final AstCode PutField = new AstCode("PutField", 181);
    public static final AstCode InvokeVirtual
	= new AstCode("InvokeVirtual", 182);
    public static final AstCode InvokeSpecial
	= new AstCode("InvokeSpecial", 183);
    public static final AstCode InvokeStatic
	= new AstCode("InvokeStatic", 184);
    public static final AstCode InvokeInterface
	= new AstCode("InvokeInterface", 185);
    public static final AstCode InvokeDynamic
	= new AstCode("InvokeDynamic", 186);
    public static final AstCode __New = new AstCode("__New", 187);
    public static final AstCode __NewArray = new AstCode("__NewArray", 188);
    public static final AstCode __ANewArray = new AstCode("__ANewArray", 189);
    public static final AstCode ArrayLength = new AstCode("ArrayLength", 190);
    public static final AstCode AThrow = new AstCode("AThrow", 191);
    public static final AstCode CheckCast = new AstCode("CheckCast", 192);
    public static final AstCode InstanceOf = new AstCode("InstanceOf", 193);
    public static final AstCode MonitorEnter
	= new AstCode("MonitorEnter", 194);
    public static final AstCode MonitorExit = new AstCode("MonitorExit", 195);
    public static final AstCode MultiANewArray
	= new AstCode("MultiANewArray", 196);
    public static final AstCode __IfNull = new AstCode("__IfNull", 197);
    public static final AstCode __IfNonNull = new AstCode("__IfNonNull", 198);
    public static final AstCode __GotoW = new AstCode("__GotoW", 199);
    public static final AstCode __JsrW = new AstCode("__JsrW", 200);
    public static final AstCode Breakpoint = new AstCode("Breakpoint", 201);
    public static final AstCode __ILoadW = new AstCode("__ILoadW", 202);
    public static final AstCode __LLoadW = new AstCode("__LLoadW", 203);
    public static final AstCode __FLoadW = new AstCode("__FLoadW", 204);
    public static final AstCode __DLoadW = new AstCode("__DLoadW", 205);
    public static final AstCode __ALoadW = new AstCode("__ALoadW", 206);
    public static final AstCode __IStoreW = new AstCode("__IStoreW", 207);
    public static final AstCode __LStoreW = new AstCode("__LStoreW", 208);
    public static final AstCode __FStoreW = new AstCode("__FStoreW", 209);
    public static final AstCode __DStoreW = new AstCode("__DStoreW", 210);
    public static final AstCode __AStoreW = new AstCode("__AStoreW", 211);
    public static final AstCode __IIncW = new AstCode("__IIncW", 212);
    public static final AstCode __RetW = new AstCode("__RetW", 213);
    public static final AstCode Leave = new AstCode("Leave", 214);
    public static final AstCode EndFinally = new AstCode("EndFinally", 215);
    public static final AstCode Load = new AstCode("Load", 216);
    public static final AstCode Store = new AstCode("Store", 217);
    public static final AstCode LoadElement = new AstCode("LoadElement", 218);
    public static final AstCode StoreElement
	= new AstCode("StoreElement", 219);
    public static final AstCode Add = new AstCode("Add", 220);
    public static final AstCode Sub = new AstCode("Sub", 221);
    public static final AstCode Mul = new AstCode("Mul", 222);
    public static final AstCode Div = new AstCode("Div", 223);
    public static final AstCode Rem = new AstCode("Rem", 224);
    public static final AstCode Neg = new AstCode("Neg", 225);
    public static final AstCode Shl = new AstCode("Shl", 226);
    public static final AstCode Shr = new AstCode("Shr", 227);
    public static final AstCode UShr = new AstCode("UShr", 228);
    public static final AstCode And = new AstCode("And", 229);
    public static final AstCode Or = new AstCode("Or", 230);
    public static final AstCode Not = new AstCode("Not", 231);
    public static final AstCode Xor = new AstCode("Xor", 232);
    public static final AstCode Inc = new AstCode("Inc", 233);
    public static final AstCode CmpEq = new AstCode("CmpEq", 234);
    public static final AstCode CmpNe = new AstCode("CmpNe", 235);
    public static final AstCode CmpLt = new AstCode("CmpLt", 236);
    public static final AstCode CmpGe = new AstCode("CmpGe", 237);
    public static final AstCode CmpGt = new AstCode("CmpGt", 238);
    public static final AstCode CmpLe = new AstCode("CmpLe", 239);
    public static final AstCode IfTrue = new AstCode("IfTrue", 240);
    public static final AstCode Return = new AstCode("Return", 241);
    public static final AstCode NewArray = new AstCode("NewArray", 242);
    public static final AstCode LoadException
	= new AstCode("LoadException", 243);
    public static final AstCode LogicalNot = new AstCode("LogicalNot", 244);
    public static final AstCode LogicalAnd = new AstCode("LogicalAnd", 245);
    public static final AstCode LogicalOr = new AstCode("LogicalOr", 246);
    public static final AstCode InitObject = new AstCode("InitObject", 247);
    public static final AstCode InitArray = new AstCode("InitArray", 248);
    public static final AstCode Switch = new AstCode("Switch", 249);
    public static final AstCode Wrap = new AstCode("Wrap", 250);
    public static final AstCode Bind = new AstCode("Bind", 251);
    public static final AstCode TernaryOp = new AstCode("TernaryOp", 252);
    public static final AstCode LoopOrSwitchBreak
	= new AstCode("LoopOrSwitchBreak", 253);
    public static final AstCode LoopContinue
	= new AstCode("LoopContinue", 254);
    public static final AstCode CompoundAssignment
	= new AstCode("CompoundAssignment", 255);
    public static final AstCode PreIncrement
	= new AstCode("PreIncrement", 256);
    public static final AstCode PostIncrement
	= new AstCode("PostIncrement", 257);
    public static final AstCode Box = new AstCode("Box", 258);
    public static final AstCode Unbox = new AstCode("Unbox", 259);
    public static final AstCode DefaultValue
	= new AstCode("DefaultValue", 260);
    private static final OpCode[] STANDARD_CODES;
    /*synthetic*/ private static final AstCode[] $VALUES
		      = { Nop, AConstNull, __IConstM1, __IConst0, __IConst1,
			  __IConst2, __IConst3, __IConst4, __IConst5,
			  __LConst0, __LConst1, __FConst0, __FConst1,
			  __FConst2, __DConst0, __DConst1, __BIPush, __SIPush,
			  LdC, __LdCW, __LdC2W, __ILoad, __LLoad, __FLoad,
			  __DLoad, __ALoad, __ILoad0, __ILoad1, __ILoad2,
			  __ILoad3, __LLoad0, __LLoad1, __LLoad2, __LLoad3,
			  __FLoad0, __FLoad1, __FLoad2, __FLoad3, __DLoad0,
			  __DLoad1, __DLoad2, __DLoad3, __ALoad0, __ALoad1,
			  __ALoad2, __ALoad3, __IALoad, __LALoad, __FALoad,
			  __DALoad, __AALoad, __BALoad, __CALoad, __SALoad,
			  __IStore, __LStore, __FStore, __DStore, __AStore,
			  __IStore0, __IStore1, __IStore2, __IStore3,
			  __LStore0, __LStore1, __LStore2, __LStore3,
			  __FStore0, __FStore1, __FStore2, __FStore3,
			  __DStore0, __DStore1, __DStore2, __DStore3,
			  __AStore0, __AStore1, __AStore2, __AStore3,
			  __IAStore, __LAStore, __FAStore, __DAStore,
			  __AAStore, __BAStore, __CAStore, __SAStore, Pop,
			  Pop2, Dup, DupX1, DupX2, Dup2, Dup2X1, Dup2X2, Swap,
			  __IAdd, __LAdd, __FAdd, __DAdd, __ISub, __LSub,
			  __FSub, __DSub, __IMul, __LMul, __FMul, __DMul,
			  __IDiv, __LDiv, __FDiv, __DDiv, __IRem, __LRem,
			  __FRem, __DRem, __INeg, __LNeg, __FNeg, __DNeg,
			  __IShl, __LShl, __IShr, __LShr, __IUShr, __LUShr,
			  __IAnd, __LAnd, __IOr, __LOr, __IXor, __LXor, __IInc,
			  I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I,
			  D2L, D2F, I2B, I2C, I2S, __LCmp, __FCmpL, __FCmpG,
			  __DCmpL, __DCmpG, __IfEq, __IfNe, __IfLt, __IfGe,
			  __IfGt, __IfLe, __IfICmpEq, __IfICmpNe, __IfICmpLt,
			  __IfICmpGe, __IfICmpGt, __IfICmpLe, __IfACmpEq,
			  __IfACmpNe, Goto, Jsr, Ret, __TableSwitch,
			  __LookupSwitch, __IReturn, __LReturn, __FReturn,
			  __DReturn, __AReturn, __Return, GetStatic, PutStatic,
			  GetField, PutField, InvokeVirtual, InvokeSpecial,
			  InvokeStatic, InvokeInterface, InvokeDynamic, __New,
			  __NewArray, __ANewArray, ArrayLength, AThrow,
			  CheckCast, InstanceOf, MonitorEnter, MonitorExit,
			  MultiANewArray, __IfNull, __IfNonNull, __GotoW,
			  __JsrW, Breakpoint, __ILoadW, __LLoadW, __FLoadW,
			  __DLoadW, __ALoadW, __IStoreW, __LStoreW, __FStoreW,
			  __DStoreW, __AStoreW, __IIncW, __RetW, Leave,
			  EndFinally, Load, Store, LoadElement, StoreElement,
			  Add, Sub, Mul, Div, Rem, Neg, Shl, Shr, UShr, And,
			  Or, Not, Xor, Inc, CmpEq, CmpNe, CmpLt, CmpGe, CmpGt,
			  CmpLe, IfTrue, Return, NewArray, LoadException,
			  LogicalNot, LogicalAnd, LogicalOr, InitObject,
			  InitArray, Switch, Wrap, Bind, TernaryOp,
			  LoopOrSwitchBreak, LoopContinue, CompoundAssignment,
			  PreIncrement, PostIncrement, Box, Unbox,
			  DefaultValue };
    
    public static AstCode[] values() {
	return (AstCode[]) $VALUES.clone();
    }
    
    public static AstCode valueOf(String name) {
	return (AstCode) Enum.valueOf(AstCode.class, name);
    }
    
    private AstCode(String string, int i) {
	super(string, i);
    }
    
    public final String getName() {
	return StringUtilities.trimAndRemoveLeft(name().toLowerCase(), "__");
    }
    
    public final boolean isLoad() {
	switch (ANONYMOUS CLASS com.strobel.decompiler.ast.AstCode$1.$SwitchMap$com$strobel$decompiler$ast$AstCode[ordinal()]) {
	case 1:
	case 2:
	case 3:
	    return true;
	default:
	    return false;
	}
    }
    
    public final boolean isFieldRead() {
	switch (ANONYMOUS CLASS com.strobel.decompiler.ast.AstCode$1.$SwitchMap$com$strobel$decompiler$ast$AstCode[ordinal()]) {
	case 4:
	case 5:
	    return true;
	default:
	    return false;
	}
    }
    
    public final boolean isFieldWrite() {
	switch (ANONYMOUS CLASS com.strobel.decompiler.ast.AstCode$1.$SwitchMap$com$strobel$decompiler$ast$AstCode[ordinal()]) {
	case 6:
	case 7:
	    return true;
	default:
	    return false;
	}
    }
    
    public final boolean isStore() {
	switch (ANONYMOUS CLASS com.strobel.decompiler.ast.AstCode$1.$SwitchMap$com$strobel$decompiler$ast$AstCode[ordinal()]) {
	case 3:
	case 8:
	case 9:
	case 10:
	    return true;
	default:
	    return false;
	}
    }
    
    public final boolean isDup() {
	switch (ANONYMOUS CLASS com.strobel.decompiler.ast.AstCode$1.$SwitchMap$com$strobel$decompiler$ast$AstCode[ordinal()]) {
	case 11:
	case 12:
	case 13:
	case 14:
	case 15:
	case 16:
	    return true;
	default:
	    return false;
	}
    }
    
    public final boolean isComparison() {
	switch (ANONYMOUS CLASS com.strobel.decompiler.ast.AstCode$1.$SwitchMap$com$strobel$decompiler$ast$AstCode[ordinal()]) {
	case 17:
	case 18:
	case 19:
	case 20:
	case 21:
	case 22:
	    return true;
	default:
	    return false;
	}
    }
    
    public final boolean isLogical() {
	switch (ANONYMOUS CLASS com.strobel.decompiler.ast.AstCode$1.$SwitchMap$com$strobel$decompiler$ast$AstCode[ordinal()]) {
	case 23:
	case 24:
	case 25:
	    return true;
	default:
	    return false;
	}
    }
    
    public final boolean isShortCircuiting() {
	switch (ANONYMOUS CLASS com.strobel.decompiler.ast.AstCode$1.$SwitchMap$com$strobel$decompiler$ast$AstCode[ordinal()]) {
	case 24:
	case 25:
	    return true;
	default:
	    return false;
	}
    }
    
    public final boolean isWriteOperation() {
	switch (ANONYMOUS CLASS com.strobel.decompiler.ast.AstCode$1.$SwitchMap$com$strobel$decompiler$ast$AstCode[ordinal()]) {
	case 6:
	case 7:
	case 8:
	case 26:
	    return true;
	default:
	    return false;
	}
    }
    
    public final AstCode reverse() {
	switch (ANONYMOUS CLASS com.strobel.decompiler.ast.AstCode$1.$SwitchMap$com$strobel$decompiler$ast$AstCode[ordinal()]) {
	case 17:
	    return CmpNe;
	case 18:
	    return CmpEq;
	case 19:
	    return CmpGt;
	case 20:
	    return CmpLe;
	case 21:
	    return CmpLt;
	case 22:
	    return CmpGe;
	case 24:
	    return LogicalOr;
	case 25:
	    return LogicalAnd;
	default:
	    return this;
	}
    }
    
    public final boolean isConditionalControlFlow() {
	int ordinal = ordinal();
    label_1486:
	{
	    if (ordinal >= STANDARD_CODES.length) {
		if (this != IfTrue)
		    PUSH false;
		else
		    PUSH true;
	    } else {
	    label_1485:
		{
		    OpCode standardCode = STANDARD_CODES[ordinal];
		    if (!standardCode.isBranch()
			|| standardCode.isUnconditionalBranch())
			PUSH false;
		    else
			PUSH true;
		    break label_1485;
		}
		return POP;
	    }
	}
	return POP;
	break label_1486;
    }
    
    public final boolean isUnconditionalControlFlow() {
	switch (ANONYMOUS CLASS com.strobel.decompiler.ast.AstCode$1.$SwitchMap$com$strobel$decompiler$ast$AstCode[ordinal()]) {
	case 27:
	case 28:
	case 29:
	case 30:
	case 31:
	case 32:
	    return true;
	default: {
	    int ordinal = ordinal();
	    if (ordinal >= STANDARD_CODES.length)
		return false;
	    OpCode standardCode = STANDARD_CODES[ordinal];
	    return standardCode.isUnconditionalBranch();
	}
	}
    }
    
    public static boolean expandMacro(StrongBox code, StrongBox operand,
				      MethodBody body, int offset) {
	AstCode op = (AstCode) code.get();
	switch (ANONYMOUS CLASS com.strobel.decompiler.ast.AstCode$1.$SwitchMap$com$strobel$decompiler$ast$AstCode[op.ordinal()]) {
	case 33:
	    code.set(LdC);
	    operand.set(Integer.valueOf(-1));
	    return true;
	case 34:
	    code.set(LdC);
	    operand.set(Integer.valueOf(0));
	    return true;
	case 35:
	    code.set(LdC);
	    operand.set(Integer.valueOf(1));
	    return true;
	case 36:
	    code.set(LdC);
	    operand.set(Integer.valueOf(2));
	    return true;
	case 37:
	    code.set(LdC);
	    operand.set(Integer.valueOf(3));
	    return true;
	case 38:
	    code.set(LdC);
	    operand.set(Integer.valueOf(4));
	    return true;
	case 39:
	    code.set(LdC);
	    operand.set(Integer.valueOf(5));
	    return true;
	case 40:
	    code.set(LdC);
	    operand.set(Long.valueOf(0L));
	    return true;
	case 41:
	    code.set(LdC);
	    operand.set(Long.valueOf(1L));
	    return true;
	case 42:
	    code.set(LdC);
	    operand.set(Float.valueOf(0.0F));
	    return true;
	case 43:
	    code.set(LdC);
	    operand.set(Float.valueOf(1.0F));
	    return true;
	case 44:
	    code.set(LdC);
	    operand.set(Float.valueOf(2.0F));
	    return true;
	case 45:
	    code.set(LdC);
	    operand.set(Double.valueOf(0.0));
	    return true;
	case 46:
	    code.set(LdC);
	    operand.set(Double.valueOf(1.0));
	    return true;
	case 47:
	case 48:
	    code.set(LdC);
	    operand.set(Integer.valueOf(((Number) operand.get()).intValue()));
	    return true;
	case 49:
	case 50:
	    code.set(LdC);
	    return true;
	case 51:
	    code.set(Jsr);
	    return true;
	case 52:
	    code.set(Ret);
	    return true;
	case 53:
	case 54:
	    code.set(Inc);
	    return true;
	case 55:
	case 56:
	case 57:
	case 58:
	case 59:
	case 60:
	    code.set(Return);
	    return true;
	case 61:
	case 62:
	    code.set(NewArray);
	    return true;
	case 63:
	case 64:
	case 65:
	case 66:
	case 67:
	case 68:
	case 69:
	case 70:
	case 71:
	case 72:
	    code.set(Load);
	    return true;
	case 73:
	    code.set(Load);
	    operand.set(body.getVariables().reference(0,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 74:
	    code.set(Load);
	    operand.set(body.getVariables().reference(1,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 75:
	    code.set(Load);
	    operand.set(body.getVariables().reference(2,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 76:
	    code.set(Load);
	    operand.set(body.getVariables().reference(3,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 77:
	    code.set(Load);
	    operand.set(body.getVariables().reference(0,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 78:
	    code.set(Load);
	    operand.set(body.getVariables().reference(1,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 79:
	    code.set(Load);
	    operand.set(body.getVariables().reference(2,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 80:
	    code.set(Load);
	    operand.set(body.getVariables().reference(3,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 81:
	    code.set(Load);
	    operand.set(body.getVariables().reference(0,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 82:
	    code.set(Load);
	    operand.set(body.getVariables().reference(1,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 83:
	    code.set(Load);
	    operand.set(body.getVariables().reference(2,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 84:
	    code.set(Load);
	    operand.set(body.getVariables().reference(3,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 85:
	    code.set(Load);
	    operand.set(body.getVariables().reference(0,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 86:
	    code.set(Load);
	    operand.set(body.getVariables().reference(1,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 87:
	    code.set(Load);
	    operand.set(body.getVariables().reference(2,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 88:
	    code.set(Load);
	    operand.set(body.getVariables().reference(3,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 89:
	    code.set(Load);
	    operand.set(body.getVariables().reference(0,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 90:
	    code.set(Load);
	    operand.set(body.getVariables().reference(1,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 91:
	    code.set(Load);
	    operand.set(body.getVariables().reference(2,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 92:
	    code.set(Load);
	    operand.set(body.getVariables().reference(3,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 93:
	case 94:
	case 95:
	case 96:
	case 97:
	case 98:
	case 99:
	case 100:
	    code.set(LoadElement);
	    return true;
	case 101:
	    code.set(Goto);
	    return true;
	case 102:
	case 103:
	case 104:
	case 105:
	case 106:
	case 107:
	case 108:
	case 109:
	case 110:
	case 111:
	    code.set(Store);
	    return true;
	case 112:
	    code.set(Store);
	    operand.set(body.getVariables().reference(0,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 113:
	    code.set(Store);
	    operand.set(body.getVariables().reference(1,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 114:
	    code.set(Store);
	    operand.set(body.getVariables().reference(2,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 115:
	    code.set(Store);
	    operand.set(body.getVariables().reference(3,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 116:
	    code.set(Store);
	    operand.set(body.getVariables().reference(0,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 117:
	    code.set(Store);
	    operand.set(body.getVariables().reference(1,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 118:
	    code.set(Store);
	    operand.set(body.getVariables().reference(2,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 119:
	    code.set(Store);
	    operand.set(body.getVariables().reference(3,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 120:
	    code.set(Store);
	    operand.set(body.getVariables().reference(0,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 121:
	    code.set(Store);
	    operand.set(body.getVariables().reference(1,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 122:
	    code.set(Store);
	    operand.set(body.getVariables().reference(2,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 123:
	    code.set(Store);
	    operand.set(body.getVariables().reference(3,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 124:
	    code.set(Store);
	    operand.set(body.getVariables().reference(0,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 125:
	    code.set(Store);
	    operand.set(body.getVariables().reference(1,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 126:
	    code.set(Store);
	    operand.set(body.getVariables().reference(2,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 127:
	    code.set(Store);
	    operand.set(body.getVariables().reference(3,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 128:
	    code.set(Store);
	    operand.set(body.getVariables().reference(0,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 129:
	    code.set(Store);
	    operand.set(body.getVariables().reference(1,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 130:
	    code.set(Store);
	    operand.set(body.getVariables().reference(2,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 131:
	    code.set(Store);
	    operand.set(body.getVariables().reference(3,
						      (STANDARD_CODES
						       [op.ordinal()]),
						      offset));
	    return true;
	case 132:
	case 133:
	case 134:
	case 135:
	case 136:
	case 137:
	case 138:
	case 139:
	    code.set(StoreElement);
	    return true;
	case 140:
	case 141:
	case 142:
	case 143:
	    code.set(Add);
	    return true;
	case 144:
	case 145:
	case 146:
	case 147:
	    code.set(Sub);
	    return true;
	case 148:
	case 149:
	case 150:
	case 151:
	    code.set(Mul);
	    return true;
	case 152:
	case 153:
	case 154:
	case 155:
	    code.set(Div);
	    return true;
	case 156:
	case 157:
	case 158:
	case 159:
	    code.set(Rem);
	    return true;
	case 160:
	case 161:
	case 162:
	case 163:
	    code.set(Neg);
	    return true;
	case 164:
	case 165:
	    code.set(Shl);
	    return true;
	case 166:
	case 167:
	    code.set(Shr);
	    return true;
	case 168:
	case 169:
	    code.set(UShr);
	    return true;
	case 170:
	case 171:
	    code.set(And);
	    return true;
	case 172:
	case 173:
	    code.set(Or);
	    return true;
	case 174:
	case 175:
	    code.set(Xor);
	    return true;
	case 176:
	case 177:
	    code.set(Switch);
	    return true;
	default:
	    return false;
	}
    }
    
    static {
	STANDARD_CODES = OpCode.values();
    }
}
