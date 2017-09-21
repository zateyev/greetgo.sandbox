package kz.greetgo.sandbox.db._develop_;

import kz.greetgo.sandbox.db.test.util.TestsBeanContainer;
import kz.greetgo.sandbox.db.test.util.TestsBeanContainerCreator;

public class LoadTestDataIntoDb {
  public static void main(String[] args) {
    new LoadTestDataIntoDb().run();
  }

  private void run() {
    TestsBeanContainer bc = TestsBeanContainerCreator.create();

    bc.dbLoader().loadTestData();
  }
}
