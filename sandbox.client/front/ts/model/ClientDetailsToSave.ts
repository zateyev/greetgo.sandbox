import {Phone} from "./Phone";
import {Gender} from "./Gender";
import {AddressInfo} from "./AddressInfo";

export class ClientDetailsToSave {
  public id: number | null/*long*/;
  public surname: string;
  public name: string;
  public patronymic: string;
  public gender: Gender;
  public birthdate: string;
  public charmId: number/*int*/;
  public registrationAddressInfo: AddressInfo;
  public factualAddressInfo: AddressInfo;
  public phones: Phone[];
  public deletedPhones: Phone[];
}
