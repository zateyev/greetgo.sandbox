package kz.greetgo.sandbox.stand.modelling;

import kz.greetgo.sandbox.stand.modelling.stru.ClassAttr;
import kz.greetgo.sandbox.stand.modelling.stru.ClassStructure;
import kz.greetgo.sandbox.stand.modelling.stru.Import;
import kz.greetgo.sandbox.stand.modelling.stru.SimpleType;
import kz.greetgo.sandbox.stand.modelling.stru.simple.SimpleTypeStr;
import kz.greetgo.sandbox.stand.util.FileUtils;
import kz.greetgo.util.ServerUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TsFileReference {
  public final File tsFile;
  public final String subPackage;
  public final String className;

  public List<TsFileReference> anotherFiles;

  private String content = null;
  public ClassStructure classStructure;
  public File sourceDir;

  public void defineRealPackage(String packagePrefix) {
    String realPackage = resolvePackage(packagePrefix, subPackage);
    classStructure = new ClassStructure(realPackage, className, attrList, classComment);
  }

  public String content() {

    if (content == null) try {
      content = ServerUtil.streamToStr(new FileInputStream(tsFile));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }

    return content;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("TsFileWithSubPackage{");
    sb.append("tsFile=").append(tsFile);
    sb.append(", subPackage='").append(subPackage).append('\'');
    sb.append('}');
    return sb.toString();
  }

  public TsFileReference(File tsFile, String subPackage, String className) {
    this.tsFile = tsFile;
    this.subPackage = subPackage;
    this.className = className;
  }

  public static List<TsFileReference> scanForTs(File dir) {
    List<TsFileReference> ret = new ArrayList<>();
    scanForTsInner(ret, dir, null);
    return ret;
  }

  private static void scanForTsInner(List<TsFileReference> ret,
                                     File dir, String currentSubPackage) {
    File[] files = dir.listFiles();
    if (files == null) throw new NullPointerException("dir.listFiles() == null, dir = " + dir);
    for (File subFile : files) {
      if (subFile.isDirectory()) {
        scanForTsInner(ret, subFile, resolvePackage(currentSubPackage, subFile.getName()));
        continue;
      }
      if (subFile.getName().endsWith(".ts")) {
        ret.add(new TsFileReference(subFile, currentSubPackage,
          subFile.getName().substring(0, subFile.getName().length() - 3)));
        continue;
      }
    }
  }

  public static String resolvePackage(String subPackage1, String subPackage2) {
    if (subPackage1 == null || subPackage1.length() == 0) return subPackage2;
    if (subPackage2 == null || subPackage2.length() == 0) return subPackage1;
    return subPackage1 + '.' + subPackage2;
  }

  final List<String> classComment = new ArrayList<>();
  boolean wasClassDefinition = false;

  public void fillAttributes() throws Exception {
    wasClassDefinition = false;

    int lineNo = 1;
    for (String line : content().split("\n")) {
      parseLine(lineNo++, line);
    }

    if (!wasClassDefinition) throw new RuntimeException("No class definition in " + tsFile);
  }

  private static final Pattern CLASS_DEFINITION
    = Pattern.compile("\\s*export\\s+class\\s+(\\w+)[^{]*\\{\\s*");

  //public world: string;
  private static final Pattern STRING_FIELD
    = Pattern.compile("\\s*public\\s+(\\w+)\\s*:\\s*string\\s*(\\|\\s*null)?\\s*(\\[\\s*]\\s*)?;\\s*.*");

  //public count: number/*int*/;
  private static final Pattern NUMBER_FIELD
    = Pattern.compile("\\s*public\\s+(\\w+)\\s*:\\s*number\\s*(\\|\\s*null)?\\s*(/\\*\\s*(\\w+)\\s*\\*/)?\\s*(\\[\\s*])?\\s*;.*");

  //import {OrgUnitKind} from "./org_unit/OrgUnitKind";
  private static final Pattern IMPORT
    = Pattern.compile("\\s*import\\s+\\{(\\w+)}\\s+from\\s*\"\\./([^\"]*\\w)\"\\s*;.*");

  //import {OrgUnitKind} from "../org_unit/OrgUnitKind";
  private static final Pattern IMPORT_PARENT
    = Pattern.compile("\\s*import\\s+\\{(\\w+)}\\s+from\\s*\"\\.\\./([^\"]*\\w)\"\\s*;.*");

  //public bArray: OrgUnitRoot|null[];
  private static final Pattern CLASS_FIELD
    = Pattern.compile("\\s*public\\s*(\\w+)\\s*:\\s*(\\w+)\\s*(\\|\\s*null)?\\s*(\\[\\s*])?\\s*;.*");


  //public hasChildren: boolean|null[];
  private static final Pattern BOOLEAN_FIELD
    = Pattern.compile("\\s*public\\s+(\\w+)\\s*:\\s*boolean(\\|\\s*null)?\\s*(\\[\\s*])?\\s*;\\s*(#*.*)?");

  private final Map<String, Import> importMap = new HashMap<>();
  public final List<ClassAttr> attrList = new ArrayList<>();

  private static final Pattern COMMENT_BEGIN = Pattern.compile("\\s*/\\*\\*\\s*");
  private static final Pattern COMMENT_END = Pattern.compile("\\s*\\*/\\s*");
  private final List<String> comment = new ArrayList<>();
  boolean inComment = false;

  private void parseLine(int lineNo, String line) throws Exception {
    System.out.println("line : " + line);

    if (inComment) {
      inComment = !COMMENT_END.matcher(line).matches();
      comment.add(line);
      return;
    }
    if (COMMENT_BEGIN.matcher(line).matches()) {
      comment.clear();
      comment.add(line);
      inComment = true;
      return;
    }

    {
      Matcher matcher = IMPORT.matcher(line);
      if (matcher.matches()) {
        String className = matcher.group(1);
        File importedFile = tsFile.getParentFile().toPath().resolve(matcher.group(2) + ".ts").toFile();

        registerImport(lineNo, className, importedFile);
        comment.clear();
        return;
      }
    }
    {
      Matcher matcher = IMPORT_PARENT.matcher(line);
      if (matcher.matches()) {
        String className = matcher.group(1);
        File importedFile = tsFile.getParentFile().getParentFile().toPath().resolve(matcher.group(2) + ".ts").toFile();

        registerImport(lineNo, className, importedFile);
        comment.clear();
        return;
      }
    }

    {
      Matcher matcher = CLASS_DEFINITION.matcher(line);
      if (matcher.matches()) {
        String lineClassName = matcher.group(1);
        registerImport(lineNo, lineClassName, tsFile);
        if (lineClassName.equals(className)) {
          wasClassDefinition = true;
          classComment.addAll(comment);
          comment.clear();
          return;
        }
        comment.clear();
        throw new RuntimeException("Left class name " + lineClassName + " at " + place(lineNo));
      }
    }

    {
      Matcher matcher = STRING_FIELD.matcher(line);
      if (matcher.matches()) {
        attrList.add(new ClassAttr(SimpleTypeStr.get(), matcher.group(1), matcher.group(3) != null, comment));
        comment.clear();
        return;
      }
    }

    {
      Matcher matcher = NUMBER_FIELD.matcher(line);
      if (matcher.matches()) {
        if (matcher.group(3) == null) {
          throw new RuntimeException("No number type at " + tsFile + ":" + lineNo + "\n" +
            "  Examples:\n" +
            "    public fieldName: number /*long*/;\n" +
            "    public fieldName: number /*int*/;\n" +
            "    public fieldName: number|null /*long*/;\n" +
            "    public fieldName: number|null /*int*/;");
        }
        attrList.add(new ClassAttr(
          SimpleType.fromStr(matcher.group(4), matcher.group(2) != null, place(lineNo)),
          matcher.group(1),
          matcher.group(5) != null,
          comment
        ));
        comment.clear();
        return;
      }
    }

    {
      Matcher matcher = BOOLEAN_FIELD.matcher(line);
      if (matcher.matches()) {

        attrList.add(new ClassAttr(
          SimpleType.fromStr("boolean", matcher.group(2) != null, place(lineNo)),
          matcher.group(1),
          matcher.group(3) != null,
          comment
        ));

        comment.clear();
        return;
      }
    }

    {
      Matcher matcher = CLASS_FIELD.matcher(line);
      if (matcher.matches()) {
        String fieldName = matcher.group(1);
        String className = matcher.group(2);
        boolean isArray = matcher.group(4) != null;

        Import anImport = importMap.get(className);
        if (anImport == null) throw new RuntimeException("Cannot find class [[" + className + "]] in " + place(lineNo));

        attrList.add(new ClassAttr(anImport.toClassStru(), fieldName, isArray, comment));
        comment.clear();
        return;
      }
    }

  }

  private void registerImport(int lineNo, String className, File importedFile) throws Exception {
    {
      Import anotherImport = importMap.get(className);
      if (anotherImport != null) {
        throw new RuntimeException(className + " already defined at line " + anotherImport.lineNo
          + " in " + place(lineNo));
      }
    }

    if (!importedFile.exists()) {
      throw new RuntimeException("No file " + importedFile + " in import at " + place(lineNo));
    }

    TsFileReference ref = tsFile.equals(importedFile) ? this : findTsFileReference(importedFile, lineNo);

    if (ref != null) importMap.put(className, new Import(className, ref, lineNo));
  }

  private TsFileReference findTsFileReference(File importedFile, int lineNo) throws IOException {
    File canonicalFile = importedFile.getCanonicalFile();

    for (TsFileReference anotherFile : anotherFiles) {
      if (canonicalFile.equals(anotherFile.tsFile.getCanonicalFile())) return anotherFile;
    }

    if (FileUtils.isParent(sourceDir, importedFile)) {
      throw new RuntimeException("Cannot find " + importedFile + " at " + place(lineNo));
    }

    return null;
  }

  private String place(int lineNo) {
    return tsFile + ":" + lineNo;
  }
}
