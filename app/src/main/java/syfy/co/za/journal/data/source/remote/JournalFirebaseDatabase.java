package syfy.co.za.journal.data.source.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import syfy.co.za.journal.data.JournalEntry;
import syfy.co.za.journal.data.source.local.JournalDatabase;
import syfy.co.za.journal.ui.Authentication.AuthenticationActivity;

import static android.content.Context.MODE_PRIVATE;

public class JournalFirebaseDatabase implements JournalFirebaseImpl {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private JournalEntry journalEntry;
    private List<JournalEntry> journalEntries;
    private String usersUid;
    private SharedPreferences prefs;

    private static JournalFirebaseDatabase INSTANCE;

    private JournalFirebaseDatabase(){
        setUpSharedPreferences();
        }

    public static JournalFirebaseDatabase getInstance(Context context){
        if(INSTANCE == null) {
            INSTANCE = new JournalFirebaseDatabase();
        }
        return INSTANCE;
    }

    //Get all entries
    @Override
    public List<JournalEntry> getAllEntries() {
        usersUid = prefs.getString("UserUid","");
        journalEntries = new ArrayList<>();
        databaseReference.child("Entries").child(usersUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                journalEntries.clear();
                for (DataSnapshot entrySnapshot : dataSnapshot.getChildren()) {
                    JournalEntry entry = entrySnapshot.getValue(JournalEntry.class);
                    journalEntries.add(entry);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError",databaseError.getDetails());
            }
        });
        return journalEntries;
    }

    //Get single entry by entry id
    @Override
    public JournalEntry getEntryById(String entryId) {
        usersUid = prefs.getString("UserUid","");
        databaseReference.child("Entries").child(usersUid).child(entryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                journalEntry = dataSnapshot.getValue(JournalEntry.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Read Entry","Failed");
            }
        });
        return journalEntry;
    }

    //Insert an entry
    @Override
    public void insertEntry(JournalEntry entry) {
        usersUid = prefs.getString("UserUid","");
        String entryId = entry.getId();
        databaseReference.child("Entries").child(usersUid).child(entryId).setValue(entry);
    }

    //Delete an entry
    @Override
    public void deleteEntry(String entryId) {
        usersUid = prefs.getString("UserUid","");
        databaseReference.child("Entries").child(usersUid).child(entryId).removeValue();
    }

    private void setUpSharedPreferences(){
        Context applicationContext = AuthenticationActivity.getContextOfApplication();
        String MY_PREFS_NAME = "UserUid";
        prefs = applicationContext.getSharedPreferences(MY_PREFS_NAME,Context.MODE_PRIVATE);
    }
}


