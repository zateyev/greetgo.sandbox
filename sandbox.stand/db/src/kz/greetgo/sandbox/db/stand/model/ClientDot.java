package kz.greetgo.sandbox.db.stand.model;


import kz.greetgo.sandbox.controller.model.ClientRecord;

public class ClientDot {
  public String id, fio,  charm;
  public int age,  totalBalance,  maxBalance, minBalance;

  public ClientRecord toClientRecord(){
    ClientRecord ret = new ClientRecord();
    ret.id = id;
    ret.fio = fio;
    ret.charm = charm;
    ret.age = age;
    ret.totalBalance = totalBalance;
    ret.maxBalance = maxBalance;
    ret.minBalance = minBalance;
    return ret;
  }
}
