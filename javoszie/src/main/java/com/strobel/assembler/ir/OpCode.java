 package com.strobel.assembler.ir;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public enum OpCode
 {
   NOP(0, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.Pop0, StackBehavior.Push0), 
   ACONST_NULL(1, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.Pop0, StackBehavior.PushA), 
   ICONST_M1(2, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushI4), 
   ICONST_0(3, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushI4), 
   ICONST_1(4, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushI4), 
   ICONST_2(5, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushI4), 
   ICONST_3(6, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushI4), 
   ICONST_4(7, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushI4), 
   ICONST_5(8, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushI4), 
   LCONST_0(9, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushI8), 
   LCONST_1(10, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushI8), 
   FCONST_0(11, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushR4), 
   FCONST_1(12, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushR4), 
   FCONST_2(13, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushR4), 
   DCONST_0(14, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushR8), 
   DCONST_1(15, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushR8), 
   BIPUSH(16, FlowControl.Next, OpCodeType.Primitive, OperandType.I1, StackBehavior.Pop0, StackBehavior.PushI4), 
   SIPUSH(17, FlowControl.Next, OpCodeType.Primitive, OperandType.I2, StackBehavior.Pop0, StackBehavior.PushI4), 
   LDC(18, FlowControl.Next, OpCodeType.Primitive, OperandType.Constant, StackBehavior.Pop0, StackBehavior.Push1), 
   LDC_W(19, FlowControl.Next, OpCodeType.Primitive, OperandType.WideConstant, StackBehavior.Pop0, StackBehavior.Push1), 
   LDC2_W(20, FlowControl.Next, OpCodeType.Primitive, OperandType.WideConstant, StackBehavior.Pop0, StackBehavior.Push2), 
   ILOAD(21, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.Pop0, StackBehavior.PushI4), 
   LLOAD(22, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.Pop0, StackBehavior.PushI8), 
   FLOAD(23, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.Pop0, StackBehavior.PushR4), 
   DLOAD(24, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.Pop0, StackBehavior.PushR8), 
   ALOAD(25, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.Pop0, StackBehavior.PushA), 
   ILOAD_0(26, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushI4), 
   ILOAD_1(27, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushI4), 
   ILOAD_2(28, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushI4), 
   ILOAD_3(29, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushI4), 
   LLOAD_0(30, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushI8), 
   LLOAD_1(31, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushI8), 
   LLOAD_2(32, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushI8), 
   LLOAD_3(33, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushI8), 
   FLOAD_0(34, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushR4), 
   FLOAD_1(35, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushR4), 
   FLOAD_2(36, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushR4), 
   FLOAD_3(37, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushR4), 
   DLOAD_0(38, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushR8), 
   DLOAD_1(39, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushR8), 
   DLOAD_2(40, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushR8), 
   DLOAD_3(41, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushR8), 
   ALOAD_0(42, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushA), 
   ALOAD_1(43, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushA), 
   ALOAD_2(44, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushA), 
   ALOAD_3(45, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.Pop0, StackBehavior.PushA), 
   IALOAD(46, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopI4_PopA, StackBehavior.PushI4), 
   LALOAD(47, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopI4_PopA, StackBehavior.PushI8), 
   FALOAD(48, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopI4_PopA, StackBehavior.PushR4), 
   DALOAD(49, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopI4_PopA, StackBehavior.PushR8), 
   AALOAD(50, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopI4_PopA, StackBehavior.PushA), 
   BALOAD(51, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopI4_PopA, StackBehavior.PushI4), 
   CALOAD(52, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopI4_PopA, StackBehavior.PushI4), 
   SALOAD(53, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopI4_PopA, StackBehavior.PushI4), 
   ISTORE(54, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.PopI4, StackBehavior.Push0), 
   LSTORE(55, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.PopI8, StackBehavior.Push0), 
   FSTORE(56, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.PopR4, StackBehavior.Push0), 
   DSTORE(57, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.PopR8, StackBehavior.Push0), 
   ASTORE(58, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.PopA, StackBehavior.Push0), 
   ISTORE_0(59, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopI4, StackBehavior.Push0), 
   ISTORE_1(60, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopI4, StackBehavior.Push0), 
   ISTORE_2(61, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopI4, StackBehavior.Push0), 
   ISTORE_3(62, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopI4, StackBehavior.Push0), 
   LSTORE_0(63, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopI8, StackBehavior.Push0), 
   LSTORE_1(64, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopI8, StackBehavior.Push0), 
   LSTORE_2(65, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopI8, StackBehavior.Push0), 
   LSTORE_3(66, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopI8, StackBehavior.Push0), 
   FSTORE_0(67, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopR4, StackBehavior.Push0), 
   FSTORE_1(68, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopR4, StackBehavior.Push0), 
   FSTORE_2(69, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopR4, StackBehavior.Push0), 
   FSTORE_3(70, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopR4, StackBehavior.Push0), 
   DSTORE_0(71, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopR8, StackBehavior.Push0), 
   DSTORE_1(72, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopR8, StackBehavior.Push0), 
   DSTORE_2(73, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopR8, StackBehavior.Push0), 
   DSTORE_3(74, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopR8, StackBehavior.Push0), 
   ASTORE_0(75, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopA, StackBehavior.Push0), 
   ASTORE_1(76, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopA, StackBehavior.Push0), 
   ASTORE_2(77, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopA, StackBehavior.Push0), 
   ASTORE_3(78, FlowControl.Next, OpCodeType.Macro, OperandType.None, StackBehavior.PopA, StackBehavior.Push0), 
   IASTORE(79, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopI4_PopI4_PopA, StackBehavior.Push0), 
   LASTORE(80, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopI8_PopI4_PopA, StackBehavior.Push0), 
   FASTORE(81, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopR4_PopI4_PopA, StackBehavior.Push0), 
   DASTORE(82, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopR8_PopI4_PopA, StackBehavior.Push0), 
   AASTORE(83, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopA_PopI4_PopA, StackBehavior.Push0), 
   BASTORE(84, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopI4_PopI4_PopA, StackBehavior.Push0), 
   CASTORE(85, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopI4_PopI4_PopA, StackBehavior.Push0), 
   SASTORE(86, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopI4_PopI4_PopA, StackBehavior.Push0), 
   POP(87, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.Pop1, StackBehavior.Push0), 
   POP2(88, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.Pop2, StackBehavior.Push0), 
   DUP(89, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.Pop1, StackBehavior.Push1_Push1), 
   DUP_X1(90, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.Pop1_Pop1, StackBehavior.Push1_Push1_Push1), 
   DUP_X2(91, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.Pop2_Pop1, StackBehavior.Push1_Push2_Push1), 
   DUP2(92, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.Pop2, StackBehavior.Push2_Push2), 
   DUP2_X1(93, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.Pop1_Pop2, StackBehavior.Push2_Push1_Push2), 
   DUP2_X2(94, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.Pop2_Pop2, StackBehavior.Push2_Push2_Push2), 
   SWAP(95, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.Pop1_Pop1, StackBehavior.Push1_Push1), 
   IADD(96, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4_PopI4, StackBehavior.PushI4), 
   LADD(97, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI8_PopI8, StackBehavior.PushI8), 
   FADD(98, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR4_PopR4, StackBehavior.PushR4), 
   DADD(99, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR8_PopR8, StackBehavior.PushR8), 
   ISUB(100, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4_PopI4, StackBehavior.PushI4), 
   LSUB(101, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI8_PopI8, StackBehavior.PushI8), 
   FSUB(102, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR4_PopR4, StackBehavior.PushR4), 
   DSUB(103, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR8_PopR8, StackBehavior.PushR8), 
   IMUL(104, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4_PopI4, StackBehavior.PushI4), 
   LMUL(105, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI8_PopI8, StackBehavior.PushI8), 
   FMUL(106, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR4_PopR4, StackBehavior.PushR4), 
   DMUL(107, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR8_PopR8, StackBehavior.PushR8), 
   IDIV(108, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4_PopI4, StackBehavior.PushI4), 
   LDIV(109, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI8_PopI8, StackBehavior.PushI8), 
   FDIV(110, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR4_PopR4, StackBehavior.PushR4), 
   DDIV(111, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR8_PopR8, StackBehavior.PushR8), 
   IREM(112, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4_PopI4, StackBehavior.PushI4), 
   LREM(113, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI8_PopI8, StackBehavior.PushI8), 
   FREM(114, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR4_PopR4, StackBehavior.PushR4), 
   DREM(115, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR8_PopR8, StackBehavior.PushR8), 
   INEG(116, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4, StackBehavior.PushI4), 
   LNEG(117, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI8, StackBehavior.PushI8), 
   FNEG(118, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR4, StackBehavior.PushR4), 
   DNEG(119, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR8, StackBehavior.PushR8), 
   ISHL(120, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4_PopI4, StackBehavior.PushI4), 
   LSHL(121, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4_PopI8, StackBehavior.PushI8), 
   ISHR(122, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4_PopI4, StackBehavior.PushI4), 
   LSHR(123, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4_PopI8, StackBehavior.PushI8), 
   IUSHR(124, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4_PopI4, StackBehavior.PushI4), 
   LUSHR(125, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4_PopI8, StackBehavior.PushI8), 
   IAND(126, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4_PopI4, StackBehavior.PushI4), 
   LAND(127, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI8_PopI8, StackBehavior.PushI8), 
   IOR(128, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4_PopI4, StackBehavior.PushI4), 
   LOR(129, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI8_PopI8, StackBehavior.PushI8), 
   IXOR(130, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4_PopI4, StackBehavior.PushI4), 
   LXOR(131, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI8_PopI8, StackBehavior.PushI8), 
   IINC(132, FlowControl.Next, OpCodeType.Primitive, OperandType.LocalI1, StackBehavior.Pop0, StackBehavior.Push0), 
   I2L(133, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4, StackBehavior.PushI8), 
   I2F(134, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4, StackBehavior.PushR4), 
   I2D(135, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4, StackBehavior.PushR8), 
   L2I(136, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI8, StackBehavior.PushI4), 
   L2F(137, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI8, StackBehavior.PushR4), 
   L2D(138, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI8, StackBehavior.PushR8), 
   F2I(139, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR4, StackBehavior.PushI4), 
   F2L(140, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR4, StackBehavior.PushI8), 
   F2D(141, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR4, StackBehavior.PushR8), 
   D2I(142, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR8, StackBehavior.PushI4), 
   D2L(143, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR8, StackBehavior.PushI8), 
   D2F(144, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR8, StackBehavior.PushR4), 
   I2B(145, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4, StackBehavior.PushI4), 
   I2C(146, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4, StackBehavior.PushI4), 
   I2S(147, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4, StackBehavior.PushI4), 
   LCMP(148, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI8_PopI8, StackBehavior.PushI4), 
   FCMPL(149, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR4_PopR4, StackBehavior.PushI4), 
   FCMPG(150, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR4_PopR4, StackBehavior.PushI4), 
   DCMPL(151, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR8_PopR8, StackBehavior.PushI4), 
   DCMPG(152, FlowControl.Next, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR8_PopR8, StackBehavior.PushI4), 
   IFEQ(153, FlowControl.ConditionalBranch, OpCodeType.Primitive, OperandType.BranchTarget, StackBehavior.PopI4, StackBehavior.Push0), 
   IFNE(154, FlowControl.ConditionalBranch, OpCodeType.Primitive, OperandType.BranchTarget, StackBehavior.PopI4, StackBehavior.Push0), 
   IFLT(155, FlowControl.ConditionalBranch, OpCodeType.Primitive, OperandType.BranchTarget, StackBehavior.PopI4, StackBehavior.Push0), 
   IFGE(156, FlowControl.ConditionalBranch, OpCodeType.Primitive, OperandType.BranchTarget, StackBehavior.PopI4, StackBehavior.Push0), 
   IFGT(157, FlowControl.ConditionalBranch, OpCodeType.Primitive, OperandType.BranchTarget, StackBehavior.PopI4, StackBehavior.Push0), 
   IFLE(158, FlowControl.ConditionalBranch, OpCodeType.Primitive, OperandType.BranchTarget, StackBehavior.PopI4, StackBehavior.Push0), 
   IF_ICMPEQ(159, FlowControl.ConditionalBranch, OpCodeType.Macro, OperandType.BranchTarget, StackBehavior.PopI4_PopI4, StackBehavior.Push0), 
   IF_ICMPNE(160, FlowControl.ConditionalBranch, OpCodeType.Macro, OperandType.BranchTarget, StackBehavior.PopI4_PopI4, StackBehavior.Push0), 
   IF_ICMPLT(161, FlowControl.ConditionalBranch, OpCodeType.Macro, OperandType.BranchTarget, StackBehavior.PopI4_PopI4, StackBehavior.Push0), 
   IF_ICMPGE(162, FlowControl.ConditionalBranch, OpCodeType.Macro, OperandType.BranchTarget, StackBehavior.PopI4_PopI4, StackBehavior.Push0), 
   IF_ICMPGT(163, FlowControl.ConditionalBranch, OpCodeType.Macro, OperandType.BranchTarget, StackBehavior.PopI4_PopI4, StackBehavior.Push0), 
   IF_ICMPLE(164, FlowControl.ConditionalBranch, OpCodeType.Macro, OperandType.BranchTarget, StackBehavior.PopI4_PopI4, StackBehavior.Push0), 
   IF_ACMPEQ(165, FlowControl.ConditionalBranch, OpCodeType.Macro, OperandType.BranchTarget, StackBehavior.PopA_PopA, StackBehavior.Push0), 
   IF_ACMPNE(166, FlowControl.ConditionalBranch, OpCodeType.Macro, OperandType.BranchTarget, StackBehavior.PopA_PopA, StackBehavior.Push0), 
   GOTO(167, FlowControl.Branch, OpCodeType.Primitive, OperandType.BranchTarget, StackBehavior.Pop0, StackBehavior.Push0), 
   JSR(168, FlowControl.Branch, OpCodeType.Primitive, OperandType.BranchTarget, StackBehavior.Pop0, StackBehavior.Push0), 
   RET(169, FlowControl.Branch, OpCodeType.Primitive, OperandType.Local, StackBehavior.Pop0, StackBehavior.Push0), 
   TABLESWITCH(170, FlowControl.Branch, OpCodeType.Primitive, OperandType.Switch, StackBehavior.PopI4, StackBehavior.Push0), 
   LOOKUPSWITCH(171, FlowControl.Branch, OpCodeType.Primitive, OperandType.Switch, StackBehavior.PopI4, StackBehavior.Push0), 
   IRETURN(172, FlowControl.Return, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI4, StackBehavior.Push0), 
   LRETURN(173, FlowControl.Return, OpCodeType.Primitive, OperandType.None, StackBehavior.PopI8, StackBehavior.Push0), 
   FRETURN(174, FlowControl.Return, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR4, StackBehavior.Push0), 
   DRETURN(175, FlowControl.Return, OpCodeType.Primitive, OperandType.None, StackBehavior.PopR8, StackBehavior.Push0), 
   ARETURN(176, FlowControl.Return, OpCodeType.Primitive, OperandType.None, StackBehavior.PopA, StackBehavior.Push0), 
   RETURN(177, FlowControl.Return, OpCodeType.Primitive, OperandType.None, StackBehavior.Pop0, StackBehavior.Push0), 
   GETSTATIC(178, FlowControl.Next, OpCodeType.ObjectModel, OperandType.FieldReference, StackBehavior.Pop0, StackBehavior.Push1), 
   PUTSTATIC(179, FlowControl.Next, OpCodeType.ObjectModel, OperandType.FieldReference, StackBehavior.Pop1, StackBehavior.Push0), 
   GETFIELD(180, FlowControl.Next, OpCodeType.ObjectModel, OperandType.FieldReference, StackBehavior.PopA, StackBehavior.Push1), 
   PUTFIELD(181, FlowControl.Next, OpCodeType.ObjectModel, OperandType.FieldReference, StackBehavior.Pop1_PopA, StackBehavior.Push0), 
   INVOKEVIRTUAL(182, FlowControl.Call, OpCodeType.ObjectModel, OperandType.MethodReference, StackBehavior.VarPop, StackBehavior.VarPush), 
   INVOKESPECIAL(183, FlowControl.Call, OpCodeType.ObjectModel, OperandType.MethodReference, StackBehavior.VarPop, StackBehavior.VarPush), 
   INVOKESTATIC(184, FlowControl.Call, OpCodeType.Primitive, OperandType.MethodReference, StackBehavior.VarPop, StackBehavior.VarPush), 
   INVOKEINTERFACE(185, FlowControl.Call, OpCodeType.ObjectModel, OperandType.MethodReference, StackBehavior.VarPop, StackBehavior.VarPush), 
   INVOKEDYNAMIC(186, FlowControl.Call, OpCodeType.ObjectModel, OperandType.DynamicCallSite, StackBehavior.VarPop, StackBehavior.VarPush), 
   NEW(187, FlowControl.Next, OpCodeType.ObjectModel, OperandType.TypeReference, StackBehavior.Pop0, StackBehavior.PushA), 
   NEWARRAY(188, FlowControl.Next, OpCodeType.ObjectModel, OperandType.PrimitiveTypeCode, StackBehavior.PopI4, StackBehavior.PushA), 
   ANEWARRAY(189, FlowControl.Next, OpCodeType.ObjectModel, OperandType.TypeReference, StackBehavior.PopI4, StackBehavior.PushA), 
   ARRAYLENGTH(190, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopA, StackBehavior.PushI4), 
   ATHROW(191, FlowControl.Throw, OpCodeType.ObjectModel, OperandType.None, StackBehavior.VarPop, StackBehavior.Push0), 
   CHECKCAST(192, FlowControl.Next, OpCodeType.ObjectModel, OperandType.TypeReference, StackBehavior.PopA, StackBehavior.PushA), 
   INSTANCEOF(193, FlowControl.Next, OpCodeType.ObjectModel, OperandType.TypeReference, StackBehavior.PopA, StackBehavior.PushI4), 
   MONITORENTER(194, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopA, StackBehavior.Push0), 
   MONITOREXIT(195, FlowControl.Next, OpCodeType.ObjectModel, OperandType.None, StackBehavior.PopA, StackBehavior.Push0), 
   MULTIANEWARRAY(197, FlowControl.Next, OpCodeType.ObjectModel, OperandType.TypeReferenceU1, StackBehavior.VarPop, StackBehavior.PushA), 
   IFNULL(198, FlowControl.ConditionalBranch, OpCodeType.Primitive, OperandType.BranchTarget, StackBehavior.PopA, StackBehavior.Push0), 
   IFNONNULL(199, FlowControl.ConditionalBranch, OpCodeType.Primitive, OperandType.BranchTarget, StackBehavior.PopA, StackBehavior.Push0), 
   GOTO_W(200, FlowControl.Branch, OpCodeType.Primitive, OperandType.BranchTargetWide, StackBehavior.Pop0, StackBehavior.Push0), 
   JSR_W(201, FlowControl.Branch, OpCodeType.Primitive, OperandType.BranchTargetWide, StackBehavior.Pop0, StackBehavior.Push0), 
   BREAKPOINT(201, FlowControl.Breakpoint, OpCodeType.Primitive, OperandType.None, StackBehavior.Pop0, StackBehavior.Push0), 
   ILOAD_W(50197, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.Pop0, StackBehavior.PushI4), 
   LLOAD_W(50198, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.Pop0, StackBehavior.PushI8), 
   FLOAD_W(50199, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.Pop0, StackBehavior.PushR4), 
   DLOAD_W(50200, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.Pop0, StackBehavior.PushR8), 
   ALOAD_W(50201, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.Pop0, StackBehavior.PushA), 
   ISTORE_W(50230, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.PopI4, StackBehavior.Push0), 
   LSTORE_W(50231, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.PopI8, StackBehavior.Push0), 
   FSTORE_W(50232, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.PopR4, StackBehavior.Push0), 
   DSTORE_W(50233, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.PopR8, StackBehavior.Push0), 
   ASTORE_W(50234, FlowControl.Next, OpCodeType.Primitive, OperandType.Local, StackBehavior.PopA, StackBehavior.Push0), 
   IINC_W(50308, FlowControl.Next, OpCodeType.Primitive, OperandType.LocalI2, StackBehavior.Pop0, StackBehavior.Push0), 
   RET_W(50345, FlowControl.Branch, OpCodeType.Primitive, OperandType.Local, StackBehavior.Pop0, StackBehavior.Push0), 
   LEAVE(254, FlowControl.Branch, OpCodeType.Primitive, OperandType.None, StackBehavior.Pop0, StackBehavior.Push0), 
   ENDFINALLY(255, FlowControl.Branch, OpCodeType.Primitive, OperandType.None, StackBehavior.Pop0, StackBehavior.Push0);
   
   private final int _code;
   private final FlowControl _flowControl;
   private final OpCodeType _opCodeType;
   private final OperandType _operandType;
   private final StackBehavior _stackBehaviorPop;
   
   private OpCode(int code, FlowControl flowControl, OpCodeType opCodeType, OperandType operandType, StackBehavior stackBehaviorPop, StackBehavior stackBehaviorPush)
   {
     this._code = code;
     this._flowControl = flowControl;
     this._opCodeType = opCodeType;
     this._operandType = operandType;
     this._stackBehaviorPop = stackBehaviorPop;
     this._stackBehaviorPush = stackBehaviorPush;
   }
   
   public int getCode() {
     return this._code;
   }
   
   public boolean isWide() {
     return (this._code >> 8 & 0xC4) == 196;
   }
   
   public OperandType getOperandType() {
     return this._operandType;
   }
   
   public FlowControl getFlowControl() {
     return this._flowControl;
   }
   
   public OpCodeType getOpCodeType() {
     return this._opCodeType;
   }
   
   public StackBehavior getStackBehaviorPop() {
     return this._stackBehaviorPop;
   }
   
   public StackBehavior getStackBehaviorPush() {
     return this._stackBehaviorPush;
   }
   
   public boolean hasVariableStackBehavior() {
     return (this._stackBehaviorPop == StackBehavior.VarPop) || (this._stackBehaviorPush == StackBehavior.VarPush);
   }
   
   public boolean isReturn()
   {
     return this._flowControl == FlowControl.Return;
   }
   
   public boolean isThrow() {
     return this._flowControl == FlowControl.Throw;
   }
   
   public boolean isJumpToSubroutine() {
     switch (this) {
     case JSR: 
     case JSR_W: 
       return true;
     }
     return false;
   }
   
   public boolean isReturnFromSubroutine()
   {
     switch (this) {
     case RET: 
     case RET_W: 
       return true;
     }
     return false;
   }
   
   public boolean isLeave()
   {
     switch (this) {
     case JSR: 
     case JSR_W: 
     case LEAVE: 
     case ENDFINALLY: 
       return true;
     }
     return false;
   }
   
   public boolean isBranch()
   {
     switch (this._flowControl) {
     case Branch: 
     case ConditionalBranch: 
     case Return: 
     case Throw: 
       return true;
     }
     return false;
   }
   
   public boolean isUnconditionalBranch()
   {
     switch (this._flowControl) {
     case Branch: 
     case Return: 
     case Throw: 
       return true;
     }
     return false;
   }
   
   public boolean isMoveInstruction()
   {
     return (isLoad()) || (isStore());
   }
   
   public boolean isLoad() {
     switch (this) {
     case ILOAD: 
     case LLOAD: 
     case FLOAD: 
     case DLOAD: 
     case ALOAD: 
     case ILOAD_0: 
     case ILOAD_1: 
     case ILOAD_2: 
     case ILOAD_3: 
     case LLOAD_0: 
     case LLOAD_1: 
     case LLOAD_2: 
     case LLOAD_3: 
     case FLOAD_0: 
     case FLOAD_1: 
     case FLOAD_2: 
     case FLOAD_3: 
     case DLOAD_0: 
     case DLOAD_1: 
     case DLOAD_2: 
     case DLOAD_3: 
     case ALOAD_0: 
     case ALOAD_1: 
     case ALOAD_2: 
     case ALOAD_3: 
       return true;
     
     case ILOAD_W: 
     case LLOAD_W: 
     case FLOAD_W: 
     case DLOAD_W: 
     case ALOAD_W: 
       return true;
     
     case RET: 
     case RET_W: 
       return true;
     }
     
     return false;
   }
   
   public boolean isStore()
   {
     switch (this) {
     case ISTORE: 
     case LSTORE: 
     case FSTORE: 
     case DSTORE: 
     case ASTORE: 
     case ISTORE_0: 
     case ISTORE_1: 
     case ISTORE_2: 
     case ISTORE_3: 
     case LSTORE_0: 
     case LSTORE_1: 
     case LSTORE_2: 
     case LSTORE_3: 
     case FSTORE_0: 
     case FSTORE_1: 
     case FSTORE_2: 
     case FSTORE_3: 
     case DSTORE_0: 
     case DSTORE_1: 
     case DSTORE_2: 
     case DSTORE_3: 
     case ASTORE_0: 
     case ASTORE_1: 
     case ASTORE_2: 
     case ASTORE_3: 
       return true;
     
     case ISTORE_W: 
     case LSTORE_W: 
     case FSTORE_W: 
     case DSTORE_W: 
     case ASTORE_W: 
       return true;
     }
     
     return false;
   }
   
   public boolean isArrayLoad()
   {
     switch (this) {
     case IALOAD: 
     case LALOAD: 
     case FALOAD: 
     case DALOAD: 
     case AALOAD: 
       return true;
     }
     
     return false;
   }
   
   public boolean isArrayStore()
   {
     switch (this) {
     case IASTORE: 
     case LASTORE: 
     case FASTORE: 
     case DASTORE: 
     case AASTORE: 
       return true;
     }
     
     return false;
   }
   
   public int getSize()
   {
     return this._code >> 8 == 196 ? 2 : 1;
   }
   
   public int getStackChange() {
     return stackChange[(this._code & 0xFF)];
   }
   
   public boolean endsUnconditionalJumpBlock() {
     switch (this) {
     case JSR: 
     case RET: 
     case GOTO: 
       return true;
     
     case IRETURN: 
     case LRETURN: 
     case FRETURN: 
     case DRETURN: 
     case ARETURN: 
     case RETURN: 
       return true;
     
     case ATHROW: 
       return true;
     
     case JSR_W: 
     case GOTO_W: 
       return true;
     
     case RET_W: 
       return true;
     }
     
     return false;
   }
   
   public boolean canThrow() {
     if (this._opCodeType == OpCodeType.ObjectModel) {
       return this != INSTANCEOF;
     }
     
     switch (this) {
     case IDIV: 
     case LDIV: 
       return true;
     
     case IREM: 
     case LREM: 
       return true;
     }
     
     return false;
   }
   
   public OpCode negate()
   {
     if (this == IFNULL) {
       return IFNONNULL;
     }
     if (this == IFNONNULL) {
       return IFNULL;
     }
     
     return get((this._code + 1 ^ 0x1) - 1);
   }
   
 
   private final StackBehavior _stackBehaviorPush;
   
   public static final int STANDARD = 0;
   
   public static final int WIDE = 196;
   
   private static final OpCode[] standardOpCodes;
   
   private static final OpCode[] wideOpCodes;
   public static OpCode get(int code)
   {
     return getOpcodeBlock(code >> 8)[(code & 0xFF)];
   }
   
   private static OpCode[] getOpcodeBlock(int prefix) {
     switch (prefix) {
     case 0: 
       return standardOpCodes;
     case 196: 
       return wideOpCodes;
     }
     return null;
   }
   
 
 
 
 
 
   static
   {
     standardOpCodes = new OpCode['Ā'];
     wideOpCodes = new OpCode['Ā'];
     
 
     for (OpCode o : values()) {
       getOpcodeBlock(o._code >> 8)[(o._code & 0xFF)] = o;
     }
   }
   
   private static final byte[] stackChange = { 0, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, -1, 0, -1, 0, -1, -1, -1, -1, -1, -2, -1, -2, -1, -1, -1, -1, -1, -2, -2, -2, -2, -1, -1, -1, -1, -2, -2, -2, -2, -1, -1, -1, -1, -3, -4, -3, -4, -3, -3, -3, -3, -1, -2, 1, 1, 1, 2, 2, 2, 0, -1, -2, -1, -2, -1, -2, -1, -2, -1, -2, -1, -2, -1, -2, -1, -2, -1, -2, -1, -2, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -2, -1, -2, -1, -2, 0, 1, 0, 1, -1, -1, 0, 0, 1, 1, -1, 0, -1, 0, 0, 0, -3, -1, -1, -3, -3, -1, -1, -1, -1, -1, -1, -2, -2, -2, -2, -2, -2, -2, -2, 0, 1, 0, -1, -1, -1, -2, -1, -2, -1, 0, 1, -1, 1, -1, -1, -1, 0, -1, -1, 1, 0, 0, 0, -1, 0, 0, -1, -1, 0, 1, -1, -1, 0, 1, 0, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
 }


