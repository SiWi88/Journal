package syfy.co.za.journal.ui.JournalEntries;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

import syfy.co.za.journal.data.JournalEntry;

import java.util.List;

/**
 * Contains {@link BindingAdapter}s for the {@link JournalEntry} list.
 */
public class JournalEntriesListBindings {

    @BindingAdapter("app:items")
    public static void setItems(RecyclerView listView, List<JournalEntry> items) {
        JournalEntriesAdapter adapter = (JournalEntriesAdapter)listView.getAdapter();
        if (adapter != null)
        {
            adapter.replaceData(items);
        }
    }
}
