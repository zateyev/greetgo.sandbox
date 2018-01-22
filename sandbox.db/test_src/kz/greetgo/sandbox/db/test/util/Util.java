package kz.greetgo.sandbox.db.test.util;

import kz.greetgo.sandbox.controller.model.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Util {

  // https://stackoverflow.com/a/3985467
  public static String generateDateString() {
    long time = -946771200000L + (Math.abs(new Random().nextLong()) % (70L * 365 * 24 * 60 * 60 * 1000));
    Date date = new Date(time);

    return new SimpleDateFormat("yyyy-MM-dd").format(date);
  }
/*
  private static Charm generateCharm() {
    Object[] values = charmStorage.values().toArray();
    CharmDot charmDot = (CharmDot) values[new Random().nextInt(values.length)];

    return charmDot.toCharm();
  }*/

  private static ResidentialAddressInfo generateResidentialAddress() {
    Random random = new Random();
    ResidentialAddressInfo ret = new ResidentialAddressInfo();

    ret.street = generateString(random.nextInt(10) + 5, false);
    ret.home = generateString(random.nextInt(3) + 2, false);
    ret.flat = generateString(random.nextInt(3) + 2, true);

    return ret;
  }

  private static RegistrationAddressInfo generateRegistrationAddressInfo() {
    Random random = new Random();
    RegistrationAddressInfo ret = new RegistrationAddressInfo();

    ret.street = generateString(random.nextInt(10) + 5, false);
    ret.home = generateString(random.nextInt(3) + 2, false);
    ret.flat = generateString(random.nextInt(3) + 2, true);

    return ret;
  }

  private static List<Phone> generatePhones() {
    Random random = new Random();
    List<Phone> ret = new ArrayList<>();
    int n = random.nextInt(3) + 1;

    for(int i = 0; i < n; i++) {
      Phone phone = new Phone();
      phone.number = "+" + generateString(11, true);
      phone.type = toPhoneType(random.nextInt(PhoneType.values().length));

      ret.add(phone);
    }

    return ret;
  }

  // https://stackoverflow.com/a/20536597 (modified)
  public static String generateString(int len, boolean digitsOnly) {
    Random random = new Random();
    String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String DIGITS = "1234567890";
    String CHARS = LETTERS + DIGITS;
    StringBuilder salt = new StringBuilder();

    while (salt.length() < len) {
      int id;

      if (digitsOnly) {
        id = (int) (random.nextFloat() * DIGITS.length());
        salt.append(DIGITS.charAt(id));
      } else {
        id = (int) (random.nextFloat() * CHARS.length());
        if (random.nextInt(3) != 0)
          salt.append(Character.toLowerCase(CHARS.charAt(id)));
        else
          salt.append(CHARS.charAt(id));
      }
    }

    return salt.toString();
  }

  private static Gender toGender(int i) {
    return Gender.values()[i];
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
      default:
        return PhoneType.OTHER;
    }
  }
}
