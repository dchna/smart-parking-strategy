package parking.store;
import java.io.*;
import java.nio.file.*;
import java.util.*;
public class DiskStore {
    private final Path dir;
    public DiskStore(Path dir) {
        this.dir=dir;
    }
    public synchronized Properties read(String category,String id) {
        Properties p=new Properties();
        Path f=file(category,id);
        if(Files.exists(f))try(InputStream in=Files.newInputStream(f)) {
            p.load(in);
        }
        catch(IOException e) {
            throw new IllegalStateException(e);
        }
        return p;
    }
    public synchronized void write(String category,String id,Properties p) {
        try {
            Files.createDirectories(dir);
            Path temp=Files.createTempFile(dir,"saving-",".tmp");
            try(OutputStream out=Files.newOutputStream(temp)) {
                p.store(out,"Smart Parking");
            }
            try {
                Files.move(temp,file(category,id),StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);
            }
            catch(AtomicMoveNotSupportedException e) {
                Files.move(temp,file(category,id),StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
    public synchronized List<String> ids(String category) {
        if(!Files.exists(dir))return List.of();
        try(var s=Files.list(dir)) {
            return s.map(x->x.getFileName().toString()).filter(x->x.startsWith(category+"_")&&x.endsWith(".properties")).map(x->x.substring(category.length()+1,x.length()-11)).sorted().toList();
        }
        catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
    private Path file(String c,String id) {
        if(!c.matches("[a-z]+")||!id.matches("[A-Za-z0-9-]+"))throw new IllegalArgumentException("Invalid store key");
        return dir.resolve(c+"_"+id+".properties");
    }
}
