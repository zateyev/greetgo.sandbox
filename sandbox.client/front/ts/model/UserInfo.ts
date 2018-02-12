import {PhoneType} from "./PhoneType";

export class UserInfo {
  public id: string;
  public accountName: string;
  public surname: string | null;
  public name: string | null;
  public patronymic: string | null;

  public charm: string | null;
  public age: number;
  public totalBalance: number;
  public minBalance: number;
  public maxBalance: number;

  public phoneType: PhoneType | null;

  public assign(o: any): UserInfo {
    this.id = o.id;
    this.accountName = o.accountName;
    this.surname = o.surname;
    this.name = o.name;
    this.patronymic = o.patronymic;
    this.phoneType = o.phoneType;

    this.charm = o.charm;
    this.age = o.age;
    this.totalBalance = o.totalBalance;
    this.minBalance = o.minBalance;
    this.maxBalance = o.maxBalance;

    return this;
  }

  public static copy(a: any): UserInfo {
    let ret = new UserInfo();
    ret.assign(a);
    return ret;
  }
}
