package syfy.co.za.journal.util;

import android.databinding.BindingAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

import syfy.co.za.journal.ui.JournalEntries.JournalEntriesViewModel;

import syfy.co.za.journal.ui.JournalEntries.JournalEntriesViewModel;

public class SwipeRefreshLayoutDataBinding {

    /**
     * Reloads the data when the pull-to-refresh is triggered.
     * <p>
     * Creates the {@code android:onRefresh} for a {@link SwipeRefreshLayout}.
     */
    @BindingAdapter("android:onRefresh")
    public static void setSwipeRefreshLayoutOnRefreshListener(ScrollChildSwipeRefreshLayout view,
                                                              final JournalEntriesViewModel viewModel) {
        view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.loadEntries(true);
            }
        });
    }

}
