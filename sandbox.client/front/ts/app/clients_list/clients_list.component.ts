import {Component, EventEmitter, Inject, Input, OnInit, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PagerService} from "../PagerService";
import * as $ from 'jquery';
import 'datatables.net'
import {NgForm, Validators} from "@angular/forms";
import {ClientDot} from "../../model/ClientDot";
import {PhoneNumber} from "../../model/PhoneNumber";
import {PhoneType} from "../../model/PhoneType";

@Component({
  selector: 'clients-list-component',
  template: require('./clients-list-component.html'),
  styles: [require('./clients-list-component.css')],
})
export class ClientsListComponent {
  @Output() exit = new EventEmitter<void>();

  phoneNumbers: any = [];
  clientsList: Array<UserInfo> | null = null;
  selectedClient: ClientDot = new ClientDot();
  editMode: boolean = false;
  currentPage: number = 0;
  pageSize: number = 10;
  selClientId: number;
  totalSize: number = 0;
  pager: any = {};
  isInitialized: boolean = false;
  isDescending: boolean = false;
  filterBy: string | null;
  filterInputs: string | null;
  orderBy: string | null;
  loadUserInfoError: string | null;

  pageSizeOptions = [10, 25, 50];
  columnsId = ['fio', 'charm', 'age', 'totalBalance', 'minBalance', 'maxBalance'];
  columns = {fio:'ФИО', charm:'Характер', age:'Возраст', totalBalance:'Общий остаток счетов',
    minBalance:'Минимальный остаток', maxBalance:'Максимальный остаток'};

  gender = ["муж", "жен"];
  charms: any = [];

  formsTitle = "";
  formsBtn = "";

  phoneType = PhoneType;
  phoneTypeKeys() : Array<string> {
    let keys = Object.keys(this.phoneType);
    return keys.slice(keys.length / 5);
  }

  constructor(private httpService: HttpService, private pagerService: PagerService) {}

  sort(colId: number) {
    if (colId > 1) {
      if (this.orderBy != this.columnsId[colId]) {
        this.orderBy = this.columnsId[colId];
        this.isDescending = false;
      }
      else {
        this.isDescending = !this.isDescending;
      }

      this.loadClientsList();

      if (this.isDescending) {
        $(this).find('.glyphicon').removeClass('glyphicon-arrow-down').removeClass('glyphicon-sort').addClass('glyphicon-arrow-up');
      } else {
        $(this).find('.glyphicon').removeClass('glyphicon-arrow-up').removeClass('glyphicon-sort').addClass('glyphicon-arrow-down');
      }
    }
  }

  addPhoneNumber() {
    this.phoneNumbers.push(new PhoneNumber());
  }

  setPageSize(size: number) {
    this.pageSize = size;
    console.log(size);

    this.setPage(1);
  }

  selectClient(id: number) {
    this.selClientId = id;
    $('#edit-button').prop("disabled", false);
    $('#btn-remove').prop("disabled", false);
    console.log(id);
  }

  removePhoneNumber(index: number) {
    if (this.phoneNumbers.length > 1) {
      this.phoneNumbers.splice(index, 1);
    }
  }

  setPage(page: number) {
    if (page < 1 || page > this.pager.totalPages) {
      return;
    }
    // get pager object from service
    this.pager = this.pagerService.getPager(this.totalSize, page, this.pageSize);

    this.currentPage = page - 1;

    this.loadClientsList();
  }

  loadClientsList() {
    this.httpService.post("/clientsList/clientsList", {
      filterBy: this.filterBy,
      filterInputs: this.filterInputs,
      orderBy: this.orderBy,
      isDesc: this.isDescending.toString(),
      page: this.currentPage,
      pageSize: this.pageSize
  }).toPromise().then(result => {
      this.clientsList = this.parseClientsList(result.json());
      if (!this.isInitialized) {
        this.isInitialized = true;
        this.setPage(1);
      }
    }, error => {
      console.log("ClientsList");
      console.log(error);
      this.loadUserInfoError = error;
      this.clientsList = null;
    });
  }

  loadClientDetails() {
    this.httpService.post("/clientsList/clientDetails", {
      clientsId: this.clientsList[this.selClientId].id
    }).toPromise().then(result => {
      console.log(result.json());
      this.selectedClient = ClientDot.copy(result.json());
      if (result.json().phoneNumbers && result.json().phoneNumbers.length > 0) {
        this.phoneNumbers = this.parsePhoneNumbers(result.json().phoneNumbers);
      } else {
        this.phoneNumbers = [new PhoneNumber()];
      }
    }, error => {
      console.log("clientDetails");
      console.log(error);
      this.loadUserInfoError = error;
    });
  }

  filterList(f: NgForm) {
    console.log(f.value);
    this.filterBy = f.value.filterBy;
    this.filterInputs = f.value.filterInputs;
    this.getTotalSize(this.filterBy, this.filterInputs);
    this.isInitialized = false;

    this.setPage(1);
    // this.loadClientsList();
  }

  modalFormSubmitted(formVals: NgForm) {
    if (this.editMode) {
      this.updateClient(formVals);
    }
    else {
      this.addClient(formVals);
    }
  }

  addClient(clientRecord: NgForm) {
    this.httpService.post("/clientsList/addClient", {
      newClient: JSON.stringify(clientRecord.value)
    }).toPromise().then(result => {
      let userInfo = UserInfo.copy(result.json());
      this.clientsList.push(userInfo);
      this.totalSize++;
      this.setPage(this.currentPage + 1);
    }, error => {
      console.log("addClient");
      console.log(error);
    });
  }

  updateClient(clientParams: NgForm) {
    console.log(clientParams.value);
    let params = Object.assign({}, clientParams.value, {
      id: this.clientsList[this.selClientId].id
    });
    this.httpService.post("/clientsList/updateClient", {
      clientParams: JSON.stringify(params)
    }).toPromise().then(result => {
      this.clientsList[this.selClientId] = UserInfo.copy(result.json());
    }, error => {
      console.log("updateClient");
      console.log(error);
    });
  }

  removeClient() {
    this.httpService.post("/clientsList/removeClient", {
      clientsId: this.clientsList[this.selClientId].id,
      page: this.currentPage,
      pageSize: this.pageSize
    }).toPromise().then(result => {
      console.log(result.json());

      this.clientsList.splice(this.selClientId, 1);
      this.totalSize--;
      this.setPage(this.currentPage + 1);
    }, error => {
      console.log("removeClient");
      console.log(error);
    });
  }

  private parseClientsList(clients: any) {
    let clientsList = [];

    for (let client of clients) {
      let userInfo = UserInfo.copy(client);
      clientsList.push(userInfo);
    }

    return clientsList;
  }

  private parsePhoneNumbers(phoneNumbers: any) {
    let numbers = [];

    for (let item of phoneNumbers) {
      let phoneNumber = PhoneNumber.copy(item);
      numbers.push(phoneNumber);
    }

    return numbers;
  }

  onEditBtnClicked() {
    console.log(this.selectedClient);
    this.formsTitle = "Изменение данных клиента";
    this.formsBtn = "Изменить";
    $('#id01').show();
    this.editMode = true;
    this.loadClientDetails();
  }

  onAddBtnClicked() {
    this.formsTitle = "Добавление нового пользователя";
    this.formsBtn = "Добавить";
    $('#id01').show();
    this.editMode = false;
    this.phoneNumbers = [new PhoneNumber()];
  }

  onModalFormAction() {
    $('#id01').hide();
    this.selectedClient = new ClientDot();
  }

  getTotalSize(filterBy: string, filterInputs: string) {
    this.httpService.post("/clientsList/totalSize", {
      filterBy: filterBy,
      filterInputs: filterInputs
    }).toPromise().then(result => {
      console.log(result.json());
      this.totalSize = result.json();
    }, error => {
      console.log("totalSize");
      console.log(error);
    });
  }

  loadCharms() {
    this.httpService.get("/clientsList/charms").toPromise().then(result => {
      console.log(result.json());
      this.charms = result.json();
    }, error => {
      console.log("charms");
      console.log(error);
    });
  }

  public ngOnInit()
  {
    this.getTotalSize(null, null);
    this.loadCharms();
    this.loadClientsList();
  }
}
