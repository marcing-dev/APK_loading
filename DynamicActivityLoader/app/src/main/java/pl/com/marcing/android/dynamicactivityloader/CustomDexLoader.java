package pl.com.marcing.android.dynamicactivityloader;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

public class CustomDexLoader {
    public static void loadClass(String TAG, Context context) {
        Log.i(TAG, "Trying to load new class from apk.");
        final File dexInternalStoragePath = new File(context.getDir("dex", Context.MODE_PRIVATE),
                "test.apk");
        final File optimizedDexOutputPath = context.getDir("outdex", Context.MODE_PRIVATE);
        Log.i(TAG, "dexInternalStoragePath: " + dexInternalStoragePath.getAbsolutePath());
        if(dexInternalStoragePath.exists()) {
            Log.i(TAG, "New apk found!");
            DexClassLoader dexLoader = new DexClassLoader(dexInternalStoragePath.getAbsolutePath(),
                    optimizedDexOutputPath.getAbsolutePath(),
                    null,
                    ClassLoader.getSystemClassLoader().getParent());
            try {
                Class klazz = dexLoader.loadClass("pl.com.marcing.android.customdex.NewObject");
                Constructor constructor = klazz.getConstructor(String.class);
                Method method = klazz.getDeclaredMethod("getInfo");
                Object newObject = constructor.newInstance("New object info");
                Log.i(TAG, "New object has class: " + newObject.getClass().getName());
                Log.i(TAG, "Invoking getInfo on new object: " + method.invoke(newObject));
            } catch(Exception e) {
                Log.e(TAG, "Exception:", e);
            }
        } else {
            Log.i(TAG, "Sorry new apk doesn't exist.");
        }
    }

    public static void loadActivity(String TAG, Context context) {
        Log.i(TAG, "Trying to load new activity from apk.");
        final File dexInternalStoragePath = new File(context.getDir("dex", Context.MODE_PRIVATE),
                "test.apk");
        final File optimizedDexOutputPath = context.getDir("outdex", Context.MODE_PRIVATE);
        Log.i(TAG, "dexInternalStoragePath: " + dexInternalStoragePath.getAbsolutePath());
        if(dexInternalStoragePath.exists()) {
            Log.i(TAG, "New apk found!");
            DexClassLoader dexLoader = new DexClassLoader(dexInternalStoragePath.getAbsolutePath(),
                    optimizedDexOutputPath.getAbsolutePath(),
                    null,
                    ClassLoader.getSystemClassLoader().getParent());
            try {
                Class klazz = dexLoader.loadClass("pl.com.marcing.android.customdex.TestActivity");
                Intent intent = new Intent(context, klazz);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch(Exception e) {
                Log.e(TAG, "Exception:", e);
            }
        } else {
            Log.i(TAG, "Sorry new apk doesn't exist.");
        }
    }

    public static void invokeAdditionalActivity(String TAG, Context context) {
        try {
            Field f = BaseDexClassLoader.class.getDeclaredField("pathList");
            f.setAccessible(true);
            StringBuilder libPath = new StringBuilder();
            StringBuilder apkPath = new StringBuilder();

            Object dexPathObj = f.get(context.getClassLoader());

            Log.i(TAG, "DexPath object class:" + dexPathObj.getClass().getName());
            f = dexPathObj.getClass().getDeclaredField("nativeLibraryDirectories");
            f.setAccessible(true);
            Object libsObj = f.get(dexPathObj);
            Log.i(TAG, "Libs object class:" + libsObj.getClass().getName());
            String delim = "";
            if (libsObj instanceof File[]) {
                Log.i(TAG, "It is file array");
                File[] testArray = (File[]) libsObj;
                for (File libFile : testArray) {
                    Log.i(TAG, libFile.getAbsolutePath());
                    libPath.append(delim);
                    delim = File.pathSeparator;
                    libPath.append(libFile.getAbsolutePath());
                }
            }
            //dexElements
            f = dexPathObj.getClass().getDeclaredField("dexElements");
            f.setAccessible(true);
            Object elemsObj = f.get(dexPathObj);
            Object[] elemsArray = (Object[]) elemsObj;
            for (Object element : elemsArray) {
                Log.i(TAG, "Element object class:" + element.getClass().getName());
                Log.i(TAG, "Fields:");
                for (Field elemField : element.getClass().getDeclaredFields()) {
                    Log.i(TAG, elemField.getName());
                    elemField.setAccessible(true);
                    try {
                        Object tmpObj = elemField.get(element);
                        if (tmpObj != null) {
                            if (tmpObj instanceof File) {
                                File testF = (File) tmpObj;
                                if (elemField.getName().equals("file")) {
                                    apkPath.append(testF.getAbsolutePath());
                                }
                            }
                            if (elemField.getName().equals("dexFile")) {
                                Field anotherField = tmpObj.getClass().getField("mFileName");
                                anotherField.setAccessible(true);
                                String mFileName = (String) anotherField.get(tmpObj);
                                Log.i(TAG, mFileName);
                            }
                        } else {
                            Log.i(TAG, "^^null!!");
                        }
                    } catch (Exception e) {
                        Log.i(TAG, "^^can't access");
                    }
                }
            }
            final File dexInternalStoragePath = new File(context.getDir("dex", Context.MODE_PRIVATE),
                    "test.apk");
            final File optimizedDexOutputPath = context.getDir("outdex", Context.MODE_PRIVATE);
            Log.i(TAG, "apkPath:" + apkPath.toString());
            Log.i(TAG, "libPath:" + libPath.toString());
            DexClassLoader cl = new DexClassLoader(dexInternalStoragePath.getAbsolutePath() + File.pathSeparator + apkPath.toString(),
                    optimizedDexOutputPath.getAbsolutePath(),
                    libPath.toString(),
                    context.getClassLoader());
            Log.i(TAG, "Test: " + cl.toString());
            f = BaseDexClassLoader.class.getDeclaredField("pathList");
            f.setAccessible(true);
            Object dexPathObjNew = f.get(cl);
            f.set(context.getClassLoader(), dexPathObjNew);

            Class klazz = context.getClassLoader().loadClass("pl.com.marcing.android.customdex.TestActivity");
            Intent intent = new Intent(context, klazz);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("Invoker", "invoke exception", e);
        }
    }
}
