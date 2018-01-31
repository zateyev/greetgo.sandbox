import {Charm} from "./Charm";
import {Phone} from "./Phone";
import {Gender} from "./Gender";
import {AddressInfo} from "./AddressInfo";

export class ClientDetails {
  public id: number | null/*long*/;
  public surname: string;
  public name: string;
  public patronymic: string;
  public gender: Gender;
  public birthdate: string;
  public charmId: number/*int*/;
  public charmList: Charm[];
  public registrationAddressInfo: AddressInfo;
  public factualAddressInfo: AddressInfo;
  public phones: Phone[];
}
