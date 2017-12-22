import {CharmRecord} from "./CharmRecord";
import {ClientAddress} from "./ClientAddress";
import {ClientPhones} from "./ClientPhones";
export class ClientDetails {
  public id: string;
  public name: string;
  public surname: string;
  public patronymic: string;
  public charmId: string;
  public gender: string;
  public dateOfBirth: string;
  public factAddress: ClientAddress;
  public regAddress: ClientAddress;
  public phones: ClientPhones;
  public charms: CharmRecord[];


  constructor(){
    this.id = null;
    this.name = "";
    this.surname = "";
    this.patronymic = "";
    this.gender = "";
    this.charmId = "";
    this.dateOfBirth = "";
    this.factAddress = new ClientAddress();
    this.regAddress = new ClientAddress();
    this.phones = new ClientPhones();
  }

  public assign(o: any): ClientDetails{
    this.id = o.id;
    this.name = o.name;
    this.surname = o.surname;
    this.patronymic = o.patronymic;
    this.gender = o.gender;
    this.charmId = o.charmId;
    this.dateOfBirth = o.dateOfBirth;
    this.phones = o.phones;
    this.charms = o.charms;
    this.factAddress = o.factAddress;
    this.regAddress = o.regAddress;
    this.phones = o.phones;
    return this;
  }

}