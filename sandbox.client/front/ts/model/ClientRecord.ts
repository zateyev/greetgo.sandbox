export class ClientRecord {
  public id: string;
  public fio: string;
  public charm: string;
  public age: number /*int*/;
  public totalBalance: number /*int*/;
  public maxBalance: number /*int*/;
  public minBalance: number /*int*/;

  assign(a: any) {
    this.id = a.id;
    this.fio = a.fio;
    this.charm = a.charm;
    this.age = a.age;
    this.totalBalance = a.totalBalance;
    this.maxBalance = a.maxBalance;
    this.minBalance = a.minBalance;
  }

  static copy(a: any): ClientRecord {
    let ret = new ClientRecord();
    ret.assign(a);
    return ret;
  }
}