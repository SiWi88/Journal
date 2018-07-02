package syfy.co.za.journal.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import syfy.co.za.journal.data.JournalEntry;
import syfy.co.za.journal.util.DateConverter;

/**
 * The Room Database that contains the Journal Entries table.
 */
@Database(entities = {JournalEntry.class}, version = 2 , exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class JournalDatabase extends RoomDatabase {

    private static JournalDatabase INSTANCE;

    public abstract JournalDao journalDao();

    private static final Object lock = new Object();

    public static JournalDatabase getInstance(Context context){
        synchronized (lock) {
            if(INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        JournalDatabase.class,"Journal.db")
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return INSTANCE;
    }
}