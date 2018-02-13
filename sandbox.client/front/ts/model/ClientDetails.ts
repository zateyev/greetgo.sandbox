import {PhoneNumber} from "./PhoneNumber";
import {Address} from "./Address";
import {Charm} from "./Charm";
import {Gender} from "./Gender";

export class ClientDetails {
  public id: string;
  public surname: string;
  public name: string;
  public patronymic: string;
  public charm: Charm;
  public gender: Gender;
  public dateOfBirth: string;
  public addressF: Address;
  public addressR: Address;
  public phoneNumbers: PhoneNumber[];
  public totalBalance: number/*int*/;
  public minBalance: number/*int*/;
  public maxBalance: number/*int*/;

  constructor() {
    this.charm = new Charm();
    // this.gender = new Gender();
    this.addressF = new Address();
    this.addressR = new Address();
    this.phoneNumbers = [new PhoneNumber()];
  }

  public assign(o: any): ClientDetails {
    this.id = o.id;
    this.name = o.name;
    this.surname = o.surname;
    this.patronymic = o.patronymic;
    this.charm = o.charm;
    this.gender = o.gender;
    this.dateOfBirth = o.dateOfBirth;

    this.totalBalance = o.totalBalance;
    this.minBalance = o.minBalance;
    this.maxBalance = o.maxBalance;

    // if (o.addressF) {
    //   // let addressF = new Address();
    //   // addressF.street = o.addressF.street;
    //   // addressF.house = o.addressF.house;
    //   // addressF.flat = o.addressF.flat;
    //   // this.addressF = addressF;
    //   this.addressF = o.addressF;
    // }
    this.addressF = o.addressF;

    // if (o.addressR) {
    //   // let addressR = new Address();
    //   // addressR.street = o.addressR.street;
    //   // addressR.house = o.addressR.house;
    //   // addressR.flat = o.addressR.flat;
    //   // this.addressR = addressR;
    //   this.addressR = o.addressR;
    // }
    this.addressR = o.addressR;

    return this;
  }

  public static copy(a: any): ClientDetails {
    let ret = new ClientDetails();
    ret.assign(a);
    return ret;
  }
}
