import {Component, EventEmitter, OnInit, Output} from "@angular/core";
import {HttpService} from "../HttpService";
import * as _ from "underscore";

@Component({
  selector: 'pagination',
  template: require('./pagination.component.html')
})


export class ClientListPagination implements OnInit {

  @Output() page = new EventEmitter<number>();

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
    this.httpService.get("/client/getSize").toPromise().then(res => {
      this.totalSizeOfList = res.json() as number;
      if(this.totalSizeOfList === 0) return null;
      console.log(this.totalSizeOfList + " TOTAL SIZE");
      this.totalPages = Math.ceil(this.totalSizeOfList / 5);
      this.setNumberOfPages();
    });
  }

  setNumberOfPages(): void {
    let startIndex = 0, endIndex = 5;
    console.log("total " + this.totalPages + "   " + "current " + this.currentPage);
    if (this.totalPages < 6) {
      startIndex = 0;
      endIndex = this.totalPages;
      console.log("total end set");
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

  setCurrentPage(p: number) {
    this.currentPage = p;
    this.setNumberOfPages();
    this.page.emit(this.currentPage);
  }
}
