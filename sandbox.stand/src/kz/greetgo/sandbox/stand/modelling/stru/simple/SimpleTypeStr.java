package kz.greetgo.sandbox.stand.modelling.stru.simple;


import kz.greetgo.sandbox.stand.modelling.stru.SimpleType;

public class SimpleTypeStr extends SimpleType {
  @Override
  public String javaName(boolean boxed) {
    return String.class.getSimpleName();
  }

  private enum Wrapper {
    ELEMENT;

    private final SimpleTypeStr instance = new SimpleTypeStr();
  }

  public static SimpleTypeStr get() {
    return Wrapper.ELEMENT.instance;
  }

  private SimpleTypeStr() {
  }

  @Override
  public String toString() {
    return "STRING";
  }
}
