package syfy.co.za.journal.ui.JournalEntries;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

import syfy.co.za.journal.R;
import syfy.co.za.journal.common.SingleLiveEvent;
import syfy.co.za.journal.data.JournalEntry;
import syfy.co.za.journal.data.source.JournalDataSource;
import syfy.co.za.journal.data.source.JournalRepository;
import syfy.co.za.journal.ui.AddEditEntry.AddEditEntryActivity;
import syfy.co.za.journal.ui.EntryDetail.EntryDetailActivity;
import syfy.co.za.journal.util.SnackbarMessage;

public class JournalEntriesViewModel extends AndroidViewModel {

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    private final ObservableBoolean mIsDataLoadingError = new ObservableBoolean(false);

    public final ObservableBoolean empty = new ObservableBoolean(false);

    public final ObservableList<JournalEntry> items = new ObservableArrayList<>();

    private final SingleLiveEvent<String> mOpenEntryEvent = new SingleLiveEvent<>();

    private final SingleLiveEvent<Void> mNewEntryEvent = new SingleLiveEvent<>();

    public final ObservableField<String> noTasksLabel = new ObservableField<>();

    public final ObservableField<Drawable> noTaskIconRes = new ObservableField<>();

    public final ObservableBoolean entriesAddViewVisible = new ObservableBoolean();


    @SuppressLint("StaticFieldLeak")
    private final Context mContext; // To avoid leaks, this must be an Application Context.

    private final SnackbarMessage mSnackbarText = new SnackbarMessage();

    private final JournalRepository mJournalRepository;

    public JournalEntriesViewModel(Application context, JournalRepository repository) {
        super(context);
        mContext = context.getApplicationContext(); // Force use of Application Context.
        mJournalRepository = repository;
    }

    public void start() {
        loadEntries(false);
    }

    public void loadEntries(boolean forceUpdate) {
        loadEntries(forceUpdate, true);
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link syfy.co.za.journal.data.source.JournalDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadEntries(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            dataLoading.set(true);
        }
        if (forceUpdate) {
            mJournalRepository.refreshEntries();
        }

        mJournalRepository.getEntries(new JournalDataSource.LoadJournalEntriesCallback() {
            @Override
            public void onEntriesLoaded(List<JournalEntry> entries) {
                List<JournalEntry> entriesToShow = new ArrayList<>(entries);
                if (showLoadingUI) {
                    dataLoading.set(false);
                }
                mIsDataLoadingError.set(false);
                items.clear();
                items.addAll(entriesToShow);
                empty.set(items.isEmpty());
            }
            @Override
            public void onDataNotAvailable() {
                mIsDataLoadingError.set(true);
            }
        });
    }

    SnackbarMessage getSnackbarMessage() {
        return mSnackbarText;
    }

    SingleLiveEvent<String> getOpenEntryEvent() {
        return mOpenEntryEvent;
    }

    SingleLiveEvent<Void> getNewEntryEvent() {
        return mNewEntryEvent;
    }

    /**
     * Called by the Data Binding library and the FAB's click listener.
     */
    public void addNewEntry() {
        mNewEntryEvent.call();
    }

    void handleActivityResult(int requestCode, int resultCode) {
        if (AddEditEntryActivity.REQUEST_CODE == requestCode) {
            switch (resultCode) {
                case EntryDetailActivity.EDIT_RESULT_OK:
                    mSnackbarText.setValue(R.string.successfully_saved_entry_message);
                    break;
                case AddEditEntryActivity.ADD_EDIT_RESULT_OK:
                    mSnackbarText.setValue(R.string.successfully_added_entry_message);
                    break;
                case EntryDetailActivity.DELETE_RESULT_OK:
                    mSnackbarText.setValue(R.string.successfully_deleted_entry_message);
                    break;
            }
        }
    }

    public void clearCachedEntries(){
        mJournalRepository.refreshEntries();
    }
}
