import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {ClientDetails} from "../../model/ClientDetails";
import {HttpService} from "../HttpService";
@Component({
  selector: 'change-component',
  template: require('./change.component.html'),
  styles: [require('./list.component.css')],
})

export class ChangeComponent implements OnInit{
  @Input() id:string = "";
  @Output() close = new EventEmitter<void>();


  clientDetails: ClientDetails = new ClientDetails();
  ////////////
  errors: string = "";

  add:boolean = false;
  change:boolean = false;
  constructor(private httpService: HttpService) {
  }

  ngOnInit(){
    if(this.id == ''){
      this.openModalAddForm();
      this.add = true;
    }
    else {
      this.openModalChangeForm(this.id);
      this.change = true;
    }
  }


  openModalChangeForm(id: string) {
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

  saveChangedClient() {
    this.errors = "savingClient";
    this.httpService.post("/client/saveClient", {
      id: this.clientDetails.id,
      json: JSON.stringify(this.clientDetails),
    }).toPromise().then(ignore => {
      this.close.emit();
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
      this.close.emit();
    }, error => {
      this.errors = "errorSavingClient";
      console.log(error);
    })
  }

}