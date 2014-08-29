/**
 * 
 */
package thaggie.jwalk;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

/**
 * This works through an archive file looking for classes NOTE: this class is only supposed to be
 * called with on thread at a time.
 * 
 * @author thaggie
 */
public class ZipFileProcessor {

    private static final int BUFFER_SIZE = 1024 * 1024 * 10; // 10MB
    private static final int BAOS_INITIAL_SIZE = 1024 * 1024 * 10; // 10MB
    private byte[] buffer = new byte[BUFFER_SIZE];
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream(BAOS_INITIAL_SIZE);

    /**
     * Helper to test a file name to see if it ends with jar, zip or war
     * 
     * @param fileName
     * @return
     */
    public static boolean isArchive(String fileName) {
        return fileName.endsWith(".jar") || fileName.endsWith(".zip") || fileName.endsWith(".war");
    }

    /**
     * Work through a file and its archives writing out all .class files.
     * 
     * @param file
     * @throws IOException
     */
    public void processZipFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        try {
            BufferedInputStream bis = new BufferedInputStream(fis);
            try {
                String[] path = {file.getCanonicalPath()};
                processZipStream(bis, path);
            } finally {
                bis.close();
            }
        } finally {
            fis.close();
        }
    }

    private void processZipStream(InputStream is, String[] path) throws IOException {
        ZipInputStream zis = new ZipInputStream(is);
        try {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                String name = ze.getName();
                if (name.endsWith(".class")) {
                    writeWithPath(System.out, path, name);
                } else if (isArchive(name)) {
                    byte[] entryData = readCurrenZipEntry(zis, ze);
                    String[] newPath = Arrays.copyOf(path, path.length + 1);
                    newPath[path.length] = name;
                    ByteArrayInputStream bais = new ByteArrayInputStream(entryData);
                    try {
                        processZipStream(bais, newPath);
                    } finally {
                        bais.close();
                    }
                }
            }
        } catch (ZipException ze) {
            // zip files might be compressed with an algorithm java can't process, log and continue
            writeWithPath(System.err, path, ze.getMessage());
        } finally {
            zis.close();
        }
    }

    private static void writeWithPath(PrintStream pw, String[] path, String name) {
        for (String part : path) {
            pw.print(part);
            pw.print(File.pathSeparator);
        }
        pw.println(name);
    }

    private byte[] readCurrenZipEntry(ZipInputStream zis, ZipEntry ze) throws IOException {
        int count;
        baos.reset();
        while ((count = zis.read(buffer)) != -1) {
            baos.write(buffer, 0, count);
        }
        byte[] bytes = baos.toByteArray();
        return bytes;
    }
}
