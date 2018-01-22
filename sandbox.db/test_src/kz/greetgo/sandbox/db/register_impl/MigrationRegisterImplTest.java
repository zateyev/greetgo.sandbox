package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.db.register_impl.migration.SshConnector.SSHManager;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;

public class MigrationRegisterImplTest extends ParentTestNg {

  public BeanGetter<MigrationRegister> migrationRegister;
  public BeanGetter<SSHManager> sshManager;

  @Test
  public void doMigrate_TestForFiles() throws Exception {


    FileUtils.deleteDirectory(new File("build/migration"));

    SSHManager sshManager = this.sshManager.get();
    sshManager.renameToDefault();

    //
    //
    //migrationRegister.get().doMigrate();
    //
    //

    File migrationFolder = new File("build/migration");

//    assertThat(migrationFolder.list().length  > 0);

  }

}