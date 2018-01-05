package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.register_impl.jdbc.*;
import kz.greetgo.sandbox.db.report.ClientRecord.ClientRecordListReportViewPdf;
import kz.greetgo.sandbox.db.report.ClientRecord.ClientRecordListReportViewXslx;
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import kz.greetgo.util.RND;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {
  public BeanGetter<ClientDao> clientDao;

  public BeanGetter<JdbcSandbox> jdbc;

  @Override
  public long getSize(ClientListRequest clientListRequest) {
    return jdbc.get().execute(new GetClientListSize(clientListRequest));
  }

  @Override
  public List<ClientRecord> getList(ClientListRequest clientListRequest) {
    return jdbc.get().execute(new GetClientList(clientListRequest));
  }


  @Override
  public ClientDetails getClient(String id) {
    ClientDetails ret = new ClientDetails();

    if (id != null && !"".equals(id)) {
      ret = clientDao.get().loadDetails(id);
      ret.factAddress = clientDao.get().getFactAddress(id);
      ret.regAddress = clientDao.get().getRegAddress(id);
      ClientPhones phones = new ClientPhones();

      ret.phones = jdbc.get().execute(new GetClientPhones(id));
    }
    ret.charms = clientDao.get().loadCharmList();

    return ret;
  }

  public BeanGetter<IdGenerator> idGen;

  @Override
  public ClientRecord saveClient(ClientToSave clientToSave) {

    java.sql.Date dateOfBirth = java.sql.Date.valueOf(clientToSave.dateOfBirth);

    if (clientToSave.id != null) {

      clientDao.get().updateClient(
        clientToSave.id,
        clientToSave.name.trim(),
        clientToSave.surname.trim(),
        clientToSave.patronymic.trim(),
        clientToSave.gender.trim().toLowerCase(),
        clientToSave.charmId,
        dateOfBirth
      );

      clientDao.get().updateAddress(
        clientToSave.id,
        "fact",
        clientToSave.factAddress.street,
        clientToSave.factAddress.house,
        clientToSave.factAddress.flat
      );

      clientDao.get().updateAddress(
        clientToSave.id,
        "reg",
        clientToSave.regAddress.street,
        clientToSave.regAddress.house,
        clientToSave.regAddress.flat
      );

      clientDao.get().deleteClientPhone(clientToSave.id);

      clientDao.get().insertClientPhone(
        clientToSave.id,
        clientToSave.phones.home,
        "home"
      );

      clientDao.get().insertClientPhone(
        clientToSave.id,
        clientToSave.phones.work,
        "work"
      );

      for (String s : clientToSave.phones.mobile)
        clientDao.get().insertClientPhone(
          clientToSave.id,
          s,
          "mobile"
        );

    } else {
      clientToSave.id = idGen.get().newId();
      clientDao.get().insertClient(
        clientToSave.id,
        clientToSave.name,
        clientToSave.surname,
        clientToSave.patronymic,
        dateOfBirth,
        clientToSave.gender,
        clientToSave.charmId);

      clientDao.get().insertClientAddress(
        clientToSave.id,
        clientToSave.factAddress.street,
        clientToSave.factAddress.house,
        clientToSave.factAddress.flat,
        "fact");

      clientDao.get().insertClientAddress(
        clientToSave.id,
        clientToSave.regAddress.street,
        clientToSave.regAddress.house,
        clientToSave.regAddress.flat,
        "reg");

      clientDao.get().insertClientPhone(
        clientToSave.id,
        clientToSave.phones.home,
        "home"
      );

      clientDao.get().insertClientPhone(
        clientToSave.id,
        clientToSave.phones.work,
        "work"
      );

      for (String s : clientToSave.phones.mobile)
        clientDao.get().insertClientPhone(
          clientToSave.id,
          s,
          "mobile"
        );

      clientDao.get().insertClientAccount(
        idGen.get().newId(),
        clientToSave.id,
        0,
        RND.intStr(10),
        new Date()
      );

    }

    ClientRecord rec = clientDao.get().getClientRecord(clientToSave.id);

    return rec;
  }

  @Override
  public void deleteClient(String id) {
    if (id == null) return;

    clientDao.get().deleteClient(id);
    clientDao.get().deleteClientAddress(id);
    clientDao.get().deleteClientPhone(id);
    clientDao.get().deleteClientAccount(id);
  }


  @Override
  public void download(ClientListRequest clientListRequest, OutputStream outputStream, String contentType, String personId) throws Exception {

    if (contentType.contains("pdf")) {

     ClientRecordListReportViewPdf pdf = new ClientRecordListReportViewPdf();
      clientListRequest.count = 0;
     try{
       pdf.start(outputStream);

       pdf.initContent();

       jdbc.get().execute(new GetPdfReport(pdf, clientListRequest));
     }
     finally {
       pdf.close(outputStream);
     }

    } else {
      ClientRecordListReportViewXslx xslx = new ClientRecordListReportViewXslx(outputStream);
      clientListRequest.count = 0;

      try {
        xslx.start(new Date());

       jdbc.get().execute(new GetXlsxReport(xslx, clientListRequest));

      } finally {
        xslx.finish();
      }
    }
  }

}
