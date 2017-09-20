package kz.greetgo.sandbox.stand.modelling.stru;


import kz.greetgo.sandbox.stand.modelling.TsFileReference;

import static kz.greetgo.util.ServerUtil.notNull;

public class Import {
  public final String className;
  public final TsFileReference tsFileReference;
  public final int lineNo;

  public Import(String className, TsFileReference tsFileReference, int lineNo) {
    this.className = className;
    this.tsFileReference = tsFileReference;
    this.lineNo = lineNo;
  }

  public ClassStructure toClassStru() {
    return notNull(tsFileReference.classStructure);
  }
}
