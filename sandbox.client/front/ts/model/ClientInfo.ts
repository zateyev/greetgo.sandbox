import {CharmType} from "./CharmType";
import {RegistrationAddressInfo} from "./RegistrationAddressInfo";
import {ResidentialAddressInfo} from "./ResidentialAddressInfo";
import {PhoneInfo} from "./PhoneInfo";
import {GenderType} from "./GenderType";

export class ClientInfo {
  public id: number/*long*/;
  public surname: string | null;
  public lastname: string | null;
  public patronymic: string | null;
  public gender: GenderType | null;
  public birthDate: string | null;
  public charm: CharmType | null;
  public registrationAddressInfo: RegistrationAddressInfo | null;
  public residentialAddressInfo: ResidentialAddressInfo | null;
  public phoneInfo: PhoneInfo | null;
}
