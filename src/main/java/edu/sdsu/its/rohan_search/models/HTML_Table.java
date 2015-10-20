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
                "   <td class=\"column_header\">File Name <i>(Click on the Title to Request the Streaming Ticket or File)</i></td>" +
                "   <td class=\"column_header\">File Size</td>" +
                "</tr>";
    }

    public void addFile(File file) {
        String addition = "<tr>";
        addition += String.format("<td><a href=\"#\" onclick=\"getEmail('%s', '%s', '%s')\">%s</a></td>",
                file.getFile_name(),
                file.getFile_path(),
                Boolean.toString(file.getFile_size().contains(" KB") || file.getFile_size().contains(" B")), // Only attach files smaller than 1MB
                file.getFile_name());
        addition += String.format("<td>%s</td>", file.getFile_size());
        addition += "</tr>";

        html_content += addition;
    }

    public String getHtml_content() {
        html_content += "</table>";
        return html_content;
    }
}
