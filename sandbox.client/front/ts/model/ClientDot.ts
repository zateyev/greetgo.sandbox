
import {UserInfo} from "./UserInfo";
import {PhoneNumber} from "./PhoneNumber";
import {Address} from "./Address";

export class ClientDot {
    public id: string;
    public surname: string;
    public name: string;
    public patronymic: string;
    public charm: string;
    public gender: string;
    public dateOfBirth: string;
    public addressF: Address;
    public addressR: Address;
    public phoneNumbers: Array<PhoneNumber>;
    public totalBalance: number;
    public minBalance: number;
    public maxBalance: number;


    constructor() {
        this.addressF = new Address();
        this.addressR = new Address();
    }

    public assign(o: any): ClientDot {
        this.id = o.id;
        this.name = o.name;
        this.surname = o.surname;
        this.patronymic = o.patronymic;
        this.charm = o.charm;
        this.gender = o.gender;
        this.dateOfBirth= o.dateOfBirth;

        this.totalBalance = o.totalBalance;
        this.minBalance = o.minBalance;
        this.maxBalance = o.maxBalance;

        if (o.addressF) {
            let addressF = new Address();
            addressF.street = o.addressF.street;
            addressF.building = o.addressF.building;
            addressF.apartment = o.addressF.apartment;
            this.addressF = addressF;
        }

        if (o.addressR) {
            let addressR = new Address();
            addressR.street = o.addressR.street;
            addressR.building = o.addressR.building;
            addressR.apartment = o.addressR.apartment;
            this.addressR = addressR;
        }

        //
        // this.phoneNumbers = [];
        // for (let phoneNumber of o.phoneNumbers) {
        //     let phone = new PhoneNumber();
        //     phone.phoneType = phoneNumber.phoneType;
        //     phone.number = phoneNumber.number;
        //     this.phoneNumbers.push(phone);
        // }

        return this;
    }

    public static copy(a: any): ClientDot {
        let ret = new ClientDot();
        ret.assign(a);
        return ret;
    }
}
