export class ClientRecord {
  public id: number/*long*/;
  public fullName: string;
  public charm: string;
  public age: number/*int*/;
  //TODO: уверены ли мы, что данные придут не null?
  public totalAccountBalance: number/*long*/;
  public maxAccountBalance: number/*long*/;
  public minAccountBalance: number/*long*/;

  public assign(o: any): ClientRecord {
    this.id = o.id;
    this.fullName = o.fullName;
    this.charm = o.charm;
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
