import {Component, ElementRef, HostListener} from "@angular/core";
import {HttpService} from "../HttpService";
import {ClientRecord} from "../../model/ClientRecord";

@Component({
  selector: 'client-list-component',
  template: require('./client-list-component.html'),
  styles: [require('./client-list-component.css')],
})

export class ClientListComponent {
  curPageNum: number = 1;
  pageCount: number;
  pageNums: number[] = [];
  clientRecords: ClientRecord[] = [];
  selectedClientRecordId: number | null = null;

  clientRecordClicked(clientId: number) {
    this.selectedClientRecordId = clientId;
  }

  //TODO: clicking on non-client record disables buttons
  @HostListener('document:click', ['$event'])
  clickout(event) {
    if (this.eRef.nativeElement.contains(event.target)) {
    } else {
      this.selectedClientRecordId = null;
    }
  }

  constructor(private httpService: HttpService, private eRef: ElementRef) {
    this.updatePageNumeration();
    this.getClientRecordList();
  }

  private getClientRecordList() {
    this.httpService.get("/client/list", {
      'clientRecordCountToSkip': this.curPageNum * this.httpService.pageSize,
      'clientRecordCount': this.httpService.pageSize
    }).toPromise().then(result => {
      this.clientRecords = (result.json() as ClientRecord[]).map(ClientRecord.copy);
    }, error => {
      console.log(error);
    });
  }

  private updatePageNumeration() {
    this.httpService.get("/client/pageCount", {
      'clientRecordCount': this.httpService.pageSize
    }).toPromise().then(result => {
      this.pageCount = result.json() as number;
      for (let i = 0; i < this.pageCount; i++) {
        this.pageNums[i] = i + 1;
      }
    }, error => {
      console.log(error);
    });
  }

  editClientRecord(clientRecordId: number | null) {
    //TODO open edit form
  }

  removeClientRecord(clientRecordId: number) {

  }
}
