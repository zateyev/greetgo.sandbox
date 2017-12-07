export class ClientToSave {
  public id: string;
  public name: string;
  public surname: string;
  public patronymic: string;
  public phone: string;

  public assign(o: any): ClientToSave {
    this.id = o.id;
    this.name = o.name;
    this.surname = o.surname;
    if (o.patronymic == null) o.patronymic = "";
    this.patronymic = o.patronymic;
    this.phone = o.phone;
    return this;
  }
}