package com.example.game.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DatabaseProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.game.provider";

    private static UriMatcher uriMatcher;

    private MyDBHelper myDBHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //表示查出表中的所有内容
        uriMatcher.addURI(AUTHORITY,"rankinglist",0);
        //表示查出表中的单行内容（单条数据）
        uriMatcher.addURI(AUTHORITY,"rankinglist/#",1);
    }

    public DatabaseProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        int deleteRows = 0;
        switch (uriMatcher.match(uri)){
            case 0:
                deleteRows = db.delete("rankinglist",selection,selectionArgs);
                break;
            case 1:
                String id = uri.getPathSegments().get(1);
                deleteRows = db.delete("rankinglist","id=?",new String[]{id});
                break;
                default:
                    break;
        }
        return deleteRows;
    }

    @Override
    public String getType(Uri uri) {
        //content://com.example.app.provider/table/1
        //该方法用于获取Uri对象所对应的MIME类型
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (uriMatcher.match(uri)){
            case 0:
                return "vnd.android.cursor.dir/vnd.com.example.game.provider.rankinglist";
            case 1:
                return "vnd.android.cursor.item/vnd.com.example.game.provider.rankinglist";
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        myDBHelper = new MyDBHelper(getContext(),"rankinglist",null,1);
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.

        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        Cursor cursor = null;
        //降序查询数据库排名信息
        switch (uriMatcher.match(uri)){
            case 0:
                cursor = db.query("rankinglist",null,null,null,null,null,"score desc",null);
                break;
            case 1:
                //"rankinglist/#"中/作为分割，后面信息存储在列表中，第零个位置存放的是路径，第一个位置存放的是id
                String rankinglistId = uri.getPathSegments().get(1);
                cursor = db.query("rankinglist",null,"id=?",new String[]{rankinglistId},null,null,"score desc",null);
                break;
                default:
                    break;
        }

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
