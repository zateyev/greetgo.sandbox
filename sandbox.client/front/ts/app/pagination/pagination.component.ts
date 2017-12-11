import {Component, EventEmitter, OnInit, Output} from "@angular/core";
import {HttpService} from "../HttpService";
import * as _ from "underscore";
import {ClientListRequest} from "../../model/ClientListRequest";

@Component({
  selector: 'pagination',
  template: require('./pagination.component.html')
})


export class ClientListPagination implements OnInit {

  @Output() page = new EventEmitter<ClientListRequest>();

  listInfo: ClientListRequest = new ClientListRequest();

  currentPage: number = 0;
  totalSizeOfList: number = 0;
  totalPages: number = 0;
  numberOfPages: number[] = [];

  constructor(private httpService: HttpService) {
  }

  ngOnInit() {
    this.getTotalSizeOfList();
  }

  getTotalSizeOfList() {
    this.httpService.get("/client/getSize?filter="+JSON.stringify(this.listInfo)).toPromise().then(res => {
      this.totalSizeOfList = res.json() as number;
      if(this.totalSizeOfList === 0) return null;
      this.totalPages = Math.ceil(this.totalSizeOfList / 5);
      if (this.totalSizeOfList <= 5) {
        this.listInfo.skipFirst = 0;
        this.listInfo.count = this.totalSizeOfList;
      }
      else this.setNumberOfPages();
    });
  }

  setNumberOfPages(): void {
    let startIndex = 0, endIndex = 5;
    if (this.totalPages < 6) {
      startIndex = 0;
      endIndex = this.totalPages;
      console.log(this.listInfo.filterByFio);
    }
    else {
      if (this.currentPage <= 2) {
        startIndex = 0;
        endIndex = 5;
      }
      else if (this.currentPage + 2 >= this.totalPages) {
        startIndex = this.totalPages - 5;
        endIndex = this.totalPages;
      }
      else {
        startIndex = this.currentPage - 2;
        endIndex = this.currentPage + 3;
      }
    }
    this.numberOfPages = _.range(startIndex, endIndex);
  }

  setCurrentPage(n: number) {
    this.currentPage = n;
    this.setNumberOfPages();
    let listSize = 5;
    this.listInfo.skipFirst = n * listSize;
    this.listInfo.count = this.listInfo.skipFirst + listSize;
    this.page.emit(this.listInfo);
  }
}
