 package com.strobel.assembler.metadata.signatures;
 
 import com.strobel.assembler.metadata.TypeReference;
 import java.io.PrintStream;
 import java.util.Arrays;
 import java.util.Iterator;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Reifier
   implements TypeTreeVisitor<TypeReference>
 {
   private final MetadataFactory factory;
   private TypeReference resultType;
   
   private Reifier(MetadataFactory f)
   {
     this.factory = f;
   }
   
   public static Reifier make(MetadataFactory f) {
     return new Reifier(f);
   }
   
   private MetadataFactory getFactory() {
     return this.factory;
   }
   
   private TypeReference[] reifyTypeArguments(TypeArgument[] tas) {
     TypeReference[] ts = new TypeReference[tas.length];
     for (int i = 0; i < tas.length; i++) {
       tas[i].accept(this);
       ts[i] = this.resultType;
       if (ts[i] == null)
         System.err.println("BAD TYPE ARGUMENTS: " + Arrays.toString(tas) + "; " + Arrays.toString(ts));
       assert (ts[i] != null);
     }
     return ts;
   }
   
   public TypeReference getResult() {
     assert (this.resultType != null);
     return this.resultType;
   }
   
   public void visitFormalTypeParameter(FormalTypeParameter ftp) {
     FieldTypeSignature[] bounds = ftp.getBounds();
     this.resultType = getFactory().makeTypeVariable(ftp.getName(), bounds);
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public void visitClassTypeSignature(ClassTypeSignature ct)
   {
     List<SimpleClassTypeSignature> scts = ct.getPath();
     assert (!scts.isEmpty());
     Iterator<SimpleClassTypeSignature> iter = scts.iterator();
     SimpleClassTypeSignature sc = (SimpleClassTypeSignature)iter.next();
     StringBuilder n = new StringBuilder(sc.getName());
     
 
 
 
     while ((iter.hasNext()) && (sc.getTypeArguments().length == 0)) {
       sc = (SimpleClassTypeSignature)iter.next();
       boolean dollar = sc.useDollar();
       n.append(dollar ? "$" : ".").append(sc.getName());
     }
     
 
 
     assert ((!iter.hasNext()) || (sc.getTypeArguments().length > 0));
     
     TypeReference c = getFactory().makeNamedType(n.toString());
     
     if (sc.getTypeArguments().length == 0)
     {
       assert (!iter.hasNext());
       this.resultType = c;
     }
     else {
       assert (sc.getTypeArguments().length > 0);
       
 
 
 
 
       TypeReference[] pts = reifyTypeArguments(sc.getTypeArguments());
       
       TypeReference owner = getFactory().makeParameterizedType(c, null, pts);
       
       while (iter.hasNext()) {
         sc = (SimpleClassTypeSignature)iter.next();
         boolean dollar = sc.useDollar();
         n.append(dollar ? "$" : ".").append(sc.getName());
         c = getFactory().makeNamedType(n.toString());
         pts = reifyTypeArguments(sc.getTypeArguments());
         
 
         owner = getFactory().makeParameterizedType(c, owner, pts);
       }
       this.resultType = owner;
     }
   }
   
   public void visitArrayTypeSignature(ArrayTypeSignature a)
   {
     a.getComponentType().accept(this);
     TypeReference ct = this.resultType;
     assert (ct != null);
     this.resultType = getFactory().makeArrayType(ct);
   }
   
   public void visitTypeVariableSignature(TypeVariableSignature tv) {
     this.resultType = getFactory().findTypeVariable(tv.getName());
   }
   
   public void visitWildcard(Wildcard w) {
     this.resultType = getFactory().makeWildcard(w.getSuperBound(), w.getExtendsBound());
   }
   
   public void visitSimpleClassTypeSignature(SimpleClassTypeSignature sct) {
     this.resultType = getFactory().makeNamedType(sct.getName());
   }
   
   public void visitBottomSignature(BottomSignature b) {
     this.resultType = null;
   }
   
   public void visitByteSignature(ByteSignature b) {
     this.resultType = getFactory().makeByte();
   }
   
   public void visitBooleanSignature(BooleanSignature b) {
     this.resultType = getFactory().makeBoolean();
   }
   
   public void visitShortSignature(ShortSignature s) {
     this.resultType = getFactory().makeShort();
   }
   
   public void visitCharSignature(CharSignature c) {
     this.resultType = getFactory().makeChar();
   }
   
   public void visitIntSignature(IntSignature i) {
     this.resultType = getFactory().makeInt();
   }
   
   public void visitLongSignature(LongSignature l) {
     this.resultType = getFactory().makeLong();
   }
   
   public void visitFloatSignature(FloatSignature f) {
     this.resultType = getFactory().makeFloat();
   }
   
   public void visitDoubleSignature(DoubleSignature d) {
     this.resultType = getFactory().makeDouble();
   }
   
   public void visitVoidSignature(VoidSignature v) {
     this.resultType = getFactory().makeVoid();
   }
 }


