import {Component} from "@angular/core";
import {HttpService} from "../HttpService";
import {ClientRecord} from "../../model/ClientRecord";

@Component({
  selector: 'client-list-component',
  template: require('./client-list-component.html'),
  styles: [require('./client-list-component.css')],
  host: {'(document:click)': 'onClick($event)'},
})

export class ClientListComponent {
  curPageNum: number = 0;
  pageCount: number;
  pageNums: number[];

  //charmDictionary: any[];

  clientRecords: ClientRecord[] = [];
  selectedClientRecordId: number | null = null;

  constructor(private httpService: HttpService) {
    //this.getInitData();
    this.updatePageNumeration();
    this.getClientRecordList();
  }

  /*private getInitData() {
   this.httpService.get("/client/init/charm", {}).toPromise().then(result => {
   this.charmDictionary = result.json() as any[];
   }, error => {
   console.log(error);
   });
   }*/

  private updatePageNumeration() {
    this.httpService.get("/client/pageCount", {
      'clientRecordCount': this.httpService.pageSize
    }).toPromise().then(result => {
      this.pageCount = result.json() as number;
      this.pageNums = [];
      for (let i = 0; i < this.pageCount; i++) {
        this.pageNums[i] = i + 1;
      }
    }, error => {
      console.log(error);
    });
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

  clientRecordClicked(clientId: number) {
    this.selectedClientRecordId = clientId;
  }

  //Оно используется
  onClick(event) {
    if (!(<HTMLElement>event.target).classList.contains('client-record'))
      this.selectedClientRecordId = null;
  }

  pageNumClicked(pageNum: number) {
    // TODO: может ли клиент кликать по текущей странице?
    //if (this.curPageNum == pageNum)
    //  return;

    this.curPageNum = pageNum;
    this.selectedClientRecordId = null;
    // TODO: что если другой клиент удалил самую последную страницу, а мы нажмем на нее?
    this.updatePageNumeration();
    this.getClientRecordList();
  }

  editClientRecord(clientRecordId: number | null) {

  }

  removeClientRecord(clientRecordId: number) {
    let ret: boolean = false;

    this.httpService.get("/client/remove", {
      'clientRecordId': clientRecordId
    }).toPromise().then(result => {
      ret = result.json() as boolean;
      console.log("received " + ret);

      if (ret) {
        this.pageNumClicked(this.curPageNum);

        console.log(this.clientRecords.length);
        console.log(this.pageCount);
        console.log(this.curPageNum);

        //TODO размер списка должен приходить измененным, но это не так
        if (this.clientRecords.length == 1) {
          this.curPageNum--;

          if (this.curPageNum < 0)
            this.curPageNum = 0;
          else {
            this.getClientRecordList();
          }
        }
      }
    }, error => {
      console.log(error);
    });
  }
}
