package kz.greetgo.sandbox.db.register_impl.migration;

import org.xml.sax.Attributes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CiaHandler extends TagHandler implements AutoCloseable {

  private final int maxBatchSize;

  private PreparedStatement clientPS;
  private final Connection connection;

  public CiaHandler(int maxBatchSize, String clientTable, Connection connection) throws SQLException {
    this.maxBatchSize = maxBatchSize;
    this.connection = connection;
    connection.setAutoCommit(false);
    clientPS = connection.prepareStatement(
      "insert into " + clientTable + " (no, id, surname, name) values (?, ?, ?, ?)"
    );
  }

  int batchSize = 0;
  int recordsCount = 0;

  private void eddBatch() throws SQLException {
    clientPS.setLong(1, no);
    clientPS.setString(2, id);
    clientPS.setString(3, surname);
    clientPS.setString(4, name);
    clientPS.addBatch();
    recordsCount++;
    batchSize++;

    if (batchSize >= maxBatchSize) {
      clientPS.executeBatch();
      connection.commit();
      batchSize = 0;
    }
  }

  @Override
  public void close() throws Exception {

    if (batchSize > 0) {
      clientPS.executeBatch();
      connection.commit();
      batchSize = 0;
    }

    clientPS.close();
    connection.setAutoCommit(true);
  }

  String id;
  String surname, name;
  long no = 0;

  @Override
  protected void startTag(Attributes attributes) throws Exception {
    String path = path();

    if ("/cia/client".equals(path)) {
      id = attributes.getValue("id");
      no++;
      surname = name = null;
      return;
    }

    if ("/cia/client/surname".equals(path)) {
      surname = attributes.getValue("value");
      return;
    }

    if ("/cia/client/name".equals(path)) {
      name = attributes.getValue("value");
      return;
    }

  }

  @Override
  protected void endTag() throws Exception {
    String path = path();
    if ("/cia/client".equals(path)) {

      eddBatch();

      return;
    }
  }


}
