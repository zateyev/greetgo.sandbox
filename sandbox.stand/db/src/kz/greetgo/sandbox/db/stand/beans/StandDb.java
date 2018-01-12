package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.PersonDot;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Bean
public class StandDb implements HasAfterInject {
  public final Map<Integer, List<String>> charmStorage = new HashMap<>();
  public final Map<String, PersonDot> personStorage = new HashMap<>();
  public final Map<Long, ClientDot> clientStorage = new HashMap<>();

  private final Random random = new Random();

  @Override
  public void afterInject() throws Exception {
    this.prepareInitData();
    this.parsePersons();
    this.parseClients();
  }

  private void prepareInitData() {
    charmStorage.put(CharmType.CALM.ordinal(), Arrays.asList("Неизвестно", "Спокойный", "Спокойная"));
    charmStorage.put(CharmType.CONSERVATIVE.ordinal(), Arrays.asList("Неизвестно", "Консервативный", "Консервативная"));
    charmStorage.put(CharmType.CONSCIOUS.ordinal(), Arrays.asList("Неизвестно", "Понимающий", "Понимающая"));
    charmStorage.put(CharmType.OPEN.ordinal(), Arrays.asList("Неизвестно", "Открытый", "Открытая"));
    charmStorage.put(CharmType.MYSTERIOUS.ordinal(), Arrays.asList("Неизвестно", "Загадочный", "Загадочная"));
    charmStorage.put(CharmType.WILD.ordinal(), Arrays.asList("Неизвестно", "Буйный", "Буйная"));


  }

  private void parsePersons() throws Exception {
    try (BufferedReader br = new BufferedReader(
      new InputStreamReader(getClass().getResourceAsStream("StandDbInitData.txt"), "UTF-8"))) {

      int lineNo = 0;

      while (true) {
        String line = br.readLine();
        if (line == null) break;
        lineNo++;
        String trimmedLine = line.trim();
        if (trimmedLine.length() == 0) continue;
        if (trimmedLine.startsWith("#")) continue;

        String[] splitLine = line.split(";");

        String command = splitLine[0].trim();
        switch (command) {
          case "PERSON":
            appendPerson(splitLine, line, lineNo);
            break;

          default:
            throw new RuntimeException("Unknown command " + command);
        }
      }
    }
  }

  @SuppressWarnings("unused")
  private void appendPerson(String[] splitLine, String line, int lineNo) {
    PersonDot p = new PersonDot();
    p.id = splitLine[1].trim();
    String[] ap = splitLine[2].trim().split("\\s+");
    String[] fio = splitLine[3].trim().split("\\s+");
    p.accountName = ap[0];
    p.password = ap[1];
    p.surname = fio[0];
    p.name = fio[1];
    if (fio.length > 2) p.patronymic = fio[2];
    personStorage.put(p.id, p);
  }

  private void parseClients() throws Exception {
    try (BufferedReader br = new BufferedReader(
      new InputStreamReader(getClass().getResourceAsStream("StandDbClientData.txt"), "UTF-8"))) {

      int lineNo = 0;

      while (true) {
        String line = br.readLine();
        if (line == null) break;
        lineNo++;
        String trimmedLine = line.trim();
        if (trimmedLine.length() == 0) continue;
        if (trimmedLine.startsWith("#")) continue;

        String[] splitLine = line.split(";");

        String command = splitLine[0].trim();
        switch (command) {
          case "CLIENT":
            appendClient(splitLine, line, lineNo);
            break;

          default:
            throw new RuntimeException("Unknown command " + command);
        }
      }
    }
  }

  @SuppressWarnings("unused")
  private void appendClient(String[] splitLine, String line, int lineNo) {
    ClientDot c = new ClientDot();
    c.id = lineNo - 1;
    c.surname = splitLine[1].trim();
    c.lastname = splitLine[2].trim();
    c.patronymic = splitLine[3].trim();
    c.gender = toGenderType(Integer.parseInt(splitLine[4].trim()));
    c.birthDate = this.generateDate();
    c.charm = this.generateCharmType();
    c.residentialAddressInfo = this.generateResidentialAddress();
    c.registrationAddressInfo = this.generateRegistrationAddressInfo();
    c.phoneInfo = this.generatePhoneInfo();

    c.age = this.random.nextInt(40) + 18;
    c.totalAccountBalance = this.random.nextInt();
    c.maxAccountBalance = this.random.nextInt();
    c.minAccountBalance = this.random.nextInt();

    clientStorage.put(c.id, c);
  }

  private GenderType generateGenderType() {
    return toGenderType(this.random.nextInt(GenderType.values().length));
  }

  // https://stackoverflow.com/a/3985467
  private String generateDate() {
    long time = -946771200000L + (Math.abs(this.random.nextLong()) % (70L * 365 * 24 * 60 * 60 * 1000));
    Date dt = new Date(time);

    return dt.toString();
  }

  private CharmType generateCharmType() {
    return toCharmType(this.random.nextInt(CharmType.values().length));
  }

  private ResidentialAddressInfo generateResidentialAddress() {
    ResidentialAddressInfo ret = new ResidentialAddressInfo();

    ret.street = this.generateString(this.random.nextInt(10) + 5, false);
    ret.home = this.generateString(this.random.nextInt(3) + 2, false);
    ret.flat = this.generateString(this.random.nextInt(3) + 2, true);

    return ret;
  }

  private RegistrationAddressInfo generateRegistrationAddressInfo() {
    RegistrationAddressInfo ret = new RegistrationAddressInfo();

    ret.street = this.generateString(this.random.nextInt(10) + 5, false);
    ret.home = this.generateString(this.random.nextInt(3) + 2, false);
    ret.flat = this.generateString(this.random.nextInt(3) + 2, true);

    return ret;
  }

  private PhoneInfo generatePhoneInfo() {
    PhoneInfo ret = new PhoneInfo();

    ret.home = toPhoneType(this.random.nextInt(PhoneType.values().length));
    ret.work = toPhoneType(this.random.nextInt(PhoneType.values().length));
    ret.mobile1 = toPhoneType(this.random.nextInt(PhoneType.values().length));
    ret.mobile2 = toPhoneType(this.random.nextInt(PhoneType.values().length));
    ret.mobile3 = toPhoneType(this.random.nextInt(PhoneType.values().length));

    return ret;
  }

  // https://stackoverflow.com/a/20536597 (modified)
  private String generateString(int len, boolean digitsOnly) {
    String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String DIGITS = "1234567890";
    String CHARS = LETTERS + DIGITS;
    StringBuilder salt = new StringBuilder();

    while (salt.length() < len) {
      int id;

      if (digitsOnly) {
        id = (int) (this.random.nextFloat() * DIGITS.length());
        salt.append(DIGITS.charAt(id));
      } else {
        id = (int) (this.random.nextFloat() * CHARS.length());
        if (this.random.nextInt(3) != 0)
          salt.append(Character.toLowerCase(CHARS.charAt(id)));
        else
          salt.append(CHARS.charAt(id));
      }
    }

    return salt.toString();
  }

  // TODO конвертация моделей перезаписывает файл, в том числе статический метод для конвертации чисел в энумераторы
  private static GenderType toGenderType(int i) {
    switch (i) {
      case 0:
        return GenderType.UNKNOWN;
      case 1:
        return GenderType.MALE;
      case 2:
        return GenderType.FEMALE;
    }

    return null;
  }

  private static CharmType toCharmType(int i) {
    switch (i) {
      case 0:
        return CharmType.CALM;
      case 1:
        return CharmType.WILD;
      case 2:
        return CharmType.MYSTERIOUS;
      case 3:
        return CharmType.OPEN;
      case 4:
        return CharmType.CONSCIOUS;
      case 5:
        return CharmType.CONSERVATIVE;
    }

    return null;
  }

  private static PhoneType toPhoneType(int i) {
    switch (i) {
      case 0:
        return PhoneType.HOME;
      case 1:
        return PhoneType.WORK;
      case 2:
        return PhoneType.MOBILE;
      case 3:
        return PhoneType.EMBEDDED;
    }

    return null;
  }
}
