export class ClientToSave {
  public id: string;
  public name: string;
  public surname: string;
  public patronymic: string;
  public gender: string;
  public charmId: string;
  public dateOfBirth: string;
  public firstAddress: string[];
  public secondAddress: string[];
  public phones: string[];

  public assign(o: any): ClientToSave {
    this.id = o.id;
    this.name = o.name;
    this.surname = o.surname;
    this.dateOfBirth = o.dateOfBirth;
    this.charmId = o.charmId;
    this.firstAddress = o.firstAddress;
    this.phones = o.phones;
    this.gender = o.gender;
    if (o.patronymic == null) o.patronymic = "";
    if (o.secondAddress == null) o.secondAddress = o.firstAddress;
    this.patronymic = o.patronymic;
    return this;
  }
}