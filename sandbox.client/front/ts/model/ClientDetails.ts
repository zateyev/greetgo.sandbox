export class ClientDetails {
  public id: string;
  public name: string;
  public surname: string;
  public patronymic: string;
  public temper: string;
  public gender: string;
  public dateOfBirth: string;
  public firstAddress: string[];
  public secondAddress: string[];
  public phones: string[];


  constructor(){
    // this.id = "";
    // this.name = "";
    // this.surname = "";
    // this.patronymic = "";
    // this.gender = "";
    // this.temper = "";
    // this.dateOfBirth = "";
    this.firstAddress = [];
    this.secondAddress = [];
    this.phones = [];
  }

  public assign(o: any): ClientDetails{
    this.id = o.id;
    this.name = o.name;
    this.surname = o.surname;
    this.patronymic = o.patronymic;
    this.gender = o.gender;
    this.temper = o.temper;
    this.dateOfBirth = o.dateOfBirth;
    this.firstAddress = o.firstAddress;
    this.secondAddress = o.secondAddress;
    this.phones = o.phones;
    return this;
  }

}