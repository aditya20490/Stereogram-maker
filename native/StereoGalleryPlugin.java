package com.aditya.stereo3d;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.OutputStream;

@CapacitorPlugin(name = "StereoGallery")
public class StereoGalleryPlugin extends Plugin {

    @PluginMethod
    public void save(PluginCall call) {
        String data = call.getString("data");
        String name = call.getString("name", "stereo3d.jpg");
        if (data == null) {
            call.reject("No image data");
            return;
        }
        try {
            byte[] bytes = Base64.decode(data, Base64.DEFAULT);
            ContentResolver resolver = getContext().getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, name);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            if (Build.VERSION.SDK_INT >= 29) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Stereo3D");
                values.put(MediaStore.Images.Media.IS_PENDING, 1);
            }
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri == null) {
                call.reject("Could not create gallery entry");
                return;
            }
            OutputStream os = resolver.openOutputStream(uri);
            if (os == null) {
                call.reject("Could not open gallery file");
                return;
            }
            os.write(bytes);
            os.close();
            if (Build.VERSION.SDK_INT >= 29) {
                ContentValues done = new ContentValues();
                done.put(MediaStore.Images.Media.IS_PENDING, 0);
                resolver.update(uri, done, null, null);
            }
            JSObject ret = new JSObject();
            ret.put("uri", uri.toString());
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Save failed: " + e.getMessage());
        }
    }

    @PluginMethod
    public void remove(PluginCall call) {
        String uri = call.getString("uri");
        JSObject ret = new JSObject();
        boolean deleted = false;
        try {
            if (uri != null) {
                int n = getContext().getContentResolver().delete(Uri.parse(uri), null, null);
                deleted = n > 0;
            }
        } catch (Exception e) {
            deleted = false;
        }
        ret.put("deleted", deleted);
        call.resolve(ret);
    }
}
