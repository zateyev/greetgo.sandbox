import {Component, EventEmitter, Output} from "@angular/core";
import {ClientDetails} from "../../model/ClientDetails";
import {ClientRecord} from "../../model/ClientRecord";
import {HttpService} from "../HttpService";
@Component({
  selector: 'change-component',
  template: require('./change.component.html'),
  styles: [require('./list.component.css')],
})

export class ChangeComponent {
  @Output() saved = new EventEmitter<string>();
  visible = false;


  clientDetails: ClientDetails = new ClientDetails();
  ////////////
  errors: string = "";

  add:boolean = false;
  change:boolean = false;

  constructor(private httpService: HttpService) {
  }

  public showForm(id: string) {
    this.change = true;
    this.visible = true;
    this.errors = 'loadingClient';
    this.httpService.post("/client/getClient", {id: id})
      .toPromise().then(res => {
      this.clientDetails = new ClientDetails().assign(res.json() as ClientDetails);
      this.errors = 'success';
    }, error => {
      this.errors = 'errorLoading';
      console.log(error);
    })
  }


  openModalAddForm(){
    this.errors = "success";
    this.clientDetails = new ClientDetails();
  }

  closeForm() {
    this.errors = "savingClient";
    this.httpService.post("/client/saveClient", {
      id: this.clientDetails.id,
      json: JSON.stringify(this.clientDetails)
    }).toPromise().then(ignore => {
      //this.saved.emit(JSON.stringify(clientRecord.json()));
      this.saved.emit("asfd");
      this.visible = false;
      this.errors = "";
    }, error => {
      this.errors = "errorSavingClient";
      console.log(error);
    })
  }

  addClient() {
    this.errors = "savingClient";
    this.httpService.post("/client/addClient", {
      json: JSON.stringify(this.clientDetails)
    }).toPromise().then(ignore=>{
      this.saved.emit();
    }, error => {
      this.errors = "errorSavingClient";
      console.log(error);
    })
  }

}