import {Component, EventEmitter, Output} from "@angular/core";
import {ClientRecord} from "../../model/ClientRecord.ts"
import {HttpService} from "../HttpService";
@Component({
  selector: 'list-component',
  template: require('./list-component.html'),
  styles: [require('./list-component.css')],
})
export class ListComponent {
  @Output() exit = new EventEmitter<void>();
  list: ClientRecord[] = [];
  num: string  = "5";
  constructor(private httpService: HttpService) {}
  loadUserInfoButtonClicked() {
    //this.num = "6";
    this.httpService.get("/client/getList").toPromise().then(result => {
       this.list = result.json().map

      // this.ClientList[i] = new ListOfClients().assign(result.json() as ListOfClients);
    }, error => {

      console.log(error);
    });

  }
}