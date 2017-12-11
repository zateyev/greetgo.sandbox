export class CharmRecord {
  public id: number/*int*/;
  public charm: string;

  public assign(o: any): CharmRecord {
    this.id = o.id;
    this.charm = o.charm;
    return this;
  }

  public static copy(o: any): CharmRecord {
    let ch = new CharmRecord();
    ch.assign(o);
    return ch;
  }
}