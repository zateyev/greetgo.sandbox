import {Component, EventEmitter, Output} from "@angular/core";
import {HttpService} from "../HttpService";
import {ClientInfo} from "../../model/ClientInfo";
import {AddressType} from "../../model/AddressType";
import {PhoneNumber} from "../../model/PhoneNumber";
import {ClientDetails} from "../../model/ClientDetails";
import {ClientRecordsToSave} from "../../model/ClientRecordsToSave";

@Component({
  selector: 'client-form-component',
  template: require('./client-form-component.html'),
  styles: [require('./client-form-component.css')],
})
export class ClientFormComponent {
  @Output() exit = new EventEmitter<void>();

  requiredNotFilled: boolean = false;
  editMode: boolean = false;
  clientRecordsToSave: ClientRecordsToSave = new ClientRecordsToSave();
  formsTitle = "";
  formsBtn = "";

  constructor(private httpService: HttpService) {}

  addOrUpdateClient() {

    if (!this.allFieldsFilled()) {
      this.requiredNotFilled = true;
      alert("Заполните все обязательные поля");
      return;
    }
    this.requiredNotFilled = false;
    this.clientRecordsToSave.addressF.type = AddressType.FACT;
    this.clientRecordsToSave.addressR.type = AddressType.REG;
    $('#id01').hide();

    this.httpService.post("/clientsList/addOrUpdateClient", {
      clientRecordsToSave: JSON.stringify(this.clientRecordsToSave)
    }).toPromise().then(result => {
      if (result.json()) {
        // let clientInfo = ClientInfo.copy(result.json());
        // this.clientsList.push(clientInfo);
        this.getTotalSize();
      }
    }, error => {
      console.log("addOrUpdateClient");
      console.log(error);
    });

  }

  private allFieldsFilled() {
    return this.clientRecordsToSave.addressF.street != null && this.clientRecordsToSave.addressF.house != null && this.clientRecordsToSave.addressF.flat != null &&
      this.clientRecordsToSave.addressR.street != null && this.clientRecordsToSave.addressR.house != null && this.clientRecordsToSave.addressR.flat != null &&
      this.clientRecordsToSave.surname != null && this.clientRecordsToSave.name != null && this.clientRecordsToSave.charm.id != null &&
      this.clientRecordsToSave.gender != null && this.clientRecordsToSave.dateOfBirth != null && this.phoneNumbersFilled()
  }

  private phoneNumbersFilled() {
    for (let phone of this.clientRecordsToSave.phoneNumbers) {
      if (phone.phoneType == null || phone.number == null) return false;
    }
    return true;
  }

  addPhoneNumber() {
    this.clientRecordsToSave.phoneNumbers.push(new PhoneNumber);
  }

  removePhoneNumber(index: number) {
    if (this.clientRecordsToSave.phoneNumbers.length > 1) {
      this.clientRecordsToSave.phoneNumbers.splice(index, 1);
    }
  }

  closeModalForm() {
    $('#id01').hide();
    this.requiredNotFilled = false;
  }

  openForAdding() {
    this.clientRecordsToSave = new ClientDetails();
    this.formsTitle = "Добавление нового пользователя";
    this.formsBtn = "Добавить";
    $('#id01').show();
    this.editMode = false;
    this.phoneNumbers = [new PhoneNumber()];
  }

  openForUpdate() {
    this.formsTitle = "Изменение данных клиента";
    this.formsBtn = "Изменить";
    $('#id01').show();
    this.editMode = true;
    this.loadClientDetails();
  }

  loadClientDetails() {
    this.httpService.post("/clientsList/clientDetails", {
      clientsId: this.clientsList[this.selClientId].id
    }).toPromise().then(result => {
      this.clientRecordsToSave = ClientRecordsToSave.copy(result.json());
    }, error => {
      console.log("clientDetails");
      console.log(error);
    });
  }
}
