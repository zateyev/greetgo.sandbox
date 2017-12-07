package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.util.ArrayList;

/**
 * Created by jgolibzhan on 11/30/17.
 */
public class ClientDot {

  public String id;
  public String name;
  public String surname;
  public String patronymic;
  public String phone;

  public ClientRecord toClientRecord(){
    ClientRecord rec = new ClientRecord();
    rec.id = this.id;
    rec.fio = this.surname + " " + this.name + " " + this.patronymic;
    return rec;
  }

  public ClientDetails toClientDetails(){
    ClientDetails rec = new ClientDetails();
    rec.id = this.id;
    rec.name = this.name;
    rec.surname = this.surname;
    rec.patronymic = this.patronymic;
    rec.phone = this.phone;
    return rec;
  }
}
