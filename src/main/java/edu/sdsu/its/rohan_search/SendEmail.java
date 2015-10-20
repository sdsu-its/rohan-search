package edu.sdsu.its.rohan_search;

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

    /**
     * Send Email for Files that are self contained and do not have Legal Restrictions on their transmission.
     *
     * @param  attach Wether or not to attach the file in the email
     * @param file {@link String} File that should be sent to requester
     * @return {@link SendEmail} Instance of SendEmail
     */
    public SendEmail email_file(final boolean attach, final File file) {
        Logger.getLogger(getClass()).info(String.format("Emailing File with name %s", file.getFile_name()));
        EmailAttachment attachment = new EmailAttachment();
        if (attach) {
            attachment.setPath(download(file));
            attachment.setDisposition(EmailAttachment.ATTACHMENT);
            attachment.setDescription("Requested File");
            attachment.setName(file.getFile_name());
        }

        mEmail.setHostName(new Config().getEmail_host());
        mEmail.setSmtpPort(new Config().getEmail_port());
        mEmail.setAuthenticator(new DefaultAuthenticator(new Config().getEmail_username(), new Config().getEmail_password()));
        mEmail.setSSLOnConnect(new Config().getEmail_ssl());
        try {
            mEmail.setFrom(new Config().getEmail_from_email(), new Config().getEmail_from_name());
            mEmail.setSubject("Requested File: " + file.getFile_name());
            mEmail.setHtmlMsg(make_file_message(attach, file.getFile_name(), file.getPublic_link()));
            if (attach) {
                mEmail.attach(attachment);
            }
        } catch (EmailException e) {
            Logger.getLogger(getClass()).error("Problem Making Email", e);
        }
        return this;
    }

    /**
     * Send Email for Files that are Copyrighted and use a Streaming Ticket to be compliant with laws and regulations.
     *
     * @param ticket_name {@link String} Name of the Ticket File
     * @param file        {@link File} File that should be sent to requester
     * @return {@link SendEmail} Instance of SendEmail
     */
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

    /**
     * Read file from Local File System
     *
     * @param path {@link String} File Path of file to read in
     * @return {@link String} File contents as a String
     */
    String readFile(final String path) {
        Logger.getLogger(getClass()).debug(String.format("Reading file from path %s into memory", path));
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    /**
     * Generate HTML Message for files that have streaming tickets associated with them.
     * Includes instructions for how to upload and use the streaming ticket in Blackboard.
     *
     * @param file_name {@link String} Name of the file that was requested.
     * @param file_link {@link String} Direct link to the File.
     * @return {@link String} HTML Message for Sending
     */
    private String make_ticket_message(final String file_name, final String file_link) {
        Logger.getLogger(getClass()).debug(String.format("Generating HTML with Streaming Ticket for %s", file_name));

        String message;
        java.util.Date date = new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        message = this.readFile("ticket_email_template.html").replace("{{ file_name }}", file_name)
                .replace("{{ file_link }}", file_link)
                .replace("{{ generated_on_date_footer }}", timestamp.toString());

        return message;
    }

    /**
     * Generate HTML Message for files that do not have streaming tickets associated with them.
     *
     * @param  isAttached If the File is attached to the email (Used to determine which email template should be used.
     * @param file_name {@link String} Name of the File
     * @param file_link {@link String} Public Link to the File
     * @return {@link String} HTML Message
     */
    private String make_file_message(final boolean isAttached, final String file_name, final String file_link) {
        Logger.getLogger(getClass()).debug(String.format("Generating HTML with File Attachment for %s", file_name));

        String message;
        java.util.Date date = new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        if (isAttached) {
            message = this.readFile("small_file_email_template.html").replace("{{ file_name }}", file_name)
                    .replace("{{ file_link }}", file_link)
                    .replace("{{ generated_on_date_footer }}", timestamp.toString());
        } else {
            message = this.readFile("large_file_email_template.html").replace("{{ file_name }}", file_name)
                    .replace("{{ file_link }}", file_link)
                    .replace("{{ generated_on_date_footer }}", timestamp.toString());
        }

        return message;
    }

    /**
     * Download Files from Streaming Server so that they can be attached to the Email
     *
     * @param file {@link File} File to Download
     * @return {@link String} File Path where the file was saved
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
            Logger.getLogger(getClass()).error(String.format("Problem Downloading %s", file.getFile_name()), e);
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
            Logger.getLogger(getClass()).info(String.format("Sending Email Message TO: %s", to_email));
            mEmail.addTo(to_email);
            mEmail.send();
        } catch (EmailException e) {
            Logger.getLogger(getClass()).error("Problem Sending Email", e);
        }
    }
}
