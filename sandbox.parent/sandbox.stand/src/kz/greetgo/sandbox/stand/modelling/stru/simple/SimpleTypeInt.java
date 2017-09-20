package kz.greetgo.sandbox.stand.modelling.stru.simple;


import kz.greetgo.sandbox.stand.modelling.stru.SimpleType;

public class SimpleTypeInt extends SimpleType {
  @Override
  public String javaName(boolean boxed) {
    return boxed ? Integer.class.getSimpleName() : "int";
  }

  private enum Wrapper {
    ELEMENT;

    private final SimpleTypeInt instance = new SimpleTypeInt();
  }

  public static SimpleTypeInt get() {
    return Wrapper.ELEMENT.instance;
  }

  private SimpleTypeInt() {
  }

  @Override
  public String toString() {
    return "INT";
  }
}
