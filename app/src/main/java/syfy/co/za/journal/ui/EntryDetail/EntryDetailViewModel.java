package syfy.co.za.journal.ui.EntryDetail;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

import syfy.co.za.journal.common.SingleLiveEvent;
import syfy.co.za.journal.data.JournalEntry;
import syfy.co.za.journal.data.source.JournalDataSource;
import syfy.co.za.journal.data.source.JournalRepository;
import syfy.co.za.journal.util.SnackbarMessage;

public class EntryDetailViewModel extends AndroidViewModel implements JournalDataSource.GetEntryCallback {

    public final ObservableField<JournalEntry> entry = new ObservableField<>();

    private final SingleLiveEvent<Void> mEditEntryCommand = new SingleLiveEvent<>();

    private final SingleLiveEvent<Void> mDeleteEntryCommand = new SingleLiveEvent<>();

    private final JournalRepository journalRepository;

    private boolean mIsDataLoading;

    private final SnackbarMessage mSnackbarText = new SnackbarMessage();

    public String dateFormatted;

    public EntryDetailViewModel(@NonNull Application application, JournalRepository journalRepository) {
        super(application);
        this.journalRepository = journalRepository;
    }

    @Override
    public void onEntryLoaded(JournalEntry entry) {
        setEntry(entry);
        mIsDataLoading = false;
    }

    @Override
    public void onDataNotAvailable() {
        entry.set(null);
        mIsDataLoading = false;
    }

    public void start(String entryId) {
        if (entryId != null) {
            mIsDataLoading = true;
            journalRepository.getEntry(entryId, this);
        }
    }

    public void setEntry(JournalEntry entry) {
        this.entry.set(entry);
        displayDate();
    }

    public void deleteEntry() {
        if (entry.get() != null) {
            journalRepository.deleteEntry(entry.get().getId());
            mDeleteEntryCommand.call();
        }
    }

    public void editEntry() {
        mEditEntryCommand.call();
    }

    public SnackbarMessage getSnackbarMessage() {
        return mSnackbarText;
    }

    public SingleLiveEvent<Void> getEditEntryCommand() {
        return mEditEntryCommand;
    }

    public SingleLiveEvent<Void> getDeleteEntryCommand() {
        return mDeleteEntryCommand;
    }

    public boolean isDataAvailable() {
        return entry.get() != null;
    }

    public boolean isDataLoading() {
        return mIsDataLoading;
    }

    public void onRefresh() {
        if (entry.get() != null) {
            start(entry.get().getId());
        }
    }

    private void displayDate(){
        Date date = entry.get().getDate();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE dd MMM yyyy hh:mm aa");
        dateFormatted = formatter.format(date);
    }
}