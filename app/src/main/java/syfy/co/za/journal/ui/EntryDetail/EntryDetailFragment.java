package syfy.co.za.journal.ui.EntryDetail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.FirebaseDatabase;

import syfy.co.za.journal.R;
import syfy.co.za.journal.databinding.EntrydetailFragmentBinding;
import syfy.co.za.journal.util.SnackbarMessage;
import syfy.co.za.journal.util.SnackbarUtils;

public class EntryDetailFragment extends Fragment {

    public static final int REQUEST_EDIT_ENTRY = 1;

    public final static String ARGUMENT_ENTRY_ID = "ENTRY_ID";

    private EntryDetailViewModel mViewModel;

    public static EntryDetailFragment newInstance(String entryId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_ENTRY_ID, entryId);
        EntryDetailFragment fragment = new EntryDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupFab();

        setupSnackbar();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.start(getArguments().getString(ARGUMENT_ENTRY_ID));
        }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.entrydetail_fragment, container, false);

        EntrydetailFragmentBinding viewDataBinding = EntrydetailFragmentBinding.bind(view);

        mViewModel = EntryDetailActivity.obtainViewModel(getActivity());

        viewDataBinding.setViewmodel(mViewModel);

        setHasOptionsMenu(true);

        return view;
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
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.editEntry();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                mViewModel.deleteEntry();
                return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu);
    }
}
