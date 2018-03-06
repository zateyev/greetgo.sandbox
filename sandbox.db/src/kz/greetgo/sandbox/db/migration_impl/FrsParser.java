package kz.greetgo.sandbox.db.migration_impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import kz.greetgo.sandbox.db.migration_impl.model.Account;
import kz.greetgo.sandbox.db.migration_impl.model.Transaction;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.*;
import java.util.function.Consumer;

public class FrsParser {

  private InputStream inputStream;
  private FrsTableWorker frsTableWorker;

  public FrsParser(TarArchiveInputStream inputStream, FrsTableWorker frsTableWorker) {
    this.inputStream = inputStream;
    this.frsTableWorker = frsTableWorker;
  }

//  public int parseAndSave() {
//
//    return 0;
//  }

  public static void main(String[] args) throws IOException {
    JsonParser jsonParser = new JsonFactory().createParser(
      new File("build/files_to_send/build/out_files/from_frs_2018-02-27-154844-1-30012.json_row.txt"));

    Account account = new Account();

//    parseJSON(jsonParser, account);

    System.out.println("type: " + account.type + "\nclient id: " + account.clientId);

    jsonParser.close();


  }

  private int parseAndSave() throws IOException {
    int recordCount = 0;
    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
    String line;
    while ((line = br.readLine()) != null) {
      Account account = new Account();
      Transaction transaction = new Transaction();

      JsonParser jsonParser = new JsonFactory().createParser(line);
      //loop through the JsonTokens
      while(jsonParser.nextToken() != JsonToken.END_OBJECT){
        String name = jsonParser.getCurrentName();
        String type = null;
        switch (name) {
          case "type":
            jsonParser.nextToken();
            type = jsonParser.getText();
            account.type = type;
            transaction.type = type;
            break;
          case "client_id":
            jsonParser.nextToken();
            account.clientId = jsonParser.getText();
            break;
          case "account_number":
            jsonParser.nextToken();
            account.accountNumber = jsonParser.getText();
            transaction.accountNumber = jsonParser.getText();
            break;
          case "registered_at":
            jsonParser.nextToken();
            account.registeredAt = jsonParser.getText();
            break;
          case "money":
            jsonParser.nextToken();
            transaction.money = jsonParser.getText();
            break;
          case "finished_at":
            jsonParser.nextToken();
            transaction.finishedAt = jsonParser.getText();
            break;
          case "transaction_type":
            jsonParser.nextToken();
            transaction.transactionType = jsonParser.getText();
            break;
        }

        if ("transaction".equals(type)) sendTo(frsTableWorker::addToBatch, transaction);
        else if ("new_account".equals(type)) sendTo(frsTableWorker::addToBatch, account);
      }
      recordCount++;
    }
    return recordCount;
  }

  private void sendTo(Consumer<Account> func, Account account) {
    func.accept(account);
  }

  private void sendTo(Consumer<Transaction> func, Transaction transaction) {
    func.accept(transaction);
  }

}