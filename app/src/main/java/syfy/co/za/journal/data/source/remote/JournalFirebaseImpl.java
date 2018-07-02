package syfy.co.za.journal.data.source.remote;

import java.util.List;

import syfy.co.za.journal.data.JournalEntry;

public interface JournalFirebaseImpl {

    void insertEntry(JournalEntry entry);

    void deleteEntry(String entryId);

    List<JournalEntry> getAllEntries();

    JournalEntry getEntryById(String entryId);
}
