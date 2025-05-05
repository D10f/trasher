package com.d10f.trasher.entities;

import org.ini4j.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a trash can directory
 */
public class Trash {

    private static final String TRASH_FILES_DIR = "files";

    private static final String TRASH_INFO_DIR = "info";
    private static final String TRASH_INFO_INI_HEADER = "Trash Info";
    private static final String TRASH_INFO_INI_PROP_PATH = "Path";
    private static final String TRASH_INFO_INI_PROP_DATE = "DeletionDate";

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

        info = new File(root, TRASH_INFO_DIR);
        files = new File(root, TRASH_FILES_DIR);
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
    public void deleteFiles(File... filesToDelete) {
        for (File file : filesToDelete) {
            File dest = getDestinationFilePath(file);
            createTrashMetadataFile(dest);
//            File meta = createTrashMetadataFile(file);
            System.out.println("Sending " + file.getName() + " to: " + files.getAbsolutePath() + "/" + dest);
//            file.renameTo(dest);
        }
    }

    private void createTrashMetadataFile(File fileToDelete) {
        try {
            String deletionDate = LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            Wini ini = new Wini(new File(info, fileToDelete.getName() + ".trashinfo"));
            ini.put(TRASH_INFO_INI_HEADER, TRASH_INFO_INI_PROP_PATH, fileToDelete.getAbsoluteFile());
            ini.put(TRASH_INFO_INI_HEADER, TRASH_INFO_INI_PROP_DATE, deletionDate);
            ini.store();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private File readTrashMetadataFile(File fileToDelete) {
        try {
            Wini ini = new Wini(fileToDelete);

            String path = ini.get(TRASH_INFO_INI_HEADER, "Path");
            String DeletionDate = ini.get(TRASH_INFO_INI_HEADER, "DeletionDate");

            System.out.println("Path: " + path);
            System.out.println("DeletionDate: " + DeletionDate);

            // To catch basically any error related to finding the file e.g
            // (The system cannot find the file specified)

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return fileToDelete;
    }

    private File getDestinationFilePath(File fileToDelete) {
        File dest = new File(this.files.getAbsoluteFile(), fileToDelete.getName());
        Pattern versionRe = Pattern.compile("\\.(\\d+)(\\..*)$");

        while (dest.exists()) {
            String baseName = dest.getName().replaceAll("\\..*$", "");
            String extension = dest.getName().replaceAll("^[^.]+", "");

            Matcher versionMatcher = versionRe.matcher(extension);

            int versionNum = 2;

            if (versionMatcher.find()) {
                String version = versionMatcher.group(1);
                String ext = versionMatcher.group(2);

                versionNum = Integer.parseInt(version) + 1;
                extension = ext;
            }

            dest = new File(this.files.getAbsoluteFile(), baseName + "." + versionNum + extension);
        }

        return dest;
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
