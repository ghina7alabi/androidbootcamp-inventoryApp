package com.example.user.products;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.products.data.ProductContract.ProductEntry;


/**
 * Created by User on 23-Jul-18.
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView supNameTextView = (TextView) view.findViewById(R.id.supName);
        TextView status = (TextView) view.findViewById(R.id.status);

        final Button saleButton = (Button) view.findViewById(R.id.saleButton);


        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int supNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPNAME);
        int supNumColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPNUMBER);
        int statusColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_STATUS);


        String productName = cursor.getString(nameColumnIndex);
        final int productQuantity = cursor.getInt(quantityColumnIndex);
        int productPrice = cursor.getInt(priceColumnIndex);
        String productSupName = cursor.getString(supNameColumnIndex);
        String productSupNum = cursor.getString(supNumColumnIndex);
        int productStatus = cursor.getInt(statusColumnIndex);

        if (TextUtils.isEmpty(productSupName)) {
            productSupName = context.getString(R.string.unknown_supName);
        }

        nameTextView.setText(productName);
        quantityTextView.setText(Integer.toString(productQuantity));
        priceTextView.setText(Integer.toString(productPrice) + "$");
        supNameTextView.setText(productSupName);

        switch (productStatus) {
            case ProductEntry.STATUS_ORDERED:
                status.setText(R.string.ordered);
                break;
            case ProductEntry.STATUS_DELIVERED:
                status.setText(R.string.Delivered);
                break;
            case ProductEntry.STATUS_RECIEVED:
                status.setText(R.string.received);
            default:
                status.setText(R.string.unknown);
                break;
        }


        final int id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry._ID));
        final int finalProductQuantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalProductQuantity > 0) {
                    int quantity = finalProductQuantity - 1;
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
                    Uri newUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                    context.getContentResolver().update(newUri, values, null, null);
                } else {
                    Toast.makeText(context, R.string.no_products_left, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


}