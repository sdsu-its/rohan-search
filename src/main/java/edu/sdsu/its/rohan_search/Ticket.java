package edu.sdsu.its.rohan_search;

import edu.sdsu.its.rohan_search.models.File;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

/**
 * Generate Streaming Ticket for the faculty member to upload to Blackboard.
 *
 * @author Tom Paulus
 *         Created on 9/25/15.
 */

public class Ticket {
    public final String ticket_file_name;

    /**
     * Generate the Streaming Ticket and save it to a temporary file.
     *
     * @param file {@link File} File for which the Streaming Ticket should be generated
     */
    public Ticket(final File file) {
        Logger.getLogger(getClass()).info(String.format("Generating Streaming Ticket for %s", file.getStripped_file_name()));
        Logger.getLogger(getClass()).debug(String.format("Stripped File Name: %s\nStripped File Path: %s", file.getStripped_file_name(), file.getStripped_file_path()));

        String html = this.readFile("ticket_template.html");

        html = String.format(html, file.getStripped_file_name(), file.getStripped_file_name(), file.getExtension(), "user/" + file.getStripped_file_path());

        ticket_file_name = String.format("%s-stream.html", file.getStripped_file_name());

        try {
            writeFile("tmp/" + ticket_file_name, html);
        } catch (Exception e) {
            Logger.getLogger(getClass()).error("Problem Saving out Streaming Ticket", e);
        }
    }

    public static void main(final String[] args) {
        File file = new File();
        file.setFile_name("WorldInABox_YT.mp4");
        file.setFile_path("/nas/streaming/faculty/ondemand/user/yu_elena/WorldInABox_YT.mp4");

        new Ticket(file);
    }

    String readFile(String path) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    void writeFile(String path, String data) throws FileNotFoundException, UnsupportedEncodingException {
        java.io.File tmp_folder = new java.io.File("tmp");
        if (!tmp_folder.exists()) {
            Logger.getLogger(getClass()).info("Making tmp Folder");
            if (!tmp_folder.mkdir()) {
                Logger.getLogger(getClass()).error("Problem making tmp folder.");
            }
        }

        PrintWriter writer = new PrintWriter(path, "UTF-8");
        writer.println(data);
        writer.close();
    }
}
