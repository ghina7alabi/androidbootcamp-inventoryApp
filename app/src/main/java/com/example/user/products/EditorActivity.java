package com.example.user.products;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.example.user.products.data.ProductContract.ProductEntry;

/**
 * Created by User on 16-Jul-18.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mNameEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierNumEditText;
    private Spinner mStatusSpinner;

    private Button mCallSupplierButton;
    private Button mDecrementButton;
    private Button mIncrementButton;
    private int mQuantity = 0;


    private int mStatus = 0;

    private boolean mProductHasChanged = false;

    private final static int PRODUCT_LOADER = 0;
    private Uri mCurrentUri;


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        if (mCurrentUri == null) {
            setTitle(getString(R.string.add_product_title));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_product_title));
            getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_product_supname);
        mSupplierNumEditText = (EditText) findViewById(R.id.edit_product_supnum);
        mStatusSpinner = (Spinner) findViewById(R.id.spinner_status);


        mCallSupplierButton = (Button) findViewById(R.id.callSupplier);
        mDecrementButton = (Button) findViewById(R.id.decrement);
        mIncrementButton = (Button) findViewById(R.id.increment);
        String quantity = String.valueOf(mQuantity);
        mQuantityEditText.setText(quantity);


        mNameEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierNumEditText.setOnTouchListener(mTouchListener);
        mStatusSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();

        mCallSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSupplier();
            }
        });

        mDecrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementQuantity();
            }
        });

        mIncrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementQuantity();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    private void setupSpinner() {

        ArrayAdapter statusSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_status_options, android.R.layout.simple_spinner_item);

        statusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mStatusSpinner.setAdapter(statusSpinnerAdapter);

        mStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.status_ordered))) {
                        mStatus = ProductEntry.STATUS_ORDERED;
                    } else if (selection.equals(getString(R.string.status_delivered))) {
                        mStatus = ProductEntry.STATUS_DELIVERED;
                    } else {
                        mStatus = ProductEntry.STATUS_RECIEVED;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mStatus = 0;
            }
        });
    }

    private void saveProduct() {
        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String supNameString = mSupplierNameEditText.getText().toString().trim();
        String supNumString = mSupplierNumEditText.getText().toString().trim();


        if (mCurrentUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, "Name and price are required fields.", Toast.LENGTH_SHORT).show();
            return;
        } else if (mCurrentUri == null && TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, "Name is a required field.", Toast.LENGTH_SHORT).show();
            return;
        } else if (mCurrentUri == null && TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, "Price is a required field.", Toast.LENGTH_SHORT).show();
            return;
        }


        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPNAME, supNameString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPNUMBER, supNumString);
        values.put(ProductEntry.COLUMN_PRODUCT_STATUS, mStatus);

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }

        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        if (mCurrentUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.error_saving), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.product_saved), Toast.LENGTH_SHORT).show();
            }

        } else {
            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.error_saving), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.product_saved), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                saveProduct();
                finish();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_SUPNAME,
                ProductEntry.COLUMN_PRODUCT_SUPNUMBER,
                ProductEntry.COLUMN_PRODUCT_STATUS
        };

        return new CursorLoader(this, mCurrentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int supNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPNAME);
            int supNumColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPNUMBER);
            int statusColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_STATUS);

            //extract
            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            String supName = cursor.getString(supNameColumnIndex);
            String supNum = cursor.getString(supNumColumnIndex);
            int status = cursor.getInt(statusColumnIndex);

            mNameEditText.setText(name);
            mQuantityEditText.setText(Integer.toString(quantity));
            mPriceEditText.setText(Integer.toString(price));
            mSupplierNameEditText.setText(supName);
            mSupplierNumEditText.setText(supNum);

            switch (status) {
                case ProductEntry.STATUS_ORDERED:
                    mStatusSpinner.setSelection(1);
                    break;
                case ProductEntry.STATUS_DELIVERED:
                    mStatusSpinner.setSelection(2);
                    break;
                case ProductEntry.STATUS_RECIEVED:
                    mStatusSpinner.setSelection(3);
                default:
                    mStatusSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierNumEditText.setText("");
        mStatusSpinner.setSelection(0);
    }


    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteProduct() {
        if (mCurrentUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, R.string.delete_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.delete_successful, Toast.LENGTH_SHORT).show();
            }
            finish();
        }

    }


    private void decrementQuantity() {
        String currentQuantityString = mQuantityEditText.getText().toString().trim();
        mQuantity = Integer.parseInt(currentQuantityString);
        if (mQuantity > 0) {
            mQuantity--;
            String quantity = String.valueOf(mQuantity);
            mQuantityEditText.setText(quantity);
        } else {
            Toast.makeText(EditorActivity.this, "Quantity cannot be less than zero.", Toast.LENGTH_SHORT).show();
        }
    }

    private void incrementQuantity() {
        String currentQuantity = mQuantityEditText.getText().toString().trim();
        mQuantity = Integer.parseInt(currentQuantity);
        if (mQuantity < 100) {
            mQuantity++;
            String quantity = String.valueOf(mQuantity);
            mQuantityEditText.setText(quantity);
        } else {
            Toast.makeText(EditorActivity.this, "Quantity cannot be above than 100.", Toast.LENGTH_SHORT).show();
        }
    }

    private void callSupplier() {
        String supplierNumber = mSupplierNumEditText.getText().toString().trim();
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+Uri.encode(supplierNumber.trim())));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (callIntent.resolveActivity(getPackageManager()) != null)
        { startActivity(callIntent); }
    }


}