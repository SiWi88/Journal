package syfy.co.za.journal.ui.EntryDetail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import syfy.co.za.journal.R;
import syfy.co.za.journal.common.ViewModelFactory;
import syfy.co.za.journal.ui.AddEditEntry.AddEditEntryActivity;
import syfy.co.za.journal.ui.AddEditEntry.AddEditEntryFragment;
import syfy.co.za.journal.util.ActivityUtils;

import static syfy.co.za.journal.ui.AddEditEntry.AddEditEntryActivity.ADD_EDIT_RESULT_OK;
import static syfy.co.za.journal.ui.EntryDetail.EntryDetailFragment.REQUEST_EDIT_ENTRY;

public class EntryDetailActivity extends AppCompatActivity implements EntryDetailNavigator {

    public static final String EXTRA_ENTRY_ID = "ENTRY_ID";

    public static final  int DELETE_RESULT_OK = RESULT_FIRST_USER + 2;

    public static final  int EDIT_RESULT_OK = RESULT_FIRST_USER + 3;

    private EntryDetailViewModel entryDetailViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.entrydetail_activity);

        setupToolbar();

        EntryDetailFragment entryDetailFragment = findOrCreateViewFragment();

        ActivityUtils.replaceFragmentInActivity(getSupportFragmentManager(),
                entryDetailFragment, R.id.contentFrame);

        entryDetailViewModel = obtainViewModel(this);

        subscribeToNavigationChanges(entryDetailViewModel);
    }

    @NonNull
    private EntryDetailFragment findOrCreateViewFragment(){

        String entryId = getIntent().getStringExtra(EXTRA_ENTRY_ID);

        EntryDetailFragment entryDetailFragment = (EntryDetailFragment)getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if(entryDetailFragment == null) {
            entryDetailFragment = EntryDetailFragment.newInstance(entryId);
        }
        return entryDetailFragment;
    }

    @NonNull
    public static EntryDetailViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return ViewModelProviders.of(activity,factory).get(EntryDetailViewModel.class);
    }

    private void subscribeToNavigationChanges(EntryDetailViewModel entryDetailViewModel) {
        // The activity observes the navigation commands in the ViewModel
        entryDetailViewModel.getEditEntryCommand().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void empty) {
                EntryDetailActivity.this.onStartEntryEdit();
            }
        });
        entryDetailViewModel.getDeleteEntryCommand().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void empty) {
                EntryDetailActivity.this.onEntryDeleted();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_ENTRY) {
            // If the entry was edited successfully, go back to the list.
            if (resultCode == ADD_EDIT_RESULT_OK) {
                // If the result comes from the add/edit screen, it's an edit.
                setResult(EDIT_RESULT_OK);
                finish();
            }
        }    }

    @Override
    public void onEntryDeleted() {
        setResult(DELETE_RESULT_OK);
        finish();
    }

    @Override
    public void onStartEntryEdit() {
        String entryId = getIntent().getStringExtra(EXTRA_ENTRY_ID);
        Intent intent = new Intent(this, AddEditEntryActivity.class);
        intent.putExtra(AddEditEntryFragment.ARGUMENT_EDIT_ENTRY_ID, entryId);
        startActivityForResult(intent,REQUEST_EDIT_ENTRY);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }
}