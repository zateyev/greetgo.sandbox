
import {PhoneType} from "./PhoneType";

export class PhoneNumber {
    public phoneType: PhoneType;
    public number: number;

    public assign(o: any): PhoneNumber {
        this.phoneType = o.phoneType;
        this.number = o.number;

        return this;
    }

    public static copy(a: any): PhoneNumber {
        let ret = new PhoneNumber();
        ret.assign(a);
        return ret;
    }
}
