package kz.greetgo.sandbox.stand.util;

import kz.greetgo.sandbox.stand.modelling.TsFileReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConvertModelUtil {
  public static List<TsFileReference> deleteIt(TsFileReference file, List<TsFileReference> files) {
    List<TsFileReference> ret = new ArrayList<>();
    for (TsFileReference another : files) {
      if (another != file) ret.add(another);
    }
    return Collections.unmodifiableList(ret);
  }
}
