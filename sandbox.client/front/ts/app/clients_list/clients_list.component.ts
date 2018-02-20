import {Component, EventEmitter, Inject, Input, OnInit, Output} from "@angular/core";
import {HttpService} from "../HttpService";
import {PagerService} from "../PagerService";
import * as $ from 'jquery';
import 'datatables.net'
import {ClientDetails} from "../../model/ClientDetails";
import {PhoneNumber} from "../../model/PhoneNumber";
import {PhoneType} from "../../model/PhoneType";
import {ClientInfo} from "../../model/ClientInfo";
import {ClientRecords} from "../../model/ClientRecords";
import {Gender} from "../../model/Gender";

@Component({
  selector: 'clients-list-component',
  template: require('./clients-list-component.html'),
  styles: [require('./clients-list-component.css')],
})
export class ClientsListComponent {
  @Output() exit = new EventEmitter<void>();

  phoneNumbers: PhoneNumber[];
  clientsList: Array<ClientInfo> | null = null;
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
  loadClientInfoError: string | null;
  clientRecords: ClientRecords = new ClientRecords();
  charms: any = [];

  pageSizeOptions = [10, 25, 50];
  columnsId = ['fio', 'charm', 'age', 'totalBalance', 'minBalance', 'maxBalance'];
  filterColumns = ['Фамилия', 'Имя', 'Отчество'];

  columns = {
    fio: 'ФИО', charm: 'Характер', age: 'Возраст', totalBalance: 'Общий остаток счетов',
    minBalance: 'Минимальный остаток', maxBalance: 'Максимальный остаток'
  };

  formsTitle = "";
  formsBtn = "";

  phoneType = PhoneType;

  phoneTypeKeys(): Array<string> {
    let keys = Object.keys(this.phoneType);
    return keys.slice(keys.length / 5);
  }

  gender = Gender;

  genderTypes(): Array<string> {
    let keys = Object.keys(this.gender);
    return keys.slice(keys.length / 5);
  }

  constructor(private httpService: HttpService, private pagerService: PagerService) {
  }

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
    this.clientRecords.phoneNumbers.push(new PhoneNumber);
  }

  setPageSize(size: number) {
    this.pageSize = size;
    this.setPage(1);
  }

  selectClient(id: number) {
    this.selClientId = id;
    $('#edit-button').prop("disabled", false);
    $('#btn-remove').prop("disabled", false);
  }

  removePhoneNumber(index: number) {
    if (this.clientRecords.phoneNumbers.length > 1) {
      this.clientRecords.phoneNumbers.splice(index, 1);
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
      this.loadClientInfoError = error;
      this.clientsList = null;
    });
  }

  loadClientDetails() {
    this.httpService.post("/clientsList/clientDetails", {
      clientsId: this.clientsList[this.selClientId].id
    }).toPromise().then(result => {
      this.clientRecords = ClientRecords.copy(result.json());
    }, error => {
      console.log("clientRecords");
      console.log(error);
      this.loadClientInfoError = error;
    });
  }

  filterList() {
    this.getTotalSize();
    this.isInitialized = false;

    this.setPage(1);
  }

  addOrUpdateClient() {
    $('#id01').hide();
    this.httpService.post("/clientsList/addOrUpdateClient", {
      clientRecords: JSON.stringify(this.clientRecords)
    }).toPromise().then(result => {
      if (result.json()) {
        let clientInfo = ClientInfo.copy(result.json());
        this.clientsList.push(clientInfo);
        // this.getTotalSize();
        if (!this.editMode) this.totalSize++;
        this.setPage(this.currentPage + 1);
      }
    }, error => {
      console.log("addClient");
      console.log(error);
    });
  }

  removeClient() {
    this.httpService.post("/clientsList/removeClient", {
      clientsId: this.clientsList[this.selClientId].id,
      page: this.currentPage,
      pageSize: this.pageSize
    }).toPromise().then(result => {
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
      let clientInfo = ClientInfo.copy(client);
      clientsList.push(clientInfo);
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
    this.formsTitle = "Изменение данных клиента";
    this.formsBtn = "Изменить";
    $('#id01').show();
    this.editMode = true;
    this.loadClientDetails();
  }

  onAddBtnClicked() {
    this.clientRecords = new ClientDetails();
    this.formsTitle = "Добавление нового пользователя";
    this.formsBtn = "Добавить";
    $('#id01').show();
    this.editMode = false;
    this.phoneNumbers = [new PhoneNumber()];
  }

  getTotalSize() {
    this.httpService.post("/clientsList/totalSize", {
      filterBy: this.filterBy,
      filterInputs: this.filterInputs
    }).toPromise().then(result => {
      this.totalSize = result.json();
    }, error => {
      console.log("totalSize");
      console.log(error);
    });
  }

  loadCharms() {
    this.httpService.get("/charm/getCharms").toPromise().then(result => {
      this.charms = result.json();
    }, error => {
      console.log("charms");
      console.log(error);
    });
  }

  loadReport() {
    this.httpService.get("/big_report/pdf").toPromise().then(result => {
      // this.charms = result.json();
      console.log("report loaded");
    }, error => {
      console.log("report");
      console.log(error);
    });
  }

  public ngOnInit() {
    this.getTotalSize();
    this.loadCharms();
    this.loadClientsList();
  }
}
