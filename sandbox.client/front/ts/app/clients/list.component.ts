import {Component, EventEmitter, OnInit, Output, ViewChild} from "@angular/core";
import {ClientRecord} from "../../model/ClientRecord";
import {HttpService} from "../HttpService";
import {ChangeComponent} from "./change.component";

@Component({
  selector: 'list-component',
  template: require('./list.component.html'),
  styles: [require('./list.component.css')],
})
export class ListComponent implements OnInit {
  @Output() exit = new EventEmitter<void>();

  @ViewChild("changeForm") changeForm: ChangeComponent;

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
    this.changeForm.showForm(id);
  }

  closeModalForm(client:any) {
    this.modalChangeForm = false;
    // this.modalAddForm = false;
    //this.clientDetails = null;
    //this.changeForm.closeForm();
    console.log(client);
  }


  openModalAddForm() {
    this.idForChange = "";
    this.modalChangeForm = true;
  }

  loadList() {
    let page: number = 1;
    this.httpService.get("/client/getList?page=" + page).toPromise().then(result => {
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