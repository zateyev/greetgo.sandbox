package kz.greetgo.sandbox.stand.modelling.stru;


import kz.greetgo.sandbox.stand.modelling.stru.simple.SimpleTypeBoolean;
import kz.greetgo.sandbox.stand.modelling.stru.simple.SimpleTypeBoxedBoolean;
import kz.greetgo.sandbox.stand.modelling.stru.simple.SimpleTypeBoxedInt;
import kz.greetgo.sandbox.stand.modelling.stru.simple.SimpleTypeBoxedLong;
import kz.greetgo.sandbox.stand.modelling.stru.simple.SimpleTypeInt;
import kz.greetgo.sandbox.stand.modelling.stru.simple.SimpleTypeLong;

public abstract class SimpleType extends TypeStructure {
  public static SimpleType fromStr(String strType, boolean boxed, String place) {
    switch (strType) {
      case "int":
        return boxed ? SimpleTypeBoxedInt.get() : SimpleTypeInt.get();

      case "long":
        return boxed ? SimpleTypeBoxedLong.get() : SimpleTypeLong.get();

      case "boolean":
        return boxed ? SimpleTypeBoxedBoolean.get() : SimpleTypeBoolean.get();

      default:
        throw new IllegalArgumentException("Unknown type " + strType + " at " + place);
    }
  }

  public abstract String javaName(boolean boxed);
}
