export class ClientPhones{
  public home:string;
  public work:string;
  public mobile:string[];

  constructor(){
    this.home = "";
    this.work = "";
    this.mobile = ["", "", ""];
  }
}