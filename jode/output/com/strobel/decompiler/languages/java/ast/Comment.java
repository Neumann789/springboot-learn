/* Comment - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.core.Comparer;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class Comment extends AstNode
{
    private CommentType _commentType;
    private boolean _startsLine;
    private String _content;
    
    public Comment(String content) {
	this(content, CommentType.SingleLine);
    }
    
    public Comment(String content, CommentType commentType) {
	_commentType = commentType;
	_content = content;
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitComment(this, data);
    }
    
    public NodeType getNodeType() {
	return NodeType.WHITESPACE;
    }
    
    public final CommentType getCommentType() {
	return _commentType;
    }
    
    public final void setCommentType(CommentType commentType) {
	verifyNotFrozen();
	_commentType = commentType;
    }
    
    public final boolean getStartsLine() {
	return _startsLine;
    }
    
    public final void setStartsLine(boolean startsLine) {
	verifyNotFrozen();
	_startsLine = startsLine;
    }
    
    public final String getContent() {
	return _content;
    }
    
    public final void setContent(String content) {
	verifyNotFrozen();
	_content = content;
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof Comment))
	    return false;
    label_1593:
	{
	    Comment otherComment = (Comment) other;
	    if (otherComment._commentType != _commentType
		|| !Comparer.equals(otherComment._content, _content))
		PUSH false;
	    else
		PUSH true;
	    break label_1593;
	}
	return POP;
    }
}
