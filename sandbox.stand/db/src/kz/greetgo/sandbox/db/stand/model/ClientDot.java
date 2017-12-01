package kz.greetgo.sandbox.db.stand.model;

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

  public ClientRecord toClientRecord(){
    ClientRecord rec = new ClientRecord();
    rec.id = this.id;
    rec.fio = this.surname + " " + this.name + " " + this.patronymic;
    return rec;
  }
}
