package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.jdbc.GetTotalSize;
import kz.greetgo.sandbox.db.jdbc.LoadClientList;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<JdbcSandbox> jdbcSandbox;
  public BeanGetter<IdGenerator> idGen;

  @Override
  public long getTotalSize(String filterBy, String filterInput) {
    return jdbcSandbox.get().execute(new GetTotalSize(filterBy, filterInput));
  }

  @Override
  public List<ClientInfo> getClientsList(String filterBy, String filterInput, String orderBy, boolean isDesc, int page, int pageSize) {
    return jdbcSandbox.get().execute(new LoadClientList(filterBy, filterInput, orderBy, isDesc, page, pageSize));
  }

  @Override
  public ClientDetails getClientDetails(String clientId) {
    ClientDetails clientDetails = clientDao.get().selectClientDetailsById(clientId);
    if (clientDetails == null) throw new NotFound();
    clientDetails.addressF = clientDao.get().selectAddrByClientId(clientId, AddressType.FACT);
    clientDetails.addressR = clientDao.get().selectAddrByClientId(clientId, AddressType.REG);
    clientDetails.phoneNumbers = clientDao.get().getPhonesByClientId(clientId);
    return clientDetails;
  }

  @Override
  public ClientInfo addOrUpdateClient(ClientRecords clientRecords) {

    if (clientRecords.id == null) {
      clientRecords.id = idGen.get().newId();
    }

    clientDao.get().insertOrUpdateClient(clientRecords.id,
      clientRecords.surname,
      clientRecords.name,
      clientRecords.patronymic,
      clientRecords.gender,
      Date.valueOf(clientRecords.dateOfBirth),
      clientRecords.charm.id
    );

    List<PhoneNumber> phonesToSave = clientRecords.phoneNumbers;
    List<PhoneNumber> existingPhones = clientDao.get().getPhonesByClientId(clientRecords.id);

    HashSet<String> numbersToSave = new HashSet<>();
    phonesToSave.forEach(phoneToSave -> numbersToSave.add(phoneToSave.number));

    for (PhoneNumber existingPhone : existingPhones) {
      if (!numbersToSave.contains(existingPhone.number)) {
        clientDao.get().removePhoneNumber(clientRecords.id, existingPhone.number);
      }
    }

    for (PhoneNumber phoneToSave : phonesToSave) {
      clientDao.get().insertPhoneNumber(clientRecords.id, phoneToSave.number, phoneToSave.phoneType);
    }

    Address addressFact = clientRecords.addressF;
    Address addressReg = clientRecords.addressR;
    clientDao.get().insertAddress(clientRecords.id, addressFact.type, addressFact.street, addressFact.house, addressFact.flat);
    clientDao.get().insertAddress(clientRecords.id, addressReg.type, addressReg.street, addressReg.house, addressReg.flat);


    return clientDao.get().selectClientInfoById(clientRecords.id);
  }

  @Override
  public void removeClient(String clientsId) {
    clientDao.get().removeAddressOfClient(clientsId);
    clientDao.get().removePhoneNumbersOfClient(clientsId);
    clientDao.get().removeClientAccount(clientsId);
    clientDao.get().removeClientById(clientsId);
  }
}
