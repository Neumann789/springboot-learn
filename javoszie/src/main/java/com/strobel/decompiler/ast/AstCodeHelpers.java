 package com.strobel.decompiler.ast;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class AstCodeHelpers
 {
   public static boolean isLocalStore(AstCode code)
   {
     if (code == null) {
       return false;
     }
     
     switch (code) {
     case __IStore: 
     case __LStore: 
     case __FStore: 
     case __DStore: 
     case __AStore: 
     case __IStore0: 
     case __IStore1: 
     case __IStore2: 
     case __IStore3: 
     case __LStore0: 
     case __LStore1: 
     case __LStore2: 
     case __LStore3: 
     case __FStore0: 
     case __FStore1: 
     case __FStore2: 
     case __FStore3: 
     case __DStore0: 
     case __DStore1: 
     case __DStore2: 
     case __DStore3: 
     case __AStore0: 
     case __AStore1: 
     case __AStore2: 
     case __AStore3: 
       return true;
     
     case __IStoreW: 
     case __LStoreW: 
     case __FStoreW: 
     case __DStoreW: 
     case __AStoreW: 
       return true;
     
     case Store: 
       return true;
     }
     
     return false;
   }
   
   public static boolean isLocalLoad(AstCode code)
   {
     if (code == null) {
       return false;
     }
     
     switch (code) {
     case __ILoad: 
     case __LLoad: 
     case __FLoad: 
     case __DLoad: 
     case __ALoad: 
     case __ILoad0: 
     case __ILoad1: 
     case __ILoad2: 
     case __ILoad3: 
     case __LLoad0: 
     case __LLoad1: 
     case __LLoad2: 
     case __LLoad3: 
     case __FLoad0: 
     case __FLoad1: 
     case __FLoad2: 
     case __FLoad3: 
     case __DLoad0: 
     case __DLoad1: 
     case __DLoad2: 
     case __DLoad3: 
     case __ALoad0: 
     case __ALoad1: 
     case __ALoad2: 
     case __ALoad3: 
       return true;
     
     case __ILoadW: 
     case __LLoadW: 
     case __FLoadW: 
     case __DLoadW: 
     case __ALoadW: 
       return true;
     
     case Load: 
       return true;
     }
     
     return false;
   }
   
   public static int getLoadStoreMacroArgumentIndex(AstCode code)
   {
     if (code == null) {
       return -1;
     }
     
     switch (code) {
     case __IStore0: 
     case __LStore0: 
     case __FStore0: 
     case __DStore0: 
     case __AStore0: 
     case __ILoad0: 
     case __LLoad0: 
     case __FLoad0: 
     case __DLoad0: 
     case __ALoad0: 
       return 0;
     
     case __IStore1: 
     case __LStore1: 
     case __FStore1: 
     case __DStore1: 
     case __AStore1: 
     case __ILoad1: 
     case __LLoad1: 
     case __FLoad1: 
     case __DLoad1: 
     case __ALoad1: 
       return 1;
     
     case __IStore2: 
     case __LStore2: 
     case __FStore2: 
     case __DStore2: 
     case __AStore2: 
     case __ILoad2: 
     case __LLoad2: 
     case __FLoad2: 
     case __DLoad2: 
     case __ALoad2: 
       return 2;
     
     case __IStore3: 
     case __LStore3: 
     case __FStore3: 
     case __DStore3: 
     case __AStore3: 
     case __ILoad3: 
     case __LLoad3: 
     case __FLoad3: 
     case __DLoad3: 
     case __ALoad3: 
       return 3;
     }
     
     return -1;
   }
 }


