import {Component, EventEmitter, OnInit, Output} from "@angular/core";
import {ClientRecord} from "../../model/ClientRecord"
import {HttpService} from "../HttpService";
@Component({
  selector: 'list-component',
  template: require('./list-component.html'),
  styles: [require('./list.component.css')],
})
export class ListComponent implements OnInit{
  @Output() exit = new EventEmitter<void>();
  loading:boolean = true;
  errorLoading:boolean = false;
  modalChangeForm:boolean = false;
  list: ClientRecord[] = [];
  clientToChange:ClientRecord = null;

  openModalChangeForm(id:string){
    this.modalChangeForm = true;
    this.clientToChange = this.list.find(i => i.id === id);
  }

  closeModalChangeForm(){
    this.modalChangeForm = false;
  }


  constructor(private httpService: HttpService) {}
  ngOnInit(): void {
    this.loadList();
    this.loading = false;
  }

  loadList(){
    this.httpService.get("/client/getList").toPromise().then(result => {
        this.list = result.json().map(ClientRecord.copy);
    }, error => {
        this.errorLoading = true;
        console.log(error);
    });
  }
}