export class ClientRecord{
  public id: string;
  public fio: string;

  public assign(o: ClientRecord): ClientRecord {
    this.id = o.id;
    this.fio = o.fio;
    return this;
  }

  public static copy(o: any): ClientRecord {
    let ret = new ClientRecord();
    ret.assign(o);
    return ret;
  }
}