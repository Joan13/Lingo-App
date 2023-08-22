package com.agtechnologies.lingoapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private EditText title_repas, description_repas, prix_repas;
    private ProgressBar loading;
    private Button add_repas;
    private ImageButton add_photo;
    private ImageView imageView;
    private Bitmap bitmap;
    private static String URL_PICTURE = "http://192.168.43.203/lingoApp/upload_picture.php";
    private String title, description, prix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title_repas = findViewById(R.id.title_repas);
        description_repas = findViewById(R.id.description_repas);
        prix_repas = findViewById(R.id.prix_repas);
        loading = findViewById(R.id.loading);
        add_photo = findViewById(R.id.add_photo);
        add_repas = findViewById(R.id.add_repas);
        imageView = findViewById(R.id.imageView);

        add_repas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = title_repas.getText().toString().trim();
                description = description_repas.getText().toString().trim();
                prix = prix_repas.getText().toString().trim();

                UploadPicture (title, description, prix, getStringImage(bitmap));
            }
        });

        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePic();
            }
        });

//        Init();
    }

    public Bitmap ResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();

        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        onLowMemory();

        return resizedBitmap;
    }

//    private void StartCrop(@NonNull Uri uri) {
//        String destinationFileName = SAMPLE_CROPPED_IMG_NAME;
//        destinationFileName += ".jpg";
//
//        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
//        uCrop.withAspectRatio(1, 1);
////        uCrop.withAspectRatio(3, 4);
////        uCrop.useSourceImageAspectRatio();
////        uCrop.withAspectRatio(2, 3);
////        uCrop.withAspectRatio(16, 9);
//
//        uCrop.withMaxResultSize(450, 450);
//
//        uCrop.withOptions(GetCropOptions());
//
//        uCrop.start(ProfilePictureActivity.this);
//    }

//    private UCrop.Options GetCropOptions() {
//        UCrop.Options options = new UCrop.Options();
//
//        options.setCompressionQuality(100);
//
////        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
////        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
//
//        options.setHideBottomControls(false);
//        options.setFreeStyleCropEnabled(true);
//
//        options.setStatusBarColor(getResources().getColor(R.color.colorTexts));
//
//
//        return options;
//    }

//    private void Init() {
//        this.imageProfile = findViewById(R.id.image_profile);
//    }

    private void choosePic () {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        onLowMemory();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void UploadPicture(final String title, final String description, final String prix, final String photo) {
        loading.setVisibility(View.VISIBLE);
        add_repas.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PICTURE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("uuuuu", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("message");
                            onLowMemory();

                            if (success.equals("1")) {
                                Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_LONG).show();
                                loading.setVisibility(View.GONE);
                                add_repas.setVisibility(View.VISIBLE);

                                Toast.makeText(MainActivity.this, "Success !", Toast.LENGTH_SHORT).show();

                                onLowMemory();
                            }
                            if (success.equals("0"))
                            {
                                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();

                                loading.setVisibility(View.GONE);
                                add_repas.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Wapi...", Toast.LENGTH_LONG).show();

                            loading.setVisibility(View.GONE);
                            add_repas.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Internet error", Toast.LENGTH_LONG).show();
                        loading.setVisibility(View.GONE);
                        add_repas.setVisibility(View.VISIBLE);
                        onLowMemory();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("description", description);
                params.put("prix", prix);
                params.put("photo", photo);
                onLowMemory();
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        onLowMemory();
    }

    public String getStringImage (Bitmap bitmapImage) {

        //bitmap = ResizedBitmap(bitmapImage, 1400, 1400);
        bitmap = bitmapImage;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        onLowMemory();
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);
        onLowMemory();

        return encodedImage;
    }
}
