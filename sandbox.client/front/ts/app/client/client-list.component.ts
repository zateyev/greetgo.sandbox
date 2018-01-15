import {Component} from "@angular/core";
import {HttpService} from "../HttpService";
import {ClientRecord} from "../../model/ClientRecord";
import {ClientRecordListRequest} from "../../model/ClientRecordListRequest";
import {ColumnSortType} from "../../model/ColumnSortType";

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
  records: ClientRecord[] | null = [];
  selectedRecordId: number | null = null;
  listRequest: ClientRecordListRequest = new ClientRecordListRequest();
  activeSortButtonId: string | null;

  constructor(private httpService: HttpService) {
    this.listRequest.columnSortType = ColumnSortType.NONE;
    this.listRequest.sortAscend = false;

    this.refreshClientRecordList();
  }

  private refreshClientRecordList() {
    this.selectedRecordId = null;
    // TODO: что если другой клиент удалил самую последную страницу, а мы нажмем на нее?
    this.updatePageNumeration();
    this.getClientRecordList();
  }

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
    this.listRequest.clientRecordCountToSkip = this.curPageNum * this.httpService.pageSize;
    this.listRequest.clientRecordCount = this.httpService.pageSize;

    this.httpService.get("/client/list", {
      'clientRecordListRequest': JSON.stringify(this.listRequest)
    }).toPromise().then(result => {
      this.records = (result.json() as ClientRecord[]).map(ClientRecord.copy);
    }, error => {
      console.log(error);
    });
  }

  private onClick(event) {
    if (!(<HTMLElement>event.target).classList.contains('client-record'))
      this.selectedRecordId = null;
  }

  onClientRecordButtonClick(recordId: number) {
    this.selectedRecordId = recordId;
  }

  onSortingButtonClick(sortButtonId: string, columnSortTypeName: string, sortAscend: boolean) {
    /*if(columnSortTypeName == this.listRequest.columnSortType) {
     if (columnSortTypeName == "NONE")
     return;

     if (sortAscend == this.listRequest.sortAscend)
     return;
     }*/

    this.activeSortButtonId = sortButtonId;
    this.listRequest.columnSortType = columnSortTypeName as ColumnSortType;
    this.listRequest.sortAscend = sortAscend;
    this.curPageNum = 0;

    this.refreshClientRecordList();
  }

  onPageNumberButtonClick(pageNum: number) {
    // TODO: может ли клиент кликать по текущей странице?
    //if (this.curPageNum == pageNum)
    //  return;

    this.curPageNum = pageNum;
    this.refreshClientRecordList();
  }

  onClientRecordEditButtonClick(isEditOperation: boolean) {

  }

  onClientRecordRemoveButtonClick() {
    this.httpService.get("/client/remove", {
      'clientRecordId': this.selectedRecordId
    }).toPromise().then(result => {
      let ret = result.json() as boolean;
      console.log("received " + ret);

      if (ret) {
        this.refreshClientRecordList();

        console.log("this.records.length " + this.records.length);
        console.log("this.pageCount " + this.pageCount);
        console.log("this.curPageNum " + this.curPageNum);
        //TODO размер списка должен приходить измененным, но это не так
        if (this.records.length == 1) {
          this.curPageNum--;
          if (this.curPageNum < 0)
            this.curPageNum = 0;
          else
            this.refreshClientRecordList();
        }
      } else {
        alert("Операция удаления не удалась");
        this.refreshClientRecordList();
      }
    }, error => {
      console.log(error);
    });
  }
}
