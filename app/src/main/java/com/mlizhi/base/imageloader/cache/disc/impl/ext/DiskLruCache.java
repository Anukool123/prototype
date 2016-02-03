package com.mlizhi.base.imageloader.cache.disc.impl.ext;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import p016u.aly.bq;

final class DiskLruCache implements Closeable {
    static final long ANY_SEQUENCE_NUMBER = -1;
    private static final String CLEAN = "CLEAN";
    private static final String DIRTY = "DIRTY";
    static final String JOURNAL_FILE = "journal";
    static final String JOURNAL_FILE_BACKUP = "journal.bkp";
    static final String JOURNAL_FILE_TEMP = "journal.tmp";
    static final Pattern LEGAL_KEY_PATTERN;
    static final String MAGIC = "libcore.io.DiskLruCache";
    private static final OutputStream NULL_OUTPUT_STREAM;
    private static final String READ = "READ";
    private static final String REMOVE = "REMOVE";
    static final String VERSION_1 = "1";
    private final int appVersion;
    private final Callable<Void> cleanupCallable;
    private final File directory;
    final ThreadPoolExecutor executorService;
    private int fileCount;
    private final File journalFile;
    private final File journalFileBackup;
    private final File journalFileTmp;
    private Writer journalWriter;
    private final LinkedHashMap<String, Entry> lruEntries;
    private int maxFileCount;
    private long maxSize;
    private long nextSequenceNumber;
    private int redundantOpCount;
    private long size;
    private final int valueCount;

    /* renamed from: com.mlizhi.base.imageloader.cache.disc.impl.ext.DiskLruCache.1 */
    class C01191 implements Callable<Void> {
        C01191() {
        }

        public Void call() throws Exception {
            synchronized (DiskLruCache.this) {
                if (DiskLruCache.this.journalWriter == null) {
                } else {
                    DiskLruCache.this.trimToSize();
                    DiskLruCache.this.trimToFileCount();
                    if (DiskLruCache.this.journalRebuildRequired()) {
                        DiskLruCache.this.rebuildJournal();
                        DiskLruCache.this.redundantOpCount = 0;
                    }
                }
            }
            return null;
        }
    }

    /* renamed from: com.mlizhi.base.imageloader.cache.disc.impl.ext.DiskLruCache.2 */
    class C01202 extends OutputStream {
        C01202() {
        }

        public void write(int b) throws IOException {
        }
    }

    public final class Editor {
        private boolean committed;
        private final Entry entry;
        private boolean hasErrors;
        private final boolean[] written;

        private class FaultHidingOutputStream extends FilterOutputStream {
            private FaultHidingOutputStream(OutputStream out) {
                super(out);
            }

            public void write(int oneByte) {
                try {
                    this.out.write(oneByte);
                } catch (IOException e) {
                    Editor.this.hasErrors = true;
                }
            }

            public void write(byte[] buffer, int offset, int length) {
                try {
                    this.out.write(buffer, offset, length);
                } catch (IOException e) {
                    Editor.this.hasErrors = true;
                }
            }

            public void close() {
                try {
                    this.out.close();
                } catch (IOException e) {
                    Editor.this.hasErrors = true;
                }
            }

            public void flush() {
                try {
                    this.out.flush();
                } catch (IOException e) {
                    Editor.this.hasErrors = true;
                }
            }
        }

        private Editor(Entry entry) {
            this.entry = entry;
            this.written = entry.readable ? null : new boolean[DiskLruCache.this.valueCount];
        }

        public InputStream newInputStream(int index) throws IOException {
            synchronized (DiskLruCache.this) {
                if (this.entry.currentEditor != this) {
                    throw new IllegalStateException();
                } else if (this.entry.readable) {
                    try {
                        InputStream fileInputStream = new FileInputStream(this.entry.getCleanFile(index));
                        return fileInputStream;
                    } catch (FileNotFoundException e) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }

        public String getString(int index) throws IOException {
            InputStream in = newInputStream(index);
            return in != null ? DiskLruCache.inputStreamToString(in) : null;
        }

        public OutputStream newOutputStream(int index) throws IOException {
            OutputStream access$10;
            synchronized (DiskLruCache.this) {
                FileOutputStream outputStream;
                if (this.entry.currentEditor != this) {
                    throw new IllegalStateException();
                }
                if (!this.entry.readable) {
                    this.written[index] = true;
                }
                dirtyFile = this.entry.getDirtyFile(index);
                try {
                    outputStream = new FileOutputStream(dirtyFile);
                } catch (FileNotFoundException e) {
                    DiskLruCache.this.directory.mkdirs();
                    try {
                        File dirtyFile;
                        outputStream = new FileOutputStream(dirtyFile);
                    } catch (FileNotFoundException e2) {
                        access$10 = DiskLruCache.NULL_OUTPUT_STREAM;
                    }
                }
                access$10 = new FaultHidingOutputStream(outputStream, null);
            }
            return access$10;
        }

        public void set(int index, String value) throws IOException {
            Throwable th;
            Writer writer = null;
            try {
                Writer writer2 = new OutputStreamWriter(newOutputStream(index), Util.UTF_8);
                try {
                    writer2.write(value);
                    Util.closeQuietly(writer2);
                } catch (Throwable th2) {
                    th = th2;
                    writer = writer2;
                    Util.closeQuietly(writer);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                Util.closeQuietly(writer);
                throw th;
            }
        }

        public void commit() throws IOException {
            if (this.hasErrors) {
                DiskLruCache.this.completeEdit(this, false);
                DiskLruCache.this.remove(this.entry.key);
            } else {
                DiskLruCache.this.completeEdit(this, true);
            }
            this.committed = true;
        }

        public void abort() throws IOException {
            DiskLruCache.this.completeEdit(this, false);
        }

        public void abortUnlessCommitted() {
            if (!this.committed) {
                try {
                    abort();
                } catch (IOException e) {
                }
            }
        }
    }

    private final class Entry {
        private Editor currentEditor;
        private final String key;
        private final long[] lengths;
        private boolean readable;
        private long sequenceNumber;

        private Entry(String key) {
            this.key = key;
            this.lengths = new long[DiskLruCache.this.valueCount];
        }

        public String getLengths() throws IOException {
            StringBuilder result = new StringBuilder();
            for (long size : this.lengths) {
                result.append(' ').append(size);
            }
            return result.toString();
        }

        private void setLengths(String[] strings) throws IOException {
            if (strings.length != DiskLruCache.this.valueCount) {
                throw invalidLengths(strings);
            }
            int i = 0;
            while (i < strings.length) {
                try {
                    this.lengths[i] = Long.parseLong(strings[i]);
                    i++;
                } catch (NumberFormatException e) {
                    throw invalidLengths(strings);
                }
            }
        }

        private IOException invalidLengths(String[] strings) throws IOException {
            throw new IOException("unexpected journal line: " + Arrays.toString(strings));
        }

        public File getCleanFile(int i) {
            return new File(DiskLruCache.this.directory, this.key + "." + i);
        }

        public File getDirtyFile(int i) {
            return new File(DiskLruCache.this.directory, this.key + "." + i + ".tmp");
        }
    }

    public final class Snapshot implements Closeable {
        private File[] files;
        private final InputStream[] ins;
        private final String key;
        private final long[] lengths;
        private final long sequenceNumber;

        private Snapshot(String key, long sequenceNumber, File[] files, InputStream[] ins, long[] lengths) {
            this.key = key;
            this.sequenceNumber = sequenceNumber;
            this.files = files;
            this.ins = ins;
            this.lengths = lengths;
        }

        public Editor edit() throws IOException {
            return DiskLruCache.this.edit(this.key, this.sequenceNumber);
        }

        public File getFile(int index) {
            return this.files[index];
        }

        public InputStream getInputStream(int index) {
            return this.ins[index];
        }

        public String getString(int index) throws IOException {
            return DiskLruCache.inputStreamToString(getInputStream(index));
        }

        public long getLength(int index) {
            return this.lengths[index];
        }

        public void close() {
            for (InputStream in : this.ins) {
                Util.closeQuietly(in);
            }
        }
    }

    static {
        LEGAL_KEY_PATTERN = Pattern.compile("[a-z0-9_-]{1,64}");
        NULL_OUTPUT_STREAM = new C01202();
    }

    private DiskLruCache(File directory, int appVersion, int valueCount, long maxSize, int maxFileCount) {
        this.size = 0;
        this.fileCount = 0;
        this.lruEntries = new LinkedHashMap(0, 0.75f, true);
        this.nextSequenceNumber = 0;
        this.executorService = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
        this.cleanupCallable = new C01191();
        this.directory = directory;
        this.appVersion = appVersion;
        this.journalFile = new File(directory, JOURNAL_FILE);
        this.journalFileTmp = new File(directory, JOURNAL_FILE_TEMP);
        this.journalFileBackup = new File(directory, JOURNAL_FILE_BACKUP);
        this.valueCount = valueCount;
        this.maxSize = maxSize;
        this.maxFileCount = maxFileCount;
    }

    public static DiskLruCache open(File directory, int appVersion, int valueCount, long maxSize, int maxFileCount) throws IOException {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        } else if (maxFileCount <= 0) {
            throw new IllegalArgumentException("maxFileCount <= 0");
        } else if (valueCount <= 0) {
            throw new IllegalArgumentException("valueCount <= 0");
        } else {
            File backupFile = new File(directory, JOURNAL_FILE_BACKUP);
            if (backupFile.exists()) {
                File journalFile = new File(directory, JOURNAL_FILE);
                if (journalFile.exists()) {
                    backupFile.delete();
                } else {
                    renameTo(backupFile, journalFile, false);
                }
            }
            DiskLruCache cache = new DiskLruCache(directory, appVersion, valueCount, maxSize, maxFileCount);
            if (cache.journalFile.exists()) {
                try {
                    cache.readJournal();
                    cache.processJournal();
                    cache.journalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cache.journalFile, true), Util.US_ASCII));
                    return cache;
                } catch (IOException journalIsCorrupt) {
                    System.out.println("DiskLruCache " + directory + " is corrupt: " + journalIsCorrupt.getMessage() + ", removing");
                    cache.delete();
                }
            }
            directory.mkdirs();
            cache = new DiskLruCache(directory, appVersion, valueCount, maxSize, maxFileCount);
            cache.rebuildJournal();
            return cache;
        }
    }

    private void readJournal() throws IOException {
        StrictLineReader reader = new StrictLineReader(new FileInputStream(this.journalFile), Util.US_ASCII);
        int lineCount;
        try {
            String magic = reader.readLine();
            String version = reader.readLine();
            String appVersionString = reader.readLine();
            String valueCountString = reader.readLine();
            String blank = reader.readLine();
            if (MAGIC.equals(magic) && VERSION_1.equals(version) && Integer.toString(this.appVersion).equals(appVersionString) && Integer.toString(this.valueCount).equals(valueCountString) && bq.f888b.equals(blank)) {
                lineCount = 0;
                while (true) {
                    readJournalLine(reader.readLine());
                    lineCount++;
                }
            } else {
                throw new IOException("unexpected journal header: [" + magic + ", " + version + ", " + valueCountString + ", " + blank + "]");
            }
        } catch (EOFException e) {
            this.redundantOpCount = lineCount - this.lruEntries.size();
            Util.closeQuietly(reader);
        } catch (Throwable th) {
            Util.closeQuietly(reader);
        }
    }

    private void readJournalLine(String line) throws IOException {
        int firstSpace = line.indexOf(32);
        if (firstSpace == -1) {
            throw new IOException("unexpected journal line: " + line);
        }
        String key;
        int keyBegin = firstSpace + 1;
        int secondSpace = line.indexOf(32, keyBegin);
        if (secondSpace == -1) {
            key = line.substring(keyBegin);
            if (firstSpace == REMOVE.length() && line.startsWith(REMOVE)) {
                this.lruEntries.remove(key);
                return;
            }
        }
        key = line.substring(keyBegin, secondSpace);
        Entry entry = (Entry) this.lruEntries.get(key);
        if (entry == null) {
            entry = new Entry(key, null);
            this.lruEntries.put(key, entry);
        }
        if (secondSpace != -1 && firstSpace == CLEAN.length() && line.startsWith(CLEAN)) {
            String[] parts = line.substring(secondSpace + 1).split(" ");
            entry.readable = true;
            entry.currentEditor = null;
            entry.setLengths(parts);
        } else if (secondSpace == -1 && firstSpace == DIRTY.length() && line.startsWith(DIRTY)) {
            entry.currentEditor = new Editor(entry, null);
        } else if (secondSpace != -1 || firstSpace != READ.length() || !line.startsWith(READ)) {
            throw new IOException("unexpected journal line: " + line);
        }
    }

    private void processJournal() throws IOException {
        deleteIfExists(this.journalFileTmp);
        Iterator<Entry> i = this.lruEntries.values().iterator();
        while (i.hasNext()) {
            Entry entry = (Entry) i.next();
            int t;
            if (entry.currentEditor == null) {
                for (t = 0; t < this.valueCount; t++) {
                    this.size += entry.lengths[t];
                    this.fileCount++;
                }
            } else {
                entry.currentEditor = null;
                for (t = 0; t < this.valueCount; t++) {
                    deleteIfExists(entry.getCleanFile(t));
                    deleteIfExists(entry.getDirtyFile(t));
                }
                i.remove();
            }
        }
    }

    private synchronized void rebuildJournal() throws IOException {
        if (this.journalWriter != null) {
            this.journalWriter.close();
        }
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.journalFileTmp), Util.US_ASCII));
        writer.write(MAGIC);
        writer.write("\n");
        writer.write(VERSION_1);
        writer.write("\n");
        writer.write(Integer.toString(this.appVersion));
        writer.write("\n");
        writer.write(Integer.toString(this.valueCount));
        writer.write("\n");
        writer.write("\n");
        for (Entry entry : this.lruEntries.values()) {
            try {
                if (entry.currentEditor != null) {
                    writer.write("DIRTY " + entry.key + '\n');
                } else {
                    writer.write("CLEAN " + entry.key + entry.getLengths() + '\n');
                }
            } catch (Throwable th) {
                writer.close();
            }
        }
        writer.close();
        if (this.journalFile.exists()) {
            renameTo(this.journalFile, this.journalFileBackup, true);
        }
        renameTo(this.journalFileTmp, this.journalFile, false);
        this.journalFileBackup.delete();
        this.journalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.journalFile, true), Util.US_ASCII));
    }

    private static void deleteIfExists(File file) throws IOException {
        if (file.exists() && !file.delete()) {
            throw new IOException();
        }
    }

    private static void renameTo(File from, File to, boolean deleteDestination) throws IOException {
        if (deleteDestination) {
            deleteIfExists(to);
        }
        if (!from.renameTo(to)) {
            throw new IOException();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized Snapshot get(String r14) throws IOException {
        /*
        r13 = this;
        r0 = 0;
        monitor-enter(r13);
        r13.checkNotClosed();	 Catch:{ all -> 0x0065 }
        r13.validateKey(r14);	 Catch:{ all -> 0x0065 }
        r1 = r13.lruEntries;	 Catch:{ all -> 0x0065 }
        r10 = r1.get(r14);	 Catch:{ all -> 0x0065 }
        r10 = (com.mlizhi.base.imageloader.cache.disc.impl.ext.DiskLruCache.Entry) r10;	 Catch:{ all -> 0x0065 }
        if (r10 != 0) goto L_0x0014;
    L_0x0012:
        monitor-exit(r13);
        return r0;
    L_0x0014:
        r1 = r10.readable;	 Catch:{ all -> 0x0065 }
        if (r1 == 0) goto L_0x0012;
    L_0x001a:
        r1 = r13.valueCount;	 Catch:{ all -> 0x0065 }
        r5 = new java.io.File[r1];	 Catch:{ all -> 0x0065 }
        r1 = r13.valueCount;	 Catch:{ all -> 0x0065 }
        r6 = new java.io.InputStream[r1];	 Catch:{ all -> 0x0065 }
        r12 = 0;
    L_0x0023:
        r1 = r13.valueCount;	 Catch:{ FileNotFoundException -> 0x0078 }
        if (r12 < r1) goto L_0x0068;
    L_0x0027:
        r0 = r13.redundantOpCount;	 Catch:{ all -> 0x0065 }
        r0 = r0 + 1;
        r13.redundantOpCount = r0;	 Catch:{ all -> 0x0065 }
        r0 = r13.journalWriter;	 Catch:{ all -> 0x0065 }
        r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0065 }
        r2 = "READ ";
        r1.<init>(r2);	 Catch:{ all -> 0x0065 }
        r1 = r1.append(r14);	 Catch:{ all -> 0x0065 }
        r2 = 10;
        r1 = r1.append(r2);	 Catch:{ all -> 0x0065 }
        r1 = r1.toString();	 Catch:{ all -> 0x0065 }
        r0.append(r1);	 Catch:{ all -> 0x0065 }
        r0 = r13.journalRebuildRequired();	 Catch:{ all -> 0x0065 }
        if (r0 == 0) goto L_0x0054;
    L_0x004d:
        r0 = r13.executorService;	 Catch:{ all -> 0x0065 }
        r1 = r13.cleanupCallable;	 Catch:{ all -> 0x0065 }
        r0.submit(r1);	 Catch:{ all -> 0x0065 }
    L_0x0054:
        r0 = new com.mlizhi.base.imageloader.cache.disc.impl.ext.DiskLruCache$Snapshot;	 Catch:{ all -> 0x0065 }
        r3 = r10.sequenceNumber;	 Catch:{ all -> 0x0065 }
        r7 = r10.lengths;	 Catch:{ all -> 0x0065 }
        r8 = 0;
        r1 = r13;
        r2 = r14;
        r0.<init>(r2, r3, r5, r6, r7, r8);	 Catch:{ all -> 0x0065 }
        goto L_0x0012;
    L_0x0065:
        r0 = move-exception;
        monitor-exit(r13);
        throw r0;
    L_0x0068:
        r11 = r10.getCleanFile(r12);	 Catch:{ FileNotFoundException -> 0x0078 }
        r5[r12] = r11;	 Catch:{ FileNotFoundException -> 0x0078 }
        r1 = new java.io.FileInputStream;	 Catch:{ FileNotFoundException -> 0x0078 }
        r1.<init>(r11);	 Catch:{ FileNotFoundException -> 0x0078 }
        r6[r12] = r1;	 Catch:{ FileNotFoundException -> 0x0078 }
        r12 = r12 + 1;
        goto L_0x0023;
    L_0x0078:
        r9 = move-exception;
        r12 = 0;
    L_0x007a:
        r1 = r13.valueCount;	 Catch:{ all -> 0x0065 }
        if (r12 >= r1) goto L_0x0012;
    L_0x007e:
        r1 = r6[r12];	 Catch:{ all -> 0x0065 }
        if (r1 == 0) goto L_0x0012;
    L_0x0082:
        r1 = r6[r12];	 Catch:{ all -> 0x0065 }
        com.mlizhi.base.imageloader.cache.disc.impl.ext.Util.closeQuietly(r1);	 Catch:{ all -> 0x0065 }
        r12 = r12 + 1;
        goto L_0x007a;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mlizhi.base.imageloader.cache.disc.impl.ext.DiskLruCache.get(java.lang.String):com.mlizhi.base.imageloader.cache.disc.impl.ext.DiskLruCache$Snapshot");
    }

    public Editor edit(String key) throws IOException {
        return edit(key, ANY_SEQUENCE_NUMBER);
    }

    private synchronized Editor edit(String key, long expectedSequenceNumber) throws IOException {
        Editor editor = null;
        synchronized (this) {
            checkNotClosed();
            validateKey(key);
            Entry entry = (Entry) this.lruEntries.get(key);
            if (expectedSequenceNumber == ANY_SEQUENCE_NUMBER || (entry != null && entry.sequenceNumber == expectedSequenceNumber)) {
                if (entry == null) {
                    entry = new Entry(key, null);
                    this.lruEntries.put(key, entry);
                } else if (entry.currentEditor != null) {
                }
                editor = new Editor(entry, null);
                entry.currentEditor = editor;
                this.journalWriter.write("DIRTY " + key + '\n');
                this.journalWriter.flush();
            }
        }
        return editor;
    }

    public File getDirectory() {
        return this.directory;
    }

    public synchronized long getMaxSize() {
        return this.maxSize;
    }

    public synchronized int getMaxFileCount() {
        return this.maxFileCount;
    }

    public synchronized void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
        this.executorService.submit(this.cleanupCallable);
    }

    public synchronized long size() {
        return this.size;
    }

    public synchronized long fileCount() {
        return (long) this.fileCount;
    }

    private synchronized void completeEdit(Editor editor, boolean success) throws IOException {
        Entry entry = editor.entry;
        if (entry.currentEditor != editor) {
            throw new IllegalStateException();
        }
        int i;
        if (success) {
            if (!entry.readable) {
                i = 0;
                while (i < this.valueCount) {
                    if (!editor.written[i]) {
                        editor.abort();
                        throw new IllegalStateException("Newly created entry didn't create value for index " + i);
                    } else if (!entry.getDirtyFile(i).exists()) {
                        editor.abort();
                        break;
                    } else {
                        i++;
                    }
                }
            }
        }
        for (i = 0; i < this.valueCount; i++) {
            File dirty = entry.getDirtyFile(i);
            if (!success) {
                deleteIfExists(dirty);
            } else if (dirty.exists()) {
                File clean = entry.getCleanFile(i);
                dirty.renameTo(clean);
                long oldLength = entry.lengths[i];
                long newLength = clean.length();
                entry.lengths[i] = newLength;
                this.size = (this.size - oldLength) + newLength;
                this.fileCount++;
            }
        }
        this.redundantOpCount++;
        entry.currentEditor = null;
        if ((entry.readable | success) != 0) {
            entry.readable = true;
            this.journalWriter.write("CLEAN " + entry.key + entry.getLengths() + '\n');
            if (success) {
                long j = this.nextSequenceNumber;
                this.nextSequenceNumber = 1 + j;
                entry.sequenceNumber = j;
            }
        } else {
            this.lruEntries.remove(entry.key);
            this.journalWriter.write("REMOVE " + entry.key + '\n');
        }
        this.journalWriter.flush();
        if (this.size > this.maxSize || this.fileCount > this.maxFileCount || journalRebuildRequired()) {
            this.executorService.submit(this.cleanupCallable);
        }
    }

    private boolean journalRebuildRequired() {
        return this.redundantOpCount >= 2000 && this.redundantOpCount >= this.lruEntries.size();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean remove(String r8) throws IOException {
        /*
        r7 = this;
        monitor-enter(r7);
        r7.checkNotClosed();	 Catch:{ all -> 0x0078 }
        r7.validateKey(r8);	 Catch:{ all -> 0x0078 }
        r3 = r7.lruEntries;	 Catch:{ all -> 0x0078 }
        r0 = r3.get(r8);	 Catch:{ all -> 0x0078 }
        r0 = (com.mlizhi.base.imageloader.cache.disc.impl.ext.DiskLruCache.Entry) r0;	 Catch:{ all -> 0x0078 }
        if (r0 == 0) goto L_0x0017;
    L_0x0011:
        r3 = r0.currentEditor;	 Catch:{ all -> 0x0078 }
        if (r3 == 0) goto L_0x001a;
    L_0x0017:
        r3 = 0;
    L_0x0018:
        monitor-exit(r7);
        return r3;
    L_0x001a:
        r2 = 0;
    L_0x001b:
        r3 = r7.valueCount;	 Catch:{ all -> 0x0078 }
        if (r2 < r3) goto L_0x0053;
    L_0x001f:
        r3 = r7.redundantOpCount;	 Catch:{ all -> 0x0078 }
        r3 = r3 + 1;
        r7.redundantOpCount = r3;	 Catch:{ all -> 0x0078 }
        r3 = r7.journalWriter;	 Catch:{ all -> 0x0078 }
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0078 }
        r5 = "REMOVE ";
        r4.<init>(r5);	 Catch:{ all -> 0x0078 }
        r4 = r4.append(r8);	 Catch:{ all -> 0x0078 }
        r5 = 10;
        r4 = r4.append(r5);	 Catch:{ all -> 0x0078 }
        r4 = r4.toString();	 Catch:{ all -> 0x0078 }
        r3.append(r4);	 Catch:{ all -> 0x0078 }
        r3 = r7.lruEntries;	 Catch:{ all -> 0x0078 }
        r3.remove(r8);	 Catch:{ all -> 0x0078 }
        r3 = r7.journalRebuildRequired();	 Catch:{ all -> 0x0078 }
        if (r3 == 0) goto L_0x0051;
    L_0x004a:
        r3 = r7.executorService;	 Catch:{ all -> 0x0078 }
        r4 = r7.cleanupCallable;	 Catch:{ all -> 0x0078 }
        r3.submit(r4);	 Catch:{ all -> 0x0078 }
    L_0x0051:
        r3 = 1;
        goto L_0x0018;
    L_0x0053:
        r1 = r0.getCleanFile(r2);	 Catch:{ all -> 0x0078 }
        r3 = r1.exists();	 Catch:{ all -> 0x0078 }
        if (r3 == 0) goto L_0x007b;
    L_0x005d:
        r3 = r1.delete();	 Catch:{ all -> 0x0078 }
        if (r3 != 0) goto L_0x007b;
    L_0x0063:
        r3 = new java.io.IOException;	 Catch:{ all -> 0x0078 }
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0078 }
        r5 = "failed to delete ";
        r4.<init>(r5);	 Catch:{ all -> 0x0078 }
        r4 = r4.append(r1);	 Catch:{ all -> 0x0078 }
        r4 = r4.toString();	 Catch:{ all -> 0x0078 }
        r3.<init>(r4);	 Catch:{ all -> 0x0078 }
        throw r3;	 Catch:{ all -> 0x0078 }
    L_0x0078:
        r3 = move-exception;
        monitor-exit(r7);
        throw r3;
    L_0x007b:
        r3 = r7.size;	 Catch:{ all -> 0x0078 }
        r5 = r0.lengths;	 Catch:{ all -> 0x0078 }
        r5 = r5[r2];	 Catch:{ all -> 0x0078 }
        r3 = r3 - r5;
        r7.size = r3;	 Catch:{ all -> 0x0078 }
        r3 = r7.fileCount;	 Catch:{ all -> 0x0078 }
        r3 = r3 + -1;
        r7.fileCount = r3;	 Catch:{ all -> 0x0078 }
        r3 = r0.lengths;	 Catch:{ all -> 0x0078 }
        r4 = 0;
        r3[r2] = r4;	 Catch:{ all -> 0x0078 }
        r2 = r2 + 1;
        goto L_0x001b;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mlizhi.base.imageloader.cache.disc.impl.ext.DiskLruCache.remove(java.lang.String):boolean");
    }

    public synchronized boolean isClosed() {
        return this.journalWriter == null;
    }

    private void checkNotClosed() {
        if (this.journalWriter == null) {
            throw new IllegalStateException("cache is closed");
        }
    }

    public synchronized void flush() throws IOException {
        checkNotClosed();
        trimToSize();
        trimToFileCount();
        this.journalWriter.flush();
    }

    public synchronized void close() throws IOException {
        if (this.journalWriter != null) {
            Iterator it = new ArrayList(this.lruEntries.values()).iterator();
            while (it.hasNext()) {
                Entry entry = (Entry) it.next();
                if (entry.currentEditor != null) {
                    entry.currentEditor.abort();
                }
            }
            trimToSize();
            trimToFileCount();
            this.journalWriter.close();
            this.journalWriter = null;
        }
    }

    private void trimToSize() throws IOException {
        while (this.size > this.maxSize) {
            remove((String) ((java.util.Map.Entry) this.lruEntries.entrySet().iterator().next()).getKey());
        }
    }

    private void trimToFileCount() throws IOException {
        while (this.fileCount > this.maxFileCount) {
            remove((String) ((java.util.Map.Entry) this.lruEntries.entrySet().iterator().next()).getKey());
        }
    }

    public void delete() throws IOException {
        close();
        Util.deleteContents(this.directory);
    }

    private void validateKey(String key) {
        if (!LEGAL_KEY_PATTERN.matcher(key).matches()) {
            throw new IllegalArgumentException("keys must match regex [a-z0-9_-]{1,64}: \"" + key + "\"");
        }
    }

    private static String inputStreamToString(InputStream in) throws IOException {
        return Util.readFully(new InputStreamReader(in, Util.UTF_8));
    }
}
