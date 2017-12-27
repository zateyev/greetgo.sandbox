import {Component, EventEmitter, Output} from "@angular/core";
import {ClientDetails} from "../../model/ClientDetails";
import {ClientRecord} from "../../model/ClientRecord";
import {HttpService} from "../HttpService";
import {ClientToSave} from "../../model/ClientToSave";
import {CharmRecord} from "../../model/CharmRecord";
@Component({
  selector: 'change-component',
  template: require('./change.component.html'),
  styles: [require('./list.component.css')],
})

export class ChangeClientComponent {
  @Output() saved = new EventEmitter<ClientRecord>();
  visible = false;

  clientDetails: ClientDetails = null;
  charms: CharmRecord[] = null;
  clientToSave: ClientToSave = new ClientToSave;

  /////////////////////////////////////////////////////
  errors: string = "";
  errorMessage: string = "* поля объязательны для заполнения"
  /////////////////////////////////////////////////////
  buttonEnabled: boolean = false;
  add:boolean = false;
  change:boolean = false;

  constructor(private httpService: HttpService) {
  }

  public showForm(id: string) {
    this.visible = true;
    this.add = this.change = false;
    this.errors = 'loadingClient';
    if(id!=""){
      this.change = true; // change value for button
      this.httpService.post("/client/getClient", {id: id})
        .toPromise().then(res => {
          console.log(res.json());
        this.clientDetails = new ClientDetails().assign(res.json());
        this.charms = this.clientDetails.charms;
        console.log(this.clientDetails);
        this.updateButton();
        this.errors = 'success';
      }, error => {
        this.errors = 'errorLoading';
        console.log(error);
      })
    }
    else{
      this.add = true; // add value for button
      this.clientDetails = new ClientDetails();
      this.httpService.post("/client/getClient", {id: null})
        .toPromise().then(res => {
        this.charms = res.json().charms;
        this.errors = 'success';
        this.updateButton();
      }, error => {
        this.errors = 'errorLoading';
        console.log(error);
      });
    }

  }

  updateButton(){
    this.buttonEnabled = !!this.clientDetails.name && !!this.clientDetails.surname
    && !!this.clientDetails.gender && !!this.clientDetails.charmId && !!this.clientDetails.dateOfBirth
    && !!this.clientDetails.factAddress.street && !!this.clientDetails.factAddress.house
    && !!this.clientDetails.factAddress.flat && !!this.clientDetails.phones.home
    && !!this.clientDetails.phones.work && !!this.clientDetails.phones.mobile[0];
    if (this.buttonEnabled === true) {
      this.buttonEnabled = this.clientDetails.phones.home !== this.clientDetails.phones.work
        && this.clientDetails.phones.home !== this.clientDetails.phones.mobile[0]
        && this.clientDetails.phones.work !== this.clientDetails.phones.mobile[0];
      this.errorMessage = "Номера телефонов не могут быть одинаковыми"
    }
  }


  closeForm() {
    this.errors = "savingClient";

    this.clientToSave.assign(this.clientDetails);
    console.log(this.clientToSave);
    if (this.clientToSave.name == null || this.clientToSave.surname == null){
      this.errors = "errorSavingClient";
      return;
    }

    this.httpService.post("/client/saveClient", {
      clientToSave: JSON.stringify(this.clientToSave)
    }).toPromise().then(res => {
      let clientRecord = new ClientRecord;
      this.saved.emit(clientRecord.assign(res.json()));
      this.visible = false;
      this.errors = "";
    }, error => {
      this.errors = "errorSavingClient";
      console.log(error);
    })
  }

  hideForm() {
    this.visible = false;
    this.saved.emit();
    this.errors = "";
  }

  genderToM() {
    this.clientDetails.gender = "male";
  }

  genderToF() {
    this.clientDetails.gender = "female";
  }

}