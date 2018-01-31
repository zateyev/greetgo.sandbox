package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.errors.InvalidParameter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

import java.io.OutputStream;
import java.util.List;

import static kz.greetgo.mvc.core.RequestMethod.*;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @MethodFilter(GET)
  @Mapping("/count")
  public long getCount(@Par("clientRecordRequest") @Json ClientRecordRequest request) {
    return clientRegister.get().getCount(request);
  }

  @ToJson
  @MethodFilter(GET)
  @Mapping("/list")
  public List<ClientRecord> getRecordList(@Par("clientRecordRequest") @Json ClientRecordRequest request) {
    return clientRegister.get().getRecordList(request);
  }

  @MethodFilter(DELETE)
  @Mapping("/remove")
  public void removeRecord(@Par("clientRecordId") long id) {
    clientRegister.get().removeRecord(id);
  }

  @MethodFilter(GET)
  @ToJson
  @Mapping("/details")
  public ClientDetails getDetails(@Par("clientRecordId") Long id) {
    return clientRegister.get().getDetails(id);
  }

  @MethodFilter(POST)
  @Mapping("/save")
  public void saveDetails(@Par("clientDetailsToSave") @Json ClientDetailsToSave detailsToSave) {
    clientRegister.get().saveDetails(detailsToSave);
  }

  @NoSecurity
  @MethodFilter(GET)
  @Mapping("/report")
  public void streamRecordList(@Par("clientRecordRequest") @Json ClientRecordRequest request,
                               @Par("fileContentType") @Json FileContentType fileContentType,
                               //@ParSession("personId") String personId,
                               RequestTunnel requestTunnel) {
    String contentType, fileType;
    switch (fileContentType) {
      case PDF:
        contentType = "application/pdf";
        fileType = "pdf";
        break;
      case XLSX:
        contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        fileType = "xlxs";
        break;
      default:
        throw new InvalidParameter();
    }
    requestTunnel.setResponseContentType(contentType);
    requestTunnel.setResponseHeader("Content-Disposition", "attachment; filename=\"client_records." + fileType + "\"");

    OutputStream outStream = requestTunnel.getResponseOutputStream();
    try {
      clientRegister.get().streamRecordList(outStream, request, fileContentType, "");//personId);
      outStream.flush();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new RuntimeException();
    }
  }
}
