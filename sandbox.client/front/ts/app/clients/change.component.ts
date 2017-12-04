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


  clientToChange: ClientDetails = new ClientDetails();
  errorLoading:boolean = false;
  loadingClient:boolean = true;

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
    this.httpService.post("/client/getClient", {id: id})
      .toPromise().then(res => {
      this.clientToChange = new ClientDetails().assign(res.json() as ClientDetails)
      this.loadingClient = false;
    }, error => {
      this.errorLoading = true;
      console.log(error);
    })
  }

  openModalAddForm(){
    this.loadingClient = false;
  }

  saveChangedClient() {
    this.httpService.post("/client/saveClient", {
      id: this.clientToChange.id,
      json: JSON.stringify(this.clientToChange),
    })
  }

}