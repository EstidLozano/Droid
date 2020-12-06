package com.blieve.droid;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.graphics.Bitmap.createBitmap;

public class Droid {

    public static int
            SCREEN_W,
            SCREEN_H;
    private static float
            SCREEN_W1P,
            SCREEN_H1P;

    public static void initApp(Context ctx) {
        getScreenSize(ctx);
    }

    private static void getScreenSize(@NotNull Context ctx) {
        Point size = new Point();
        ctx.getDisplay().getRealSize(size);
        SCREEN_W = size.x;
        SCREEN_H = size.y;
        SCREEN_W1P = SCREEN_W / 100f;
        SCREEN_H1P = SCREEN_H / 100f;
    }

    public static int width(float percent) {
        return (int) (SCREEN_W1P * percent);
    }

    public static int height(float percent) {
        return (int) (SCREEN_H1P * percent);
    }

    public static void vibrate(Vibrator v, int ms) {
        if(Build.VERSION.SDK_INT >= 26) v.vibrate(VibrationEffect
                .createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
        else v.vibrate(ms);
    }

    public static class Lang {
        private static final ArrayList<String> langs = new ArrayList<>();
        private final Map<String, ArrayList<String>> texts = new HashMap<>();
        private static int lang;

        public static void addLang(String lang) {
            langs.add(lang);
        }

        public static int getLang() {
            return lang;
        }

        public static void setLang(int lang) {
            Lang.lang = lang;
        }

        public static String getLangName(int index) {
            return langs.get(index);
        }

        public static int getLangIndex(String name) {
            return langs.indexOf(name);
        }

        public void addText(int langIndex, String name, String content) {
            if(!texts.containsKey(name)) texts.put(name, new ArrayList<>());
            texts.get(name).add(langIndex, content);
        }

        public void addTextToLastLang(String name, String content) {
            addText(langs.size() - 1, name, content);
        }

        public String getText(String name) {
            try {
                return texts.get(name).get(lang);
            } catch (NullPointerException e) { return "-"; }
        }

    }

    public static class Pref {
        private final Map<String, Object[]> prefs;
        private final SharedPreferences sharedPrefs;

        public Pref(SharedPreferences preferences) {
            this.prefs = new HashMap<>();
            this.sharedPrefs = preferences;
        }

        public void add(String preference, Object defValue) {
            Object value;
            if(defValue instanceof Boolean)
                value = sharedPrefs.getBoolean(preference, (Boolean) defValue);
            else if(defValue instanceof Integer)
                value = sharedPrefs.getInt(preference, (Integer) defValue);
            else if(defValue instanceof Long)
                value = sharedPrefs.getLong(preference, (Long) defValue);
            else if(defValue instanceof Float)
                value = sharedPrefs.getFloat(preference, (Float) defValue);
            else // if(defValue instanceof String)
                value = sharedPrefs.getString(preference, (String) defValue);
            prefs.put(preference, new Object[]{defValue, value});
        }

        public Object get(String preference) {
            return prefs.get(preference)[1];
        }

        public Object getDefault(String preference) {
            return prefs.get(preference)[0];
        }

        public void set(String preference, @NotNull Object value) {
            Object[] p = prefs.get(preference);
            if(value.equals(p[0])) sharedPrefs.edit().remove(preference).apply();
            else if(value instanceof Boolean)
                sharedPrefs.edit().putBoolean(preference, (Boolean) value).apply();
            else if(value instanceof Integer)
                sharedPrefs.edit().putInt(preference, (Integer) value).apply();
            else if(value instanceof Long)
                sharedPrefs.edit().putLong(preference, (Long) value).apply();
            else if(value instanceof Float)
                sharedPrefs.edit().putFloat(preference, (Float) value).apply();
            else // if(value instanceof String)
                sharedPrefs.edit().putString(preference, (String) value).apply();
            prefs.get(preference)[1] = value;
        }

        public void reset() {
            for(Object[] v : prefs.values()) v[1] = v[0];
            sharedPrefs.edit().clear().apply();
        }
    }

    public static class Data {
        @NotNull
        public static String inputStream(File path, String filename) {
            File file = new File(path, filename);
            byte[] bytes = new byte[(int) file.length()];
            try {
                if(!file.exists()) file.createNewFile();
                FileInputStream in = new FileInputStream(file);
                in.read(bytes);
                in.close();
            } catch (IOException e) { e.printStackTrace(); }
            return new String(bytes);
        }

        public static void outputStream(File path, String filename, @NotNull String data) {
            File file = new File(path, filename);
            try {
                FileOutputStream stream = new FileOutputStream(file);
                stream.write(data.getBytes());
                stream.close();
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public static class Img {
        public static Bitmap drawToBmp(@NotNull Drawable drawable, int width, int height) {
            Bitmap bmp = createBitmap(width, height, ARGB_8888);
            Canvas cvs = new Canvas(bmp);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(cvs);
            return bmp;
        }

        public static Bitmap bmpMerge(@NotNull Bitmap bmp1, @NotNull Bitmap bmp2) {
            Bitmap result = createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(bmp1, 0, 0, null);
            canvas.drawBitmap(bmp2, 0, 0, null);
            return result;
        }
    }

    public static class Listen {
        private OnCallListen listen;

        public void setOnCallListener(OnCallListen listen) {
            this.listen = listen;
        }

        public Listen() { }

        public void call() {
            if(this.listen != null) listen.onCall();
        }

        public interface OnCallListen {
            void onCall();
        }
    }

}