import {Component, EventEmitter, Inject, Input, OnInit, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PagerService} from "../PagerService";
import * as $ from 'jquery';
import 'datatables.net'
import {FormBuilder, FormGroup, NgForm, Validators} from "@angular/forms";
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

    phoneNumbers: Array<PhoneNumber> = [new PhoneNumber()];
    // phoneNumbers = [new PhoneNumber()];

    clientsList: Array<UserInfo> | null = null;
    selectedClient: ClientDot = new ClientDot();
    editMode: boolean = false;
    loadUserInfoError: string | null;
    currentPage: number = 0;
    pageSize: number = 10;
    selClientId: number;
    totalClientsNumber: number = 0;
    pagedItems: any[];
    pager: any = {};
    filterParams: NgForm;
    listMode: string = "";
    sortBy: string = "";
    isDescending: boolean = false;

    pageSizeOptions = [10, 25, 50];
    columns = ['ФИО', 'Характер', 'Возраст', 'Общий остаток счетов', 'Минимальный остаток', 'Максимальный остаток'];
    columnsId = ['fio', 'charm', 'age', 'totalBalance', 'minBalance', 'maxBalance'];
    gender = ["муж", "жен"];
    charm = ["Уситчивый", "Агрессивный", "Общительный"];

    fio = 'ФИО';

    phoneType = PhoneType;
    keys() : Array<string> {
        let keys = Object.keys(this.phoneType);
        return keys.slice(keys.length / 5);
    }



    // form: FormGroup;
    constructor(/*@Inject(FormBuilder) fb: FormBuilder, */private httpService: HttpService, private pagerService: PagerService) {
        // this.form = fb.group({
        //     name: fb.group({
        //         first: ['Nancydf', Validators.minLength(2)],
        //         last: 'Drew',
        //     }),
        //     email: '',
        // });
    }

    orderBy(colId: number) {
        if (colId > 1) {
            if (this.sortBy != this.columnsId[colId]) {
                this.sortBy = this.columnsId[colId];
                this.isDescending = false;
            }
            this.sort(this.sortBy, this.isDescending);
            if (this.isDescending) {
                $(this).find('.glyphicon').removeClass('glyphicon-arrow-down').removeClass('glyphicon-sort').addClass('glyphicon-arrow-up');
            } else {
                $(this).find('.glyphicon').removeClass('glyphicon-arrow-up').removeClass('glyphicon-sort').addClass('glyphicon-arrow-down');
            }
            this.isDescending = !this.isDescending;
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
        this.pager = this.pagerService.getPager(this.totalClientsNumber, page, this.pageSize);
        // get current page of items
        // this.pagedItems = this.clientsList.slice(this.pager.startIndex, this.pager.endIndex + 1);

        this.currentPage = page - 1;

        if (this.listMode == "whole") {
            this.loadClientsList();
        } else if (this.listMode == "filtered") {
            this.filterList(this.filterParams);
        } else if (this.listMode == "sorted") {
            this.sort(this.sortBy, !this.isDescending);
        }
    }

    loadClientsList() {
        this.loadUserInfoError = null;

        this.httpService.post("/auth/clientsList", {
            page: this.currentPage,
            pageSize: this.pageSize
        }).toPromise().then(result => {
            this.clientsList = this.parseClientsList(result.json().clients);
            this.totalClientsNumber = result.json().totalClientsNumber;
            if (this.listMode != "whole") {
                this.listMode = "whole";
                this.setPage(1);
            }
        }, error => {
            console.log("ClientsList");
            console.log(error);
            this.loadUserInfoError = error;
            this.clientsList = null;
        });
    }

    loadClientsFullInfo() {
        this.httpService.post("/auth/clientsFullInfo", {
            clientsId: this.clientsList[this.selClientId].id
        }).toPromise().then(result => {
            console.log(result.json());
            this.selectedClient = ClientDot.copy(result.json());
            if (result.json().phoneNumbers && result.json().phoneNumbers.length > 0) {
                this.phoneNumbers = this.parsePhoneNumbers(result.json().phoneNumbers);
            }
        }, error => {
            console.log("clientsFullInfo");
            console.log(error);
            this.loadUserInfoError = error;
        });
    }

    filterList(f: NgForm) {
        console.log(f.value);
        this.filterParams = f;


        this.httpService.post("/auth/filterClientsList", Object.assign({}, this.filterParams.value, {
            page: this.currentPage,
            pageSize: this.pageSize
        })).toPromise().then(result => {
            console.log(Object.assign({}, this.filterParams.value, {
                page: this.currentPage,
                pageSize: this.pageSize
            }));
            this.clientsList = this.parseClientsList(result.json().clients);
            this.totalClientsNumber = result.json().totalClientsNumber;
            if (this.listMode != "filtered") {
                this.listMode = "filtered";
                this.setPage(1);
            }
        }, error => {
            console.log("filterClientsList");
            console.log(error);
        });

    }

    sort(sortBy: string, desc: boolean) {
        this.httpService.post("/auth/sortClientsList", {
            sortBy: sortBy,
            desc: desc.toString(),
            page: this.currentPage,
            pageSize: this.pageSize
        }).toPromise().then(result => {
            this.clientsList = this.parseClientsList(result.json().clients);
            this.totalClientsNumber = result.json().totalClientsNumber;
            this.listMode = "sorted";
        }, error => {
            console.log("sortClientsList");
            console.log(error);
        });
    }

    modalFormSubmitted(formVals: NgForm) {
        if (this.editMode) {
            this.updateClient(formVals);
        }
        else {
            this.addNewClient(formVals);
        }
    }

    addNewClient(newClient: NgForm) {
        console.log(newClient.value);
        this.httpService.post("/auth/addNewClient", {
            newClient: JSON.stringify(newClient.value)
        }).toPromise().then(result => {
            console.log(result.json());
            let userInfo = UserInfo.copy(result.json());
            this.clientsList.push(userInfo);
            this.totalClientsNumber++;
            this.setPage(this.currentPage + 1);
        }, error => {
            console.log("addNewClient");
            console.log(error);
        });
    }

    updateClient(clientParams: NgForm) {
        let params = Object.assign({}, clientParams.value, {
            // id: this.selectedClientsId,
            id: this.clientsList[this.selClientId].id
        });
        console.log(JSON.stringify(params));
        this.httpService.post("/auth/updateClient", {
            clientParams: JSON.stringify(params)
        }).toPromise().then(result => {
            // this.clientsList = this.parseClientsList(result.json().clients);
            this.clientsList[this.selClientId] = UserInfo.copy(result.json());
            this.totalClientsNumber = result.json().totalClientsNumber;
            // this.setPage(this.currentPage + 1);
            // this.loadClientsList();
        }, error => {
            console.log("updateClient");
            console.log(error);
        });
    }

    removeClient() {
        this.httpService.post("/auth/removeClient", {
            clientsId: this.clientsList[this.selClientId].id,
            page: this.currentPage,
            pageSize: this.pageSize
        }).toPromise().then(result => {
            console.log(result.json());
            // this.clientsList = this.parseClientsList(result.json().clients);
            // this.totalClientsNumber = result.json().totalClientsNumber;

            this.clientsList.splice(this.selClientId, 1);
            this.totalClientsNumber--;
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
        $('#id01').show();
        this.editMode = true;
        this.loadClientsFullInfo();
    }

    onAddBtnClicked() {
        $('#id01').show();
        this.editMode = false;
    }

    onModalFormAction() {
        $('#id01').hide();
        this.selectedClient = new ClientDot();
    }

    public ngOnInit()
    {
        // let self = this;

        this.loadClientsList();

        // $('#modal-form-submit').on('click', function (event) {
        //     $('#id01').hide();
        //     // location.reload();
        //     self.selectedClient = new ClientDot();
        // });
        //
        // $('#cancel-modal-submit').on('click', function (event) {
        //     $('#id01').hide();
        //     // location.reload();
        //     self.selectedClient = new ClientDot();
        // });
    }
}
