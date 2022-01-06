/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 *
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package net.duckling.ddl.service.mail.impl;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import net.duckling.ddl.common.DucklingProperties;
import net.duckling.ddl.service.mail.Mail;
import net.duckling.ddl.service.mail.MailService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sun.mail.smtp.SMTPMessage;


/**
 * @date Mar 15, 2010
 * @author xiejj@cnic.cn
 */
public class MailServiceImpl implements MailService {
    private DucklingProperties systemProperty;
    public void setSystemProperty(DucklingProperties systemProperty){
        this.systemProperty = systemProperty;
    }

    private static class ValueBag{
        public Authenticator m_authenticator;
        public InternetAddress m_fromAddress;
        public Properties m_mailProperties;
    }
    public static final String EMAIL_CONTENT_TYPE="text/html;charset=UTF-8";

    private static final String EMAIL_DISPLAY_NAME="科研在线";

    private static final Logger LOG = Logger.getLogger(MailService.class);

    private static final String PROP_EMAIL_FROMADDRESS = "email.fromAddress";
    private static final String PROP_EMAIL_PASSWORD = "email.password";

    private static final String PROP_EMAIL_USERID = "email.username";

    private static final String UTF_8="UTF-8";

    private ValueBag m_bag;
    private void cheat(MimeMessage mimeMessage, String serverDomain)
            throws MessagingException {
        mimeMessage.saveChanges();
        mimeMessage.setHeader("User-Agent",
                              "Thunderbird 2.0.0.16 (Windows/20080708)");
        String messageid = mimeMessage.getHeader("Message-ID", null);
        messageid = messageid.replaceAll("\\.JavaMail.*", "@" + serverDomain
                                         + ">");
        mimeMessage.setHeader("Message-ID", messageid);
    }

    private MimeMessage getMessage(Address[] addressArray, String from, String content, String title)
            throws MessagingException {
        Session session = Session.getInstance(m_bag.m_mailProperties, m_bag.m_authenticator);
        session.setDebug(false);
        SMTPMessage msg = new SMTPMessage(session);
        if (StringUtils.isNotEmpty(from)) {
            InternetAddress ss;
            try {
                ss = new InternetAddress(m_bag.m_fromAddress.getAddress(), MimeUtility.encodeText(from, "gb2312", "b"));
                msg.setFrom(ss);
            } catch (UnsupportedEncodingException e) {
                msg.setFrom(m_bag.m_fromAddress);
            }
            msg.setReplyTo(new InternetAddress[] { new InternetAddress(from) });
        } else {
            msg.setFrom(m_bag.m_fromAddress);
        }
        msg.setRecipients(Message.RecipientType.TO, addressArray);
        try {
            msg.setSubject(MimeUtility.encodeText(title, UTF_8, "B"));
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage(), e);
        }
        msg.setSentDate(new Date());
        msg.setContent(content, EMAIL_CONTENT_TYPE);
        return msg;
    }
    private ValueBag readProperties() {
        ValueBag bag = new ValueBag();
        bag.m_mailProperties = new Properties();
        bag.m_mailProperties.put("mail.smtp.host", systemProperty.getProperty("email.mail.smtp.host"));
        bag.m_mailProperties.put("mail.smtp.auth", systemProperty.getProperty("email.mail.smtp.auth"));
        bag.m_mailProperties.put("mail.pop3.host", systemProperty.getProperty("email.mail.pop3.host"));
        String userId = systemProperty.getProperty(PROP_EMAIL_USERID);
        String password = systemProperty.getProperty(PROP_EMAIL_PASSWORD);
        bag.m_authenticator = new EmailAuthenticator(userId, password);
        String mailFrom = systemProperty.getProperty(PROP_EMAIL_FROMADDRESS);
        try {
            bag.m_fromAddress = new InternetAddress(mailFrom,EMAIL_DISPLAY_NAME,UTF_8);
        }catch (UnsupportedEncodingException e) {
            LOG.error("",e);
        }
        return bag;
    }

    public void destroy(){
        m_bag=null;
    }
    public void init() {
        m_bag = readProperties();
    }

    public void sendMail(Mail mail) {
        LOG.debug("sendEmail() to: " + mail.getRecipient());
        try {
            Session session = Session.getInstance(m_bag.m_mailProperties, m_bag.m_authenticator);
            session.setDebug(false);
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(m_bag.m_fromAddress);
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(mail
                                                                           .getRecipient()));
            msg.setSubject(mail.getSubject());
            msg.setSentDate(new Date());

            Multipart mp = new MimeMultipart();

            MimeBodyPart txtmbp = new MimeBodyPart();
            txtmbp.setContent(mail.getMessage(), EMAIL_CONTENT_TYPE);
            mp.addBodyPart(txtmbp);

            List<String> attachments = mail.getAttachments();
            for (Iterator<String> it=attachments.iterator(); it.hasNext(); ) {
                MimeBodyPart mbp = new MimeBodyPart();
                String filename = it.next();
                FileDataSource fds = new FileDataSource(filename);
                mbp.setDataHandler(new DataHandler(fds));
                mbp.setFileName(MimeUtility.encodeText(fds.getName()));
                mp.addBodyPart(mbp);
            }

            msg.setContent(mp);

            if ((m_bag.m_fromAddress != null) && (m_bag.m_fromAddress.getAddress() != null)
                && (m_bag.m_fromAddress.getAddress().indexOf("@") != -1)){
                cheat(msg, m_bag.m_fromAddress.getAddress().substring(m_bag.m_fromAddress.getAddress().indexOf("@")));
            }


            Transport.send(msg);

            LOG.info("Successfully send the mail to " + mail.getRecipient());

        } catch (Throwable e) {

            LOG.error("Exception occured while trying to send notification to: "+ mail.getRecipient(),e);
            LOG.debug("Details:", e);
        }
    }

    @Override
    public void sendSimpleMail(String[] address, String title, String content) {
        sendSimpleMail(address, null, title, content);
    }

    @Override
    public void sendSimpleMail(String[] address, String from,String title, String content) {
        LOG.debug("sendEmail() to: " +Arrays.toString(address));
        if(address==null||address.length==0){
            LOG.error("the address is empty!");
            return ;
        }
        try {
            Address[] addressArray=new Address[address.length];
            int index=0;
            for(String str:address){
                addressArray[index++]=new InternetAddress(str);
            }
            MimeMessage msg=getMessage(addressArray,from,content, title);
            if (m_bag.m_fromAddress!=null
                && (m_bag.m_fromAddress.getAddress().indexOf("@") != -1)) {
                cheat(msg, m_bag.m_fromAddress.getAddress().substring(m_bag.m_fromAddress.getAddress().indexOf("@")));
            }
            Transport.send(msg);
            LOG.info("Successfully send the mail to " + Arrays.toString(address));

        } catch (MessagingException e) {
            LOG.error("Exception occured while trying to send notification to: " + Arrays.toString(address),e);
            LOG.debug("Details:", e);
        }
    }
}
