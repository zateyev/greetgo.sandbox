import {Component} from "@angular/core";
import {HttpService} from "../HttpService";
import {ClientRecord} from "../../model/ClientRecord";
import {ClientRecordListRequest} from "../../model/ClientRecordListRequest";
import {ColumnSortType} from "../../model/ColumnSortType";
import {ClientDetailsComponent} from "./client-details.component";

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
  filterSuccessState: boolean | null = null;
  isModalFormActive: boolean = false;

  constructor(private httpService: HttpService) {
    this.listRequest.columnSortType = ColumnSortType.NONE;
    this.listRequest.sortAscend = false;
    this.listRequest.nameFilter = "";

    this.refreshClientRecordList();
  }

  private refreshClientRecordList() {
    this.selectedRecordId = null;
    this.updatePageNumeration();
    this.getClientRecordList();
  }

  private updatePageNumeration() {
    this.httpService.get("/client/count", {
      'clientRecordNameFilter': this.listRequest.nameFilter
    }).toPromise().then(result => {
      this.pageCount = Math.floor(result.json() as number / this.httpService.pageSize);
      if ( result.json() as number % this.httpService.pageSize > 0 )
        this.pageCount++;

      this.pageNums = [];

      if (this.pageCount > 0) {
        this.filterSuccessState = true;

        for (let i = 0; i < this.pageCount; i++) {
          this.pageNums[i] = i + 1;
        }
      } else {
        this.filterSuccessState = false;
      }
    }, error => {
      console.log(error);
    });
  }

  private getClientRecordList() {
    this.listRequest.clientRecordCountToSkip = this.curPageNum * this.httpService.pageSize;
    this.listRequest.clientRecordCount = this.httpService.pageSize;

    console.log(JSON.stringify(this.listRequest));

    this.httpService.get("/client/list", {
      'clientRecordListRequest': JSON.stringify(this.listRequest)
    }).toPromise().then(result => {
      this.records = (result.json() as ClientRecord[]).map(ClientRecord.copy);

      if (this.records.length == 0) {
        this.curPageNum--;
        if (this.curPageNum < 0)
          this.curPageNum = 0;
        else
          this.refreshClientRecordList();
      }
    }, error => {
      console.log(error);
    });
  }

  private onClick(event: any) {
    /*console.log("active " + this.isModalFormActive);

     if (!(<HTMLElement>event.target).classList.contains('modal')) {
     event.stopPropagation();
     }

     if (!this.isModalFormActive && !(<HTMLElement>event.target).classList.contains('client-record-keep-selection'))
     this.selectedRecordId = null;*/
  }

  protected onFilterTextChange(event) {
    if (this.filterSuccessState || this.filterSuccessState == false)
      this.filterSuccessState = null;
  }

  onFilterButtonClick(filterValue: any) {
    let json = JSON.stringify(filterValue).trim();
    let filter = JSON.parse(json).filter;

    if (filter == null || filter.length == 0)
      filter = "";

    this.listRequest.nameFilter = filter;
    this.refreshClientRecordList();
  }

  onClientRecordClick(recordId: number) {
    this.selectedRecordId = recordId;
  }

  onSortingButtonClick(columnSortTypeName: string, sortAscend: boolean) {
    this.listRequest.columnSortType = columnSortTypeName as ColumnSortType;
    this.listRequest.sortAscend = sortAscend;
    this.curPageNum = 0;

    this.refreshClientRecordList();
  }

  isSortingButtonActive(columnSortTypeName: string, sortAscend: boolean): boolean {
    if (columnSortTypeName == this.listRequest.columnSortType) {
      if (columnSortTypeName == "NONE")
        return true;

      if (sortAscend == this.listRequest.sortAscend)
        return true;
    }

    return false;
  }

  onPageNumberButtonClick(pageNum: number) {
    // TODO: может ли клиент кликать по текущей странице?
    //if (this.curPageNum == pageNum)
    //  return;

    this.curPageNum = pageNum;
    this.refreshClientRecordList();
  }

  onClientRecordEditButtonClick(clientRecordComponent: ClientDetailsComponent, isEditOperation: boolean) {
    isEditOperation ? clientRecordComponent.show(this.selectedRecordId) : clientRecordComponent.show(null);
  }

  onClientRecordRemoveButtonClick() {
    this.httpService.delete("/client/remove", {
      'clientRecordId': this.selectedRecordId
    }).toPromise().then(result => {
      this.refreshClientRecordList();
    }, error => {
      alert("Операция удаления не удалась");
      console.log(error);
    });
  }

  onModalClose(event: any) {
    this.refreshClientRecordList();
  }
}
