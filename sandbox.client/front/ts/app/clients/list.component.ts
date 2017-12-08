import {Component, EventEmitter, OnInit, Output, ViewChild} from "@angular/core";
import {ClientRecord} from "../../model/ClientRecord";
import {HttpService} from "../HttpService";
import {ChangeClientComponent} from "./change.component";

@Component({
  selector: 'list-component',
  template: require('./list.component.html'),
  styles: [require('./list.component.css')],
})
export class ListComponent implements OnInit {
  @Output() exit = new EventEmitter<void>();
  @ViewChild("changeForm") changeForm: ChangeClientComponent;

  loading: boolean = true;
  deletingClient:string = "";
  errorLoading: boolean = false;
  emptyList: boolean = false;
  modalChangeForm: boolean = false;

  list: ClientRecord[] = [];

  currentPage: number = 0;

  temperOptions: any = [{value: 'good', name: 'Хороший'},
    {value: 'bad', name: 'Плохой'},
    {value: 'veryBad', name: 'Очень Плохой'}];

  temperValue(k: string) {
    for (let op of this.temperOptions) if (k === op.value) return op.name;
  }
  constructor(private httpService: HttpService) {
  }

  ngOnInit(): void {
    this.loading = true;
    this.loadList();
  }

  deleteClient(id: string) {
    this.deletingClient = id;
    this.httpService.post("/client/deleteClient", {
      id: id,}).toPromise().then(ignore => {
      this.list.splice(this.list.findIndex(res => res.id == id), 1);
      this.deletingClient = "";
    }, error => {
      this.deletingClient = "";
      this.errorLoading = true;
      console.log(error);
    });
  }

  openModalChangeForm(id: string) {
    this.modalChangeForm = true;
    this.changeForm.showForm(id);
  }

  closeModalForm(client:any) {
    if(client == null){
      this.modalChangeForm = false;
      return;
    }
    this.modalChangeForm = false;
    if(this.list.findIndex(x => x.id === client.id) !>= 0) {
      this.list[this.list.findIndex(res => res.id == client.id)] = client;
    }
    else {
      this.list.push(client);
    }
  }

  openModalAddForm() {
    this.modalChangeForm = true;
    this.changeForm.showForm("");
  }

  loadPageOfList(p: number) {
    this.currentPage = p;
    this.loading = true;
    console.log(this.currentPage);
    this.loadList();
  }

  loadList() {
    this.httpService.get("/client/getList?page=" + this.currentPage + "&" + "sort=0").toPromise().then(result => {
      this.list = result.json().map(ClientRecord.copy);
      if(result.json().length == 0){
        this.emptyList = true;
      }
      this.loading = false;
    }, error => {
      this.errorLoading = true;
      this.loading = false;
      console.log(error);
    });
  }
}