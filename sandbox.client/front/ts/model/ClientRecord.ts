export class ClientRecord {
  public id: number/*long*/;
  public fullName: string;
  public charmName: string;
  public age: number/*int*/;
  public totalAccountBalance: string;
  public maxAccountBalance: string;
  public minAccountBalance: string;

  public assign(o: any): ClientRecord {
    this.id = o.id;
    this.fullName = o.fullName;
    this.charmName = o.charmName;
    this.age = o.age;
    this.totalAccountBalance = o.totalAccountBalance;
    this.maxAccountBalance = o.maxAccountBalance;
    this.minAccountBalance = o.minAccountBalance;
    return this;
  }

  public static copy(a: any): ClientRecord {
    let ret = new ClientRecord();
    ret.assign(a);
    return ret;
  }
}
