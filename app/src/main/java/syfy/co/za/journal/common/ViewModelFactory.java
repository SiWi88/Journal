package syfy.co.za.journal.common;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import syfy.co.za.journal.data.source.JournalRepository;
import syfy.co.za.journal.ui.AddEditEntry.AddEditEntryViewModel;
import syfy.co.za.journal.ui.EntryDetail.EntryDetailViewModel;
import syfy.co.za.journal.ui.JournalEntries.JournalEntriesViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @SuppressLint("StaticFieldLeak")
    private static volatile ViewModelFactory INSTANCE;

    private final Application mApplication;

    private final JournalRepository journalRepository;

    public static ViewModelFactory getInstance(Application application) {

        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModelFactory(application, Injection.provideTasksRepository(application.getApplicationContext()));
                }
            }
        }
        return INSTANCE;
    }

    private ViewModelFactory(Application application, JournalRepository repository) {
        mApplication = application;
        journalRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AddEditEntryViewModel.class)) {
            //noinspection unchecked
            return (T) new AddEditEntryViewModel(mApplication, journalRepository);
        }
        else if (modelClass.isAssignableFrom(EntryDetailViewModel.class)) {
            //noinspection unchecked
            return (T) new EntryDetailViewModel(mApplication, journalRepository);
        }
        else if (modelClass.isAssignableFrom(JournalEntriesViewModel.class)) {
            //noinspection unchecked
            return (T) new JournalEntriesViewModel(mApplication, journalRepository);
        }
//        else if (modelClass.isAssignableFrom(JournalEntriesViewModel.class)) {
//            //noinspection unchecked
//            return (T) new TasksViewModel(mApplication, mTasksRepository);
//        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}

