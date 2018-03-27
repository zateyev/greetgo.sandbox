package kz.greetgo.sandbox.db.input_file_generator;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.model.PhoneNumber;
import kz.greetgo.sandbox.db.migration_impl.model.*;
import kz.greetgo.util.RND;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GenerateInputFiles {

  private int ciaLimit;
  private int frsLimit;

  private final int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);

  private Map<String, Client> uniqueGoodClients;
  private Map<String, ClientDetails> uniqueGoodClientDetails;
//  private List<ClientDetails> goodClients;
  private List<Client> generatedClients;
  private List<kz.greetgo.sandbox.db.migration_impl.model.Address> generatedAddresses;
  private List<kz.greetgo.sandbox.db.migration_impl.model.PhoneNumber> generatedPhoneNumbers;
  private Map<String, kz.greetgo.sandbox.db.migration_impl.model.Account> clientAccounts;
  private Map<String, Transaction> accountTransactions;
  private boolean testMode;
  private String outCiaFileName;
  private List<Client> errorClients;
  private String outFrsFileName;
  private List<kz.greetgo.sandbox.db.migration_impl.model.Account> generatedAccounts;
  private List<Transaction> generatedTransactions;

  public GenerateInputFiles(int ciaLimit, int frsLimit) {
    this.ciaLimit = ciaLimit;
    this.frsLimit = frsLimit;

    uniqueGoodClients = new HashMap<>();
    uniqueGoodClientDetails = new HashMap<>();
//    goodClients = new ArrayList<>();
    errorClients = new ArrayList<>();
    clientAccounts = new HashMap<>();
    accountTransactions = new HashMap<>();

    generatedClients = new ArrayList<>();
    generatedAddresses = new ArrayList<>();
    generatedPhoneNumbers = new ArrayList<>();

    generatedAccounts = new ArrayList<>();
    generatedTransactions = new ArrayList<>();
  }

  public static void main(String[] args) throws Exception {
    GenerateInputFiles generateInputFiles = new GenerateInputFiles(50, 50);
    generateInputFiles.setTestMode();
    generateInputFiles.execute();
    System.out.println("Out file name " + generateInputFiles.outFrsFileName);
  }

  private static final String ENG = "abcdefghijklmnopqrstuvwxyz";
  private static final String DEG = "0123456789";

  private static final char[] ALL = (ENG + ENG.toUpperCase() + DEG).toCharArray();
  private static final char[] BIG = (ENG.toUpperCase() + DEG).toCharArray();

  private static final Random random = new Random();

  public Set<String> getGoodClientIds() {
    return info.goodClientIds;
  }

  public Map<String, Client> getUniqueGoodClients() {
    return uniqueGoodClients;
  }

  public Map<String, ClientDetails> getUniqueGoodClientDetails() {
    return uniqueGoodClientDetails;
  }

  public long getGoodClientCount() {
    return info.goodClientIds.size();
  }

  public long getTransactionCount() {
    return info.transactionCount;
  }

  public long getAccountCount() {
    return info.accountCount;
  }

  public int getErrorRecordCount() {
    return info.clientErrorRecordCount;
  }

  public void setTestMode() {
    if (this.ciaLimit > 100) this.ciaLimit = 100;
    if (this.frsLimit > 100) this.frsLimit = 100;
    this.testMode = true;
  }

  public Map<String, kz.greetgo.sandbox.db.migration_impl.model.Account> getClientAccounts() {
    return clientAccounts;
  }

  public Map<String, Transaction> getAccountTransactions() {
    return accountTransactions;
  }

//  public List<ClientDetails> getGoodClients() {
//    return goodClients;
//  }

  public List<Client> getGeneratedClients() {
    return generatedClients;
  }

  public List<kz.greetgo.sandbox.db.migration_impl.model.Address> getGeneratedAddresses() {
    return generatedAddresses;
  }

  public List<kz.greetgo.sandbox.db.migration_impl.model.PhoneNumber> getGeneratedPhoneNumbers() {
    return generatedPhoneNumbers;
  }

  public String getOutCiaFileName() {
    return outCiaFileName;
  }

  public List<Client> getErrorClients() {
    return errorClients;
  }

  public String getOutFrsFileName() {
    return outFrsFileName;
  }

  public List<kz.greetgo.sandbox.db.migration_impl.model.Account> getGeneratedAccounts() {
    return generatedAccounts;
  }

  public List<Transaction> getGeneratedTransactions() {
    return generatedTransactions;
  }

  private static class Info {

    int transactionCount = 0;
    int accountCount = 0;
    int clientErrorRecordCount = 0;

    final Set<String> goodClientIds = new HashSet<>();

    public void newErrorClient() {
      clientErrorRecordCount++;
    }

    public void appendGoodClientId(String clientId) {
      goodClientIds.add(clientId);
    }

    public void newAccount() {
      accountCount++;
    }

    public void newTransaction() {
      transactionCount++;
    }

    public void printTo(PrintStream pr) {
      pr.println("Unique good client count = " + goodClientIds.size());
      pr.println("Client error record count = " + clientErrorRecordCount);
      pr.println("Transaction count = " + transactionCount);
      pr.println("Account count = " + accountCount);
    }
  }

  final Info info = new Info();

  @SuppressWarnings("SameParameterValue")
  private static String rndStr(int len) {
    final int allLength = ALL.length;
    final char ret[] = new char[len];
    for (int i = 0; i < len; i++) {
      ret[i] = ALL[random.nextInt(allLength)];
    }
    return String.valueOf(ret);
  }

  private static String rndClientId() {
    char cc[] = new char[8];
    cc[0] = DEG.charAt(random.nextInt(DEG.length()));
    for (int i = 1; i < cc.length; i++) {
      cc[i] = BIG[random.nextInt(BIG.length)];
    }

    return String.valueOf(cc, 0, 1) + '-' +
      String.valueOf(cc, 1, 3) + '-' +
      String.valueOf(cc, 4, 2) + '-' +
      String.valueOf(cc, 6, 2) + '-' +
      rndStr(10);
  }

  private static String rndAccountNumber() {
    return RND.intStr(5) + "KZ" + RND.intStr(3) + "-" + RND.intStr(5) + "-" + RND.intStr(5) + "-" + RND.intStr(7);
  }

  public static String formatBD(BigDecimal a) {
    if (a == null) return "";
    DecimalFormat df = new DecimalFormat();
    df.setMaximumFractionDigits(2);

    df.setMinimumFractionDigits(0);

    df.setGroupingUsed(true);
    DecimalFormatSymbols dfs = new DecimalFormatSymbols();
    dfs.setGroupingSeparator('_');
    dfs.setDecimalSeparator('.');
    df.setDecimalFormatSymbols(dfs);
    String ret = df.format(a.setScale(2, RoundingMode.HALF_UP));
    if (ret.startsWith("-")) return ret;
    return "+" + ret;
  }

  enum ErrorType {
    NO_SURNAME(1),
    EMPTY_SURNAME(1),
    NO_NAME(1),
    EMPTY_NAME(1),
    NO_CHARM(1),

    BIRTH_DATE_TOO_OLD(1),
    BIRTH_DATE_TOO_YOUNG(1),
    BIRTH_DATE_NO(1),
    BIRTH_DATE_LEFT(1),

    //
    ;

    public final float priority;

    ErrorType(float priority) {
      this.priority = priority;
    }

    public float getPriority() {
      return priority;
    }
  }

  enum RowType {
    NEW_FOR_PAIR(100),
    EXISTS(100),
    @SuppressWarnings("unused")
    NEW_ALONE(10),
    ERROR(10),

    //
    ;
    final float priority;

    RowType(float priority) {
      this.priority = priority;
    }

    public float getPriority() {
      return priority;
    }
  }

  final List<String> commonClientIdList = new ArrayList<>();

  private static class SelectorRnd<T> {
    private Object[] values;

    public SelectorRnd(T[] values, Function<T, Float> priority) {

      int len = values.length * 10;
      this.values = new Object[len];
      float total = 0;
      for (T value : values) {
        total += priority.apply(value);
      }

      outer_for:
      for (int i = 0; i < len; i++) {

        float I = (float) i / (float) len * total;

        float current = 0;
        for (T value : values) {
          float p = priority.apply(value);
          if (current + p > I) {
            this.values[i] = value;
            continue outer_for;
          }
          current += p;
        }

        throw new RuntimeException("Left error");
      }

    }

    public T next() {
      //noinspection unchecked
      return (T) values[random.nextInt(values.length)];
    }

    public void showInfo() {
      System.out.println("Selector rnd: values.length = " + values.length);
      int i = 0;
      for (Object value : values) {
        System.out.println("  " + i++ + " : " + value);
      }
    }
  }

  final SelectorRnd<RowType> rowTypeRnd = new SelectorRnd<>(RowType.values(), RowType::getPriority);
  final SelectorRnd<ErrorType> errorTypeRnd = new SelectorRnd<>(ErrorType.values(), ErrorType::getPriority);

  private static String spaces(int count) {
    char ret[] = new char[count];
    for (int i = 0; i < count; i++) ret[i] = ' ';
    return String.valueOf(ret);
  }

  static final String DIR = "build/out_files";

  final AtomicBoolean clearPrinter = new AtomicBoolean(false);
  PrintStream printer = null;
  String outFileName;
  File outFile;

  public void execute() throws Exception {
    File file = new File(DIR);
    file.mkdirs();
    FileUtils.cleanDirectory(file);

    rowTypeRnd.showInfo();
    errorTypeRnd.showInfo();

    final File workingFile = new File(DIR + "/__working__");
    final File newCiaFile = new File(DIR + "/__new_cia_file__");
    final File newFrsFile = new File(DIR + "/__new_frs_file__");

    workingFile.getParentFile().mkdirs();
    workingFile.createNewFile();

    newCiaFile.getParentFile().mkdirs();
    newCiaFile.createNewFile();

    final AtomicBoolean working = new AtomicBoolean(true);
    final AtomicBoolean showInfo = new AtomicBoolean(false);

    new Thread(() -> {

      while (workingFile.exists() && working.get()) {

        try {
          Thread.sleep(300);
        } catch (InterruptedException e) {
          break;
        }

        if (!newCiaFile.exists()) {
          clearPrinter.set(true);

          newCiaFile.getParentFile().mkdirs();
          try {
            newCiaFile.createNewFile();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      }

      working.set(false);

    }).start();

    new Thread(() -> {

      while (working.get()) {

        try {
          Thread.sleep(700);
        } catch (InterruptedException e) {
          break;
        }

        showInfo.set(true);
      }

    }).start();

    {
      int i = 1;
      for (RowType rowType : RowType.values()) {
        printClient(i++, rowType);
      }

      currentFileRecords = 0;

      int fileIndex = 1;
      for (; currentFileRecords < ciaLimit && working.get(); i++) {

        printClient(i, rowTypeRnd.next());
        currentFileRecords++;

        if (fileIndex == 1 && currentFileRecords >= 300) {
          fileIndex++;
          clearPrinter.set(true);
        }
        if (fileIndex == 2 && currentFileRecords >= 3000) {
          fileIndex++;
          clearPrinter.set(true);
        }
        if (fileIndex == 3 && currentFileRecords >= 30_000) {
          fileIndex++;
          clearPrinter.set(true);
        }
        if (fileIndex == 4 && currentFileRecords >= 300_000) {
          fileIndex++;
          clearPrinter.set(true);
        }

        if (showInfo.get()) {
          showInfo.set(false);
          System.out.println("Сформировано записей в текущем файле CIA: "
            + currentFileRecords + ", всего записей: " + i);
        }
      }

      System.out.println("ИТОГО: Сформировано записей в текущем файле CIA: "
        + currentFileRecords + ", всего записей: " + i);
    }

    finishPrinter("</cia>", ".xml");
    outCiaFileName = outFileName + '-' + currentFileRecords + ".xml";

    System.out.println("Файлы CIA сформированы: приступаем к формированию файлов FRS...");

    {
      newFrsFile.getParentFile().mkdirs();
      newFrsFile.createNewFile();

      currentFileRecords = 0;

      int i = 1, fileIndex = 1;
      for (; currentFileRecords < frsLimit && working.get(); i++) {

        printAccountWithTransactions(i);
        currentFileRecords++;

        if (fileIndex == 1 && currentFileRecords >= 30_000) {
          fileIndex++;
          clearPrinter.set(true);
        }
        if (fileIndex == 2 && currentFileRecords >= 700_000) {
          fileIndex++;
          clearPrinter.set(true);
        }

        if (showInfo.get()) {
          showInfo.set(false);
          System.out.println("Сформировано записей в текущем файле FRS: "
            + currentFileRecords + ", всего записей: " + i);
        }
      }

      System.out.println("ИТОГО: Сформировано записей в текущем файле FRS: "
        + currentFileRecords + ", всего записей: " + i);
    }

    finishPrinter(null, ".json_row.txt");
    outFrsFileName = outFileName + '-' + currentFileRecords + ".json_row.txt";
//    outFrsFileName = outFile.getName();

    working.set(false);

    if (!testMode) archive();

    workingFile.delete();
    newCiaFile.delete();
    newFrsFile.delete();

    saveInfoFile();
  }

  private void saveInfoFile() throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
    Date now = new Date();

    File file = new File(DIR + "/info_" + sdf.format(now) + ".txt");
    file.getParentFile().mkdirs();

    try (PrintStream pr = new PrintStream(file, "UTF-8")) {
      info.printTo(pr);
    }
  }

  int currentFileRecords = 0;


  @SuppressWarnings("unused")
  enum PhoneType {
    homePhone, mobilePhone, workPhone,
  }

  static final char DEG_CHARS[] = DEG.toCharArray();
  static final int DEG_CHARS_LENGTH = DEG_CHARS.length;

  static class Phone {
    String number;

    static Phone next() {

      StringBuilder sb = new StringBuilder();
      sb.append("+7-");

      sb.append(DEG_CHARS[random.nextInt(DEG_CHARS_LENGTH)]);
      sb.append(DEG_CHARS[random.nextInt(DEG_CHARS_LENGTH)]);
      sb.append(DEG_CHARS[random.nextInt(DEG_CHARS_LENGTH)]);
      sb.append('-');
      sb.append(DEG_CHARS[random.nextInt(DEG_CHARS_LENGTH)]);
      sb.append(DEG_CHARS[random.nextInt(DEG_CHARS_LENGTH)]);
      sb.append(DEG_CHARS[random.nextInt(DEG_CHARS_LENGTH)]);
      sb.append('-');
      sb.append(DEG_CHARS[random.nextInt(DEG_CHARS_LENGTH)]);
      sb.append(DEG_CHARS[random.nextInt(DEG_CHARS_LENGTH)]);
      sb.append('-');
      sb.append(DEG_CHARS[random.nextInt(DEG_CHARS_LENGTH)]);
      sb.append(DEG_CHARS[random.nextInt(DEG_CHARS_LENGTH)]);

      if (random.nextInt(5) == 0) {
        sb.append(" вн. ").append(RND.intStr(4));
      }

      {
        Phone ret = new Phone();
        ret.number = sb.toString();
        return ret;
      }
    }

    String tag(PhoneType type, ClientDetails goodClient) {
      if (type == null || number == null) return null;
      PhoneNumber phoneNumber = new PhoneNumber();
      switch (type) {
        case homePhone:
          phoneNumber.phoneType = kz.greetgo.sandbox.controller.model.PhoneType.HOME;
          break;
        case workPhone:
          phoneNumber.phoneType = kz.greetgo.sandbox.controller.model.PhoneType.WORK;
          break;
        case mobilePhone:
          phoneNumber.phoneType = kz.greetgo.sandbox.controller.model.PhoneType.MOBILE;
      }
      phoneNumber.number = number;
      goodClient.phoneNumbers.add(phoneNumber);
      return "<" + type.name() + ">" + number + "</" + type.name() + ">";
    }
  }

  static class Address {
    String street, house, flat;

    static Address next() {
      Address ret = new Address();
      ret.street = RND.str(20);
      ret.house = RND.str(2);
      ret.flat = RND.str(2);
      return ret;
    }

    String toTag(String tagName, ClientDetails goodClient) {
      if ("fact".equals(tagName)) {
        goodClient.addressF.type = AddressType.FACT;
        goodClient.addressF.street = street;
        goodClient.addressF.house = house;
        goodClient.addressF.flat = flat;
      } else {
        goodClient.addressR.type = AddressType.REG;
        goodClient.addressR.street = street;
        goodClient.addressR.house = house;
        goodClient.addressR.flat = flat;
      }
      return "<" + tagName + " street=\"" + street + "\" house=\"" + house + "\" flat=\"" + flat + "\"/>";
    }
  }

  private void finishPrinter(String lastLine, String extension) {
    PrintStream pr = printer;
    if (pr != null) {
      if (lastLine != null) pr.println(lastLine);
      pr.close();
      printer = null;

      File newCiaFile = new File(outFileName + '-' + currentFileRecords + extension);
      outFile.renameTo(newCiaFile);
    }
  }

  private void printClient(int clientIndex, RowType rowType) throws Exception {

    List<String> tags = new ArrayList<>();
    ClientDetails goodClient = new ClientDetails();
    goodClient.addressF = new kz.greetgo.sandbox.controller.model.Address();
    goodClient.addressR = new kz.greetgo.sandbox.controller.model.Address();
    goodClient.charm = new Charm();
    goodClient.phoneNumbers = new ArrayList<>();

    Client client = new Client();

    ErrorType errorType = null;

    if (rowType == RowType.ERROR) errorType = errorTypeRnd.next();

    if (errorType != ErrorType.NO_SURNAME) {

      if (errorType == ErrorType.EMPTY_SURNAME) {
        client.surname = "";
        tags.add("    <surname value=\"\"/>");
      } else {
        goodClient.surname = nextSurname();
        client.surname = goodClient.surname;
        tags.add("    <surname value=\"" + goodClient.surname + "\"/>");
      }

    }

    if (errorType != ErrorType.NO_NAME) {

      if (errorType == ErrorType.EMPTY_NAME) {
        client.name = "";
        tags.add("    <name value=\"\"/>");
      } else {
        goodClient.name = nextName();
        client.name = goodClient.name;
        tags.add("    <name value=\"" + goodClient.name + "\"/>");
      }

    }

    if (errorType != ErrorType.BIRTH_DATE_NO) {

      if (errorType == ErrorType.BIRTH_DATE_LEFT) {

        tags.add("    <birth value=\"" + RND.str(10) + "\"/>");

      } else {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;

        if (errorType == ErrorType.BIRTH_DATE_TOO_OLD) {
          date = RND.dateYears(-CURRENT_YEAR + 1, -200);
        } else if (errorType == ErrorType.BIRTH_DATE_TOO_YOUNG) {
          date = RND.dateYears(-10, 0);
        }

        if (date == null) {
          date = RND.dateYears(-100, -18);
          goodClient.dateOfBirth = sdf.format(date);
          client.birth_date = date;
        } else {
          client.birth_date = date;
        }



        tags.add("    <birth value=\"" + sdf.format(date) + "\"/>");

      }

    }

    switch (random.nextInt(5)) {
      case 1:
        break;

      case 2:
        goodClient.patronymic = spaces(random.nextInt(3));
        client.patronymic = goodClient.patronymic;
        tags.add("    <patronymic value=\"" + goodClient.patronymic + "\"/>");
        break;

      default:
        goodClient.patronymic = nextPatronymic();
        client.patronymic = goodClient.patronymic;
        tags.add("    <patronymic value=\"" + goodClient.patronymic + "\"/>");
        break;
    }

    tags.add("    <address>\n" +
      "      " + Address.next().toTag("fact", goodClient) + "\n" +
      "      " + Address.next().toTag("register", goodClient) + "\n" +
      "    </address>"
    );

    if (testMode) {
      kz.greetgo.sandbox.db.migration_impl.model.Address address = new kz.greetgo.sandbox.db.migration_impl.model.Address();
      address.type = goodClient.addressF.type.toString();
      address.street = goodClient.addressF.street;
      address.house = goodClient.addressF.house;
      address.flat = goodClient.addressF.flat;
      generatedAddresses.add(address);

      address = new kz.greetgo.sandbox.db.migration_impl.model.Address();
      address.type = goodClient.addressR.type.toString();
      address.street = goodClient.addressR.street;
      address.house = goodClient.addressR.house;
      address.flat = goodClient.addressR.flat;
      generatedAddresses.add(address);
    }

    if (errorType != ErrorType.NO_CHARM) {
      goodClient.charm.name = nextCharm();
      client.charm_name = goodClient.charm.name;
      tags.add("    <charm value=\"" + goodClient.charm.name + "\"/>");
    }

    goodClient.gender = Gender.valueOf(random.nextBoolean() ? "MALE" : "FEMALE");
    client.gender = goodClient.gender.toString();
    tags.add("    <gender value=\"" + goodClient.gender.toString() + "\"/>");

    {
      int phoneCount = 2 + random.nextInt(5);
      for (int i = 0; i < phoneCount; i++) {
        tags.add("    " + Phone.next().tag(PhoneType.values()[random.nextInt(PhoneType.values().length)], goodClient));
      }

      if (testMode) addToGeneratedPhoneNumbers(goodClient.phoneNumbers);
    }

    {
      PrintStream pr = printer;

      if (pr == null || clearPrinter.get()) {
        clearPrinter.set(false);

        finishPrinter("</cia>", ".xml");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        Date now = new Date();

        outFileName = DIR + "/from_cia_" + sdf.format(now) + "-" + ciaFileNo++;
        outFile = new File(outFileName + ".xml");
        outFile.getParentFile().mkdirs();

        pr = new PrintStream(outFile, "UTF-8");
        printer = pr;
        pr.println("<cia>");
        currentFileRecords = 0;
      }

      Collections.shuffle(tags);

      String clientId = rndClientId();
      if (rowType == RowType.EXISTS && commonClientIdList.size() > 0) {
        clientId = commonClientIdList.get(random.nextInt(commonClientIdList.size()));
      }

      if (rowType == RowType.NEW_FOR_PAIR) {
        commonClientIdList.add(clientId);
      }

      if (rowType == RowType.ERROR) {
        if (testMode) {
          client.hasError = true;
          errorClients.add(client);
        }
        info.newErrorClient();
      } else {
        if (testMode) {
//          goodClients.add(goodClient);
//          if (info.goodClientIds.contains(clientId))
          uniqueGoodClients.put(clientId, client);
          uniqueGoodClientDetails.put(clientId, goodClient);
        }
        info.appendGoodClientId(clientId);
      }

      if (testMode) {
        client.cia_id = clientId;
        generatedClients.add(client);
      }

      pr.println("  <client id=\"" + clientId + "\"> <!-- " + clientIndex + " -->");
      tags.forEach(pr::println);
      pr.println("  </client>");
    }
  }

  private void addToGeneratedPhoneNumbers(List<PhoneNumber> phoneNumbers) {
    for (int i = 0; i < phoneNumbers.size(); i++) {
      kz.greetgo.sandbox.db.migration_impl.model.PhoneNumber pn = new kz.greetgo.sandbox.db.migration_impl.model.PhoneNumber();
      pn.type = kz.greetgo.sandbox.db.migration_impl.model.PhoneType.valueOf(phoneNumbers.get(i).phoneType.toString());
      pn.phone_number = phoneNumbers.get(i).number;
      generatedPhoneNumbers.add(pn);
    }
  }

  int ciaFileNo = 1;
  int frsFileNo = 1;

  final AtomicReference<List<String>> charmList = new AtomicReference<>(Collections.emptyList());

  {
    moreCharms(89);
  }

  @SuppressWarnings("SameParameterValue")
  void moreCharms(int count) {

    List<String> list = new ArrayList<>(charmList.get());

    for (int i = 0; i < count; i++) {
      list.add(RND.str(10));
    }

    charmList.set(Collections.unmodifiableList(list));
  }

  private String nextCharm() {
    List<String> list = charmList.get();
    return list.get(random.nextInt(list.size()));
  }

  private String nextPatronymic() {
    return RND.str(13);
  }

  private String nextName() {
    return RND.str(10);
  }

  private String nextSurname() {
    return RND.str(10);
  }

  private static List<String> addingTransactionType = new ArrayList<>();
  private static List<String> subtractingTransactionType = new ArrayList<>();

  static {
    addingTransactionType.add("Списывание с федерального бюджета");
    addingTransactionType.add("Списывание с регионального бюджета Алматинской области");
    addingTransactionType.add("Списывание с регионального бюджета г.Алматы");
    addingTransactionType.add("Списывание с регионального бюджета Карагандинского области");
    addingTransactionType.add("Списывание с регионального бюджета г.Караганда");
    subtractingTransactionType.add("Перевод в офшоры");
    subtractingTransactionType.add("Перевод на подставной счёт");
    subtractingTransactionType.add("Отмывание на ботинках");
    subtractingTransactionType.add("Отмывание на компьютерной технике");
  }

  private static String nextTransactionType(boolean adding) {
    List<String> list = adding ? addingTransactionType : subtractingTransactionType;
    return list.get(random.nextInt(list.size()));
  }


  static GregorianCalendar registeredAtCal = null;

  static GregorianCalendar finishedAtCal = null;

  static class Account {
    String clientId, number;
    Date registeredAt;

    final List<Transaction> transactionList = new ArrayList<>();

    double ceil;

    class Transaction {
      Date finishedAt;
      String type;
      BigDecimal money;

      String toJson(kz.greetgo.sandbox.db.migration_impl.model.Transaction transaction) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        List<String> pairs = new ArrayList<>();
        pairs.add("'type':'transaction'");
        pairs.add("'money':'" + formatBD(money) + "'");
        pairs.add("'finished_at':'" + sdf.format(finishedAt) + "'");
        pairs.add("'transaction_type':'" + type + "'");
        pairs.add("'account_number':'" + number + "'");

        transaction.money = money;
        transaction.account_number = number;
        transaction.transaction_type = type;
        transaction.finishedAt = sdf.format(finishedAt);

        Collections.shuffle(pairs);

        return pairs.stream().collect(Collectors.joining(",", "{", "}")).replace('\'', '"');
      }

      public void normMoney() {
        money = money.setScale(2, RoundingMode.HALF_UP);
      }
    }

    static Account next(String clientId) {
      if (registeredAtCal == null) {
        registeredAtCal = new GregorianCalendar();
        registeredAtCal.add(Calendar.YEAR, -17);
      }

      registeredAtCal.add(Calendar.SECOND, +2);

      {
        Account ret = new Account();
        ret.registeredAt = registeredAtCal.getTime();
        ret.clientId = clientId;
        ret.number = rndAccountNumber();

        switch (random.nextInt(12)) {
          case 0:
            ret.ceil = 1000;
            break;
          case 1:
          case 2:
          case 3:
          case 4:
            ret.ceil = 10000;
            break;
          case 5:
          case 6:
          case 7:
            ret.ceil = 100000;
            break;
          case 8:
          case 9:
          case 10:
            ret.ceil = 1000000;
            break;
          case 11:
            ret.ceil = 10000000;
            break;
        }

        return ret;
      }
    }

    String toJson() {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

      List<String> pairs = new ArrayList<>();
      pairs.add("'type':'new_account'");
      pairs.add("'client_id':'" + clientId + "'");
      pairs.add("'account_number':'" + number + "'");
      pairs.add("'registered_at':'" + sdf.format(registeredAt) + "'");

      Collections.shuffle(pairs);

      return pairs.stream().collect(Collectors.joining(",", "{", "}")).replace('\'', '"');
    }

    BigDecimal rest() {
      BigDecimal ret = BigDecimal.ZERO;
      for (Transaction transaction : transactionList) {
        ret = ret.add(transaction.money);
      }
      return ret;
    }

    public Transaction addTransaction(int rowIndex, boolean last) {
      Transaction ret = new Transaction();

      if (finishedAtCal == null) {
        finishedAtCal = new GregorianCalendar();
        finishedAtCal.add(Calendar.YEAR, -7);
      }

      finishedAtCal.add(Calendar.SECOND, +1 + (rowIndex % 2));

      ret.finishedAt = finishedAtCal.getTime();

      BigDecimal rest = rest();

      if (last) {

        ret.money = new BigDecimal(random.nextInt(10) * 1000).subtract(rest);
        ret.normMoney();

      } else {

        switch (random.nextInt(3)) {
          case 0:
            if (rest.compareTo(BigDecimal.ZERO) > 0) {
              ret.money = rest.negate();
              ret.type = nextTransactionType(false);
              break;
            } //else goto case 1
          case 1:
            ret.money = new BigDecimal(123.45 + random.nextDouble() * ceil);
            ret.type = nextTransactionType(true);
            ret.normMoney();
            break;
          case 2:
            ret.money = new BigDecimal(random.nextDouble() * rest.doubleValue()).negate();
            ret.type = nextTransactionType(false);
            ret.normMoney();
            break;
        }

      }

      transactionList.add(ret);
      return ret;
    }

  }

  private void printAccountWithTransactions(int rowIndex) throws Exception {
    List<String> records = new ArrayList<>();

    String clientId = commonClientIdList.get(random.nextInt(commonClientIdList.size()));

    Account account = Account.next(clientId);

    records.add(account.toJson());
    info.newAccount();

    kz.greetgo.sandbox.db.migration_impl.model.Account newAccount = new kz.greetgo.sandbox.db.migration_impl.model.Account();
    newAccount.account_number = account.number;
//    newAccount.registeredAt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(account.registeredAt);
    newAccount.registeredAtD = account.registeredAt;
    if (testMode) {
      clientAccounts.put(clientId, newAccount);
      generatedAccounts.add(newAccount);
    }


    Transaction transaction;

    int count = 2 + random.nextInt(10);
    for (int i = 0; i < count; i++) {
      transaction = new Transaction();
      records.add(account.addTransaction(rowIndex, false).toJson(transaction));
      info.newTransaction();
      if (testMode) {
        accountTransactions.put(clientId, transaction);
        generatedTransactions.add(transaction);
      }
    }
    transaction = new Transaction();
    records.add(account.addTransaction(rowIndex, true).toJson(transaction));
    info.newTransaction();
    if (testMode) {
      accountTransactions.put(clientId, transaction);
      generatedTransactions.add(transaction);
    }

    Collections.shuffle(records);

    {
      PrintStream pr = printer;

      if (pr == null || clearPrinter.get()) {
        clearPrinter.set(false);

        finishPrinter(null, ".json_row.txt");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        Date now = new Date();

        outFileName = DIR + "/from_frs_" + sdf.format(now) + "-" + frsFileNo++;
        outFile = new File(outFileName + ".json_row.txt");
        outFile.getParentFile().mkdirs();

        pr = new PrintStream(outFile, "UTF-8");
        printer = pr;
        currentFileRecords = 0;
      }

      records.forEach(pr::println);
      currentFileRecords += records.size();
    }
  }

  private void archive() throws Exception {
    File[] files = new File(DIR).listFiles(file ->
      file.getName().startsWith("from")
        && !file.getName().endsWith(".bz2"));

    if (files == null) return;

    ConcurrentLinkedQueue<File> fileQueue = new ConcurrentLinkedQueue<>();
    Collections.addAll(fileQueue, files);

    List<Thread> threadList = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      threadList.add(new Thread(() -> archiveQueue(fileQueue)));
    }

    threadList.forEach(Thread::start);
    for (Thread thread : threadList) {
      thread.join();
    }
  }

  private void archiveQueue(ConcurrentLinkedQueue<File> fileQueue) {
    try {
      archiveQueueEx(fileQueue);
    } catch (Exception e) {
      if (e instanceof RuntimeException) throw (RuntimeException) e;
      throw new RuntimeException(e);
    }
  }

  private void archiveQueueEx(ConcurrentLinkedQueue<File> fileQueue) throws Exception {
    while (true) {
      File file = fileQueue.poll();
      if (file == null) return;
      archiveFile(file);
    }
  }

  private void archiveFile(File file) throws Exception {
    File dest = new File(file.getPath() + ".tar.bz2");
    System.out.println("Start archiving file " + file.getName()
      + " with size " + file.length() + " -> " + dest.getName());

    ProcessBuilder builder = new ProcessBuilder();
    builder.command("tar", "-cvjSf", dest.getPath(), file.getPath());
    builder.inheritIO();
    Process process = builder.start();
    int exitStatus = process.waitFor();
    if (exitStatus != 0) throw new RuntimeException("Error archiving file " + file + " with exit status " + exitStatus);

    file.delete();
  }
}
