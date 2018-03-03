package kz.greetgo.sandbox.db.migration;

import kz.greetgo.sandbox.controller.model.ClientRecordsToSave;

import java.util.List;

public class FrsMigrationWorker extends Worker {
  @Override
  void dropTmpTables() {

  }

  @Override
  void handleErrors() {

  }

  @Override
  void uploadAndDropErrors() {

  }

  @Override
  void createTmpTables() {

  }

  @Override
  long migrateFromTmp() {
    return 0;
  }

  @Override
  int download() {
    return 0;
  }

  @Override
  void insertIntoTmpTables(List<ClientRecordsToSave> clientRecords) {

  }

  @Override
  List<String> renameFiles() {
    return null;
  }
}
