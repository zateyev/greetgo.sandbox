package kz.greetgo.sandbox.db.migration_impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import kz.greetgo.sandbox.db.migration_impl.model.Account;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FrsParser {

  private InputStream inputStream;
  private TableWorker tableWorker;

  public FrsParser(TarArchiveInputStream inputStream, TableWorker tableWorker) {
    this.inputStream = inputStream;
    this.tableWorker = tableWorker;
  }

  public int parseAndSave() {

    return 0;
  }

  public static void main(String[] args) throws IOException {
    JsonParser jsonParser = new JsonFactory().createParser(new File(""));
    Account account = new Account();

    parseJSON(jsonParser, account);
  }

  private static void parseJSON(JsonParser jsonParser, Account account) throws IOException {// throws JsonParseException, IOException {

    //loop through the JsonTokens
    while(jsonParser.nextToken() != JsonToken.END_OBJECT){
      String name = jsonParser.getCurrentName();
      if("type".equals(name)){
        jsonParser.nextToken();
        account.type = jsonParser.getText();
      }else if("client_id".equals(name)){
        jsonParser.nextToken();
        account.clientId = jsonParser.getText();
      }else if("account_number".equals(name)){
        jsonParser.nextToken();
        account.accountNumber = jsonParser.getText();
      }else if("registered_at".equals(name)){
        jsonParser.nextToken();
        account.registeredAt = jsonParser.getText();
      }
    }
  }

}