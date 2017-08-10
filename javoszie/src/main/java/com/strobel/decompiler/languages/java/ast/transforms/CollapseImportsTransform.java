 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.PackageReference;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.DecompilerSettings;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.CompilationUnit;
 import com.strobel.decompiler.languages.java.ast.Identifier;
 import com.strobel.decompiler.languages.java.ast.ImportDeclaration;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.PackageDeclaration;
 import com.strobel.decompiler.languages.java.ast.Roles;
 import java.util.ArrayList;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Set;
 
 
 
 
 
 
 
 
 public class CollapseImportsTransform
   implements IAstTransform
 {
   private final DecompilerSettings _settings;
   
   public CollapseImportsTransform(DecompilerContext context)
   {
     this._settings = context.getSettings();
   }
   
   public void run(AstNode root)
   {
     if (!(root instanceof CompilationUnit)) {
       return;
     }
     
     CompilationUnit compilationUnit = (CompilationUnit)root;
     
     if (this._settings.getForceExplicitImports()) {
       removeRedundantImports(compilationUnit);
       return;
     }
     
     AstNodeCollection<ImportDeclaration> imports = compilationUnit.getImports();
     PackageDeclaration packageDeclaration = (PackageDeclaration)compilationUnit.getChildByRole(Roles.PACKAGE);
     String filePackage = packageDeclaration.isNull() ? null : packageDeclaration.getName();
     
     if (imports.isEmpty()) {
       return;
     }
     
     Set<String> newImports = new LinkedHashSet();
     List<ImportDeclaration> removedImports = new ArrayList();
     
     for (ImportDeclaration oldImport : imports) {
       Identifier importedType = oldImport.getImportIdentifier();
       
       if ((importedType != null) && (!importedType.isNull())) {
         TypeReference type = (TypeReference)oldImport.getUserData(Keys.TYPE_REFERENCE);
         
         if (type != null) {
           String packageName = type.getPackageName();
           
           if ((!StringUtilities.isNullOrEmpty(packageName)) && (!StringUtilities.equals(packageName, "java.lang")) && (!StringUtilities.equals(packageName, filePackage)))
           {
 
 
             newImports.add(packageName);
           }
           
           removedImports.add(oldImport);
         }
       }
     }
     
     if (removedImports.isEmpty()) {
       return;
     }
     
     ImportDeclaration lastRemoved = (ImportDeclaration)removedImports.get(removedImports.size() - 1);
     
     for (String packageName : newImports) {
       compilationUnit.insertChildAfter(lastRemoved, new ImportDeclaration(PackageReference.parse(packageName)), CompilationUnit.IMPORT_ROLE);
     }
     
 
 
 
 
     for (ImportDeclaration removedImport : removedImports) {
       removedImport.remove();
     }
   }
   
   private void removeRedundantImports(CompilationUnit compilationUnit) {
     AstNodeCollection<ImportDeclaration> imports = compilationUnit.getImports();
     PackageDeclaration packageDeclaration = (PackageDeclaration)compilationUnit.getChildByRole(Roles.PACKAGE);
     String filePackage = packageDeclaration.isNull() ? null : packageDeclaration.getName();
     
     for (ImportDeclaration oldImport : imports) {
       Identifier importedType = oldImport.getImportIdentifier();
       
       if ((importedType != null) && (!importedType.isNull())) {
         TypeReference type = (TypeReference)oldImport.getUserData(Keys.TYPE_REFERENCE);
         
         if (type != null) {
           String packageName = type.getPackageName();
           
           if ((StringUtilities.isNullOrEmpty(packageName)) || (StringUtilities.equals(packageName, "java.lang")) || (StringUtilities.equals(packageName, filePackage)))
           {
 
 
             oldImport.remove();
           }
         }
       }
     }
   }
 }


