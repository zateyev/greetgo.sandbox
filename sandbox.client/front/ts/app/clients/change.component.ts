import {Component, EventEmitter, Output} from "@angular/core";
import {ClientDetails} from "../../model/ClientDetails";
import {ClientRecord} from "../../model/ClientRecord";
import {HttpService} from "../HttpService";
import {ClientToSave} from "../../model/ClientToSave";
@Component({
  selector: 'change-component',
  template: require('./change.component.html'),
  styles: [require('./list.component.css')],
})

export class ChangeClientComponent {
  @Output() saved = new EventEmitter<ClientRecord>();
  visible = false;

  clientDetails: ClientDetails = null;
  clientToSave: ClientToSave = new ClientToSave;

  /////////////////////////////////////////////////////
  errors: string = "";
  /////////////////////////////////////////////////////
  buttonEnabled: boolean = false;
  add:boolean = false;
  change:boolean = false;

  constructor(private httpService: HttpService) {
  }

  public showForm(id: string) {
    this.visible = true;
    this.errors = 'loadingClient';
    if(id!=""){
      this.change = true; // change value for button
      this.httpService.post("/client/getClient", {id: id})
        .toPromise().then(res => {
        this.clientDetails = new ClientDetails().assign(res.json() as ClientDetails);
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
      this.updateButton();
      this.errors = "success";
    }

  }

  updateButton(){
    this.buttonEnabled = !!this.clientDetails.name && !!this.clientDetails.surname;
  }


  closeForm() {
    this.errors = "savingClient";
    this.add = this.change = false;

    this.clientToSave.assign(this.clientDetails);
    if (this.clientToSave.name == null || this.clientToSave.surname == null){
      this.errors = "errorSavingClient";
      return;
    }

    this.httpService.post("/client/saveClient", {
      clientToSave: JSON.stringify(this.clientToSave)
    }).toPromise().then(res => {
      this.saved.emit(res.json() as ClientRecord);
      this.visible = false;
      this.errors = "";
    }, error => {
      this.errors = "errorSavingClient";
      console.log(error);
    })
  }


}