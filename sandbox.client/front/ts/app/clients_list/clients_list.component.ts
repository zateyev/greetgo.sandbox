import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PagerService} from "../PagerService";
import * as $ from 'jquery';
import 'datatables.net'
import {NgForm} from "@angular/forms";
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

    clientsList: Array<UserInfo> | null = null;
    selectedClient: ClientDot = new ClientDot();
    ageDesc: boolean = false;
    tBDesc: boolean = false;
    minBDesc: boolean = false;
    maxBDesc: boolean = false;
    editMode: boolean = false;
    loadUserInfoError: string | null;
    currentPage: number = 0;
    pageSize: number = 10;
    selClientId: number;
    totalClientsNumber: number = 0;
    pagedItems: any[];
    pager: any = {};

    fio = 'ФИО';

    phoneType = PhoneType;
    keys() : Array<string> {
        let keys = Object.keys(this.phoneType);
        return keys.slice(keys.length / 5);
    }

    constructor(private httpService: HttpService, private pagerService: PagerService) {}

    addPhoneNumber() {
        this.phoneNumbers.push(new PhoneNumber());
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
        this.pagedItems = this.clientsList.slice(this.pager.startIndex, this.pager.endIndex + 1);

        this.currentPage = page - 1;
        this.loadClientsList();
    }

    loadClientsList() {
        this.loadUserInfoError = null;

        this.httpService.post("/auth/clientsList", {
            page: this.currentPage,
            pageSize: this.pageSize
        }).toPromise().then(result => {
            this.clientsList = this.parseClientsList(result.json().clients);
            this.totalClientsNumber = result.json().totalClientsNumber;
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
            this.phoneNumbers = this.parsePhoneNumbers(result.json().phoneNumbers);
            console.log(this.phoneNumbers[0].number);
            // this.totalClientsNumber = result.json().totalClientsNumber;
        }, error => {
            console.log("clientsFullInfo");
            console.log(error);
            this.loadUserInfoError = error;
        });
    }

    filter(f: NgForm) {
        console.log(f.value);
        this.httpService.post("/auth/filterClientsList", Object.assign({}, f.value, {
            page: this.currentPage,
            pageSize: this.pageSize
        })).toPromise().then(result => {
            console.log(Object.assign({}, f.value, {
                page: this.currentPage,
                pageSize: this.pageSize
            }));
            this.clientsList = this.parseClientsList(result.json().clients);
            this.totalClientsNumber = result.json().totalClientsNumber;
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
            console.log(result);
            // TODO
        }, error => {
            console.log("addNewClient");
            console.log(error);
        });
    }

    updateClient(clientParams: NgForm) {
        let params = Object.assign({}, clientParams.value, {
            // id: this.selectedClientsId,
            id: this.clientsList[this.selClientId].id,
            page: this.currentPage,
            pageSize: this.pageSize
        });
        // console.log(JSON.stringify(params));
        this.httpService.post("/auth/updateClient", {
            clientParams: JSON.stringify(params)
        }).toPromise().then(result => {
            this.clientsList = this.parseClientsList(result.json().clients);
            this.totalClientsNumber = result.json().totalClientsNumber;
            // this.loadClientsList();
        }, error => {
            console.log("updateClient");
            console.log(error);
        });
    }

    removeClient() {
        this.httpService.post("/auth/removeClient", {
            // clientsId: this.selectedClientsId,
            id: this.clientsList[this.selClientId].id,
            page: this.currentPage,
            pageSize: this.pageSize
        }).toPromise().then(result => {
            console.log(result.json());
            this.clientsList = this.parseClientsList(result.json().clients);
            this.totalClientsNumber = result.json().totalClientsNumber;
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

    public ngOnInit()
    {
        let self = this;

        this.httpService.post("/auth/clientsList", {
            page: this.currentPage,
            pageSize: this.pageSize
        }).toPromise().then(result => {
            this.clientsList = this.parseClientsList(result.json().clients);
            this.totalClientsNumber = result.json().totalClientsNumber;
            console.log(result.json().totalClientsNumber);
            this.setPage(1);

        }, error => {
            console.log("ClientsList");
            console.log(error);
            this.loadUserInfoError = error;
            this.clientsList = null;
        });

        $('#clientsListTable').on('click', '.clickable-row', function(event) {
            self.selClientId = parseInt($(this).attr('id'));

            console.log(self.selClientId);

            $(this).addClass('active').siblings().removeClass('active');
            $('#edit-button').prop("disabled", false);
            $('#btn-remove').prop("disabled", false);
            // self.loadClientsFullInfo();
        });

        $('#edit-button').on('click', function (event) {
            self.editMode = true;
            self.loadClientsFullInfo();
        });

        $('#add-client-btn').on('click', function (event) {
            self.editMode = false;
        });

        $('#age').on('click', function(event) {
            self.sort("age", self.ageDesc);
            if (self.ageDesc) {
                $(this).find('.glyphicon').removeClass('glyphicon-arrow-down').removeClass('glyphicon-sort').addClass('glyphicon-arrow-up');
            } else {
                $(this).find('.glyphicon').removeClass('glyphicon-arrow-up').removeClass('glyphicon-sort').addClass('glyphicon-arrow-down');
            }
            self.ageDesc = !self.ageDesc;
        });

        $('#totalBalance').on('click', function(event) {
            self.sort("totalBalance", self.tBDesc);
            if (self.tBDesc) {
                $(this).find('.glyphicon').removeClass('glyphicon-arrow-down').removeClass('glyphicon-sort').addClass('glyphicon-arrow-up');
            } else {
                $(this).find('.glyphicon').removeClass('glyphicon-arrow-up').removeClass('glyphicon-sort').addClass('glyphicon-arrow-down');
            }
            self.tBDesc = !self.tBDesc;
        });

        $('#minBalance').on('click', function(event) {
            self.sort("minBalance", self.minBDesc);
            if (self.minBDesc) {
                $(this).find('.glyphicon').removeClass('glyphicon-arrow-down').removeClass('glyphicon-sort').addClass('glyphicon-arrow-up');
            } else {
                $(this).find('.glyphicon').removeClass('glyphicon-arrow-up').removeClass('glyphicon-sort').addClass('glyphicon-arrow-down');
            }
            self.minBDesc = !self.minBDesc;
        });

        $('#maxBalance').on('click', function(event) {
            self.sort("maxBalance", self.maxBDesc);
            if (self.maxBDesc) {
                $(this).find('.glyphicon').removeClass('glyphicon-arrow-down').removeClass('glyphicon-sort').addClass('glyphicon-arrow-up');
            } else {
                $(this).find('.glyphicon').removeClass('glyphicon-arrow-up').removeClass('glyphicon-sort').addClass('glyphicon-arrow-down');
            }
            self.maxBDesc = !self.maxBDesc;
        });

        $( "#entry-num" ).on('change', function() {
            self.pageSize = parseInt($(this).val().toString());
            if (!self.ageDesc) {
                $(this).find('.glyphicon').removeClass('glyphicon-sort-by-order').addClass('glyphicon-sort-by-order-alt');
            } else {
                $(this).find('.glyphicon').removeClass('glyphicon-sort-by-order-alt').addClass('glyphicon-sort-by-order');
            }
            self.loadClientsList();
        });

        $('#modal-form-submit').on('click', function (event) {
            $('#id01').hide();
            // location.reload();
            self.selectedClient = new ClientDot();
        });

        $('#cancel-modal-submit').on('click', function (event) {
            $('#id01').hide();
            // location.reload();
            self.selectedClient = new ClientDot();
        });
    }
}
