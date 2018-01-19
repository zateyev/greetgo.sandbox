package kz.greetgo.sandbox.db.register_impl.migration.SshConnector;


import com.jcraft.jsch.*;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.SshConfig;
import kz.greetgo.sandbox.db.register_impl.migration.MigrationCia;
import kz.greetgo.sandbox.db.register_impl.migration.MigrationFrs;
import kz.greetgo.util.RND;
import kz.greetgo.util.ServerUtil;

import java.io.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Bean
public class SSHManager {

  private static int SSH_PORT;
  private static int CONNECTION_TIMEOUT;

  private static String HOSTNAME;
  private static String USERNAME;
  private static String PASSWORD;
  private static String SERVER_DIRECTORY;
  private static String LOCAL_DIRECTORY;

  MigrationCia cia = new MigrationCia();
  MigrationFrs frc = new MigrationFrs();

  public SSHManager() throws FileNotFoundException {}

  private Session initSession() throws Exception {

    JSch jsch = new JSch();

    readConfig();

    Session session = jsch.getSession(USERNAME, HOSTNAME, SSH_PORT);
    session.setPassword(PASSWORD);
    UserInfo userInfo = new SshUserInfo();

    session.setUserInfo(userInfo);
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect(CONNECTION_TIMEOUT);
    return session;

  }

  public void connectAndMigrateCia(Connection connection) throws Exception {

    List<String> filesForMigrate = new ArrayList<>();

    Session session = initSession();

    Channel channel = session.openChannel("sftp");
    channel.connect();

    ChannelSftp channelSftp = (ChannelSftp) channel;
    channelSftp.cd(SERVER_DIRECTORY);

    Pattern pattern = Pattern.compile("from_cia_[0-9]{8}.xml");
    Vector<ChannelSftp.LsEntry> xmlList = channelSftp.ls("*.xml");
    for (ChannelSftp.LsEntry entry : xmlList) {

      String fileName = entry.getFilename();
      Matcher m = pattern.matcher(fileName);
      if (m.matches()) {
        String tempFileName = fileName.replaceAll(".xml", ".XmlToMigrate");
        channelSftp.rename(fileName, tempFileName);
        filesForMigrate.add(tempFileName);
      }

    }

    channel.disconnect();
    session.disconnect();

    migrateCia(filesForMigrate, connection);

  }

  private void migrateCia(List<String> filesForMigrate, Connection connection) throws Exception {

    Collections.sort(filesForMigrate);
    for (String fileName : filesForMigrate) {

      Session session = initSession();
      Channel channel = session.openChannel("sftp");
      channel.connect();

      ChannelSftp channelSftp = (ChannelSftp) channel;
      channelSftp.cd(SERVER_DIRECTORY);


      String errorFileName = fileName + RND.intStr(3) + "_Error.log";

      File toMigrate = new File(LOCAL_DIRECTORY + "inFile_" + RND.intStr(10) + "_" + fileName);
      toMigrate.getParentFile().mkdirs();

      try (InputStream stream = channelSftp.get(fileName)) {

        try (FileOutputStream out = new FileOutputStream(toMigrate)) {
          ServerUtil.copyStreamsAndCloseIn(stream, out);
        }

      }

      cia.connection = connection;
      cia.inFile = toMigrate;
      cia.errorsFile = new File(LOCAL_DIRECTORY + errorFileName);
      cia.errorsFile.getParentFile().mkdirs();
      //
      cia.migrate();
      //
      channelSftp.put(new FileInputStream(cia.errorsFile), cia.errorsFile.getName());

      channelSftp.rename(fileName,
        fileName.replaceAll(".XmlToMigrate", ".XmlMigrated"));

      channel.disconnect();
      session.disconnect();

    }

  }

  public void connectAndMigrateFrs(Connection connection) throws Exception {

    Session session = initSession();

    Channel channel = session.openChannel("sftp");
    channel.connect();

    ChannelSftp channelSftp = (ChannelSftp) channel;
    channelSftp.cd(SERVER_DIRECTORY);

    Vector<ChannelSftp.LsEntry> xmlList = channelSftp.ls("*.json_row");
    for (ChannelSftp.LsEntry entry : xmlList) {

      String fileName = entry.getFilename();
      String tempFileName = fileName.replaceAll(".json_row", ".JsonToMigrate");
      channelSftp.rename(fileName, tempFileName);

    }


    Vector<ChannelSftp.LsEntry> toMigrateList = channelSftp.ls("*.JsonToMigrate");
    Collections.sort(toMigrateList);
    for (ChannelSftp.LsEntry entry : toMigrateList) {

      String fileName = entry.getFilename();
      String errorFileName = fileName + RND.intStr(3) + "_Error.log";

      File toMigrate = new File(LOCAL_DIRECTORY + "inFile_" + RND.intStr(10) + "_" + fileName);
      toMigrate.getParentFile().mkdirs();

      try (InputStream stream = channelSftp.get(fileName)) {

        try (FileOutputStream out = new FileOutputStream(toMigrate)) {
          ServerUtil.copyStreamsAndCloseIn(stream, out);
        }

      }

      frc.connection = connection;
      frc.inFile = toMigrate;
      frc.errorsFile = new File("build/migration/" + errorFileName);
      //
      frc.migrate();
      //
      channelSftp.put(new FileInputStream(frc.errorsFile), frc.errorsFile.getName());

      channelSftp.rename(fileName,
        fileName.replaceAll(".JsonToMigrate", ".JsonMigrated"));

    }

    channel.disconnect();
    session.disconnect();
  }

  public BeanGetter<SshConfig> sshConfig;


  private void readConfig() {

    SSH_PORT = sshConfig.get().port();
    CONNECTION_TIMEOUT = sshConfig.get().timeout();

    HOSTNAME = sshConfig.get().hostName();
    USERNAME = sshConfig.get().username();
    PASSWORD = sshConfig.get().pass();
    SERVER_DIRECTORY = sshConfig.get().serverDir();
    LOCAL_DIRECTORY = sshConfig.get().localDir();

  }


  public void renameToDefault() throws Exception {

    Session session = initSession();

    Channel channel = session.openChannel("sftp");
    channel.connect();

    ChannelSftp channelSftp = (ChannelSftp) channel;
    channelSftp.cd(SERVER_DIRECTORY);


    Vector<ChannelSftp.LsEntry> list = channelSftp.ls("*.XmlMigrated");

    for (ChannelSftp.LsEntry entry : list) {

      String fileName = entry.getFilename();

      String tempFileName = fileName.replaceAll(".XmlMigrated", ".xml");

      channelSftp.rename(fileName, tempFileName);

    }

    Vector<ChannelSftp.LsEntry> list2 = channelSftp.ls("*.JsonMigrated");

    for (ChannelSftp.LsEntry entry : list2) {

      String fileName = entry.getFilename();

      String tempFileName = fileName.replaceAll(".JsonMigrated", ".json_row");

      channelSftp.rename(fileName, tempFileName);

    }

    channel.disconnect();
    session.disconnect();

  }

}
