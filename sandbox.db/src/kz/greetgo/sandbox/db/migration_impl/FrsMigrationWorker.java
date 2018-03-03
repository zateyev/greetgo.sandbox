package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.ClientRecordsToSave;

import java.util.List;

@Bean
public class FrsMigrationWorker extends MigrationWorkerImpl {
  @Override
  protected void dropTmpTables() {

  }

  @Override
  protected void handleErrors() {

  }

  @Override
  protected void uploadAndDropErrors() {

  }

  @Override
  protected void createTmpTables() {

  }

  @Override
  protected long migrateFromTmp() {
    return 0;
  }

  @Override
  protected int download() {
    return 0;
  }

  @Override
  protected void insertIntoTmpTables(List<ClientRecordsToSave> clientRecords) {

  }

  @Override
  protected List<String> renameFiles() {
    return null;
  }

  @Override
  public int migrate() throws Exception {
    return 0;
  }
}
