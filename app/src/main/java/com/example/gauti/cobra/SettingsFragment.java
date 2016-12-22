package com.example.gauti.cobra;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gauti.cobra.global.ApplicationSharedPreferences;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    // Constants
    // --------------------------------------------------------------------------------------------
    private static final int REQUEST_CODE_CAMERA = 42;
    private static final int REQUEST_CODE_GALLERY = 17;

    // Private fields
    // --------------------------------------------------------------------------------------------
    private EditText et_name;
    private EditText et_number;
    private Button btn_sauv;
    private ImageView iv_picture;
    private Spinner spiDelai;

    private String mUrlImg;
    private Uri mOutputFileUri;
    private String mName;
    private String mNumero;
    private int mDelai;

    private NavigationView navigationView;

    // Views
    // --------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        et_name = (EditText) view.findViewById(R.id.et_name);
        et_number = (EditText) view.findViewById(R.id.et_number);
        btn_sauv = (Button) view.findViewById(R.id.btn_settings_sauv);
        iv_picture = (ImageView) view.findViewById(R.id.iv_picture_settings);
        spiDelai = (Spinner) view.findViewById(R.id.spinnerTime);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iv_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence photos[] = new CharSequence[]{getString(R.string.popover_takePicture), getString(R.string.popover_importPicture)};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.popover_photo);
                builder.setItems(photos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on photos[which]
                        if (which == 0) {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                // Create the File where the photo should go
                                File photoFile = null;
                                try {
                                    photoFile = createImageFile();
                                } catch (IOException ex) {
                                    // Error occurred while creating the File
                                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.settings_error_create_file, Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                                // Continue only if the File was successfully created
                                if (photoFile != null) {
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(photoFile));
                                    takePictureIntent.putExtra("return-data", true);
                                    startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
                                }
                            }
                        } else {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, REQUEST_CODE_GALLERY);
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });

        btn_sauv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).setSettingsPicture(mUrlImg);
                ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).setSettingName(et_name.getText().toString());
                ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).setSettingsNumero(et_number.getText().toString().replaceFirst("0","+33"));

                navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                navigationView.getMenu().getItem(0).setChecked(true);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new HomeFragment());
                transaction.commit();
            }
        });

        // Spinner click listener
        spiDelai.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> time = new ArrayList<String>();
        time.add("30 sec");
        time.add("45 sec");
        time.add("1 min");
        time.add("1 min 15");
        time.add("1 min 30");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, time);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spiDelai.setAdapter(dataAdapter);

        if (/*(ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsPicture() != null)
                && */(ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsName() != null)
                && (ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsNumero() != null)) {
            mUrlImg = ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsPicture();
            mName = ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsName();
            mNumero = ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsNumero();
            mDelai = ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsDelai();

            refreshTextAndPictureViews();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    Picasso.with(getActivity()).load(mOutputFileUri).fit().centerInside().into(iv_picture);

                    mUrlImg = mOutputFileUri.getPath();
                }

                break;
            case REQUEST_CODE_GALLERY:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();

                    Picasso.with(getActivity()).load(selectedImage).fit().centerInside().into(iv_picture);

                    mUrlImg = getRealPathFromURI(selectedImage);
                }
                break;
        }
    }

    private void refreshTextAndPictureViews() {
        if (mName != null) {
            et_name.setText(mName);
        } else {
            et_name.setText("");
        }

        if (mNumero != null) {
            et_number.setText(mNumero);
        } else {
            et_number.setText("");
        }

        if (mUrlImg != null && !mUrlImg.equals("")) {
            Picasso.with(getActivity()).load(mUrlImg).fit().centerInside().into(iv_picture);
        } else {
            int paddingPicture = getActivity().getResources().getDimensionPixelSize(R.dimen.padding_picture_home);
            iv_picture.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            iv_picture.setPadding(paddingPicture, paddingPicture, paddingPicture, paddingPicture);
        }

        spiDelai.setSelection(mDelai);
    }

    // Picture
    // --------------------------------------------------------------------------------------------
    private File createImageFile() throws IOException {
        // Create an image file name
        final File root = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "Cobra" + File.separator);
        root.mkdirs();

        final String fname = "img_" + System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        mOutputFileUri = Uri.fromFile(sdImageMainDirectory);

        return sdImageMainDirectory;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    // Listener
    // --------------------------------------------------------------------------------------------
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // On selecting a spinner item
        String item = adapterView.getItemAtPosition(i).toString();
        ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).setSettingDelai(i);

        // Showing selected spinner item
        //Toast.makeText(adapterView.getContext(), "Selected: " + item + " : " + i, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
