package com.example.android.puraanaapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends ArrayAdapter<Informations> implements Filterable{
    private Context context;
    private static final int MY_PERMISSONS_REQUEST_READ_CONTACTS = 1;

    public ProductAdapter(Context context, int resource, List<Informations> objects) {
        super(context, resource, objects);
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.list_item , parent, false);
        }

        // Find individual views that we want to modify in the card item layout
        TextView productNameTextView = (TextView) convertView.findViewById(R.id.Product);
        TextView providerTextView = (TextView) convertView.findViewById(R.id.Provider);
        TextView priceTextView = (TextView) convertView.findViewById(R.id.product_price_card);
        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
        TextView contactTextView = (TextView) convertView.findViewById(R.id.detail_contact);
        ImageButton supplierPhoneButton = (ImageButton) convertView.findViewById(R.id.detail_phone_button);

        Informations information = getItem(position);

        boolean isPhoto = information.getPhotoUrl() != null;
        if (isPhoto) {
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(photoImageView.getContext())
                    .load(information.getPhotoUrl())
                    .into(photoImageView);
        } else {
            photoImageView.setVisibility(View.GONE);
        }
        productNameTextView.setText("Category: "+information.getProduct());

        priceTextView.setText("Price: "+information.getPrice());

        providerTextView.setText("Owner: "+information.getProvider());

        contactTextView.setText(information.getPhone());

        supplierPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneString = information.getPhone().trim();

                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse(context.getString(R.string.tel_colon) + phoneString));
                // Check whether the app has a given permission
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                            Manifest.permission.CALL_PHONE)) {

                    } else {
                        // Request permission to be granted to this application
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{ Manifest.permission.CALL_PHONE},
                                MY_PERMISSONS_REQUEST_READ_CONTACTS);
                    }
                    return;
                }
                context.startActivity(Intent.createChooser(phoneIntent, context.getString(R.string.make_a_phone_call)));
            }});
        return convertView;
    }

}
