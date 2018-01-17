package kz.greetgo.sandbox.db.register_impl.migration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClientErrorWriter {

  public ClientErrorWriter(FileWriter fileWriter, Connection connection, String tableName) throws Exception {

    String sql = "select cia_id, error from " + tableName + " where error is not null";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {

      try (ResultSet rs = statement.executeQuery()) {

        BufferedWriter bf = new BufferedWriter(fileWriter);

        while (rs.next()) {

          bf.write(createLine(rs.getString(1), rs.getString(2)));
          bf.newLine();

        }
        bf.close();

      }

    }

  }

  private String createLine(String id, String error) {
    return "CIA_ID for client is [ " + id + " ] \t Error is [" + error + "] ";
  }

}
