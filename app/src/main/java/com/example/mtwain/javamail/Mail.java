package com.example.mtwain.javamail;

import android.util.Log;

import com.sun.mail.pop3.POP3Store;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by mtwain on 19.11.14.
 */
public class Mail extends javax.mail.Authenticator {
    private static final String LOG_TAG = "email";
    private String _user;
    private String _pass;

    private String[] _to;
    private String _from;

    private String _port;
    private String _sport;

    private String _host;

    private String _subject;
    private String _body;

    private boolean _auth;

    private boolean _debuggable;

    private Multipart _multipart;


    public Mail() {
        _host = "smtp.gmail.com"; // default smtp server
        _port = "465"; // default smtp port
        _sport = "465"; // default socketfactory port

        _user = "email"; // username
        _pass = "pass"; // password
        _from = "email"; // email sent from
        _subject = "Test"; // email subject
        _body = "If you can see this message - great!"; // email body
        _debuggable = false; // debug mode on or off - default off
        _auth = true; // smtp authentication - default on

        _multipart = new MimeMultipart();

        // There is something wrong with MailCap, javamail can not find a handler for the multipart/mixed part, so this bit needs to be added.
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);

    }

    public Mail(String user, String pass) {
        this();

        _user = user;
        _pass = pass;
    }

    public boolean send() throws Exception {
        Properties props = _setProperties();
        Log.d(LOG_TAG,"user "+_user);
        Log.d(LOG_TAG,"pass "+_pass);
        Log.d(LOG_TAG,"from "+_from);
        Log.d(LOG_TAG,"to "+_to.length);
        Log.d(LOG_TAG,"subject "+_subject);
        Log.d(LOG_TAG,"body "+_body);
        if(!_user.equals("") && !_pass.equals("") && _to.length > 0 && !_from.equals("") && !_subject.equals("") && !_body.equals("")) {
            Session session = Session.getInstance(props, this);

            MimeMessage msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(_from));

            InternetAddress[] addressTo = new InternetAddress[_to.length];
            for (int i = 0; i < _to.length; i++) {
                addressTo[i] = new InternetAddress(_to[i]);
            }
            msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);

            msg.setSubject(_subject);
            msg.setSentDate(new Date());

            // setup message body
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(_body);
            _multipart.addBodyPart(messageBodyPart);

            // Put parts in message
            msg.setContent(_multipart);

            // send email
            Transport.send(msg);

            return true;
        } else {
            return false;
        }
    }

    public void addAttachment(String filename) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);

        _multipart.addBodyPart(messageBodyPart);
    }



    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(_user, _pass);
    }

    private Properties _setProperties() {
        Properties props = new Properties();

        props.put("mail.smtp.host", _host);

        if(_debuggable) {
            props.put("mail.debug", "true");
        }

        if(_auth) {
            props.put("mail.smtp.auth", "true");
        }

        props.put("mail.smtp.port", _port);
        props.put("mail.smtp.socketFactory.port", _sport);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        return props;
    }

    // the getters and setters
    public String getBody() {
        return _body;
    }

    public void setBody(String _body) {
        this._body = _body;
    }

    public void setTo(String[] to) {
        this._to = to;
    }

    // more of the getters and setters …..

    public void getInbox()
    {
        // open email and get inbox
        Properties props = System.getProperties();
        props.put("mail.smtp.host", _host);

        props.setProperty("mail.store.protocol", "imaps");
        props.setProperty("mail.imap.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.imap.socketFactory.fallback", "false");
        try
        {
            // open session
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com",_user,_pass);


            // get inbox folder
            Folder folder = store.getFolder("inbox");
            folder.open(Folder.READ_ONLY);
            if (folder.isOpen())
            {

                // get messages
                try
                {
                    Message[] msg = folder.getMessages();
                    Multipart multipart = (Multipart)msg[msg.length].getContent();
                    BodyPart plainTextPart = multipart.getBodyPart(0);
                    Log.d(LOG_TAG,"Last email: "+plainTextPart.getContent());
                } catch (IOException e)
                {
                    System.out.println("ERRROR");
                    e.printStackTrace();
                }

                // Show all messages
                /*Message[] msg = folder.getMessages();
                for (int i = 0; i < msg.length; i++)
                {
                    // senders
                    Address[] senders = msg[i].getFrom();
                    for(Address addr:senders)
                        Log.d(LOG_TAG,"Sender: "+addr);
                        //System.out.println("Sender: "+addr); // TODO

                    // message subject
                    //System.out.println("Subject: "+msg[i].getSubject()); // TODO
                    Log.d(LOG_TAG,"Subject: "+msg[i].getSubject());
                    // get message content (plain text) (part 0) , for HTML use part 1
                    try
                    {
                        Multipart multipart = (Multipart)msg[i].getContent();
                        BodyPart plainTextPart = multipart.getBodyPart(0);
                       *//* System.out.println("Body:");
                        System.out.println(plainTextPart.getContent()); // TODO*//*
                        Log.d(LOG_TAG,"Body:");
                        Log.d(LOG_TAG,""+plainTextPart.getContent());
                    } catch (IOException e)
                    {
                        System.out.println("ERRROR");
                        e.printStackTrace();
                    }
                }*/
                folder.close(true);
            } else
                System.out.println("Folder isn't open");
            store.close();
        } catch (NoSuchProviderException e)
        {
            e.printStackTrace();
        } catch (MessagingException e)
        {
            e.printStackTrace();
        }
    }

}
