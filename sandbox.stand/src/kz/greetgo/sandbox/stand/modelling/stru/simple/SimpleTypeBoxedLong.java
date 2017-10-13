package kz.greetgo.sandbox.stand.modelling.stru.simple;


import kz.greetgo.sandbox.stand.modelling.stru.SimpleType;

public class SimpleTypeBoxedLong extends SimpleType {
  @Override
  public String javaName(boolean boxed) {
    return Long.class.getSimpleName();
  }

  private enum Wrapper {
    ELEMENT;

    private final SimpleTypeBoxedLong instance = new SimpleTypeBoxedLong();
  }

  public static SimpleTypeBoxedLong get() {
    return Wrapper.ELEMENT.instance;
  }

  private SimpleTypeBoxedLong() {
  }

  @Override
  public String toString() {
    return "BOXED_LONG";
  }
}
