package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.db.register_impl.migration.SshConnector.SSHManager;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.apache.commons.io.FileUtils;
import org.fest.assertions.api.Assertions;
import org.testng.annotations.Test;

import java.io.File;

import static org.fest.assertions.api.Assertions.*;

public class MigrationRegisterImplTest extends ParentTestNg {

  public BeanGetter<MigrationRegister> migrationRegister;


  @Test
  public void doMigrate_TestForFiles() throws Exception {

    FileUtils.deleteDirectory(new File("build/migration"));

    SSHManager sshManager = new SSHManager();
    sshManager.renameToDefault();

    //
    //
    migrationRegister.get().doMigrate();
    //
    //

    File migrationFolder = new File("build/migration");

    assertThat(migrationFolder.list().length  > 0);

  }

}