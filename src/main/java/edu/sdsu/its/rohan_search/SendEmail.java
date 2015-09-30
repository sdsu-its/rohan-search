package edu.sdsu.its.rohan_search;

import com.sun.tools.javac.util.Log;
import edu.sdsu.its.rohan_search.models.File;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.Scanner;

/**
 * Send Email to user with Streaming Ticket attached.
 *
 * @author Tom Paulus
 *         Created on 9/25/15.
 */
public class SendEmail {
    HtmlEmail mEmail = new HtmlEmail();

    public static void main(final String[] args) {
        File file = new File();
        file.setFile_name("WorldInABox_YT.mp4");
        file.setFile_path("/nas/streaming/faculty/ondemand/user/yu_elena/WorldInABox_YT.mp4");

        new SendEmail().email_ticket(new Ticket(file).ticket_file_name, file).send("tom@tompaulus.com");
    }

    public SendEmail email_file(final File file) {
        Logger.getLogger(getClass()).info(String.format("Emailing File with name %s", file.getFile_name()));
        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(download(file));
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setDescription("Requested File");
        attachment.setName(file.getFile_name());

        mEmail.setHostName(new Config().getEmail_host());
        mEmail.setSmtpPort(new Config().getEmail_port());
        mEmail.setAuthenticator(new DefaultAuthenticator(new Config().getEmail_username(), new Config().getEmail_password()));
        mEmail.setSSLOnConnect(new Config().getEmail_ssl());
        try {
            mEmail.setFrom(new Config().getEmail_from_email(), new Config().getEmail_from_name());
            mEmail.setSubject("Requested File: " + file.getFile_name());
            mEmail.setHtmlMsg(make_file_message(file.getFile_name(), file.getPublic_link()));
            mEmail.attach(attachment);
        } catch (EmailException e) {
            Logger.getLogger(getClass()).error("Problem Making Email", e);
        }
        return this;
    }

    public SendEmail email_ticket(final String ticket_name, final File file) {
        Logger.getLogger(getClass()).info(String.format("Emailing Streaming Ticket for %s", ticket_name));
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
            mEmail.setHtmlMsg(make_ticket_message(file.getFile_name(), file.getPublic_link()));
            mEmail.attach(attachment);
        } catch (EmailException e) {
            Logger.getLogger(getClass()).error("Problem Making Email", e);
        }

        return this;
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
    private String make_ticket_message(final String file_name, final String file_link) {
        String message;
        java.util.Date date = new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        message = this.readFile("ticket_email_template.html").replace("{{ file_name }}", file_name)
                .replace("{{ file_link }}", file_link)
                .replace("{{ generated_on_date_footer }}", timestamp.toString());


        return message;
    }

    private String make_file_message(final String file_name, final String file_link) {
        String message;
        java.util.Date date = new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        message = this.readFile("file_email_template.html").replace("{{ file_name }}", file_name)
                .replace("{{ file_link }}", file_link)
                .replace("{{ generated_on_date_footer }}", timestamp.toString());


        return message;
    }

        /**
         * TODO JavaDoc
         *
         * @param file
         * @return
         */
    String download(final File file) {
        Logger.getLogger(getClass()).info(String.format("Downloading %s to tmp for Emailing", file.getFile_name()));

        URL url;
        URLConnection con;
        DataInputStream dis;
        FileOutputStream fos;
        byte[] fileData;
        try {
            url = new URL(file.getPublic_link()); //File Location goes here
            con = url.openConnection(); // open the url connection.
            dis = new DataInputStream(con.getInputStream());
            fileData = new byte[con.getContentLength()];
            for (int q = 0; q < fileData.length; q++) {
                fileData[q] = dis.readByte();
            }
            dis.close(); // close the data input stream
            fos = new FileOutputStream(new java.io.File("tmp/" + file.getFile_name())); //FILE Save Location goes here
            fos.write(fileData);  // write out the file we want to save.
            fos.close(); // close the output stream writer
        } catch (Exception e) {
            Logger.getLogger(String.format("Problem Downloading %s", file.getFile_name(), e));
        }

        return "tmp/" + file.getFile_name();
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
