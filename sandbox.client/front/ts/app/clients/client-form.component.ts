import {Component, EventEmitter, OnInit, Output} from "@angular/core";
import {ClientDetailsRecord} from "../../model/ClientDetailsRecord";
import {HttpService} from "../HttpService";


@Component({
  selector: "client-form",
  template: require('./client-form.component.html'),
})
export class ClientFormComponent implements OnInit {
  shown: boolean = false;
  client: ClientDetailsRecord = new ClientDetailsRecord();
  charms: string[] = ["Сангвиник", "Холерик", "Флегматик", "Меланхолик"];

  @Output() closeModal = new EventEmitter<void>();

  constructor(private httpService: HttpService) {}

  ngOnInit(): void {

  }

  closeModalForm() {
    this.closeModal.emit();
  }

  clientId: string | null;

  show(clientId: string | null) {
    this.clientId = clientId;
    this.shown = true;
  }

}