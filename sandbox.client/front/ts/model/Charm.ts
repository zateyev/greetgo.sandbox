export class Charm {
  public id: string;
  public name: string | null;
  public description: string | null;
  public energy: number/*int*/;

  public assign(o: any): Charm {
    this.id = o.id;
    this.name = o.name;
    this.description = o.description;
    this.energy = o.energy;
    return this;
  }

  public static copy(a: any): Charm {
    let ret = new Charm();
    ret.assign(a);
    return ret;
  }
}