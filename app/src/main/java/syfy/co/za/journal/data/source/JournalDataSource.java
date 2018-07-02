package syfy.co.za.journal.data.source;

import android.support.annotation.NonNull;

import java.util.List;

import syfy.co.za.journal.data.JournalEntry;

public interface JournalDataSource {

    interface LoadJournalEntriesCallback {
        void onEntriesLoaded(List<JournalEntry> entries);
        void onDataNotAvailable();
    }

    interface GetEntryCallback {
        void onEntryLoaded(JournalEntry entry);
        void onDataNotAvailable();
    }

    void getEntries(LoadJournalEntriesCallback callback);

    void getEntry(String entryId, GetEntryCallback callback);

    void saveEntry(JournalEntry entry);

    void refreshEntries();

    void deleteAllEntries();

    void deleteEntry(String entryId);
}
