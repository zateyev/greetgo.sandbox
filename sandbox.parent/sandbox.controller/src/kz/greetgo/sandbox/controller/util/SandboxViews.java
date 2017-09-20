package kz.greetgo.sandbox.controller.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.interfaces.MethodInvokedResult;
import kz.greetgo.mvc.interfaces.MethodInvoker;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.SessionParameterGetter;
import kz.greetgo.mvc.interfaces.Views;
import kz.greetgo.sandbox.controller.errors.JsonRestError;
import kz.greetgo.sandbox.controller.errors.RestError;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.model.SessionInfo;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.security.SecurityError;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;

public abstract class SandboxViews implements Views {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String toJson(Object object, RequestTunnel tunnel, Method method) throws Exception {
    return convertToJson(object);
  }

  private String convertToJson(Object object) throws Exception {
    if (object == null) return null;
    return objectMapper.writer().writeValueAsString(object);
  }

  @Override
  public String toXml(Object object, RequestTunnel tunnel, Method method) throws Exception {
    throw new UnsupportedOperationException();
  }

  public BeanGetter<AuthRegister> userRegister;

  @Override
  public void performRequest(MethodInvoker methodInvoker) throws Exception {

    beforeRequest();

    prepareSession(methodInvoker);

    MethodInvokedResult invokedResult = methodInvoker.invoke();
    if (invokedResult.tryDefaultRender()) return;

    if (invokedResult.error() != null) {
      performError(methodInvoker, invokedResult);
    } else {
      performRender(methodInvoker, invokedResult);
    }
  }

  protected void beforeRequest() {}

  private void prepareSession(MethodInvoker methodInvoker) {
    try {
      if (methodInvoker.getMethodAnnotation(NoSecurity.class) == null) {
        userRegister.get().checkTokenAndPutToThreadLocal(methodInvoker.tunnel().getRequestHeader("Token"));
      } else {
        userRegister.get().cleanTokenThreadLocal();
      }
    } catch (RestError restError) {
      restError.printStackTrace();
      return;
    }
  }

  @Override
  public Object getSessionParameter(SessionParameterGetter.ParameterContext context, RequestTunnel tunnel) {
    if ("personId".equals(context.parameterName())) {
      if (context.expectedReturnType() != String.class) throw new SecurityError("personId must be string");

      SessionInfo sessionInfo = userRegister.get().getSessionInfo();
      if (sessionInfo == null) throw new SecurityError("No session");
      return sessionInfo.personId;
    }

    throw new SecurityError("Unknown session parameter " + context.parameterName());
  }

  private void performRender(MethodInvoker methodInvoker, MethodInvokedResult invokedResult) {
    assert invokedResult.error() == null;
    Object returnedValue = invokedResult.returnedValue();
    if (returnedValue == null) return;

    if (!(returnedValue instanceof String)) {
      throw new IllegalArgumentException("Cannot view " + returnedValue.getClass()
        + " with value " + returnedValue);
    }

    String place = (String) returnedValue;

    RequestTunnel tunnel = methodInvoker.tunnel();

    for (Map.Entry<String, Object> e : methodInvoker.model().data.entrySet()) {
      tunnel.setRequestAttribute(e.getKey(), e.getValue());
    }

    tunnel.forward(place, true);
    return;
  }

  private void performError(MethodInvoker methodInvoker, MethodInvokedResult invokedResult) throws Exception {
    Throwable error = invokedResult.error();
    assert error != null;

    RequestTunnel tunnel = methodInvoker.tunnel();
    tunnel.setRequestAttribute("ERROR_TYPE", error.getClass().getSimpleName());

    if (error instanceof JsonRestError) {
      JsonRestError restError = (JsonRestError) error;
      tunnel.setResponseStatus(restError.statusCode);
      String json = convertToJson(restError.sendingAsJsonObject);
      if (json != null) try (final PrintWriter writer = tunnel.getResponseWriter()) {
        writer.print(json);
      }
      return;
    }

    if (error instanceof RestError) {
      RestError restError = (RestError) error;
      tunnel.setResponseStatus(restError.statusCode);

      if (restError.getMessage() != null) {
        try (final PrintWriter writer = tunnel.getResponseWriter()) {
          writer.print(restError.getMessage());
        }
      }

      return;
    }

    {
      tunnel.setResponseStatus(500);
      try (final PrintWriter writer = tunnel.getResponseWriter()) {
        writer.println("Internal server error: " + error.getMessage());
        writer.println();
        error.printStackTrace(writer);
      }

      error.printStackTrace();
    }

    return;
  }

}
