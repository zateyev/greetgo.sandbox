package kz.greetgo.sandbox.db.test.beans;

import kz.greetgo.depinject.core.Bean;

@Bean
public class DbLoader {
  public void loadTestData() {

    System.out.println("Loading test data....");
  }
}
