import {Component} from "@angular/core";

export class Client{
  constructor(
    public surname: string,
    public name: string,
    public patronymic: string,
    public gender: string,
    public birth_date: string,
    public charm: string,
    public factAddrStreet: string,
    public factAddrHouse: number,
    public factAddrFlat: number,
    public regAddrStreet: string,
    public regAddrHouse: number,
    public regAddrFlat: number,
    public homePhone: string,
    public workPhone: string,
    public mobilePhone: string
  ){}
}

@Component({
  selector: "modal-form-component",
  template: require('./modal-form.component.html')
})
export class ModalFormComponent {
  client: Client = new Client("", "", "", "", "", "", "", 0, 0, "", 0, 0, "", "", "");
  clients: Client[] = [];
  charms: string[] = ["Сангвиник", "Холерик", "Флегматик", "Меланхолик"];

  addClient(){
    this.clients.push(new Client(this.client.surname, this.client.name, this.client.patronymic, this.client.gender,
      this.client.birth_date, this.client.charm, this.client.factAddrStreet, this.client.factAddrHouse,
      this.client.factAddrFlat, this.client.regAddrStreet, this.client.regAddrHouse,
      this.client.regAddrFlat, this.client.homePhone, this.client.workPhone, this.client.mobilePhone));
  }
}