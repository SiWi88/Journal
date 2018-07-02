package syfy.co.za.journal.common;

import android.content.Context;
import android.support.annotation.NonNull;

import syfy.co.za.journal.data.source.JournalRepository;
import syfy.co.za.journal.data.source.local.JournalDatabase;
import syfy.co.za.journal.data.source.local.JournalLocalDataSource;
import syfy.co.za.journal.data.source.remote.JournalFirebaseDatabase;
import syfy.co.za.journal.data.source.remote.JournalRemoteDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

public class Injection {

    public static JournalRepository provideTasksRepository(@NonNull Context context) {
        checkNotNull(context);
        JournalDatabase database = JournalDatabase.getInstance(context);
        JournalFirebaseDatabase firebaseDatabase = JournalFirebaseDatabase.getInstance(context);
        return JournalRepository.getInstance(JournalRemoteDataSource.getInstance(new AppExecutors(),firebaseDatabase), JournalLocalDataSource.getInstance(new AppExecutors(), database.journalDao()));
    }
}
