package edu.sdsu.its.rohan_search.models;

/**
 * Models Files in the File System.
 *
 * @author Tom Paulus
 *         Created on 9/14/15.
 */
public class File {
    public String file_name;
    public String file_path;
    public String file_size;
    public String public_link;

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(final String file_name) {
        this.file_name = file_name;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(final String file_path) {
        this.file_path = file_path.replace(System.getenv("HOME"), "~");
        this.public_link = file_path.replace("/nas/streaming/faculty/ondemand/", "http://video.sdsu.edu/nas/");
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(final String file_size) {
        this.file_size = file_size;
    }

    public String getPublic_link() {
        return public_link;
    }

    public String getStripped_file_name() {
        String striped_file_name = file_name.replace(file_name.split("\\.")[file_name.split("\\.").length - 1], "");
        striped_file_name = striped_file_name.substring(0, striped_file_name.length() - 1);
        return striped_file_name;
    }

    public String getStripped_file_path() {
        return file_path.split("/user/")[1].split("/")[0];
    }

    public String getExtension() {
        String parts[] = file_name.split("\\.");
        return parts[parts.length - 1];
    }
}
