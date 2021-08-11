package com.example.android.puraanaapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class EditorActivity extends AppCompatActivity {
    /** EditText field to enter the product name */
    EditText mProductNameEditText;

    /** EditText field to enter the price of the product */
    EditText mPriceEditText;

    /** EditText field to enter supplier's name */
    EditText mSupplierNameEditText;

    /** EditText field to enter supplier's phone number */
    EditText mSupplierPhoneEditText;

    /** ImageView for the product image */
    ImageView mImageView;

    Button addImageButton;

    ImageButton imageButton;

    Uri selectedImageUri;
    File photoFile;

    private String mUsername;
    private StorageReference mChatPhotosStorageReference;
    private DatabaseReference mMessagesDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private FirebaseDatabase mFirebaseDatabase;
    private ProgressDialog progressDialog ;

    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mTempPhotoPath;

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        selectedImageUri = data.getData();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            mImageView.setImageBitmap(bitmap);
                        }
                        catch (IOException e) {

                            e.printStackTrace();
                        }


                    }
                }
            });

    private final ActivityResultLauncher<Intent> someCameraResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            mImageView.setImageBitmap(bitmap);
                        }
                        catch (IOException e) {

                            e.printStackTrace();
                        }


                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mProductNameEditText = findViewById(R.id.edit_product_name);
        mPriceEditText = findViewById(R.id.edit_price);
        mSupplierNameEditText = findViewById(R.id.edit_provider_name);
        mSupplierPhoneEditText = findViewById(R.id.edit_supplier_phone);
        mImageView = findViewById(R.id.edit_product_image);
        addImageButton = findViewById(R.id.edit_add_image_button);

        imageButton = findViewById(R.id.camera_button);

        progressDialog = new ProgressDialog(EditorActivity.this);

        mFirebaseStorage = FirebaseStorage.getInstance();
        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");
        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");

        // ImagePickerButton shows an image picker to upload a image for a message
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Fire an intent to show an image picker
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                someActivityResultLauncher.launch(Intent.createChooser(intent, "Complete action using"));
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View view){
                launchCamera();
                
            }
        });
    }

    /**
     * Creates a temporary image file and captures a picture to store in it.
     */
    private void launchCamera() {

        // Create the capture image intent
        final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the temporary File where the photo should go
            photoFile = null;
            try {
                photoFile = BitmapUtils.createTempImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                Log.i("launchCamera: ","launchCamera");
                // Get the path of the temporary file
                mTempPhotoPath = photoFile.getAbsolutePath();

                // Get the content URI for the image file
                selectedImageUri = FileProvider.getUriForFile(this,
                        "com.example.android.contentprovider",
                        photoFile);

                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);

                // Launch the camera activity
                someCameraResultLauncher.launch(Intent.createChooser(takePictureIntent, "Complete action using"));

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:

                progressDialog.show();

                // Get a reference to store file at chat_photos/<FILENAME>
                StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

                UploadTask uploadTask = photoRef.putFile(selectedImageUri);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return photoRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            progressDialog.dismiss();

                            Uri downloadUri = task.getResult();

                            final String image = downloadUri != null ? downloadUri.toString() : null;
                            Informations friendlyMessage = new Informations(mProductNameEditText.getText().toString(),mSupplierNameEditText.getText().toString(),mPriceEditText.getText().toString(),mSupplierPhoneEditText.getText().toString(),image);
                            mMessagesDatabaseReference.push().setValue(friendlyMessage);
                            // Clear input box
                            mProductNameEditText.setText("");
                            mPriceEditText.setText("");
                            mSupplierNameEditText.setText("");
                            mSupplierPhoneEditText.setText("");
                            finish();


                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });
                // Set the download URL to the message box, so that the user can send it to the database
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
