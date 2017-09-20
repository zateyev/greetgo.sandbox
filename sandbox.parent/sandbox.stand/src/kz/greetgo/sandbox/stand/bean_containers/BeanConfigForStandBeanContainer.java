package kz.greetgo.sandbox.stand.bean_containers;

import kz.greetgo.depinject.core.BeanConfig;
import kz.greetgo.depinject.core.Include;
import kz.greetgo.sandbox.stand.beans.BeanConfigStand;

@BeanConfig
@Include({BeanConfigStand.class})
public class BeanConfigForStandBeanContainer {}
