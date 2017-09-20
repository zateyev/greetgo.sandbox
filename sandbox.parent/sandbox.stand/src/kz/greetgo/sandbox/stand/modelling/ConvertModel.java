package kz.greetgo.sandbox.stand.modelling;

import kz.greetgo.sandbox.stand.modelling.stru.ClassAttr;
import kz.greetgo.sandbox.stand.modelling.stru.ClassStructure;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static kz.greetgo.sandbox.stand.util.ConvertModelUtil.deleteIt;


public class ConvertModel {
  private final File sourceDir;
  private final File destinationDir;
  private final String destinationPackage;

  public ConvertModel(File sourceDir, File destinationDir, String destinationPackage) {
    this.sourceDir = sourceDir;
    this.destinationDir = destinationDir;
    this.destinationPackage = destinationPackage;
  }

  public void exec() throws Exception {
    List<TsFileReference> files = TsFileReference.scanForTs(sourceDir);

    for (TsFileReference file : files) {
      file.anotherFiles = deleteIt(file, files);
      file.sourceDir = sourceDir;
    }

    files.forEach(a -> a.defineRealPackage(destinationPackage));

    for (TsFileReference file : files) {
      file.fillAttributes();
    }

    for (TsFileReference file : files) {
      System.out.println(file);
      for (ClassAttr classAttr : file.attrList) {
        System.out.println("    " + classAttr);
      }

      generate(file.classStructure, destinationDir);
    }
  }

  private void generate(ClassStructure classStructure, File destinationDir) throws Exception {
    Imports imports = new Imports(classStructure);
    StringBuilder body = new StringBuilder();

    generateBody(classStructure, imports, body);

    File javaFile = classStructure.javaFile(destinationDir);
    javaFile.getParentFile().mkdirs();

    try (PrintStream pr = new PrintStream(javaFile, "UTF-8")) {
      if (classStructure.hasPackage()) pr.println("package " + classStructure.aPackage + ";");
      pr.println();
      pr.println(imports.asStr());
      pr.println();
      pr.println(body);
    }
  }

  private void generateBody(ClassStructure classStructure, Imports imports, StringBuilder body) {
    appendComment(body, classStructure.classComment);
    body.append("public class ").append(classStructure.name).append(" {\n");

    for (ClassAttr attr : classStructure.attrList) {
      appendComment(body, attr.comment);
      body.append("  public ");
      body.append(imports.typeStr(attr.type, attr.isArray)).append(' ').append(attr.name);
      if (attr.isArray) {
        body.append(" = new ");
        body.append(imports.name(ArrayList.class.getName()));
        body.append("<>()");
      }
      body.append(";\n");
    }

    body.append("}");
  }

  private static void appendComment(StringBuilder dest, List<String> comment) {
    if (comment == null) return;
    for (String line : comment) {
      dest.append(line).append('\n');
    }
  }
}
