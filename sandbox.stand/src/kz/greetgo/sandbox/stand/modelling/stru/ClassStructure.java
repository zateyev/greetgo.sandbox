package kz.greetgo.sandbox.stand.modelling.stru;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class ClassStructure extends TypeStructure {

  public final String aPackage, name;
  public final List<ClassAttr> attrList;
  public final List<String> classComment;

  public boolean hasPackage() {
    return aPackage != null && aPackage.length() > 0;
  }

  private String fullName = null;

  public String fullName() {
    if (fullName == null) fullName = hasPackage() ? aPackage + '.' + name : name;
    return fullName;
  }

  public ClassStructure(String aPackage, String name, List<ClassAttr> attrList, List<String> classComment) {
    this.aPackage = aPackage;
    this.name = name;
    this.attrList = Collections.unmodifiableList(attrList);
    this.classComment = Collections.unmodifiableList(classComment);
  }

  @Override
  public String toString() {
    return "CLASS{" + aPackage + '.' + name + '}';
  }

  public File javaFile(File destinationDir) {
    String pack = hasPackage() ? aPackage.replaceAll("\\.", "/") + '/' : "";
    return destinationDir.toPath().resolve(pack + name + ".java").toFile();
  }
}
