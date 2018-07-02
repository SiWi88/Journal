package syfy.co.za.journal.ui.JournalEntries;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import syfy.co.za.journal.R;
import syfy.co.za.journal.common.ViewModelFactory;
import syfy.co.za.journal.ui.AddEditEntry.AddEditEntryActivity;
import syfy.co.za.journal.ui.EntryDetail.EntryDetailActivity;
import syfy.co.za.journal.util.ActivityUtils;

public class JournalEntriesActivity extends AppCompatActivity implements JournalEntriesNavigator{

    private JournalEntriesViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entries_activity);

        setupToolbar();

        setupViewFragment();

        mViewModel = obtainViewModel(this);

        mViewModel.getOpenEntryEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String entryId) {
                if(entryId != null) {
                    openEntryDetails(entryId);
                }
            }
        });
        mViewModel.getNewEntryEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void aVoid) {
                addNewEntry();
            }
        });
    }

    public static JournalEntriesViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        JournalEntriesViewModel viewModel = ViewModelProviders.of(activity,factory).get(JournalEntriesViewModel.class);
        return viewModel;
    }

    private void setupViewFragment() {
        JournalEntriesFragment journalEntriesFragment = (JournalEntriesFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (journalEntriesFragment == null) {
            journalEntriesFragment = JournalEntriesFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(getSupportFragmentManager(), journalEntriesFragment,R.id.contentFrame);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mViewModel.handleActivityResult(requestCode, resultCode);
    }

    @Override
    public void openEntryDetails(String taskId) {
        Intent intent = new Intent(this, EntryDetailActivity.class);
        intent.putExtra(EntryDetailActivity.EXTRA_ENTRY_ID, taskId);
        startActivityForResult(intent, AddEditEntryActivity.REQUEST_CODE);
    }

    @Override
    public void addNewEntry(){
        Intent intent = new Intent(this, AddEditEntryActivity.class);
        startActivityForResult(intent, AddEditEntryActivity.REQUEST_CODE);
    }
}
