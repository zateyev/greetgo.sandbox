import {Charm} from "./Charm";
import {RegistrationAddressInfo} from "./RegistrationAddressInfo";
import {ResidentialAddressInfo} from "./ResidentialAddressInfo";
import {PhoneInfo} from "./PhoneInfo";
import {Gender} from "./Gender";

export class ClientDetails {
  public id: number/*long*/;
  public surname: string | null;
  public lastname: string;
  public patronymic: string | null;
  public gender: Gender;
  public birthDate: string;
  public charm: Charm;
  public registrationAddressInfo: RegistrationAddressInfo | null;
  public residentialAddressInfo: ResidentialAddressInfo | null;
  public phoneInfo: PhoneInfo | null;
}
