package eu.alefzero.webdav;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.apache.commons.httpclient.methods.RequestEntity;

import eu.alefzero.webdav.OnDatatransferProgressListener;

import android.util.Log;


/**
 * A RequestEntity that represents a File.
 * 
 */
public class FileRequestEntity implements RequestEntity {

    final File file;
    final String contentType;
    OnDatatransferProgressListener listener;

    public FileRequestEntity(final File file, final String contentType) {
        super();
        if (file == null) {
            throw new IllegalArgumentException("File may not be null");
        }
        this.file = file;
        this.contentType = contentType;
    }
    
    public long getContentLength() {
        return this.file.length();
    }

    public String getContentType() {
        return this.contentType;
    }

    public boolean isRepeatable() {
        return true;
    }
    
    public void setOnDatatransferProgressListener(OnDatatransferProgressListener listener) {
        this.listener = listener;
    }
    
    public void writeRequest(final OutputStream out) throws IOException {
        //byte[] tmp = new byte[4096];
        ByteBuffer tmp = ByteBuffer.allocate(4096);
        int i = 0;
        
        FileChannel channel = new RandomAccessFile(this.file, "rw").getChannel();
        FileLock lock = channel.tryLock();
        //InputStream instream = new FileInputStream(this.file);
        
        try {
            //while ((i = instream.read(tmp)) >= 0) {
            while ((i = channel.read(tmp)) >= 0) {
                out.write(tmp.array(), 0, i);
                tmp.clear();
                if (listener != null) 
                    listener.transferProgress(i);
            }
        } catch (IOException io) {
            Log.e("FileRequestException", io.getMessage());
            throw new RuntimeException("Ugly solution to workaround the default policy of retries when the server falls while uploading ; temporal fix; really", io);   
            
        } finally {
            //instream.close();
            lock.release();
            channel.close();
        }
    }

}