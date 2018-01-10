//TODO rename to ClientRecord
export class ClientListInfo {
  public id: number/*long*/;
  public fullName: string | null;
  public charm: string | null;
  public age: string | null;
  public totalAccountBalance: number | null/*int*/;
  public maxAccountBalance: number | null/*int*/;
  public minAccountBalance: number | null/*int*/;

  public assign(o: any): ClientListInfo {
    this.id = o.id;
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
