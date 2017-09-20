package kz.greetgo.sandbox.stand.modelling.stru.simple;


import kz.greetgo.sandbox.stand.modelling.stru.SimpleType;

public class SimpleTypeBoolean extends SimpleType {
  @Override
  public String javaName(boolean boxed) {
    return boxed ? Boolean.class.getSimpleName() : "boolean";
  }

  private enum Wrapper {
    ELEMENT;

    private final SimpleTypeBoolean instance = new SimpleTypeBoolean();
  }

  public static SimpleTypeBoolean get() {
    return Wrapper.ELEMENT.instance;
  }

  private SimpleTypeBoolean() {
  }

  @Override
  public String toString() {
    return "BOOL";
  }
}
