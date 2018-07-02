package syfy.co.za.journal.ui.AddEditEntry;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import syfy.co.za.journal.R;
import syfy.co.za.journal.databinding.AddeditFragmentBinding;
import syfy.co.za.journal.util.SnackbarMessage;
import syfy.co.za.journal.util.SnackbarUtils;

public class AddEditEntryFragment extends Fragment {

    public static final String ARGUMENT_EDIT_ENTRY_ID = "EDIT_ENTRY_ID";

    private AddEditEntryViewModel mViewModel;

    private AddeditFragmentBinding mViewDataBinding;

    public static AddEditEntryFragment newInstance() {
        return new AddEditEntryFragment();
    }

    public AddEditEntryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadData();

        setupFab();

        setupSnackbar();

        setupActionBar();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.addedit_fragment,container,false);

        if(mViewDataBinding == null) {
            mViewDataBinding = AddeditFragmentBinding.bind(root);
        }

        mViewModel = AddEditEntryActivity.obtainViewModel(getActivity());

        mViewDataBinding.setViewmodel(mViewModel);

        setHasOptionsMenu(true);
        setRetainInstance(false);

        return mViewDataBinding.getRoot();
    }

    private void loadData() {
        // Add or edit an existing task?
        if (getArguments() != null) {
            mViewModel.start(getArguments().getString(ARGUMENT_EDIT_ENTRY_ID));
        } else {
            mViewModel.start(null);
        }
    }

    private void setupSnackbar() {
        mViewModel.getSnackbarMessage().observe(this, new SnackbarMessage.SnackbarObserver() {
            @Override
            public void onNewMessage(@StringRes int snackbarMessageResourceId) {
                SnackbarUtils.showSnackbar(getView(), getString(snackbarMessageResourceId));
            }
        });
    }

    private void setupFab() {
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_edit_task_done);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.saveEntry();
            }
        });
    }

    private void setupActionBar() {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        if (getArguments() != null && getArguments().get(ARGUMENT_EDIT_ENTRY_ID) != null) {
            actionBar.setTitle(R.string.edit_entry);

        } else {
            actionBar.setTitle(R.string.add_entry);
        }
    }
}
