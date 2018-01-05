import {ClientAddress} from "./ClientAddress";
import {ClientPhones} from "./ClientPhones";
export class ClientToSave {
  public id: string;
  public name: string;
  public surname: string;
  public patronymic: string;
  public gender: string;
  public charmId: string;
  public dateOfBirth: string;
  public factAddress: ClientAddress;
  public regAddress: ClientAddress;
  public phones: ClientPhones;

  public assign(o: any): ClientToSave {
    o.phones.mobile = o.phones.mobile.filter(String);
    this.id = o.id;
    this.name = o.name;
    this.surname = o.surname;
    this.dateOfBirth = o.dateOfBirth;
    this.charmId = o.charmId;
    this.gender = o.gender;
    this.patronymic = o.patronymic;
    this.factAddress = o.factAddress;
    this.regAddress = o.regAddress;
    this.phones = o.phones;
    return this;
  }
}