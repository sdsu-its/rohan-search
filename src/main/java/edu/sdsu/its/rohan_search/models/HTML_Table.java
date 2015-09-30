package edu.sdsu.its.rohan_search.models;

/**
 * @author Tom Paulus
 *         Created on 9/25/15.
 */
public class HTML_Table {
    public String html_content;

    public HTML_Table() {
        this.html_content = "<table style=\"width:100%\">" +
                "<tr>" +
                "   <td class=\"column_header\">File Name</td>" +
                "   <td class=\"column_header\">File Size</td>" +
                "   <td class=\"column_header\">Streaming Ticket</td>" +
                "</tr>";
    }

    public void addFile(File file) {
        String addition = "<tr>";
        addition += String.format("<td>%s</td>", file.getFile_name());
        addition += String.format("<td>%s</td>", file.getFile_size());
//        addition += String.format("<td><a href=\"%s\" target=\"_blank\">%s</a></td>", file.getPublic_link(), file.getPublic_link());
        if (file.getExtension().equals("html")) {
            addition += String.format("<td><button onclick=\"getEmail('%s', '%s')\">Email File &amp; Link</button></td>", file.getFile_name(), file.getFile_path());

        } else {
            addition += String.format("<td><button onclick=\"getEmail('%s', '%s')\">Email Streaming Ticket</button></td>", file.getFile_name(), file.getFile_path());
        }
        addition += "</tr>";

        html_content += addition;
    }

    public String getHtml_content() {
        html_content += "</table>";

        return html_content;
    }
}
