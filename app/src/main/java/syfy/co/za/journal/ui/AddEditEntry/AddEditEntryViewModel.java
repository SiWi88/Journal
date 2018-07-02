package syfy.co.za.journal.ui.AddEditEntry;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

import syfy.co.za.journal.R;
import syfy.co.za.journal.common.SingleLiveEvent;
import syfy.co.za.journal.data.JournalEntry;
import syfy.co.za.journal.data.source.JournalDataSource;
import syfy.co.za.journal.data.source.JournalRepository;
import syfy.co.za.journal.util.SnackbarMessage;

public class AddEditEntryViewModel extends AndroidViewModel implements JournalDataSource.GetEntryCallback {

    private final JournalRepository journalRepository;

    public final ObservableField<String> title = new ObservableField<>();

    public final ObservableField<String> body = new ObservableField<>();

    public final ObservableField<Date> date = new ObservableField<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    private final SnackbarMessage mSnackbarText = new SnackbarMessage();

    private final SingleLiveEvent<Void> mEntryUpdated = new SingleLiveEvent<>();

    @Nullable private String mEntryId;

    private boolean mIsNewEntry;

    private boolean mIsDataLoaded = false;

    public AddEditEntryViewModel(@NonNull Application application, JournalRepository journalRepository) {
        super(application);
        this.journalRepository = journalRepository;
    }

    public void start(String entryId) {
        if (dataLoading.get()) {
            // Already loading, ignore.
            return;
        }
        mEntryId = entryId;
        if (entryId == null) {
            // No need to populate, it's a new task
            mIsNewEntry = true;
            return;
        }
        if (mIsDataLoaded) {
            // No need to populate, already have data.
            return;
        }
        mIsNewEntry = false;
        dataLoading.set(true);
        journalRepository.getEntry(entryId, this);
    }

    @Override
    public void onEntryLoaded(JournalEntry entry) {
        title.set(entry.getTitle());
        body.set(entry.getBody());
        dataLoading.set(false);
        mIsDataLoaded = true;
    }

    @Override
    public void onDataNotAvailable() {
        dataLoading.set(false);
    }

    // Called when clicking on fab.
    void saveEntry() {
        JournalEntry entry = new JournalEntry(title.get(), body.get(),getCurrentDateTime());
        if (entry.isEmpty()) {
            mSnackbarText.setValue(R.string.empty_entry_message);
            return;
        }
        if (isNewEntry() || mEntryId == null) {
            createTask(entry);
        } else {
            entry = new JournalEntry(title.get(), body.get(), mEntryId,getCurrentDateTime());
            updateTask(entry);
        }
    }

    private Date getCurrentDateTime(){
        Date date = Calendar.getInstance().getTime();
        return date;
    }

    SnackbarMessage getSnackbarMessage() {
        return mSnackbarText;
    }

    SingleLiveEvent<Void> getTaskUpdatedEvent() {
        return mEntryUpdated;
    }

    private boolean isNewEntry() {
        return mIsNewEntry;
    }

    private void createTask(JournalEntry newEntry) {
        journalRepository.saveEntry(newEntry);
        mEntryUpdated.call();
    }

    private void updateTask(JournalEntry entry) {
        if (isNewEntry()) {
            throw new RuntimeException("updateTask() was called but task is new.");
        }
        journalRepository.saveEntry(entry);
        mEntryUpdated.call();
    }
}