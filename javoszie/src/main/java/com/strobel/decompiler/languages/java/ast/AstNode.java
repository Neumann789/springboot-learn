 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.componentmodel.Key;
 import com.strobel.componentmodel.UserDataStore;
 import com.strobel.componentmodel.UserDataStoreBase;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.Freezable;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilerSettings;
 import com.strobel.decompiler.ITextOutput;
 import com.strobel.decompiler.PlainTextOutput;
 import com.strobel.decompiler.languages.Region;
 import com.strobel.decompiler.languages.TextLocation;
 import com.strobel.decompiler.languages.java.JavaFormattingOptions;
 import com.strobel.decompiler.languages.java.JavaOutputVisitor;
 import com.strobel.decompiler.patterns.BacktrackingInfo;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Pattern;
 import com.strobel.decompiler.patterns.Role;
 import com.strobel.decompiler.utilities.TreeTraversal;
 import com.strobel.functions.Function;
 import com.strobel.util.ContractUtils;
 import java.lang.reflect.UndeclaredThrowableException;
 import java.util.Iterator;
 import java.util.NoSuchElementException;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class AstNode
   extends Freezable
   implements INode, UserDataStore, Cloneable
 {
   static final Role<AstNode> ROOT_ROLE = new Role("Root", AstNode.class);
   
   static final int ROLE_INDEX_MASK = 511;
   
   static final int FROZEN_BIT = 512;
   
   protected static final int AST_NODE_USED_FLAGS = 10;
   protected int flags = ROOT_ROLE.getIndex();
   
   private AstNode _parent;
   private AstNode _previousSibling;
   private AstNode _nextSibling;
   private AstNode _firstChild;
   private AstNode _lastChild;
   
   protected AstNode()
   {
     if (isNull()) {
       freeze();
     }
   }
   
   protected static boolean matchString(String pattern, String text) {
     return Pattern.matchString(pattern, text);
   }
   
   public static boolean isLoop(AstNode statement) {
     return ((statement instanceof ForStatement)) || ((statement instanceof ForEachStatement)) || ((statement instanceof WhileStatement)) || ((statement instanceof DoWhileStatement));
   }
   
 
 
   public static boolean isUnconditionalBranch(AstNode statement)
   {
     return ((statement instanceof GotoStatement)) || ((statement instanceof ReturnStatement)) || ((statement instanceof BreakStatement)) || ((statement instanceof ContinueStatement));
   }
   
 
 
   final void setRoleUnsafe(Role<?> role)
   {
     this.flags = (this.flags & 0xFE00 | role.getIndex());
   }
   
   public abstract <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> paramIAstVisitor, T paramT);
   
   public AstNode clone()
   {
     try
     {
       AstNode clone = (AstNode)super.clone();
       
       clone._parent = null;
       clone._firstChild = null;
       clone._lastChild = null;
       clone._previousSibling = null;
       clone._nextSibling = null;
       clone.flags &= 0xFDFF;
       
       for (Key<?> key : Keys.ALL_KEYS) {
         copyKey(this, clone, key);
       }
       
       for (AstNode current = this._firstChild; current != null; current = current._nextSibling) {
         clone.addChildUnsafe(current.clone(), current.getRole());
       }
       
 
 
       return clone;
     }
     catch (CloneNotSupportedException e) {
       throw new UndeclaredThrowableException(e);
     }
   }
   
   public void copyUserDataFrom(AstNode source) {
     VerifyArgument.notNull(source, "source");
     
     for (Key<?> key : Keys.ALL_KEYS) {
       copyKey(source, this, key);
     }
   }
   
   private static <T> void copyKey(AstNode source, AstNode target, Key<T> key) {
     target._dataStore.putUserDataIfAbsent(key, source._dataStore.getUserData(key));
   }
   
 
   public final AstNode getParent()
   {
     return this._parent;
   }
   
   public final AstNode getPreviousSibling() {
     return this._previousSibling;
   }
   
   public final AstNode getLastChild() {
     return this._lastChild;
   }
   
   public final AstNode getFirstChild()
   {
     return this._firstChild;
   }
   
   public final AstNode getNextSibling()
   {
     return this._nextSibling;
   }
   
   public final <T extends AstNode> T getPreviousSibling(Role<T> role)
   {
     for (AstNode current = this._previousSibling; current != null; current = current.getPreviousSibling()) {
       if (current.getRole() == role) {
         return current;
       }
     }
     return null;
   }
   
   public final <T extends AstNode> T getNextSibling(Role<T> role)
   {
     for (AstNode current = this._nextSibling; current != null; current = current.getNextSibling()) {
       if (current.getRole() == role) {
         return current;
       }
     }
     return null;
   }
   
   public final boolean hasChildren() {
     return this._firstChild != null;
   }
   
   public final AstNode getNextNode() {
     AstNode nextSibling = getNextSibling();
     
     if (nextSibling != null) {
       return nextSibling;
     }
     
     AstNode parent = getParent();
     
     if (parent != null) {
       return parent.getNextNode();
     }
     
     return null;
   }
   
   public final AstNode getPreviousNode() {
     AstNode previousSibling = getPreviousSibling();
     
     if (previousSibling != null) {
       return previousSibling;
     }
     
     AstNode parent = getParent();
     
     if (parent != null) {
       return parent.getPreviousNode();
     }
     
     return null;
   }
   
   public final Iterable<AstNode> getChildren() {
     new Iterable()
     {
       @NotNull
       public final Iterator<AstNode> iterator() {
         new Iterator() {
           AstNode next = AstNode.this._firstChild;
           
           public final boolean hasNext()
           {
             return this.next != null;
           }
           
           public final AstNode next()
           {
             AstNode result = this.next;
             
             if (result == null) {
               throw new NoSuchElementException();
             }
             
             this.next = result._nextSibling;
             
             return result;
           }
           
           public final void remove()
           {
             throw ContractUtils.unsupported();
           }
         };
       }
     };
   }
   
   public final boolean isAncestorOf(AstNode node) {
     for (AstNode n = node; n != null; n = n._parent) {
       if (n == this) {
         return true;
       }
     }
     
     return false;
   }
   
   public final boolean isDescendantOf(AstNode node) {
     return (node != null) && (node.isAncestorOf(this));
   }
   
   public final <T extends AstNode> Iterable<T> getAncestors(@NotNull Class<T> type) {
     VerifyArgument.notNull(type, "type");
     return CollectionUtilities.ofType(getAncestors(), type);
   }
   
   public final Iterable<AstNode> getAncestors() {
     new Iterable()
     {
       @NotNull
       public final Iterator<AstNode> iterator() {
         new Iterator() {
           AstNode next = AstNode.this._parent;
           
           public final boolean hasNext()
           {
             return this.next != null;
           }
           
           public final AstNode next()
           {
             AstNode result = this.next;
             
             if (result == null) {
               throw new NoSuchElementException();
             }
             
             this.next = result._parent;
             
             return result;
           }
           
           public final void remove()
           {
             throw ContractUtils.unsupported();
           }
         };
       }
     };
   }
   
   public final Iterable<AstNode> getAncestorsAndSelf() {
     new Iterable()
     {
       @NotNull
       public final Iterator<AstNode> iterator() {
         new Iterator() {
           AstNode next = AstNode.this;
           
           public final boolean hasNext()
           {
             return this.next != null;
           }
           
           public final AstNode next()
           {
             AstNode result = this.next;
             
             if (result == null) {
               throw new NoSuchElementException();
             }
             
             this.next = result._parent;
             
             return result;
           }
           
           public final void remove()
           {
             throw ContractUtils.unsupported();
           }
         };
       }
     };
   }
   
   public final Iterable<AstNode> getDescendants() {
     TreeTraversal.preOrder(getChildren(), new Function()
     {
 
       public Iterable<AstNode> apply(AstNode n)
       {
         return n.getChildren();
       }
     });
   }
   
   public final Iterable<AstNode> getDescendantsAndSelf()
   {
     TreeTraversal.preOrder(this, new Function()
     {
 
       public Iterable<AstNode> apply(AstNode n)
       {
         return n.getChildren();
       }
     });
   }
   
 
   @NotNull
   public final <T extends AstNode> T getChildByRole(Role<T> role)
   {
     VerifyArgument.notNull(role, "role");
     
     int roleIndex = role.getIndex();
     
     for (AstNode current = this._firstChild; current != null; current = current._nextSibling) {
       if ((current.flags & 0x1FF) == roleIndex) {
         return current;
       }
     }
     
     return (AstNode)role.getNullObject();
   }
   
   @NotNull
   public final <T extends AstNode> AstNodeCollection<T> getChildrenByRole(Role<T> role) {
     return new AstNodeCollection(this, role);
   }
   
   protected final <T extends AstNode> void setChildByRole(Role<T> role, T newChild) {
     T oldChild = getChildByRole(role);
     
     if (oldChild.isNull()) {
       addChild(newChild, role);
     }
     else {
       oldChild.replaceWith(newChild);
     }
   }
   
   public final <T extends AstNode> T getParent(Class<T> nodeType)
   {
     for (AstNode node : getAncestors()) {
       if (nodeType.isInstance(node)) {
         return node;
       }
     }
     
     return null;
   }
   
   public final <T extends AstNode> void addChild(T child, Role<T> role) {
     VerifyArgument.notNull(role, "role");
     
     if ((child == null) || (child.isNull())) {
       return;
     }
     
     verifyNotFrozen();
     
     if (child._parent != null) {
       throw new IllegalArgumentException("Node belongs to another tree.");
     }
     
     if (child.isFrozen()) {
       throw new IllegalArgumentException("Cannot add a frozen node.");
     }
     
     addChildUnsafe(child, role);
   }
   
   final void addChildUnsafe(AstNode child, Role<?> role) {
     child._parent = this;
     child.setRoleUnsafe(role);
     
     if (this._firstChild == null) {
       this._lastChild = (this._firstChild = child);
     }
     else {
       this._lastChild._nextSibling = child;
       child._previousSibling = this._lastChild;
       this._lastChild = child;
     }
   }
   
   @SafeVarargs
   public final <T extends AstNode> void insertChildrenBefore(AstNode nextSibling, Role<T> role, T... children) {
     VerifyArgument.notNull(children, "children");
     
     for (T child : children) {
       insertChildBefore(nextSibling, child, role);
     }
   }
   
   public final <T extends AstNode> void insertChildBefore(AstNode nextSibling, T child, Role<T> role) {
     VerifyArgument.notNull(role, "role");
     
     if ((nextSibling == null) || (nextSibling.isNull())) {
       addChild(child, role);
       return;
     }
     
     if ((child == null) || (child.isNull())) {
       return;
     }
     
     verifyNotFrozen();
     
     if (child._parent != null) {
       throw new IllegalArgumentException("Node belongs to another tree.");
     }
     
     if (child.isFrozen()) {
       throw new IllegalArgumentException("Cannot add a frozen node.");
     }
     
     if (nextSibling._parent != this) {
       throw new IllegalArgumentException("Next sibling is not a child of this node.");
     }
     
     insertChildBeforeUnsafe(nextSibling, child, role);
   }
   
   @SafeVarargs
   public final <T extends AstNode> void insertChildrenAfter(AstNode nextSibling, Role<T> role, T... children) {
     VerifyArgument.notNull(children, "children");
     
     for (T child : children) {
       insertChildAfter(nextSibling, child, role);
     }
   }
   
   public final <T extends AstNode> void insertChildAfter(AstNode previousSibling, T child, Role<T> role) {
     insertChildBefore((previousSibling == null) || (previousSibling.isNull()) ? this._firstChild : previousSibling._nextSibling, child, role);
   }
   
 
 
 
   final void insertChildBeforeUnsafe(AstNode nextSibling, AstNode child, Role<?> role)
   {
     child._parent = this;
     child.setRole(role);
     child._nextSibling = nextSibling;
     child._previousSibling = nextSibling._previousSibling;
     
     if (nextSibling._previousSibling != null) {
       assert (nextSibling._previousSibling._nextSibling == nextSibling);
       nextSibling._previousSibling._nextSibling = child;
     }
     else {
       assert (this._firstChild == nextSibling);
       this._firstChild = child;
     }
     
     nextSibling._previousSibling = child;
   }
   
   public final void remove() {
     if (this._parent == null) {
       return;
     }
     
     verifyNotFrozen();
     
     if (this._previousSibling != null) {
       assert (this._previousSibling._nextSibling == this);
       this._previousSibling._nextSibling = this._nextSibling;
     }
     else {
       assert (this._parent._firstChild == this);
       this._parent._firstChild = this._nextSibling;
     }
     
     if (this._nextSibling != null) {
       assert (this._nextSibling._previousSibling == this);
       this._nextSibling._previousSibling = this._previousSibling;
     }
     else {
       assert (this._parent._lastChild == this);
       this._parent._lastChild = this._previousSibling;
     }
     
     this._parent = null;
     this._previousSibling = null;
     this._nextSibling = null;
   }
   
   public final void replaceWith(AstNode newNode) {
     if ((newNode == null) || (newNode.isNull())) {
       remove();
       return;
     }
     
     if (newNode == this) {
       return;
     }
     
     if (this._parent == null) {
       throw new IllegalStateException(isNull() ? "Cannot replace null nodes." : "Cannot replace the root node.");
     }
     
 
 
 
     verifyNotFrozen();
     
     Role role = getRole();
     
     if (!role.isValid(newNode)) {
       throw new IllegalArgumentException(String.format("The new node '%s' is not valid for role '%s'.", new Object[] { newNode.getClass().getName(), role.toString() }));
     }
     
 
 
 
 
 
 
     if (newNode._parent != null) {
       if (CollectionUtilities.contains(newNode.getAncestors(), this)) {
         newNode.remove();
       }
       else {
         throw new IllegalArgumentException("Node belongs to another tree.");
       }
     }
     
     if (newNode.isFrozen()) {
       throw new IllegalArgumentException("Node belongs to another tree.");
     }
     
     newNode._parent = this._parent;
     newNode.setRoleUnsafe(role);
     newNode._previousSibling = this._previousSibling;
     newNode._nextSibling = this._nextSibling;
     
     if (this._parent != null) {
       if (this._previousSibling != null) {
         assert (this._previousSibling._nextSibling == this);
         this._previousSibling._nextSibling = newNode;
       }
       else {
         assert (this._parent._firstChild == this);
         this._parent._firstChild = newNode;
       }
       
       if (this._nextSibling != null) {
         assert (this._nextSibling._previousSibling == this);
         this._nextSibling._previousSibling = newNode;
       }
       else {
         assert (this._parent._lastChild == this);
         this._parent._lastChild = newNode;
       }
       
       this._parent = null;
       this._previousSibling = null;
       this._nextSibling = null;
     }
   }
   
   public final <T extends AstNode> T replaceWith(Function<? super AstNode, ? extends T> replaceFunction) {
     VerifyArgument.notNull(replaceFunction, "replaceFunction");
     
     if (this._parent == null) {
       throw new IllegalStateException(isNull() ? "Cannot replace null nodes." : "Cannot replace the root node.");
     }
     
 
 
 
     AstNode oldParent = this._parent;
     AstNode oldSuccessor = this._nextSibling;
     Role oldRole = getRole();
     
     remove();
     
     T replacement = (AstNode)replaceFunction.apply(this);
     
     if ((oldSuccessor != null) && (oldSuccessor._parent != oldParent)) {
       throw new IllegalStateException("Replace function changed next sibling of node being replaced.");
     }
     
     if ((replacement != null) && (!replacement.isNull())) {
       if (replacement.getParent() != null) {
         throw new IllegalStateException("replace function must return the root of a tree");
       }
       
       if (!oldRole.isValid(replacement)) {
         throw new IllegalStateException(String.format("The new node '%s' is not valid in the role %s.", new Object[] { replacement.getClass().getSimpleName(), oldRole }));
       }
       
 
 
 
 
 
 
       if (oldSuccessor != null) {
         oldParent.insertChildBeforeUnsafe(oldSuccessor, replacement, oldRole);
       }
       else {
         oldParent.addChildUnsafe(replacement, oldRole);
       }
     }
     
     return replacement;
   }
   
 
 
 
 
   protected void freezeCore()
   {
     for (AstNode child = this._firstChild; child != null; child = child._nextSibling) {
       child.freezeIfUnfrozen();
     }
     
     this.flags |= 0x200;
   }
   
 
 
 
 
   public static final AstNode NULL = new NullAstNode(null);
   public abstract NodeType getNodeType();
   
   private static final class NullAstNode extends AstNode {
     public boolean isNull() {
       return true;
     }
     
     public boolean matches(INode other, Match match)
     {
       return (other == null) || (other.isNull());
     }
     
     public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
     {
       return null;
     }
     
     public NodeType getNodeType()
     {
       return NodeType.UNKNOWN;
     }
   }
   
 
 
 
 
 
   public boolean isReference()
   {
     return false;
   }
   
   public boolean isNull()
   {
     return false;
   }
   
   public final Role getRole()
   {
     return Role.get(this.flags & 0x1FF);
   }
   
   public final void setRole(Role<?> role) {
     VerifyArgument.notNull(role, "role");
     
     if (!role.isValid(this)) {
       throw new IllegalArgumentException("This node is not valid for the specified role.");
     }
     
     verifyNotFrozen();
     setRoleUnsafe(role);
   }
   
 
 
 
   public abstract boolean matches(INode paramINode, Match paramMatch);
   
 
 
 
   public boolean matchesCollection(Role role, INode position, Match match, BacktrackingInfo backtrackingInfo)
   {
     return ((position == null) || ((position instanceof AstNode))) && (matches(position, match));
   }
   
   public final Match match(INode other)
   {
     Match match = Match.createNew();
     return matches(other, match) ? match : Match.failure();
   }
   
   public final boolean matches(INode other)
   {
     return matches(other, Match.createNew());
   }
   
   public static AstNode forPattern(Pattern pattern) {
     return new PatternPlaceholder((Pattern)VerifyArgument.notNull(pattern, "pattern"));
   }
   
   private static final class PatternPlaceholder extends AstNode {
     final Pattern child;
     
     PatternPlaceholder(Pattern child) {
       this.child = child;
     }
     
     public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
     {
       return (R)visitor.visitPatternPlaceholder(this, this.child, data);
     }
     
     public final NodeType getNodeType()
     {
       return NodeType.PATTERN;
     }
     
     public boolean matches(INode other, Match match)
     {
       return this.child.matches(other, match);
     }
     
     public boolean matchesCollection(Role role, INode position, Match match, BacktrackingInfo backtrackingInfo)
     {
       return this.child.matchesCollection(role, position, match, backtrackingInfo);
     }
   }
   
 
 
 
   public TextLocation getStartLocation()
   {
     AstNode child = this._firstChild;
     return child != null ? child.getStartLocation() : TextLocation.EMPTY;
   }
   
   public TextLocation getEndLocation() {
     AstNode child = this._lastChild;
     return child != null ? child.getEndLocation() : TextLocation.EMPTY;
   }
   
   public Region getRegion() {
     return new Region(getStartLocation(), getEndLocation());
   }
   
   public final boolean contains(int line, int column) {
     return contains(new TextLocation(line, column));
   }
   
   public final boolean contains(TextLocation location) {
     if ((location == null) || (location.isEmpty())) {
       return false;
     }
     
     TextLocation startLocation = getStartLocation();
     TextLocation endLocation = getEndLocation();
     
     return (startLocation != null) && (endLocation != null) && (location.compareTo(startLocation) >= 0) && (location.compareTo(endLocation) < 0);
   }
   
 
 
   public final boolean isInside(int line, int column)
   {
     return isInside(new TextLocation(line, column));
   }
   
   public final boolean isInside(TextLocation location) {
     if ((location == null) || (location.isEmpty())) {
       return false;
     }
     
     TextLocation startLocation = getStartLocation();
     TextLocation endLocation = getEndLocation();
     
     return (startLocation != null) && (endLocation != null) && (location.compareTo(startLocation) >= 0) && (location.compareTo(endLocation) <= 0);
   }
   
 
 
   public String getText()
   {
     return getText(null);
   }
   
   public String getText(JavaFormattingOptions options) {
     if (isNull()) {
       return "";
     }
     
     ITextOutput output = new PlainTextOutput();
     JavaOutputVisitor visitor = new JavaOutputVisitor(output, DecompilerSettings.javaDefaults());
     
     acceptVisitor(visitor, null);
     
     return output.toString();
   }
   
   String debugToString() {
     if (isNull()) {
       return "Null";
     }
     
     String text = StringUtilities.trimRight(getText());
     
     return text.length() > 1000 ? text.substring(0, 97) + "..." : text;
   }
   
   public String toString()
   {
     return debugToString();
   }
   
 
 
 
 
   private final UserDataStore _dataStore = new UserDataStoreBase();
   
   public final <T> T getUserData(Key<T> key)
   {
     return (T)this._dataStore.getUserData(key);
   }
   
   public final <T> void putUserData(Key<T> key, T value)
   {
     this._dataStore.putUserData(key, value);
   }
   
   public final <T> T putUserDataIfAbsent(Key<T> key, T value)
   {
     return (T)this._dataStore.putUserDataIfAbsent(key, value);
   }
   
   public final <T> boolean replace(Key<T> key, T oldValue, T newValue)
   {
     return this._dataStore.replace(key, oldValue, newValue);
   }
 }


