package neleso.db;


import java.io.ByteArrayInputStream;
import java.io.IOException;

import konstruktion.app.NelesoApp;
import konstruktion.app.ShowPdf;
import konstruktion.app.ThumbNailsGrid;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import konstruktion.app.R;
import neleso.db.DataBase_Adapter;

public class DataBase_Details extends Activity {
	
	private Long mRowId;
	private EditText mGuid;
	private TextView mTitle;
	private TextView mSubtitle;
	private EditText mThumbnailLink;
	private ImageView mThumbnail;
	private EditText mPdfLink;
	private EditText mPdfFile;
	private TextView mPubDate;
	private TextView mProductIdentifier;
	private TextView mUid;

	private DataBase_Adapter mDbHelper;
	

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		mDbHelper = new DataBase_Adapter(this);
		mDbHelper.open();
		setContentView(R.layout.todo_edit);
		mGuid = (EditText) findViewById(R.id.mGuid);
		mTitle = (TextView) findViewById(R.id.mTitle);
		mSubtitle = (TextView) findViewById(R.id.mSubtitle);
		mThumbnailLink = (EditText) findViewById(R.id.mPdfThumbnails);
		mThumbnail = (ImageView) findViewById(R.id.thumbnail);
		mPdfLink = (EditText) findViewById(R.id.mPdfLink);
		mPdfFile = (EditText) findViewById(R.id.mPdfFile);
		mPubDate = (TextView) findViewById(R.id.mPubDate);
		mProductIdentifier = (TextView) findViewById(R.id.mProductIdentifier);
		mUid = (TextView) findViewById(R.id.mUid);

	
		Button confirmButton = (Button) findViewById(R.id.todo_edit_button);
		mRowId = null;
		Bundle extras = getIntent().getExtras();
		mRowId = (bundle == null) ? null : (Long) bundle
				.getSerializable(DataBase_Adapter.KEY_ROWID);
		if (extras != null) {
			mRowId = extras.getLong(DataBase_Adapter.KEY_ROWID);
		}
		populateFields();
		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				setResult(RESULT_OK);
				finish();
			}

		});
	}
	
	 @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        // Check if the key event was the BACK key and if there's history
	        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        	Intent intent = new Intent(DataBase_Details.this, NelesoApp.class);
		    	startActivity(intent);
	            return true;
	        }
	        // If it wasn't the BACK key or there's no web page history, bubble up to the default
	        // system behavior (probably exit the activity)
	     Toast.makeText(getApplicationContext(), "No hay vuelta atras", Toast.LENGTH_LONG).show();      	

	        return super.onKeyDown(keyCode, event);
	    }

	private void populateFields() {
		
		if (mRowId != null) {
			Cursor todo = mDbHelper.fetchTodo(mRowId);
			startManagingCursor(todo);
			
			 byte[] imageByteArray;
			 Bitmap theImage = null;
			 imageByteArray   =  todo.getBlob(todo.getColumnIndex(DataBase_Adapter.KEY_THUMBNAIL));
	         ByteArrayInputStream imageStream = new ByteArrayInputStream(imageByteArray);
	         theImage= BitmapFactory.decodeStream(imageStream);
			
			mGuid.setText(todo.getString(todo
					.getColumnIndexOrThrow(DataBase_Adapter.KEY_GUID)));
			mTitle.setText(todo.getString(todo
					.getColumnIndexOrThrow(DataBase_Adapter.KEY_TITLE)));
			mSubtitle.setText(todo.getString(todo
					.getColumnIndexOrThrow(DataBase_Adapter.KEY_SUBTITLE)));
			mThumbnailLink.setText(todo.getString(todo
					.getColumnIndexOrThrow(DataBase_Adapter.KEY_THUMBNAIL_LINK)));
			mThumbnail.setImageBitmap(theImage);
			mPdfLink.setText(todo.getString(todo
					.getColumnIndexOrThrow(DataBase_Adapter.KEY_PDF_LINK)));
			mPdfFile.setText(todo.getString(todo
					.getColumnIndexOrThrow(DataBase_Adapter.KEY_PDF_FILE)));
			mPubDate.setText(todo.getString(todo
					.getColumnIndexOrThrow(DataBase_Adapter.KEY_PUBDATE)));
			mProductIdentifier.setText(todo.getString(todo
					.getColumnIndexOrThrow(DataBase_Adapter.KEY_PRODUCT_IDENTIFIER)));
			mUid.setText(todo.getString(todo
					.getColumnIndexOrThrow(DataBase_Adapter.KEY_UID)));
			
			
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(DataBase_Adapter.KEY_ROWID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	private void saveState() {
		String guid = (String) mGuid.getText().toString();
		String title = (String) mTitle.getText().toString();
		String subtitle = (String) mSubtitle.getText().toString();
		String thumbnail_link = mThumbnailLink.getText().toString();
//		byte[] thumbnail = mPdfThumbnails.getText().toString();
		String pdf_link = mPdfLink.getText().toString();
		String pdf_file = (String) mPdfFile.getText().toString();
		String pub_date = (String) mPubDate.getText().toString();
		String product_identifier = (String) mProductIdentifier.getText().toString();
		String uid = (String) mUid.getText().toString();


		if (mRowId == null) {
			long id = mDbHelper.createTodo(guid, title, subtitle, thumbnail_link, null, pdf_link, pdf_file, pub_date, product_identifier, uid);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			mDbHelper.updateTodo(mRowId, guid, title, subtitle, thumbnail_link, null, pdf_link, pdf_file, pub_date, product_identifier, uid);
		}
	}
	
	
}
	

