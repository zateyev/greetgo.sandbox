export class ClientListInfo {
  public id: number;
  //TODO: null fields?
  public fullName: string;
  public charm: string;
  public age: string;
  //TODO: numbers?
  public totalAccountBalance: string;
  public maxAccountBalance: string;
  public minAccountBalance: string;

  public assign(o: any): ClientListInfo {
    this.fullName = o.fullName;
    this.charm = o.charm;
    this.age = o.age;
    this.totalAccountBalance = o.totalAccountBalance;
    this.maxAccountBalance = o.maxAccountBalance;
    this.minAccountBalance = o.minAccountBalance;
    return this;
  }

  public static copy(a: any): ClientListInfo {
    let ret = new ClientListInfo();
    ret.assign(a);
    return ret;
  }
}
