package kz.greetgo.sandbox.stand.modelling;


import kz.greetgo.sandbox.stand.modelling.stru.ClassStructure;
import kz.greetgo.sandbox.stand.modelling.stru.SimpleType;
import kz.greetgo.sandbox.stand.modelling.stru.TypeStructure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Imports {

  final Map<String, String> fullNameMap = new HashMap<>();
  private final ClassStructure owner;

  public Imports(ClassStructure owner) {
    this.owner = owner;
  }

  public String name(String fullName) {
    int index = fullName.lastIndexOf('.');
    if (index < 0) return fullName;
    String name = fullName.substring(index + 1);

    if (fullName.equals(owner.fullName())) return name;

    String anotherFullName = fullNameMap.get(name);

    if (anotherFullName == null) {
      fullNameMap.put(name, fullName);
      return name;
    }

    if (fullName.equals(anotherFullName)) return name;

    return fullName;
  }

  private static final String LIST_NAME = List.class.getName();

  public String asStr() {
    return fullNameMap.entrySet().stream()
      .map(e -> "import " + e.getValue() + ";")
      .sorted()
      .collect(Collectors.joining("\n"))
      ;
  }

  public String typeStr(TypeStructure type, boolean isArray) {
    return isArray
      ? name(LIST_NAME) + '<' + name(typeName(type, true)) + '>'
      : name(typeName(type, false));
  }

  private String typeName(TypeStructure type, boolean boxed) {
    if (type instanceof SimpleType) return ((SimpleType) type).javaName(boxed);
    if (type instanceof ClassStructure) return ((ClassStructure) type).fullName();
    throw new IllegalArgumentException("Cannot get name of " + type.getClass());
  }
}
