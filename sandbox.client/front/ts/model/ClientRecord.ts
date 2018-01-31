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
    this.totalAccountBalance = parseFloat(o.totalAccountBalance).toFixed(2).toString();
    this.maxAccountBalance = parseFloat(o.maxAccountBalance).toFixed(2).toString();
    this.minAccountBalance = parseFloat(o.minAccountBalance).toFixed(2).toString();
    return this;
  }

  public static copy(a: any): ClientRecord {
    let ret = new ClientRecord();
    ret.assign(a);
    return ret;
  }
}
