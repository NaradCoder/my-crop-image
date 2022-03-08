package com.mycropimage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImageListActivity extends AppCompatActivity {

    RecyclerView rvMedia;
    ImageMediaAdapter adapter;
    ImageView showImage, ivDone;
    int clickPosition = -1;
    private Uri mCropImageUri;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        rvMedia = findViewById(R.id.rv_media);
        showImage = findViewById(R.id.iv_show_image);
        adapter = new ImageMediaAdapter(new ArrayList<>(), this, new ImageMediaAdapter.ClickImageListener() {
            @Override
            public void onClick(String strUri, int position, String type) {
                if (type.equalsIgnoreCase("crop")) {
                    clickPosition = position;
                    Uri uri = Uri.parse(strUri);
                    showImage.setImageURI(uri);
                } else if (type.equalsIgnoreCase("remove")) {
                    adapter.getList().remove(position);
                    adapter.notifyItemRemoved(position);
                    if (adapter.getList().size() > 0) {
                        clickPosition = 0;
                        Uri uri = Uri.parse(adapter.getList().get(0));
                        showImage.setImageURI(uri);
                    } else {
                        clickPosition = -1;
                        showImage.setImageURI(null);
                        ivDone.setVisibility(View.INVISIBLE);
                        setResultCancel();
                    }
                }
            }
        });

        rvMedia.setAdapter(adapter);

        showImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });

        ivDone = findViewById(R.id.iv_add_image);

        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra(CropImage.LIST_IMAGE_EXTRA_RESULT, (ArrayList<String>) adapter.getList());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Bundle bundle = getIntent().getBundleExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE);
        mCropImageUri = bundle.getParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE);

        if (savedInstanceState == null) {
            if (mCropImageUri == null || mCropImageUri.equals(Uri.EMPTY)) {
                if (CropImage.isExplicitCameraPermissionRequired(this)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    requestPermissions(
                            new String[]{Manifest.permission.CAMERA},
                            CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
                } else {
                    CropImage.startPickImageActivity(startActivityForGalleryImageResult, this);
                }
            } else if (CropImage.isReadExternalStoragePermissionsRequired(this, mCropImageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForGalleryImageResult.launch(Intent.createChooser(intent, "Select Picture"));

            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    private void cropImage() {
        if (clickPosition >= 0) {
            Bundle bundle = getIntent().getBundleExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE);
            bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE, Uri.parse(adapter.getList().get(clickPosition)));

            Intent intent = new Intent(ImageListActivity.this, CropImageActivity.class);
            intent.putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE, bundle);
            startActivityForCropResult.launch(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.crop_image_menu) {
            cropImage();
            return true;
        }

        if (item.getItemId() == android.R.id.home) {
            setResultCancel();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> startActivityForGalleryImageResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("NewApi")
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_CANCELED) {
                        setResultCancel();
                    } else if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        try {
                            if (data == null) {
                                mCropImageUri = CropImage.getPickImageResultUri(ImageListActivity.this, data);
                                if (CropImage.isReadExternalStoragePermissionsRequired(ImageListActivity.this, mCropImageUri)) {
                                    // request permissions and handle the result in onRequestPermissionsResult()
                                    requestPermissions(
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                                } else {
                                    adapter.setItem(mCropImageUri.toString());
                                }
                            } else {
                                if (data.getData() != null) {
                                    Uri imageUri = data.getData();
                                    adapter.setItem(imageUri.toString());
                                    Log.e("ShowCropImage", "registerForActivityResult: " + imageUri);

                                } else if (data.getClipData() != null) {
                                    ClipData mClipData = data.getClipData();
                                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                                        ClipData.Item item = mClipData.getItemAt(i);
                                        Uri imageUri = item.getUri();
                                        adapter.setItem(imageUri.toString());
                                        Log.e("ShowCropImage", "registerForActivityResult_getClipData: " + imageUri);
                                    }

                                }
                            }

                            if (clickPosition == -1 && adapter.getList().size() > 0) {
                                clickPosition = 0;
                                Uri uri = Uri.parse(adapter.getList().get(0));
                                showImage.setImageURI(uri);
                                ivDone.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ImageListActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> startActivityForCropResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("NewApi")
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        CropImage.ActivityResult result1 = CropImage.getActivityResult(data);
                        Uri selectedUri = result1.getUri();
                        adapter.updateItem(selectedUri.toString(), clickPosition);
                        showImage.setImageURI(selectedUri);
                    }
                }
            });


    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null
                    && grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
            } else {
                Toast.makeText(this, R.string.crop_image_activity_no_permissions, Toast.LENGTH_LONG).show();
                setResultCancel();
            }
        }

        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            // Irrespective of whether camera permission was given or not, we show the picker
            // The picker will not add the camera intent if permission is not available
            CropImage.startPickImageActivity(startActivityForGalleryImageResult, this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResultCancel();
    }

    /**
     * Cancel of select image.
     */
    protected void setResultCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

}