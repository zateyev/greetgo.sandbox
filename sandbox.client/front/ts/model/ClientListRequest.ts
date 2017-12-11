export class ClientListRequest {
  public skipFirst: number /*int*/;
  public count: number /*int*/;
  public sort: string;
  public filterByFio: string;

  constructor() {
    this.skipFirst = 0;
    this.count = 5;
    this.sort = "";
    this.filterByFio = "";
  }
}