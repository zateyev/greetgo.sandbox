import {Component, ElementRef, HostListener} from "@angular/core";
import {HttpService} from "../HttpService";
import {ClientListInfo} from "../../model/ClientListInfo";

@Component({
  selector: 'client-list-component',
  template: require('./client-list-component.html'),
  styles: [require('./client-list-component.css')],
})

export class ClientListComponent {
  page: number = 1;
  pages;
  clients: ClientListInfo[] = [];
  focusedClientId: number | null = null;//TODO rename to selected...

  clientClicked(clientId: number) {
    this.focusedClientId = clientId;
  }

  //TODO: clicking on non-client disables buttons
  @HostListener('document:click', ['$event'])
  clickout(event) {
    if (this.eRef.nativeElement.contains(event.target)) {
    } else {
      this.focusedClientId = null;
    }
  }

  constructor(private httpService: HttpService, private eRef: ElementRef) {
    this.updatePageNumeration();
    this.getClientList();
  }

  //TODO переименовать
  private getClientList() {
    this.httpService.get("/client/list", {
      //skipFirstCount
      'page': this.page,
      //pageSize
      'size': this.httpService.pageSize
    }).toPromise().then(result => {
      this.clients = (result.json() as ClientListInfo[]).map(ClientListInfo.copy);
    }, error => {
      console.log(error);
    });
  }

  private updatePageNumeration() {
    this.httpService.get("/client/pageNum", {
      'size': this.httpService.pageSize
    }).toPromise().then(result => {
      this.pages = [];

      for (let i = 1; i <= result.json(); i++) {
        this.pages.push(i);
      }
    }, error => {
      console.log(error);
    });
  }

  edit(clientId: number | null) {
    //TODO open edit form
  }
}
