export class ClientRecord {
  public id: string;
  public fio: string;
  public charm: string;
  public age: number /*int*/;
  public totalAccountBalance: number /*long*/;
  public maxAccountBalance: number /*long*/;
  public minAccountBalance: number /*long*/;


  public assign(o: any): ClientRecord {
    this.id = o.id;
    this.fio = o.fio;
    this.charm = o.charm;
    this.age = o.age;
    this.totalAccountBalance = o.totalAccountBalance;
    this.maxAccountBalance = o.maxAccountBalance;
    this.minAccountBalance = o.minAccountBalance;
    return this;
  }

  public static copy(o: any): ClientRecord {
    let ret = new ClientRecord();
    ret.assign(o);
    return ret;
  }
}