import {Component, EventEmitter, OnInit, Output, ViewChild} from "@angular/core";
import {ClientRecord} from "../../model/ClientRecord";
import {HttpService} from "../HttpService";
import {ChangeClientComponent} from "./change.component";
import {ClientListPagination} from "../pagination/pagination.component";
import {ListInfo} from "../../model/ListInfo";

@Component({
  selector: 'list-component',
  template: require('./list.component.html'),
  styles: [require('./list.component.css')],
})
export class ListComponent implements OnInit {
  @Output() exit = new EventEmitter<void>();
  @ViewChild("changeForm") changeForm: ChangeClientComponent;
  @ViewChild("pagination") pagination: ClientListPagination;

  loading: boolean = true;
  deletingClient:string = "";
  errorLoading: boolean = false;
  emptyList: boolean = false;
  modalChangeForm: boolean = false;

  list: ClientRecord[] = [];

  currentPage: number = 0;
  sort: string = "fio";

  temperOptions: any = [{value: 'good', name: 'Хороший'},
    {value: 'bad', name: 'Плохой'},
    {value: 'veryBad', name: 'Очень Плохой'}];

  listInfo: ListInfo = new ListInfo;

  temperValue(k: string) {
    for (let op of this.temperOptions) if (k === op.value) return op.name;
  }

  constructor(private httpService: HttpService) {
  }

  ngOnInit(): void {
    this.loading = true;
    this.loadList();
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

  deleteClient(id: string) {
    this.deletingClient = id;
    this.httpService.post("/client/deleteClient", {
      id: id,
    }).toPromise().then(ignore => {
      this.list.splice(this.list.findIndex(res => res.id === id), 1);
      this.checkEmptyList();
      this.deletingClient = "";
    }, error => {
      this.deletingClient = "";
      this.errorLoading = true;
      console.log(error);
    });
  }

  loadPageOfList(listInfo: ListInfo) {
    this.listInfo = listInfo;
    this.loading = true;
    this.loadList();
  }

  loadSortedList(sort: string) {
    this.sort = sort;
    this.listInfo.sort = this.sort;
    this.loading = true;
    this.loadList();
  }

  loadList() {
    this.httpService.post("/client/getList", {listInfo: JSON.stringify(this.listInfo)}).toPromise().then(result => {
      this.list = result.json().map(ClientRecord.copy);
      if(result.json().length === 0) this.checkEmptyList();
      this.loading = false;
    }, error => {
      this.errorLoading = true;
      this.loading = false;
      console.log(error);
    });
  }

  checkEmptyList() {
    if (this.pagination.totalSizeOfList === 0){
      this.emptyList = true;
      return;
    }
    if (this.list.length === 0) {
      console.log("empty list find");
      this.pagination.getTotalSizeOfList();
      if (this.currentPage > 0) this.pagination.setCurrentPage(this.currentPage - 1);
      else this.pagination.setCurrentPage(this.currentPage);
    }
  }
}