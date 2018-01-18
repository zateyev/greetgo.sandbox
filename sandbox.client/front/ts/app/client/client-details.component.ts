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
    this.httpService.get("/client/details", {
      "clientRecordId": clientRecordId
    }).toPromise().then(result => {
      this.clientDetails = result.json() as ClientDetails;
      this.setDefaultPhone();

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
    //fixme: ngModel конфликтует с array.unshift?
    this.clientDetails.phones.push(Phone.copy(this.phoneToAdd));
    this.setDefaultPhone();
  }

  onPhoneRemoveButtonClick(idx: number) {
    this.clientDetails.phones.splice(idx, 1);
  }

  //TODO: rawClientDetails скорее всего не нежно, т. к. используется ngModel связка с this.clientDetails
  onClientRecordFormSubmit(rawClientDetails: ClientDetails) {
    let clientDetailsToSave = new ClientDetailsToSave();

    clientDetailsToSave.id = this.clientDetails.id;
    clientDetailsToSave.surname = this.clientDetails.surname;
    clientDetailsToSave.lastname = this.clientDetails.lastname;
    clientDetailsToSave.patronymic = this.clientDetails.patronymic;
    clientDetailsToSave.gender = this.clientDetails.gender;
    clientDetailsToSave.birthdate = this.clientDetails.birthdate;
    clientDetailsToSave.charmId = this.clientDetails.charmId;
    clientDetailsToSave.registrationAddressInfo = this.clientDetails.registrationAddressInfo;
    clientDetailsToSave.residentialAddressInfo = this.clientDetails.residentialAddressInfo;

    clientDetailsToSave.phones = [];
    if (this.clientDetails.phones.length > 0) {
      for (let phone of this.clientDetails.phones) {
        clientDetailsToSave.phones.push(Phone.copy(phone))
      }
    }

    this.httpService.get("/client/save", {
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
    //TODO: что-то нужно с этим сделать
    if ((<HTMLElement>event.target).classList.contains('modal')) {
      //this.hide();
    }
  }

  private setDefaultPhone() {
    this.phoneToAdd.number = "+7";
    this.phoneToAdd.type = PhoneType.MOBILE;
  }
}
