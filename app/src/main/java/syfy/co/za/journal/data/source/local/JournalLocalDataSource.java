package syfy.co.za.journal.data.source.local;

import java.util.List;

import syfy.co.za.journal.common.AppExecutors;
import syfy.co.za.journal.data.JournalEntry;
import syfy.co.za.journal.data.source.JournalDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

public class JournalLocalDataSource implements JournalDataSource {

    private static volatile JournalLocalDataSource INSTANCE;

    private JournalDao journalDao;

    private AppExecutors appExecutors;

    // Prevent direct instantiation.
    private JournalLocalDataSource(AppExecutors appExecutors, JournalDao journalDao) {
        this.appExecutors = appExecutors;
        this.journalDao = journalDao;
    }

    public static JournalLocalDataSource getInstance( AppExecutors appExecutors, JournalDao journalDao) {
        if (INSTANCE == null) {
            synchronized (JournalLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new JournalLocalDataSource(appExecutors, journalDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getEntries(final LoadJournalEntriesCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<JournalEntry> entryList = journalDao.getEntries();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(entryList.isEmpty()){
                            callback.onDataNotAvailable();
                        } else {
                            callback.onEntriesLoaded(entryList);
                        }
                    }
                });

            }
        };
        appExecutors.diskIO().execute(runnable);
    }


    @Override
    public void getEntry(final String entryId, final GetEntryCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final JournalEntry journalEntry = journalDao.getEntryById(entryId);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(journalEntry != null){
                            callback.onEntryLoaded(journalEntry);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveEntry(final JournalEntry entry) {
        checkNotNull(entry);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                journalDao.insertEntry(entry);

            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteEntry(final String entryId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                journalDao.deleteEntryById(entryId);
            }
        };
        appExecutors.diskIO().execute(deleteRunnable);
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
