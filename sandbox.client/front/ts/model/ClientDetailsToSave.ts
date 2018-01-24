import {RegistrationAddressInfo} from "./RegistrationAddressInfo";
import {ResidentialAddressInfo} from "./ResidentialAddressInfo";
import {Phone} from "./Phone";
import {Gender} from "./Gender";

export class ClientDetailsToSave {
  public id: number | null/*long*/;
  public surname: string;
  public lastname: string;
  public patronymic: string;
  public gender: Gender;
  public birthdate: string;
  public charmId: number/*int*/;
  public registrationAddressInfo: RegistrationAddressInfo;
  public residentialAddressInfo: ResidentialAddressInfo;
  public phones: Phone[];
}
