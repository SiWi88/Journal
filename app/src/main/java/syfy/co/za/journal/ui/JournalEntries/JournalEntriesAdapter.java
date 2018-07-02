package syfy.co.za.journal.ui.JournalEntries;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import syfy.co.za.journal.R;
import syfy.co.za.journal.data.JournalEntry;

public class JournalEntriesAdapter extends RecyclerView.Adapter<JournalEntriesAdapter.EntriesViewHolder> {

    final private ListItemClickListener mOnClickListener;

    private List<JournalEntry> mEntries;

    public interface ListItemClickListener {
        void onListItemClick(JournalEntry entry);
    }

    public JournalEntriesAdapter(ListItemClickListener listener) {
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public EntriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        int layoutIdForListItem = R.layout.entry_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new EntriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntriesViewHolder holder, int position) {
        final JournalEntry journalEntry = mEntries.get(position);

        Date date = journalEntry.getDate();
        SimpleDateFormat formatterDate = new SimpleDateFormat("EEE dd MMM yyyy");
        SimpleDateFormat formatterTime = new SimpleDateFormat("hh:mm aa");
        String dateFormatted = formatterDate.format(date);
        String timeFormatted = formatterTime.format(date);

        holder.tv_title.setText(journalEntry.getTitle());
        holder.tv_date.setText(dateFormatted);
        holder.tv_time.setText(timeFormatted);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnClickListener != null){
                    mOnClickListener.onListItemClick(journalEntry);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (this.mEntries != null) ? this.mEntries.size() : 0;
    }

    class EntriesViewHolder extends RecyclerView.ViewHolder  {

        TextView tv_title,tv_date,tv_time;

        public EntriesViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_date =  itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
        }

//        @Override
//        public void onClick(View v) {
//            int clickedPosition = getAdapterPosition();
//            mOnClickListener.onListItemClick(clickedPosition);
//        }
    }

    public void replaceData(List<JournalEntry> entries){
        setList(entries);
    }

    private void setList(List<JournalEntry> entries) {
        mEntries = entries;
        notifyDataSetChanged();
    }
}
