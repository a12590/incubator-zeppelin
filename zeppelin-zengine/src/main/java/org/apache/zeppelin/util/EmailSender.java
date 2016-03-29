package org.apache.zeppelin.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.zeppelin.conf.ZeppelinConfiguration;
import org.apache.zeppelin.conf.ZeppelinConfiguration.ConfVars;
import org.apache.zeppelin.notebook.Paragraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility to send emails
 * It will be initialized only once
 */
public class EmailSender {
  private static Logger logger = LoggerFactory.getLogger(EmailSender.class);
  private static final String ZEPPELIN_MAIL_TXT = "zeppelin-mail.txt";
  private static final String REGEX_TOKEN = "\\{([^}]+)\\}";
  private static final String DEFAULT_TEMPLATE_CONTENT = "Note Name : {NOTE_NAME}\n"
          + "Paragraph Name : {PARAGRAPH_NAME}\n"
          + "Error : {RESULT}";

  private static final String TOKEN_NOTE_NAME = "NOTE_NAME";
  private static final String TOKEN_PARAGRAPH_NAME = "PARAGRAPH_NAME";
  private static final String TOKEN_RESULT = "RESULT";

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
  private String subject = null;
  private String template = null;
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
    subject = config.getString(ConfVars.ZEPPELIN_MAIL_SMTP_FROM_ADDRESS);
  }

  private void initTemplate() {
    InputStream is = getTemplateContent();
    try {
      if (is != null) {
        template = IOUtils.toString(is);
      } else {
        template = DEFAULT_TEMPLATE_CONTENT;
      }
    } catch (IOException ioe) {
      logger.error("Error while reading mail template " + ZEPPELIN_MAIL_TXT, ioe);
      template = DEFAULT_TEMPLATE_CONTENT;
    }
  }

  private InputStream getTemplateContent() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    InputStream is = EmailSender.class.getResourceAsStream(ZEPPELIN_MAIL_TXT);
    if (is == null) {
      ClassLoader cl = ZeppelinConfiguration.class.getClassLoader();
      if (cl != null) {
        is = cl.getResourceAsStream(ZEPPELIN_MAIL_TXT);
      }
    }
    if (is == null) {
      is = classLoader.getResourceAsStream(ZEPPELIN_MAIL_TXT);
    }

    return is;
  }

  public boolean canSendEmail() {
    if (config == null) {
      init();
    }

    return config.getBoolean(ConfVars.ZEPPELIN_SEND_EMAIL_ON_ERROR);
  }

  public void send(Paragraph paragraph) {

    if (canSendEmail()) {
      if (template == null) {
        initTemplate();
      }
      String message = replaceTokens(paragraph);

      RunnableEmailSender runnable = new RunnableEmailSender(subject, message);
      executor.submit(runnable);
    } else {
      logger.error("Zeppelin was not configured to send emails");
    }
  }

  private String replaceTokens(Paragraph paragraph) {
    Pattern pattern = Pattern.compile(REGEX_TOKEN);
    Matcher matcher = pattern.matcher(template);
    StringBuffer replacedBuffer = new StringBuffer();
    while (matcher.find()) {
      String replacement = getTokenValue(matcher.group(1), paragraph);
      if (replacement != null) {
        matcher.appendReplacement(replacedBuffer, "");
        replacedBuffer.append(replacement);
      }
    }

    matcher.appendTail(replacedBuffer);
    return replacedBuffer.toString();
  }

  private String getTokenValue(String token, Paragraph paragraph) {
    String value = null;
    if (TOKEN_NOTE_NAME.equals(token)) {
      value = paragraph.getNote().getName();
    } else if (TOKEN_PARAGRAPH_NAME.equals(token)) {
      value = paragraph.getTitle();
    } else if (TOKEN_RESULT.equals(token)) {
      value = paragraph.getReturn().toString();
    } else {
      value = System.getenv(token);
      if (value == null) {
        value = System.getProperty(token);
      }
    }

    return value;
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
