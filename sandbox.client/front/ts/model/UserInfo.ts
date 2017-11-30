export class UserInfo {

  public id: string;
  public accountName: string;
  public surname: string | null;
  public name: string | null;
  public patronymic: string | null;

  public assign(o: UserInfo): UserInfo {
    this.id = o.id;
    this.accountName = o.accountName;
    this.surname = o.surname;
    this.name = o.name;
    this.patronymic = o.patronymic;
    return this;
  }
}