/* INode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.patterns;
import com.strobel.functions.Function;

public interface INode
{
    public static final Function CHILD_ITERATOR
	= new ANONYMOUS CLASS com.strobel.decompiler.patterns.INode$1();
    
    public boolean isNull();
    
    public Role getRole();
    
    public INode getFirstChild();
    
    public INode getNextSibling();
    
    public boolean matches(INode inode_0_, Match match);
    
    public boolean matchesCollection(Role role, INode inode_1_, Match match,
				     BacktrackingInfo backtrackinginfo);
    
    public Match match(INode inode_2_);
    
    public boolean matches(INode inode_3_);
}
