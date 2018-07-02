package syfy.co.za.journal.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import syfy.co.za.journal.data.JournalEntry;

@Dao
public interface JournalDao {

    /**
     * Select all entries from the entries table.
     *
     * @return all entries.
     */
    @Query("SELECT * FROM Entries")
    List<JournalEntry> getEntries();

    /**
     * Select a entry by id.
     *
     * @param entryId the entry id.
     * @return the entry with entryId.
     */
    @Query("SELECT * FROM Entries WHERE EntryId = :entryId")
    JournalEntry getEntryById(String entryId);

    /**
     * Insert a entry in the database. If the entry already exists, replace it.
     *
     * @param entry the entry to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEntry(JournalEntry entry);

    /**
     * Delete a entry by id.
     *
     * @return the number of entries deleted. This should always be 1.
     */
    @Query("DELETE FROM Entries WHERE EntryId = :entryId")
    int deleteEntryById(String entryId);
}
