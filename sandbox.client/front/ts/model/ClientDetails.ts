import {CharmRecord} from "./CharmRecord";
export class ClientDetails {
  public id: string;
  public name: string;
  public surname: string;
  public patronymic: string;
  public charmId: string;
  public gender: string;
  public dateOfBirth: string;
  public firstAddress: string[];
  public secondAddress: string[];
  public phones: string[];
  public charms: CharmRecord[];


  constructor(){
    this.id = null;
    this.name = "";
    this.surname = "";
    this.patronymic = "";
    this.gender = "";
    this.charmId = "";
    this.dateOfBirth = "";
    this.firstAddress = [];
    this.secondAddress = [];
    this.phones = [];
  }

  public assign(o: any): ClientDetails{
    this.id = o.id;
    this.name = o.name;
    this.surname = o.surname;
    this.patronymic = o.patronymic;
    this.gender = o.gender;
    this.charmId = o.charmId;
    this.dateOfBirth = o.dateOfBirth;
    this.firstAddress = o.firstAddress;
    this.secondAddress = o.secondAddress;
    this.phones = o.phones;
    this.charms = o.charms;
    return this;
  }

}