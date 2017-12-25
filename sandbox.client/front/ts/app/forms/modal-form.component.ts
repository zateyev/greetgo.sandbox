import {Component, EventEmitter, OnInit, Output} from "@angular/core";
import {ClientDetailsRecord} from "../../model/ClientDetailsRecord";
import {HttpService} from "../HttpService";


@Component({
  selector: "modal-form-component",
  template: require('./modal-form.component.html'),
})
export class ModalFormComponent implements OnInit{
  client: ClientDetailsRecord = new ClientDetailsRecord();
  charms: string[] = ["Сангвиник", "Холерик", "Флегматик", "Меланхолик"];

  @Output() closeModal = new EventEmitter<void>();

  constructor(private httpService: HttpService) {}

  ngOnInit(): void {

  }

  closeModalForm(){
    this.closeModal.emit();
  }

}