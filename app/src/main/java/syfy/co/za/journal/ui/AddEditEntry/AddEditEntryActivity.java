package syfy.co.za.journal.ui.AddEditEntry;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import syfy.co.za.journal.R;
import syfy.co.za.journal.common.ViewModelFactory;
import syfy.co.za.journal.util.ActivityUtils;

public class AddEditEntryActivity extends AppCompatActivity implements AddEditEntryNavigator{

    public static final int REQUEST_CODE = 1;

    public static final int ADD_EDIT_RESULT_OK = RESULT_FIRST_USER + 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.addedit_activity);

        setUpToolbar();

        AddEditEntryFragment addEditEntryFragment = obtainViewFragment();

        ActivityUtils.replaceFragmentInActivity(getSupportFragmentManager(), addEditEntryFragment, R.id.contentFrame);

        subscribeToNavigationChanges();
    }

    @Override
    public void onEntrySaved() {
        setResult(ADD_EDIT_RESULT_OK);
        finish();
    }

    private void subscribeToNavigationChanges() {
        AddEditEntryViewModel viewModel = obtainViewModel(this);
        // The activity observes the navigation events in the ViewModel
        viewModel.getTaskUpdatedEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void empty) {
                AddEditEntryActivity.this.onEntrySaved();
            }
        });
    }

    public static AddEditEntryViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return ViewModelProviders.of(activity, factory).get(AddEditEntryViewModel.class);
    }

    @NonNull
    private AddEditEntryFragment obtainViewFragment() {
        // View Fragment
        AddEditEntryFragment addEditTaskFragment = (AddEditEntryFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (addEditTaskFragment == null) {
            addEditTaskFragment = AddEditEntryFragment.newInstance();

            // Send the task ID to the fragment
            Bundle bundle = new Bundle();
            bundle.putString(AddEditEntryFragment.ARGUMENT_EDIT_ENTRY_ID,
                    getIntent().getStringExtra(AddEditEntryFragment.ARGUMENT_EDIT_ENTRY_ID));
            addEditTaskFragment.setArguments(bundle);
        }
        return addEditTaskFragment;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setUpToolbar(){
        // Set up the toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }
}
