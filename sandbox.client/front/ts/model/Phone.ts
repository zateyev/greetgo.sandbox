import {PhoneType} from "./PhoneType";

export class Phone {
  public number: string;
  public type: PhoneType;

  public assign(o: any): Phone {
    this.number = o.number;
    this.type = o.type;
    return this;
  }

  public static compare(a: any, b: any): boolean {
    return a.number == b.number && a.type == b.type;
  }

  public static copy(a: any): Phone {
    let ret = new Phone();
    ret.assign(a);
    return ret;
  }
}
