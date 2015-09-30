package edu.sdsu.its.rohan_search;

import edu.sdsu.its.rohan_search.models.File;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Scanner;

/**
 * Send Email to user with Streaming Ticket attached.
 *
 * @author Tom Paulus
 *         Created on 9/25/15.
 */
public class SendEmail {
    final HtmlEmail mEmail = new HtmlEmail();

    /**
     * Generate Email and prepare it for sending.
     *
     * @param ticket_name {@link String} Name of the temporary file into which the Streaming Ticket was saved.
     * @param file        {@link File} File for which the Streaming Ticket was generated.
     */
    public SendEmail(final String ticket_name, final File file) {
        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath("tmp/" + ticket_name);
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setDescription("Streaming Ticket");
        attachment.setName(ticket_name);

        mEmail.setHostName(new Config().getEmail_host());
        mEmail.setSmtpPort(new Config().getEmail_port());
        mEmail.setAuthenticator(new DefaultAuthenticator(new Config().getEmail_username(), new Config().getEmail_password()));
        mEmail.setSSLOnConnect(new Config().getEmail_ssl());
        try {
            mEmail.setFrom(new Config().getEmail_from_email(), new Config().getEmail_from_name());
            mEmail.setSubject("Streaming Ticket for " + file.getFile_name());
            mEmail.setHtmlMsg(make_message(file.getFile_name(), file.getPublic_link()));
            mEmail.attach(attachment);
        } catch (EmailException e) {
            Logger.getLogger(getClass()).error("Problem Making Email", e);
        }
    }

    public static void main(final String[] args) {
        File file = new File();
        file.setFile_name("WorldInABox_YT.mp4");
        file.setFile_path("/nas/streaming/faculty/ondemand/user/yu_elena/WorldInABox_YT.mp4");

        new SendEmail(new Ticket(file).ticket_file_name, file).send("tom@tompaulus.com");
    }

    String readFile(String path) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    /**
     * Generate HTML Message for email.
     *
     * @param file_name {@link String} Name of the file that was requested.
     * @param file_link {@link String} Direct link to the File.
     * @return {@link String} HTML Message for Sending
     */
    private String make_message(final String file_name, final String file_link) {
        String message;
        java.util.Date date = new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        message = this.readFile("email_template.html").replace("{{ file_name }}", file_name)
                .replace("{{ file_link }}", file_link)
                .replace("{{ generated_on_date_footer }}", timestamp.toString());


        return message;
    }

    /**
     * Send email to requester.
     *
     * @param to_email {@link String}
     */
    public void send(final String to_email) {
        try {
            mEmail.addTo(to_email);
            mEmail.send();
        } catch (EmailException e) {
            Logger.getLogger(getClass()).error("Problem Sending Email", e);
        }
    }
}
