package edu.sdsu.its.rohan_search;

import com.google.gson.Gson;
import edu.sdsu.its.rohan_search.models.File;
import edu.sdsu.its.rohan_search.models.HTML_Table;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

@Path("/")
public class Web {
    @GET
    @Path("echo")
    @Produces(MediaType.TEXT_PLAIN)
    public String echo(@QueryParam("m") final String message) {
        Logger.getLogger(getClass()).info("Request received with m=" + message);
        return "echo: " + message;
    }

    @GET
    @Path("search")
    @Produces(MediaType.TEXT_HTML)
    public Response search(@QueryParam("q") final String query) {
        List<File> results = DB.getInstance().search(query);
        Logger.getLogger(getClass()).info(String.format("Search for %s returned %d results", query, results.size()));

        String html = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n" +
                "<html>\n" +
                "<head>\n" +
                "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
                "  <title>Search Results</title>\n" +
                "\n" +
                "  <style>\n" +
                "    a {\n" +
                "      text-decoration: none;\n" +
                "    }\n" +
                "\n" +
                "    .column_header {\n" +
                "      font-weight: bold;\n" +
                "    }\n" +
                "  </style>\n" +
                "\n" +
                "  <script>\n" +
                "    function getEmail (name, path) {\n" +
                "      var email = prompt(\"Enter your Email\");\n" +
                "      get(\"./email?name=\"+ name +\"&path=\"+ path +\"&email=\" + email);" +
                "      alert(\"Sent Streaming Ticket to \" + email);\n" +
                "    }\n" +
                "\n" +
                "    function get(url) {\n" +
                "      var xmlHttp = new XMLHttpRequest();\n" +
                "      xmlHttp.open(\"GET\", url, true); // true for asynchronous\n" +
                "      xmlHttp.send(null);\n" +
                "    }\n" +
                "  </script>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "%s\n" +
                "</body>\n" +
                "</html>\n";

        String content;
        if (results.size() != 0) {
            HTML_Table table = new HTML_Table();
            results.forEach(table::addFile);
            content = table.getHtml_content();
        } else {
            content = "<h2>No Files Found!</h2>";
        }

        return Response.status(Response.Status.OK).entity(String.format(html, content)).build();
    }

    @POST
    @Path("search")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response search_json(final String m) {
        String query = "";
        String[] pairs = m.split("&");
        for (String pair : pairs) {
            String[] fields = pair.split("=");
            try {
                String name = URLDecoder.decode(fields[0], "UTF-8");
                String value = URLDecoder.decode(fields[1], "UTF-8");
                if (name.equals("query")) {
                    query = value;
                    break;
                }
            } catch (UnsupportedEncodingException e) {
                Logger.getLogger(getClass()).error("Problem Parsing POST Payload");
            }
        }


        List<File> results = DB.getInstance().search(query);
        Logger.getLogger(getClass()).info(String.format("Search for %s returned %d results", query, results.size()));

        final Gson gson = new Gson();
        gson.toJson(results);
        return Response.status(Response.Status.OK).entity(gson.toJson(results)).build();
    }


    @GET
    @Path("email")
    @Produces(MediaType.TEXT_PLAIN)
    public Response email(@QueryParam("email") final String email, @QueryParam("path") final String path,
                          @QueryParam("name") final String name) {
        Logger.getLogger(getClass()).info(String.format("Sending Streaming Ticket for %s to %s", name, email));

        File file = new File();
        file.setFile_path(path);
        file.setFile_name(name);

        if (!file.getExtension().equals("html")) {
            String ticket_name = new Ticket(file).ticket_file_name;
            new SendEmail().email_ticket(ticket_name, file).send(email);
        }
        else {
            new SendEmail().email_file(file).send(email);
        }

        return Response.status(Response.Status.OK).build();
    }
}
