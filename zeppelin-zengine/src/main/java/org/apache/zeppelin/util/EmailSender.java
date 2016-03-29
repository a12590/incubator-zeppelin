package org.apache.zeppelin.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.zeppelin.conf.ZeppelinConfiguration;
import org.apache.zeppelin.conf.ZeppelinConfiguration.ConfVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility to send emails
 * It will be initialized only once
 */
public class EmailSender {
  private static Logger logger = LoggerFactory.getLogger(EmailSender.class);

  private ZeppelinConfiguration config = null;
  private String user = null;
  private String password = null;
  private boolean authEnabled = false;
  private boolean startTLSEnabled = false;
  private boolean sslEnabled = false;
  private String host = null;
  private int port = 0;
  private String to = null;
  private String from = null;
  private ExecutorService executor = Executors.newFixedThreadPool(5);

  public EmailSender() {
    init();
  }

  private void init() {
    config = ZeppelinConfiguration.create();

    user = config.getString(ConfVars.ZEPPELIN_MAIL_SMTP_USER);
    password = config.getString(ConfVars.ZEPPELIN_MAIL_SMTP_PASSWORD);
    authEnabled = config.getBoolean(ConfVars.ZEPPELIN_MAIL_SMTP_AUTH_ENABLE);
    startTLSEnabled = config.getBoolean(ConfVars.ZEPPELIN_MAIL_SMTP_STARTTLS_ENABLE);
    sslEnabled = config.getBoolean(ConfVars.ZEPPELIN_MAIL_SMTP_SSL_ENABLE);
    host = config.getString(ConfVars.ZEPPELIN_MAIL_SMTP_HOST);
    port = config.getInt(ConfVars.ZEPPELIN_MAIL_SMTP_PORT);
    to = config.getString(ConfVars.ZEPPELIN_MAIL_SMTP_TO_ADDRESS);
    from = config.getString(ConfVars.ZEPPELIN_MAIL_SMTP_FROM_ADDRESS);
  }

  public boolean canSendEmail() {
    if (config == null) {
      init();
    }

    return config.getBoolean(ConfVars.ZEPPELIN_SEND_EMAIL_ON_ERROR);
  }


  public void send(String subject, String message) {
    if (canSendEmail()) {
      RunnableEmailSender runnable = new RunnableEmailSender(subject, message);
      executor.submit(runnable);
    } else {
      logger.error("Zeppelin was not configured to send emails");
    }
  }

  private class RunnableEmailSender implements Runnable {
    private String subject;
    private String message;

    public RunnableEmailSender(String subject, String message) {
      this.subject = subject;
      this.message = message;
    }

    @Override
    public void run() {
      try {
        logger.info("Sending mail to " + to);
        Email email = new SimpleEmail();
        email.setHostName(host);
        email.setSmtpPort(port);
        if (authEnabled) {
          email.setAuthenticator(new DefaultAuthenticator(user, password));
        }

        if (sslEnabled) {
          email.setSSLOnConnect(true);
        }

        if (startTLSEnabled) {
          email.setStartTLSEnabled(startTLSEnabled);
        }
        email.setFrom(from);
        email.setSubject(subject);
        email.setMsg(message);
        email.addTo(to);
        email.send();

      } catch (EmailException ee) {
        logger.error("Error while sending email", ee);
      }
    }
  }
}
