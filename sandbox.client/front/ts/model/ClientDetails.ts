export class ClientDetails {
  public id: string;
  public first_name: string;
  public last_name: string;
  public patronymic: string;
  public phone: string;

  public assign(o: ClientDetails): ClientDetails{
    this.id = o.id;
    this.first_name = o.first_name;
    this.last_name = o.last_name;
    this.patronymic = o.patronymic;
    this.phone = o.phone;
    return this;
  }

  constructor(){
    this.id ="";
    this.first_name="";
    this.last_name="";
    this.phone="";
  }
}