package thaggie.jwalk;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author thaggie
 */
public final class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        if (args.length != 1 && args.length != 2) {
            System.out.println("Usage: jwalk [path/to/directory/or/archive] [filter]");
            System.exit(1);
        }
        String path = args[0];

        String match = (args.length == 2) ? args[1] : null;

        File file = new File(path);
        if (!file.exists()) {
            System.out.println(file.getAbsolutePath() + ": No such file or directory.");
            System.exit(1);
        } else if (file.isFile()) {
            if (ZipFileProcessor.isArchive(file.getName())) {
                ZipFileProcessor zp = new ZipFileProcessor(match);
                zp.processZipFile(file);
            } else {
                System.out.println(file.getAbsolutePath() + " is not recognised as an archive.");
                System.exit(1);
            }
        } else {
            int cores = Runtime.getRuntime().availableProcessors();
            ExecutorService executor = Executors.newFixedThreadPool(cores);

            ThreadLocalZipFileProcessor processor = new ThreadLocalZipFileProcessor(match);
            processDirectory(file, executor, processor, match);

            executor.shutdown();

            while (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
            }
        }
    }

    private static void processDirectory(File dir,
                                         ExecutorService executor,
                                         ThreadLocalZipFileProcessor processor,
                                         String match) throws IOException {
        String[] files = dir.list();
        if (files != null) {
            for (String fileName : files) {
                File file = new File(dir, fileName);
                if (file.exists()) {
                    if (file.isDirectory()) {
                        processDirectory(file, executor, processor, match);
                    } else {
                        if (ZipFileProcessor.isArchive(fileName)) {
                            ZipFileRunnable zfr = new ZipFileRunnable(processor, file);
                            executor.submit(zfr);
                        } else if (fileName.endsWith(".class")) {
                            if (ZipFileProcessor.shouldWrite(fileName, match)) {
                                System.out.println(file.getCanonicalPath());
                            }
                        }
                    }
                }
            }
        }
    }

    private static class ThreadLocalZipFileProcessor extends ThreadLocal<ZipFileProcessor> {
        private final String match;

        public ThreadLocalZipFileProcessor(String match) {
            this.match = match;
        }

        @Override
        protected ZipFileProcessor initialValue() {
            return new ZipFileProcessor(match);
        }
    }

    private static class ZipFileRunnable implements Runnable {
        private final ThreadLocalZipFileProcessor processor;
        private final File file;

        public ZipFileRunnable(ThreadLocalZipFileProcessor processor, File file) {
            super();
            this.processor = processor;
            this.file = file;
        }

        public void run() {
            try {
                processor.get().processZipFile(file);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
