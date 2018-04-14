package omy.boston.providersqlite.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import omy.boston.providersqlite.database.BooksDatabaseHelper;

public class BookContentProvider extends ContentProvider {

    private BooksDatabaseHelper database;
    private static final String AUTHORITY = "omy.boston.providersqlite.providers";
    private static final String PATH = "books";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);
    private static final int BOOKS = 10;
    private static final int BOOK_ID = 20;
    private static final UriMatcher URI_MATCHER = createUriMacher();
    private static UriMatcher createUriMacher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, PATH, BOOKS);
        uriMatcher.addURI(AUTHORITY, PATH + "/#", BOOK_ID);
        return uriMatcher;
    }
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/books";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/book";


    public BookContentProvider() {
    }

    @Override
    public boolean onCreate() {
        database = new BooksDatabaseHelper(getContext());
        return true;
        //DONE // TODO: Implement this to initialize your content provider on startup.
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)){
            case BOOKS:
                return CONTENT_TYPE;
            case BOOK_ID:
                return CONTENT_ITEM_TYPE;
            default:
                return null;
        }
        //DONE // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(values);
        Uri returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
        return returnUri;
        //DONE // TODO: Implement this to handle requests to insert a new row.
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String id = null;
        if (URI_MATCHER.match(uri) == BOOK_ID){
            id = uri.getPathSegments().get(1);
        }
        return database.query(id, projection, selection, selectionArgs, sortOrder);
        //DONE// TODO: Implement this to handle query requests from clients.
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        String id = null;
        if (URI_MATCHER.match(uri) == BOOK_ID){
            id = uri.getPathSegments().get(1);
        }
        return database.update(id, values);
        //DONE // TODO: Implement this to handle requests to update one or more rows.
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String id = null;
        if (URI_MATCHER.match(uri) == BOOK_ID){
            id = uri.getPathSegments().get(1);
        }
        return database.delete(id);
        //DONE // TODO: Implement this to handle requests to delete one or more rows.
    }
}
