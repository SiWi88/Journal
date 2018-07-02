package syfy.co.za.journal.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import syfy.co.za.journal.data.JournalEntry;

import static com.google.common.base.Preconditions.checkNotNull;

public class JournalRepository implements JournalDataSource {

    private volatile static JournalRepository INSTANCE = null;

    private final JournalDataSource journalRemoteDataSource;

    private final JournalDataSource journalLocalDataSource;


    Map<String, JournalEntry> mCachedTasks;

    private boolean mCacheIsDirty = true;

    private JournalRepository(@NonNull JournalDataSource journalRemoteDataSource,
                              @NonNull JournalDataSource journalLocalDataSource) {
        this.journalRemoteDataSource = journalRemoteDataSource;
        this.journalLocalDataSource = journalLocalDataSource;
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param journalRemoteDataSource the backend data source
     * @param journalLocalDataSource  the device storage data source
     * @return the {@link JournalRepository} instance
     */
    public static JournalRepository getInstance(JournalDataSource journalRemoteDataSource,
                                                JournalDataSource journalLocalDataSource) {
        if (INSTANCE == null) {
            synchronized (JournalRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new JournalRepository(journalRemoteDataSource,journalLocalDataSource);
                }
            }
        }
        return INSTANCE;
    }


    @Override
    public void getEntries(final LoadJournalEntriesCallback callback) {
        checkNotNull(callback);

        if(mCachedTasks != null && !mCacheIsDirty) {
            callback.onEntriesLoaded(new ArrayList<>(mCachedTasks.values()));
            return;
        }

        if(mCacheIsDirty) {
            getEntriesFromRemoteDataSource(callback);
        } else {
            journalLocalDataSource.getEntries(new LoadJournalEntriesCallback() {
                @Override
                public void onEntriesLoaded(List<JournalEntry> entries) {
                    refreshCache(entries);
                    callback.onEntriesLoaded(new ArrayList<>(mCachedTasks.values()));
                }
                @Override
                public void onDataNotAvailable() {
                    getEntriesFromRemoteDataSource(callback);
                }
            });
        }
    }

    @Override
    public void getEntry(final String entryId, final GetEntryCallback callback) {
        checkNotNull(entryId);
        checkNotNull(callback);

        JournalEntry cachedEntry = getEntryWithId(entryId);

        // Respond immediately with cache if available
        if (cachedEntry != null) {
            callback.onEntryLoaded(cachedEntry);
            return;
        }

        // Load from server/persisted if needed.
        // Is the task in the local data source? If not, query the network.
        journalLocalDataSource.getEntry(entryId, new GetEntryCallback() {
            @Override
            public void onEntryLoaded(JournalEntry entry) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedTasks == null) {
                    mCachedTasks = new LinkedHashMap<>();
                }
                mCachedTasks.put(entry.getId(), entry);
                callback.onEntryLoaded(entry);
            }

            @Override
            public void onDataNotAvailable() {

                journalRemoteDataSource.getEntry(entryId, new GetEntryCallback() {
                    @Override
                    public void onEntryLoaded(JournalEntry entry) {
                        if (entry == null) {
                            onDataNotAvailable();
                            return;
                        }
                        // Do in memory cache update to keep the app UI up to date
                        if (mCachedTasks == null) {
                            mCachedTasks = new LinkedHashMap<>();
                        }
                        mCachedTasks.put(entry.getId(), entry);
                        callback.onEntryLoaded(entry);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void saveEntry(JournalEntry entry) {
        checkNotNull(entry);
        journalRemoteDataSource.saveEntry(entry);
        journalLocalDataSource.saveEntry(entry);
        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(entry.getId(), entry);
    }

    @Override
    public void refreshEntries() {
        mCacheIsDirty = true;
        mCachedTasks.clear();
    }



    @Override
    public void deleteAllEntries() {
        journalRemoteDataSource.deleteAllEntries();
        journalLocalDataSource.deleteAllEntries();

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
    }

    @Override
    public void deleteEntry(String entryId) {
        journalRemoteDataSource.deleteEntry(checkNotNull(entryId));
        journalLocalDataSource.deleteEntry(checkNotNull(entryId));
        mCachedTasks.remove(entryId);
    }

    private void getEntriesFromRemoteDataSource(@NonNull final LoadJournalEntriesCallback callback) {
        journalRemoteDataSource.getEntries(new LoadJournalEntriesCallback() {
            @Override
            public void onEntriesLoaded(List<JournalEntry> entries) {
                refreshCache(entries);
                refreshLocalDataSource(entries);
                callback.onEntriesLoaded(new ArrayList<>(mCachedTasks.values()));
                }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

        private void refreshCache(List<JournalEntry> entries) {
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
        for (JournalEntry entry : entries) {
            mCachedTasks.put(entry.getId(), entry);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<JournalEntry> entries) {
        journalLocalDataSource.deleteAllEntries();
        for (JournalEntry entry : entries) {
            journalLocalDataSource.saveEntry(entry);
        }
    }

    @Nullable
    private JournalEntry getEntryWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(id);
        }
    }
}
