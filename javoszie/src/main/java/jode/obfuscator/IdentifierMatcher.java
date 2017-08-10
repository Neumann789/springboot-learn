package jode.obfuscator;

public abstract interface IdentifierMatcher
{
  public abstract boolean matches(Identifier paramIdentifier);
  
  public abstract boolean matchesSub(Identifier paramIdentifier, String paramString);
  
  public abstract String getNextComponent(Identifier paramIdentifier);
}


