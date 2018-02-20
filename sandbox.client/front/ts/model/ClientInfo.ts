import {Charm} from "./Charm";

export class ClientInfo {
  public id: string;
  public surname: string | null;
  public name: string | null;
  public patronymic: string | null;
  public charm: Charm | null;
  public age: number/*int*/;
  public totalBalance: number/*int*/;
  public minBalance: number/*int*/;
  public maxBalance: number/*int*/;

  public assign(o: any): ClientInfo {
    this.id = o.id;
    this.surname = o.surname;
    this.name = o.name;
    this.patronymic = o.patronymic;
    this.charm = o.charm;
    this.age = o.age;
    this.totalBalance = o.totalBalance;
    this.minBalance = o.minBalance;
    this.maxBalance = o.maxBalance;
    return this;
  }

  public static copy(a: any): ClientInfo {
    let ret = new ClientInfo();
    ret.assign(a);
    return ret;
  }
}
