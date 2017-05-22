package com.example.abishek.encryption;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    int mode;
    SecretKeySpec sks=null;
    private static final int PICKFILE_RESULT_CODE = 1;
    ListView lv;

    LayoutInflater inflater=null;
    LocalData ld;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv= (ListView) findViewById(R.id.listview);

        ld=new LocalData(getApplicationContext(),"MYDB",null,1);
        Button btn = (Button) findViewById(R.id.button);

        MYAdapter adp=new MYAdapter(getApplicationContext(),updateadapter(),this);
        lv.setAdapter(adp);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                mode=0;
                startActivityForResult(intent, PICKFILE_RESULT_CODE);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s=adapterView.getItemAtPosition(i).toString();
                ArrayList<String> aln=updateadapter();
                //Toast.makeText(getApplicationContext(),sn,Toast.LENGTH_LONG).show();
                Cursor cr=ld.getData(aln.get(i));

                for(cr.moveToFirst();!cr.isAfterLast();cr.moveToNext()){
                    //Toast.makeText(getApplicationContext(), "Till now no error", Toast.LENGTH_LONG).show();

                        byte[] b = cr.getBlob(cr.getColumnIndex("key"));
                        SecretKeySpec skss = new SecretKeySpec(b, "AES");
                      //  Toast.makeText(getApplicationContext(), "Till now no error", Toast.LENGTH_LONG).show();
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/01" + cr.getString(cr.getColumnIndex("name")));
                        if (!file.exists()) {
                            try {
                                file.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            FileInputStream fin = new FileInputStream(cr.getString(cr.getColumnIndex("path")));
                            byte[] ba = new byte[fin.available()];
                            fin.read(ba);
                            FileOutputStream fos = new FileOutputStream(file);
                            byte[] bn = null;
                            bn = encryption(ba, 1, skss);
                            fos.write(bn);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Main error", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                }
                Toast.makeText(getApplicationContext(),"Succesfully Decrypted",Toast.LENGTH_LONG).show();

            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public SecretKeySpec generatekey(String s) throws NoSuchAlgorithmException {
        SecretKeySpec sks1=null;
        SecureRandom sr=SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(s.getBytes());
        KeyGenerator kg=KeyGenerator.getInstance("AES");
        kg.init(128,sr);
        sks1 = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        byte[] b=sks1.getEncoded();

        return sks1;
    }
    public  byte[] encryption(byte[] b,int mode,SecretKeySpec sks) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher c=Cipher.getInstance("AES");
        if(mode==0)
            c.init(Cipher.ENCRYPT_MODE,sks);
        else
            c.init(Cipher.DECRYPT_MODE,sks);
        byte[] bn=null;
        bn=c.doFinal(b);
        return bn;
    }
    public ArrayList<String> updateadapter(){
        Cursor cr=ld.allData();
        ArrayList<String> al=new ArrayList<String>();
        for(cr.moveToFirst();!cr.isAfterLast();cr.moveToNext()){
            al.add(cr.getString(cr.getColumnIndex("name")));
            //Toast.makeText(getApplicationContext(),cr.getString(cr.getColumnIndex("name")),Toast.LENGTH_SHORT).show();
        }
        return al;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode){
            case PICKFILE_RESULT_CODE:
                if(resultCode==RESULT_OK){

                    if(mode==0) {
                        Uri uri=data.getData();
                        ContentResolver cr=this.getContentResolver();
                        try {
                            sks = generatekey("123");
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }

                        String exe[] = cr.getType(uri).split("/");
                        String name = String.valueOf(new Random().nextInt(10000)) + "." + exe[1];
                        String path=Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + name;
                        Toast.makeText(getApplicationContext(), cr.getType(uri), Toast.LENGTH_LONG).show();
                        File file = new File(path);
                        EditText et = (EditText) findViewById(R.id.key);
                        ld.insert(path,name,sks.getEncoded(),et.getText().toString());
                        if (!file.exists()) {
                            try {
                                file.createNewFile();
                            } catch (IOException e) {
                                Toast.makeText(getApplicationContext(), "Cant Create a File", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }

                        try {
                            FileInputStream fin = (FileInputStream) cr.openInputStream(uri);
                            //Toast.makeText(getApplicationContext(),"1",Toast.LENGTH_LONG).show();
                            if (fin != null) {
                                byte[] b = new byte[fin.available()];
                                FileOutputStream fos = new FileOutputStream(file);
                                fin.read(b);
                                //        sks=generatekey("123");
                                byte[] bn = null;
                                bn = encryption(b, mode, sks);
                                fos.write(bn);
                                fin.close();
                                fos.close();
                            } else
                                Toast.makeText(getApplicationContext(), "Invalid File Input", Toast.LENGTH_LONG).show();
                            MYAdapter adp=new MYAdapter(getApplicationContext(),updateadapter(),this);
                            lv.setAdapter(adp);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Cant Open that File", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }



                    //Toast.makeText(getApplicationContext(),"Created a File",Toast.LENGTH_LONG).show();
                }
                break;
            default:
                Toast.makeText(getApplicationContext(),"Invalid Data",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
