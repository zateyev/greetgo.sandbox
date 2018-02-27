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
  public long getTotalSize(RequestParameters requestParams) {
    return jdbcSandbox.get().execute(new GetTotalSize(requestParams.filterBy, requestParams.filterInput));
  }

  @Override
  public List<ClientInfo> getClientsList(RequestParameters requestParams) {
    return jdbcSandbox.get().execute(new LoadClientList(requestParams.filterBy, requestParams.filterInput, requestParams.orderBy, requestParams.isDesc, requestParams.page, requestParams.pageSize));
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
  public ClientInfo addOrUpdateClient(ClientRecordsToSave clientRecordsToSave) {

    if (clientRecordsToSave.id == null) {
      clientRecordsToSave.id = idGen.get().newId();
    }

    clientDao.get().insertOrUpdateClient(clientRecordsToSave.id,
      clientRecordsToSave.surname,
      clientRecordsToSave.name,
      clientRecordsToSave.patronymic,
      clientRecordsToSave.gender,
      Date.valueOf(clientRecordsToSave.dateOfBirth),
      clientRecordsToSave.charm.id
    );

    List<PhoneNumber> phonesToSave = clientRecordsToSave.phoneNumbers;
    List<PhoneNumber> existingPhones = clientDao.get().getPhonesByClientId(clientRecordsToSave.id);

    HashSet<String> numbersToSave = new HashSet<>();
    phonesToSave.forEach(phoneToSave -> numbersToSave.add(phoneToSave.number));

    for (PhoneNumber existingPhone : existingPhones) {
      if (!numbersToSave.contains(existingPhone.number)) {
        clientDao.get().removePhoneNumber(clientRecordsToSave.id, existingPhone.number);
      }
    }

    for (PhoneNumber phoneToSave : phonesToSave) {
      clientDao.get().insertPhoneNumber(clientRecordsToSave.id, phoneToSave.number, phoneToSave.phoneType);
    }

    Address addressFact = clientRecordsToSave.addressF;
    Address addressReg = clientRecordsToSave.addressR;
    clientDao.get().insertAddress(clientRecordsToSave.id, addressFact.type, addressFact.street, addressFact.house, addressFact.flat);
    clientDao.get().insertAddress(clientRecordsToSave.id, addressReg.type, addressReg.street, addressReg.house, addressReg.flat);


    return clientDao.get().selectClientInfoById(clientRecordsToSave.id);
  }

  @Override
  public void removeClient(String clientsId) {
    clientDao.get().removeAddressOfClient(clientsId);
    clientDao.get().removePhoneNumbersOfClient(clientsId);
    clientDao.get().removeClientAccount(clientsId);
    clientDao.get().removeClientById(clientsId);
  }
}
