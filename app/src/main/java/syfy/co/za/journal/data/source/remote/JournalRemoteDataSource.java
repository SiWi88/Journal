package syfy.co.za.journal.data.source.remote;

import java.util.List;
import android.os.Handler;

import syfy.co.za.journal.common.AppExecutors;
import syfy.co.za.journal.data.JournalEntry;
import syfy.co.za.journal.data.source.JournalDataSource;
import syfy.co.za.journal.data.source.local.JournalDao;

import static com.google.common.base.Preconditions.checkNotNull;

public class JournalRemoteDataSource implements JournalDataSource{

    private static JournalRemoteDataSource INSTANCE;

    private JournalFirebaseImpl journalFirebaseImpl;

    private AppExecutors appExecutors;

    private static final int SERVICE_LATENCY_IN_MILLIS = 3000;

    private JournalRemoteDataSource(AppExecutors appExecutors, JournalFirebaseImpl journalFirebaseImpl) {
        this.appExecutors = appExecutors;
        this.journalFirebaseImpl = journalFirebaseImpl;
    }

    public static JournalRemoteDataSource getInstance(AppExecutors appExecutors, JournalFirebaseDatabase journalFirebaseDatabase) {
        if (INSTANCE == null) {
            INSTANCE = new JournalRemoteDataSource(appExecutors,journalFirebaseDatabase);
        }
        return INSTANCE;
    }

    @Override
    public void getEntries(final LoadJournalEntriesCallback callback) {
        final List<JournalEntry> entryList = journalFirebaseImpl.getAllEntries();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onEntriesLoaded(entryList);
            }
        },SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void getEntry(final String entryId, final GetEntryCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final JournalEntry journalEntry = journalFirebaseImpl.getEntryById(entryId);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(journalEntry != null) {
                            callback.onEntryLoaded(journalEntry);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };
        appExecutors.networkIO().execute(runnable);
    }

    @Override
    public void saveEntry(final JournalEntry entry) {
        checkNotNull(entry);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                journalFirebaseImpl.insertEntry(entry);
            }
        };
        appExecutors.networkIO().execute(runnable);
    }

    @Override
    public void deleteEntry(final String entryId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                journalFirebaseImpl.deleteEntry(entryId);
            }
        };
        appExecutors.networkIO().execute(runnable);
    }

    @Override
    public void refreshEntries() {
        // Not required because the {@link EntryRepository} handles the logic of refreshing the
        // entries from all the available data sources.
        // todo:setup repository to get from local or remote depending on the needs
    }


    @Override
    public void deleteAllEntries() {
        //todo:add delete all entries logic here
    }
}