import {Component, EventEmitter, Inject, Input, OnInit, Output, ViewChild} from "@angular/core";
import {HttpService} from "../HttpService";
import {PagerService} from "../PagerService";
import * as $ from 'jquery';
import 'datatables.net'
import {PhoneNumber} from "../../model/PhoneNumber";
import {ClientInfo} from "../../model/ClientInfo";
import {saveAs as importedSaveAs} from "file-saver";
import {ClientFormComponent} from "../client_form/client_form.component";

@Component({
  selector: 'clients-list-component',
  template: require('./clients-list-component.html'),
  styles: [require('./clients-list-component.css')],
})
export class ClientsListComponent implements OnInit {
  @Output() exit = new EventEmitter<void>();

  @ViewChild(ClientFormComponent)
    private clientForm: ClientFormComponent;

  clientsList: Array<ClientInfo> | null = null;
  currentPage: number = 0;
  pageSize: number = 10;
  selectedClientId: number;
  totalSize: number = 0;
  pager: any = {};
  isDescending: boolean = false;
  filterBy = 'surname';
  filterInputs: string | null;
  orderBy: string | null;
  loadClientInfoError: string | null;

  pageSizeOptions = [10, 25, 50];
  viewTypes = ['xlsx', 'pdf'];

  columns = [
    {key: 'fio', value: 'ФИО'},
    {key: 'charm', value: 'Характер'},
    {key: 'age', value: 'Возраст'},
    {key: 'totalBalance', value: 'Общий остаток счетов'},
    {key: 'minBalance', value: 'Минимальный остаток'},
    {key: 'maxBalance', value: 'Максимальный остаток'}
  ];

  filterColumns = [
    {key: 'surname', value: 'Фамилия'},
    {key: 'name', value: 'Имя'},
    {key: 'patronymic', value: 'Отчество'}
  ];

  viewType = "";

  constructor(private httpService: HttpService, private pagerService: PagerService) {
  }

  sort(colId: number) {
    if (colId > 1) {
      if (this.orderBy != this.columns[colId].key) {
        this.orderBy = this.columns[colId].key;
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

  setPageSize(size: number) {
    this.pageSize = size;
    this.setPage(1);
  }

  selectClient(id: number) {
    this.selectedClientId = id;
    this.clientForm.clientIdToUpdate = this.clientsList[this.selectedClientId].id;
    $('#edit-button').prop("disabled", false);
    $('#btn-remove').prop("disabled", false);
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
    }, error => {
      console.log("ClientsList");
      console.log(error);
      this.loadClientInfoError = error;
      this.clientsList = null;
    });
  }

  filterList() {
    this.currentPage = 0;
    this.getTotalSize();
  }

  removeClient() {
    this.httpService.post("/clientsList/removeClient", {
      clientsId: this.clientsList[this.selectedClientId].id,
      page: this.currentPage,
      pageSize: this.pageSize
    }).toPromise().then(result => {
      this.clientsList.splice(this.selectedClientId, 1);
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
    this.clientForm.openForUpdate();
  }

  onAddBtnClicked() {
    this.clientForm.openForAdding();
  }

  getTotalSize() {
    this.httpService.post("/clientsList/totalSize", {
      filterBy: this.filterBy,
      filterInputs: this.filterInputs
    }).toPromise().then(result => {
      this.totalSize = result.json();
      this.setPage(this.currentPage + 1);
    }, error => {
      console.log("totalSize");
      console.log(error);
    });
  }

  loadReport(type: string) {
    this.viewType = type;
    this.httpService.downloadFile("/report/" + this.viewType, {
      filterBy: this.filterBy,
      filterInputs: this.filterInputs,
      orderBy: this.orderBy,
      isDesc: this.isDescending.toString()
    }).subscribe(blob => {
      importedSaveAs(blob, "report." + this.viewType);
    });
  }

  public ngOnInit() {
    // this.loadCharms();
    this.getTotalSize();
  }
}
