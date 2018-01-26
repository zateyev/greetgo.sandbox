import {Component, EventEmitter, Output} from "@angular/core";
import {ClientDetails} from "../../model/ClientDetails";
import {HttpService} from "../HttpService";
import {Gender} from "../../model/Gender";
import {PhoneType} from "../../model/PhoneType";
import {Phone} from "../../model/Phone";
import {ClientDetailsToSave} from "../../model/ClientDetailsToSave";

@Component({
  selector: 'client-details-component',
  template: require('./client-details-component.html'),
  styles: [require('./client-details-component.css')],
})
// Взято с https://embed.plnkr.co/7kqyiW97CI696Ixn020g/
export class ClientDetailsComponent {
  @Output() onModalCloseOutput = new EventEmitter<any>();

  isVisible: boolean = false;
  isAnimating: boolean = false;
  clientDetails: ClientDetails;

  //TODO: Static data
  genderEnum = Gender;
  genderList: { [key: string]: string } = {};
  phoneTypeEnum = PhoneType;
  phoneTypeList: { [key: string]: string } = {};
  phoneToAdd: Phone = new Phone();
  deletedPhoneList: Phone[] = [];
  curPhoneList: Phone[] = [];

  constructor(private httpService: HttpService) {
    this.genderList[Gender.EMPTY] = "Неизвестно";
    this.genderList[Gender.MALE] = "Мужской";
    this.genderList[Gender.FEMALE] = "Женский";

    this.phoneTypeList[PhoneType.EMBEDDED] = "Встроенный";
    this.phoneTypeList[PhoneType.MOBILE] = "Мобильный";
    this.phoneTypeList[PhoneType.HOME] = "Домашний";
    this.phoneTypeList[PhoneType.WORK] = "Рабочий";
    this.phoneTypeList[PhoneType.OTHER] = "Другой";
  }

  show(clientRecordId: number) {
    this.deletedPhoneList = [];
    this.curPhoneList = [];
    this.setDefaultPhone();

    this.httpService.get("/client/details", {
      "clientRecordId": clientRecordId
    }).toPromise().then(result => {
      this.clientDetails = result.json() as ClientDetails;
      for (let phone of this.clientDetails.phones)
        this.curPhoneList.push(Phone.copy(phone));

      this.isVisible = true;
      setTimeout(() => this.isAnimating = true, 100);
    }, error => {
      console.log(error);
    });
  }

  hide() {
    this.isAnimating = false;
    setTimeout(() => this.isVisible = false, 300);
    setTimeout(() => this.onModalCloseOutput.emit(true), 300);
  }

  onPhoneAppendButtonClick() {
    if (this.containsPhone(this.curPhoneList, this.phoneToAdd))
      return;

    let meetIdx: number = -1;
    this.deletedPhoneList.forEach((deletedPhone, index) => {
      if (Phone.compare(deletedPhone, this.phoneToAdd)) {
        meetIdx = index;
        return;
      }
    });

    if (meetIdx >= 0)
      this.deletedPhoneList.splice(meetIdx, 1);

    this.curPhoneList.push(Phone.copy(this.phoneToAdd));
    this.setDefaultPhone();

    console.log(this.curPhoneList);
    console.log(this.deletedPhoneList);
  }

  onPhoneRemoveButtonClick(phone: Phone, idx: number) {
    if (this.containsPhone(this.clientDetails.phones, phone))
      this.deletedPhoneList.push(phone);

    this.curPhoneList.splice(idx, 1);

    console.log(this.curPhoneList);
    console.log(this.deletedPhoneList);
  }

  //TODO: rawClientDetails скорее всего не нежно, т. к. используется ngModel связка с this.clientDetails
  onClientRecordFormSubmit(rawClientDetails: ClientDetails) {
    let clientDetailsToSave = new ClientDetailsToSave();

    clientDetailsToSave.id = this.clientDetails.id;
    clientDetailsToSave.surname = this.clientDetails.surname.trim();
    clientDetailsToSave.name = this.clientDetails.name.trim();
    clientDetailsToSave.patronymic = this.clientDetails.patronymic.trim();
    clientDetailsToSave.gender = this.clientDetails.gender;
    clientDetailsToSave.birthdate = this.clientDetails.birthdate;
    clientDetailsToSave.charmId = this.clientDetails.charmId;
    clientDetailsToSave.registrationAddressInfo = this.clientDetails.registrationAddressInfo;
    clientDetailsToSave.factualAddressInfo = this.clientDetails.factualAddressInfo;
    clientDetailsToSave.phones = [];
    clientDetailsToSave.deletedPhones = [];

    for (let curPhone of this.curPhoneList)
      if (!this.containsPhone(this.clientDetails.phones, curPhone))
        clientDetailsToSave.phones.push(Phone.copy(curPhone));

    for (let deletedPhone of this.deletedPhoneList)
      clientDetailsToSave.deletedPhones.push(Phone.copy(deletedPhone));

    this.httpService.post("/client/save", {
      "clientDetailsToSave": JSON.stringify(clientDetailsToSave)
    }).toPromise().then(result => {
      this.hide();
    }, error => {
      console.log(error);
    });
  }

  cancelClientRecordEdit() {
    this.hide();
  }

  //TODO: если нужно будет закрывать форму при нажатии вне нее, то раскомментить
  onContainerClicked(event: MouseEvent) {
    if ((<HTMLElement>event.target).classList.contains('modal')) {
      //this.hide();
    }
  }

  private containsPhone(phones: Phone[], phoneToSearch: Phone): boolean {
    for (let phone of phones)
      if (Phone.compare(phone, phoneToSearch))
        return true;

    return false;
  }

  private setDefaultPhone() {
    this.phoneToAdd.number = "+7";
    this.phoneToAdd.type = PhoneType.MOBILE;
  }
}
