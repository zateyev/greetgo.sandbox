//package kz.greetgo.sandbox.db.jdbc;
//
//import kz.greetgo.db.ConnectionCallback;
//import kz.greetgo.sandbox.controller.model.ClientRecordsToSave;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.util.List;
//
//import static kz.greetgo.sandbox.db.util.TimeUtils.recordsPerSecond;
//import static kz.greetgo.sandbox.db.util.TimeUtils.showTime;
//
//public class InsertTmpClient implements ConnectionCallback<Void> {
//  private List<ClientRecordsToSave> clientRecords;
//
//  public InsertTmpClient(List<ClientRecordsToSave> clientRecords) {this.clientRecords = clientRecords;}
//
//  @Override
//  public Void doInConnection(Connection connection) throws Exception {
//    connection.setAutoCommit(false);
//    try (PreparedStatement ps = connection.prepareStatement("INSERT INTO tmp_client " +
//      "(id, surname, name, patronymic, gender, birth_date, charm) " +
//      "VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT (id) " +
//      "DO UPDATE SET surname = ?, name = ?, patronymic = ?, gender = ?, birth_date = ?, charm = ?");
//
//         PreparedStatement charmPS = connection.prepareStatement("INSERT INTO tmp_charm (id, name, description, energy) " +
//           "VALUES (?, ?, ?, ?) ON CONFLICT (id) DO UPDATE SET name = ?, description = ?, energy = ?")) {
//      int batchSize = 0, recordsCount = 0;
//      long startedAt = System.nanoTime();
//      int i = 0;
//      for (ClientRecordsToSave clientRecord : clientRecords) {
//        if (clientRecord.id == null) i++;
//        ps.setString(1, clientRecord.id);
//        ps.setString(2, clientRecord.surname);
//        ps.setString(3, clientRecord.name);
//        ps.setString(4, clientRecord.patronymic);
//        ps.setString(5, clientRecord.gender.toString());
//        ps.setDate(6, java.sql.Date.valueOf(clientRecord.dateOfBirth));
//        ps.setString(7, clientRecord.charm.id);
//
//        ps.setString(8, clientRecord.surname);
//        ps.setString(9, clientRecord.name);
//        ps.setString(10, clientRecord.patronymic);
//        ps.setString(11, clientRecord.gender.toString());
//        ps.setDate(12, java.sql.Date.valueOf(clientRecord.dateOfBirth));
//        ps.setString(13, clientRecord.charm.id);
//
//        charmPS.setString(1, "" + i);
//        charmPS.setString(2, clientRecord.charm.name);
//        charmPS.setString(3, "");
//        charmPS.setDouble(4, i * i);
//        charmPS.setString(5, clientRecord.charm.name);
//        charmPS.setString(6, "");
//        charmPS.setDouble(7, i * i);
//
//        charmPS.executeUpdate();
//        charmPS.addBatch();
//
//        ps.executeUpdate();
//        ps.addBatch();
//        batchSize++;
//        recordsCount++;
//
//        if (batchSize >= downloadMaxBatchSize) {
//          charmPS.executeBatch();
//
//          ps.executeBatch();
//          connection.commit();
//          batchSize = 0;
//        }
//
//        if (showStatus.get()) {
//          showStatus.set(false);
//
//          long now = System.nanoTime();
//          info(" -- downloaded records " + recordsCount + " for " + showTime(now, startedAt)
//            + " : " + recordsPerSecond(recordsCount, now - startedAt));
//        }
//      }
//      System.out.println("Count of null ids: " + i);
//
//      if (batchSize > 0) {
//        charmPS.executeBatch();
//
//        ps.executeBatch();
//        connection.commit();
//      }
//
//      {
//        long now = System.nanoTime();
//        info("TOTAL Downloaded records " + recordsCount + " for " + showTime(now, startedAt)
//          + " : " + recordsPerSecond(recordsCount, now - startedAt));
//      }
//    } finally {
//      connection.setAutoCommit(true);
//      working.set(false);
//      see.interrupt();
//    }
//    return null;
//  }
//}
