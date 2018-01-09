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
  clients;
  focusedClientId: number | null = null;

  //TODO: clicking on non-client disables buttons
  @HostListener('document:click', ['$event'])
    clickout(event) {
      if(this.eRef.nativeElement.contains(event.target)) {

      } else {
        this.focusedClientId = null;
      }
  }

  constructor(private httpService: HttpService, private eRef: ElementRef) {
    this.updatePageNumeration();
    this.getClientList();
  }

  private getClientList() {
    this.httpService.get("/client/list", {
      'page':this.page,
      'size':this.httpService.pageSize
    }).toPromise().then(result => {
      this.clients = [];
      let temp = result.json();

      for(let i = 0; i < temp.length; i++) {
        this.clients[i] = ClientListInfo.copy(temp[i]);
      }
    }, error => {
      console.log(error);
    });
  }

  private updatePageNumeration() {
    this.httpService.get("/client/pageNum", {
      'size':this.httpService.pageSize
    }).toPromise().then(result => {
      this.pages = [];

      for(let i = 1; i <= result.json(); i++) {
        this.pages.push(i);
      }
    }, error => {
      console.log(error);
    });
  }

  clientClicked(clientId: number) {
    this.focusedClientId = clientId;
  }
}
