package com.d10f.trasher.entities;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a trash can directory
 */
public class Trash {

    private static final String TRASH_FILES = "files";
    private static final String TRASH_INFO = "info";
    private static final String TRASH_DIRECTORY_SIZES = "directorysizes";

    private final File root;
    private final File files;
    private final File info;
    private final File directorySizes;

    private int size;

    public Trash() {
        root = System.getenv().containsKey("XDG_DATA_HOME")
                ? new File(System.getenv("XDG_DATA_HOME"), "Trash")
                : new File(System.getenv("HOME"), ".local/share/Trash");

        info = new File(root, TRASH_INFO);
        files = new File(root, TRASH_FILES);
        directorySizes = new File(root, TRASH_DIRECTORY_SIZES);

        try {
            createSubdirectories();
            calculateTrashSize();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Sends the specified files to the root directory of this Trash.
     *
     * @param filesToDelete the list of files to be sent to the trash
     */
    public void send(File... filesToDelete) {
        for (File file : filesToDelete) {

            File dest = new File(this.files.getAbsoluteFile(), file.getName());

            // TODO: Check if a file with the same name already exists in $TRASH/files

            while (dest.exists()) {
                String newName = dest.getName().replaceAll("\\..*$", "");
                String extension = dest.getName().replaceAll("^[^.]+", "");

                Pattern versionRe = Pattern.compile("\\.(\\d+)(\\..*)$");
                Matcher versionMatcher = versionRe.matcher(extension);

                int versionNum = 2;

                if (versionMatcher.find()) {
                    String version = versionMatcher.group(1);
                    String ext = versionMatcher.group(2);

                    versionNum = Integer.parseInt(version) + 1;
                    extension = ext;
                }

                System.out.println("Renaming " + dest.getName() + " to: " + newName + "." + versionNum + extension);
                dest = new File(this.files.getAbsoluteFile(), newName + "." + versionNum + extension);
            }

            System.out.println("Sending " + file.getName() + " to: " + dest.getAbsolutePath());

            file.renameTo(dest);
        }
    }

    private void createSubdirectories() throws IOException {
        if (!root.exists() && !root.mkdir()) {
            throw new IOException("Unable to create Trash directory: info.");
        }

        if (!info.exists() && !info.mkdir()) {
            throw new IOException("Unable to create directory: info.");
        }

        if (!files.exists() && !files.mkdir()) {
            throw new IOException("Unable to create directory: files.");
        }

        if (!directorySizes.exists() && !directorySizes.createNewFile()) {
            throw new IOException("Unable to create file: directorySizes.");
        }
    }

    private void calculateTrashSize() {
        // Non-normative: suggested algorithm for calculating the size of a trash directory
        //
        // load directorysizes file into memory as a hash directory_name -> (size, mtime, seen=false)
        // totalsize = 0
        // list "files" directory, and for each item:
        //   stat the item
        //   if a file:
        //       totalsize += file size
        //   if a directory:
        //       stat the trashinfo file to get its mtime
        //       lookup entry in hash
        //       if no entry found or entry's cached mtime != trashinfo's mtime:
        //           calculate directory size (from disk)
        //           totalsize += calculated size
        //           add/update entry in hash (size of directory, trashinfo's mtime, seen=true)
        //       else:
        //           totalsize += entry's cached size
        //           update entry in hash to set seen=true
        // done
        // remove entries from hash which have (seen == false)
        // write out hash back to directorysizes file
        size = 0;
    }
}
