package kz.greetgo.sandbox.db.register_impl.migration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionErrorWriter {

  public TransactionErrorWriter(FileWriter fileWriter, Connection connection, String tableName) throws SQLException, IOException {

    String sql = "select account_number, error from " + tableName + " where error is not null";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {

      try (ResultSet rs = statement.executeQuery()) {

        try(BufferedWriter bf = new BufferedWriter(fileWriter)) {

          while (rs.next()) {

            bf.write(createLine(rs.getString(1), rs.getString(2)));
            bf.newLine();

          }

        }

      }

    }

  }

  private String createLine(String id, String error) {
    return "Account number is [ " + id + " ] \t Error is [" + error + "] ";
  }
}
