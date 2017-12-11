export class ListInfo{
  public startIndex:number /*int*/;
  public endIndex: number /*int*/;
  public sort:string;
  public filter: string;

  constructor(){
    this.startIndex = 0;
    this.endIndex = 5;
    this.sort = "";
    this.filter = "";
  }
}