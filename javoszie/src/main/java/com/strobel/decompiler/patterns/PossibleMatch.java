 package com.strobel.decompiler.patterns;
 
 
 
 
 
 
 final class PossibleMatch
 {
   final INode nextOther;
   
 
 
 
 
   final int checkPoint;
   
 
 
 
 
   PossibleMatch(INode nextOther, int checkPoint)
   {
     this.nextOther = nextOther;
     this.checkPoint = checkPoint;
   }
 }


