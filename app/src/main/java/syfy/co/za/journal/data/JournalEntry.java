package syfy.co.za.journal.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;

import java.util.Date;
import java.util.UUID;

@Entity(tableName = "entries")
public final class JournalEntry {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "entryId")
    private String id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "body")
    private String body;

    @ColumnInfo(name = "date")
    private Date date;

    @Ignore
    public JournalEntry(){

    }

    /**
     * Use this constructor to create a new active Task.
     *
     * @param title       title of the task
     * @param body description of the task
     */
    @Ignore
    public JournalEntry(@Nullable String title, @Nullable String body, @Nullable Date date) {
        this(title, body, UUID.randomUUID().toString(), date);
    }

    public JournalEntry(@Nullable String title, @Nullable String body, @NonNull String id, Date date) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.date = date;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(title) &&
                Strings.isNullOrEmpty(body);
    }

    public String getId() {
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getBody(){
        return body;
    }

    public Date getDate() {
        return date;
    }
}