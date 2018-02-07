import {Component, EventEmitter, OnInit, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PagerService} from "../PagerService";
import {NgForm} from "@angular/forms";

@Component({
    selector: 'clients-list-component',
    template: require('./clients-list-component.html'),
    styles: [require('./clients-list-component.css')],
})
export class ClientsListComponent {
    @Output() exit = new EventEmitter<void>();

    clientsList: Array<UserInfo> | null = null;
    loadUserInfoButtonEnabled: boolean = true;
    loadUserInfoError: string | null;
    currentPage: number = 0;
    pageSize: number = 15;
    totalClientsNumber: number = 0;
    pagedItems: any[];
    pager: any = {};

    fio = 'ФИО';

    constructor(private httpService: HttpService, private pagerService: PagerService) {}

    setPage(page: number) {
        if (page < 1 || page > this.pager.totalPages) {
            return;
        }
        // get pager object from service
        this.pager = this.pagerService.getPager(this.totalClientsNumber, page, this.pageSize);
        // get current page of items
        this.pagedItems = this.clientsList.slice(this.pager.startIndex, this.pager.endIndex + 1);

        this.currentPage = page - 1;
        this.loadClientsListButtonClicked();
    }

    loadClientsListButtonClicked() {
        this.loadUserInfoButtonEnabled = false;
        this.loadUserInfoError = null;

        this.httpService.post("/auth/clientsList", {
            page: this.currentPage,
            pageSize: this.pageSize
        }).toPromise().then(result => {
            this.clientsList = this.loadClientsList(result.json().clients);
            this.totalClientsNumber = result.json().totalClientsNumber;
            // console.log(result.json());
        }, error => {
            console.log("ClientsList");
            console.log(error);
            this.loadUserInfoButtonEnabled = true;
            this.loadUserInfoError = error;
            this.clientsList = null;
        });
    }

    filter(f: NgForm) {
        console.log(f.value);
        this.httpService.post("/auth/filterClientsList", f.value).toPromise().then(result => {
            this.clientsList = this.loadClientsList(result.json());
        }, error => {
            console.log("ClientsList");
            console.log(error);
        });
    }

    createNewClient(newClient: NgForm) {
        console.log(newClient.value);
    }

    private loadClientsList(clients: any) {
        let clientsList = [];

        for (let client of clients) {
            let userInfo = UserInfo.copy(client);
            clientsList.push(userInfo);
        }

        return clientsList;
    }

    public ngOnInit()
    {
        this.httpService.post("/auth/clientsList", {
            page: this.currentPage,
            pageSize: this.pageSize
        }).toPromise().then(result => {
            this.clientsList = this.loadClientsList(result.json().clients);
            this.totalClientsNumber = result.json().totalClientsNumber;
            console.log(result.json().totalClientsNumber);
            // this.allItems = this.loadClientsList(result.json());
            this.setPage(1);

        }, error => {
            console.log("ClientsList");
            console.log(error);
            this.loadUserInfoButtonEnabled = true;
            this.loadUserInfoError = error;
            this.clientsList = null;
        });

        let phonesCount = 1;
        $(document).on('click', '.btn-add', function(e)
        {
            e.preventDefault();

            let sInputGroup = $(this).parent().parent().parent().parent(),
                currentEntry = $(this).parents('.entry:first'),
                newEntry = $(currentEntry.clone()).appendTo(sInputGroup);

            newEntry.find('#inputPhoneType' + phonesCount).attr('name', 'phoneType' + (phonesCount + 1));

            sInputGroup.find('.entry:not(:last) .btn-add')
                .removeClass('btn-add').addClass('btn-remove')
                .removeClass('btn-success').addClass('btn-danger')
                .html('<span class="glyphicon glyphicon-minus"></span>');

            phonesCount++;
        }).on('click', '.btn-remove', function(e)
        {
            $(this).parents('.entry:first').remove();

            e.preventDefault();
            phonesCount--;
            return false;
        });

        $('#clientsListTable').on('click', '.clickable-row', function(event) {
            $(this).addClass('active').siblings().removeClass('active');
            $('#edit-button').prop("disabled", false);
            $('#new-client-button').prop("disabled", false);
        });

        $( "#entry-num" ).on('change', function() {
            // alert( $(this).val() );
            // this.pageSize = 20;
            // this.loadClientsListButtonClicked();
        });
    }
}
