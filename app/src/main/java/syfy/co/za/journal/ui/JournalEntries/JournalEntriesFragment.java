package syfy.co.za.journal.ui.JournalEntries;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import syfy.co.za.journal.R;
import syfy.co.za.journal.data.JournalEntry;
import syfy.co.za.journal.databinding.EntriesFragmentBinding;
import syfy.co.za.journal.util.ScrollChildSwipeRefreshLayout;
import syfy.co.za.journal.util.SimpleDividerItemDecoration;
import syfy.co.za.journal.util.SnackbarMessage;
import syfy.co.za.journal.util.SnackbarUtils;

public class JournalEntriesFragment extends Fragment implements JournalEntriesAdapter.ListItemClickListener {

    private JournalEntriesViewModel mViewModel;

    private EntriesFragmentBinding mFragBinding;

    private JournalEntriesAdapter journalEntriesAdapter;

    private RecyclerView entriesList;

    public JournalEntriesFragment(){
    }

    public static JournalEntriesFragment newInstance(){
        return new JournalEntriesFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mFragBinding = EntriesFragmentBinding.inflate(inflater,container,false);

        mViewModel = JournalEntriesActivity.obtainViewModel(getActivity());

        mFragBinding.setViewmodel(mViewModel);

        setHasOptionsMenu(true);

        return mFragBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        entriesList = view.findViewById(R.id.entries_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        entriesList.setLayoutManager(layoutManager);

        journalEntriesAdapter = new JournalEntriesAdapter(this);

        entriesList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        entriesList.setAdapter(journalEntriesAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupSnackbar();

        setupFab();

        setupRefreshLayout();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                signout();
                break;
        }
        return true;
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
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_add_task);
        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.addNewEntry();
            }
        });
    }

    private void setupRefreshLayout() {
        RecyclerView listView =  mFragBinding.entriesList;
        ScrollChildSwipeRefreshLayout swipeRefreshLayout = mFragBinding.refreshLayout;
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.energos),
                ContextCompat.getColor(getActivity(), R.color.sunshine),
                ContextCompat.getColor(getActivity(), R.color.energos)
        );
        swipeRefreshLayout.setScrollUpChild(listView);
    }

    @Override
    public void onListItemClick(JournalEntry entry) {
        mViewModel.getOpenEntryEvent().setValue(entry.getId());
    }

    private void signout(){
        //todo: Clean up the sign out method

        String MY_PREFS_NAME = "UserUid";
        SharedPreferences preferences = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

        mViewModel.clearCachedEntries();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
        getActivity().finish();
    }
}