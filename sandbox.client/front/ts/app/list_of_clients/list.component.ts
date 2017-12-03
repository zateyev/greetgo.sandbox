import {Component, EventEmitter, OnInit, Output} from "@angular/core";
import {ClientRecord} from "../../model/ClientRecord"
import {HttpService} from "../HttpService";
import {ClientDetails} from "../../model/ClientDetails";
import {error} from "util";

@Component({
  selector: 'list-component',
  template: require('./list-component.html'),
  styles: [require('./list.component.css')],
})
export class ListComponent implements OnInit {
  @Output() exit = new EventEmitter<void>();
  loading: boolean = true;
  errorLoading: boolean = false;

  modalChangeForm: boolean = false;
  modalAddForm: boolean = false;

  list: ClientRecord[] = [];
  clientToChange: ClientDetails = null;


  constructor(private httpService: HttpService) {
  }

  ngOnInit(): void {
    this.loadList();
    this.loading = false;
  }

  saveChangedClient() {
    this.httpService.post("/client/saveClient", {
      id: this.clientToChange.id,
      json: JSON.stringify(this.clientToChange),
    })
  }

  deleteClient(id: string) {
    this.httpService.post("/client/deleteClient", {
      id: id,
    }).toPromise().then();
    this.loadList();
  }

  openModalChangeForm(id: string) {
    this.modalChangeForm = true;
    this.httpService.post("/client/getClient", {id: id})
      .toPromise().then(res => {
      this.clientToChange = new ClientDetails().assign(res.json() as ClientDetails)
    }, error => {
      this.errorLoading = true;
      console.log(error);
    })
  }

  closeModalForm() {
    this.modalChangeForm = false;
    this.modalAddForm = false;
    this.clientToChange = null;
  }

  openModalAddForm() {
    this.modalAddForm = true;
  }

  loadList() {
    this.httpService.get("/client/getList").toPromise().then(result => {
      this.list = result.json().map(ClientRecord.copy);
    }, error => {
      this.errorLoading = true;
      console.log(error);
    });
  }
}