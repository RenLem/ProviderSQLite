package omy.boston.providersqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import omy.boston.providersqlite.database.BooksDatabaseHelper;
import omy.boston.providersqlite.providers.BookContentProvider;

public class MainActivity extends AppCompatActivity {

    private EditText edNaziv;
    private EditText edAutor;
    private Button btnDodaj;
    private Button btnUredi;
    private Button btnIzbrisi;
    private ListView listaKnjige;
    private SimpleCursorAdapter adapter;
    private long selectedId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edNaziv = (EditText) findViewById(R.id.etBookTitle);
        edAutor = (EditText) findViewById(R.id.etAuthor);
        btnDodaj = (Button) findViewById(R.id.btnAdd);
        btnUredi = (Button) findViewById(R.id.btnEdit);
        btnIzbrisi = (Button) findViewById(R.id.btnDelete);
        listaKnjige = (ListView) findViewById(R.id.list_id);

        String[] columns = {BooksDatabaseHelper.COLUMN_NAME, BooksDatabaseHelper.COLUMN_AUTHOR};
        int[] viewIds = {R.id.textView_naziv, R.id.textView_autor};
        adapter = new SimpleCursorAdapter(this, R.layout.list, null, columns, viewIds, 0);
        listaKnjige.setAdapter(adapter);

        refreshList();
        klikLisineri();
    }

    private void refreshList(){
        CursorLoader cursorLoader = new CursorLoader(this, BookContentProvider.CONTENT_URI, null, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        adapter.swapCursor(cursor);
    }

    private void klikLisineri(){
        btnDodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputOK()){
                    insertBook(edNaziv.getText().toString(), edAutor.getText().toString());
                }
            }
        });

        btnUredi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isItemSelected() && inputOK()){
                    updateBook(edNaziv.getText().toString(), edAutor.getText().toString());
                }
            }
        });

        btnIzbrisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isItemSelected()){
                    deleteBook();
                }
            }
        });

        listaKnjige.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedId = id;
                fillForm();
            }
        });
    }

    private boolean inputOK(){
        if (edNaziv.getText().length() == 0){
            Toast.makeText(this, "Molimo unesite naslov knjige", Toast.LENGTH_SHORT).show();

            edNaziv.requestFocus();
            return false;
        }
        if (edAutor.getText().length() == 0){
            Toast.makeText(this, "Molimo unesite autora knjige", Toast.LENGTH_SHORT).show();

            edAutor.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isItemSelected(){
        if (selectedId == -1){
            Toast.makeText(this, "Molimo izaberite knjigu", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void insertBook(String naziv, String autor){
        ContentValues values = new ContentValues();

        values.put(BooksDatabaseHelper.COLUMN_NAME, naziv);
        values.put(BooksDatabaseHelper.COLUMN_AUTHOR, autor);

        getContentResolver().insert(BookContentProvider.CONTENT_URI, values);
        refreshList();
        clearForm();
    }

    private void updateBook(String naziv, String autor){
        ContentValues values = new ContentValues();

        values.put(BooksDatabaseHelper.COLUMN_NAME, naziv);
        values.put(BooksDatabaseHelper.COLUMN_AUTHOR, autor);

        Uri uri = Uri.parse(BookContentProvider.CONTENT_URI + "/" + selectedId);
        getContentResolver().update(uri, values, null, null);
        refreshList();
        clearForm();
    }

    private void deleteBook(){
        Uri uri = Uri.parse(BookContentProvider.CONTENT_URI + "/" + selectedId);

        getContentResolver().delete(uri, null, null);
        refreshList();
        clearForm();
    }

    private void fillForm(){
        Uri uri = Uri.parse(BookContentProvider.CONTENT_URI + "/" + selectedId);

        CursorLoader cursorLoader = new CursorLoader(this, uri, null, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        if (cursor.moveToFirst()){
            String nazivFill = cursor.getString(cursor.getColumnIndexOrThrow(BooksDatabaseHelper.COLUMN_NAME));
            String autorFill = cursor.getString(cursor.getColumnIndexOrThrow(BooksDatabaseHelper.COLUMN_AUTHOR));
            edNaziv.setText(nazivFill);
            edAutor.setText(autorFill);
        }

    }

    private void clearForm(){
        edNaziv.setText("");
        edAutor.setText("");
        selectedId = -1;
    }
}
