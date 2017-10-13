package kz.greetgo.sandbox.stand.modelling.stru;

import java.util.ArrayList;
import java.util.List;

public class ClassAttr {
  public final TypeStructure type;
  public final boolean isArray;
  public final String name;
  public final List<String> comment = new ArrayList<>();

  public ClassAttr(TypeStructure type, String name, boolean isArray, List<String> comment) {
    this.type = type;
    this.isArray = isArray;
    this.name = name;
    this.comment.addAll(comment);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ClassAttr{");
    sb.append("name='").append(name).append('\'');
    sb.append(", type=").append(type);
    sb.append('}');
    return sb.toString();
  }
}
