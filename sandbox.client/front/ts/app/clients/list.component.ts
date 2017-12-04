import {Component, EventEmitter, OnInit, Output} from "@angular/core";
import {ClientRecord} from "../../model/ClientRecord";
import {HttpService} from "../HttpService";

@Component({
  selector: 'list-component',
  template: require('./list.component.html'),
  styles: [require('./list.component.css')],
})
export class ListComponent implements OnInit {
  @Output() exit = new EventEmitter<void>();

  loading: boolean = true;
  deletingClient:string = "";
  errorLoading: boolean = false;
  emptyList: boolean = false;

  idForChange: string = "";
  modalChangeForm: boolean = false;
  modalAddForm: boolean = false;

  list: ClientRecord[] = [];

  constructor(private httpService: HttpService) {
  }

  ngOnInit(): void {
    this.loading = true;
    this.loadList();
  }


  deleteClient(id: string) {
    this.deletingClient = id;
    this.httpService.post("/client/deleteClient", {
      id: id,
    }).toPromise().then(ignore => {
      this.list.splice(this.list.findIndex(res => res.id == id), 1);
      this.deletingClient = "";
    }, error => {
      this.deletingClient = "";
      this.errorLoading = true;
      console.log(error);
    });
  }

  openModalChangeForm(id: string) {
    this.idForChange = id;
    this.modalChangeForm = true;
  }

  closeModalForm() {
    this.modalChangeForm = false;
    this.modalAddForm = false;
    //this.clientDetails = null;
  }


  openModalAddForm() {
    this.idForChange = "";
    this.modalChangeForm = true;
  }

  loadList() {
    this.httpService.get("/client/getList").toPromise().then(result => {
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