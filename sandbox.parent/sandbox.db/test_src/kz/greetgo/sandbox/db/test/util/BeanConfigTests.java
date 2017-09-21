package kz.greetgo.sandbox.db.test.util;

import kz.greetgo.depinject.core.BeanConfig;
import kz.greetgo.depinject.core.Include;
import kz.greetgo.sandbox.db.test.dao.postgres.BeanConfigTestDao;

@BeanConfig
@Include({BeanConfigTestDao.class})
public class BeanConfigTests {}
