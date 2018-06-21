package com.noober.savehelper;

import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

public class SaveHelper {
	private static Map<String,ISaveInstanceStateHelper> helperCache = new HashMap<>();

    private static Map<String,ISaveViewStateHelper> viewHelperCache = new HashMap<>();

	private final static String HELPER_END = "_SaveStateHelper";

	/**
	 * equate to{@link SaveHelper#recover}, it's just renamed to make it easier to understand
	 */
	@Deprecated
	public static <T> void bind(T recover, Bundle savedInstanceState){
		recover(recover, savedInstanceState);
	}

	/**
	 * added while need to recover data, used in {@link android.app.Activity#onCreate(Bundle)}
	 *
	 * @param recover current Activity or Fragment
	 * @param savedInstanceState Bundle
	 */
	public static <T> void recover(T recover, Bundle savedInstanceState){
		recover(recover, savedInstanceState, null);
	}

    /**
     * equate to{@link SaveHelper#recover(Object, Bundle)}, used in {@link android.app.Activity#onCreate(Bundle, PersistableBundle)}
     * added in 2.1.0
     *
     * @param recover current Activity or Fragment
     * @param savedInstanceState Bundle
     */
    public static <T> void recover(T recover, Bundle savedInstanceState, PersistableBundle persistentState){
        if(savedInstanceState != null || persistentState != null){
            ISaveInstanceStateHelper<T> persistableSaveHelper = findSaveHelper(recover);
            if(persistableSaveHelper != null){
                persistableSaveHelper.recover(savedInstanceState, persistentState, recover);
            }
        }
    }

    /**
     * added while need to save data, used in {@link android.app.Activity#onSaveInstanceState(Bundle)}
     *
     * @param save current Activity or Fragment
     * @param outState Bundle
     */
	public static <T> void save(T save, Bundle outState){
		save(save, outState, null);
	}

    /**
     * equate to{@link SaveHelper#save(Object, Bundle)}, used in {@link android.app.Activity#onSaveInstanceState(Bundle, PersistableBundle)}
     * added in 2.1.0
     *
     * @param save current Activity or Fragment
     * @param outState Bundle
     */
    public static <T> void save(T save, Bundle outState, PersistableBundle persistentState){
        if(outState != null || persistentState != null){
			ISaveInstanceStateHelper<T> persistableSaveHelper = findSaveHelper(save);
            if(persistableSaveHelper != null){
                persistableSaveHelper.save(outState, persistentState, save);
            }
        }
    }


	/**
	 * added while need to save data, used in custom view
	 *
	 * @param save current custom view
	 * @param container SparseArray<Parcelable>
	 */
	public static <T> void save(T save, SparseArray<Parcelable> container){
        if(container != null){
            ISaveViewStateHelper<T> viewSaveHelper = findViewSaveHelper(save);
            if(viewSaveHelper != null){
                viewSaveHelper.save(save, container);
            }
        }
	}

    /**
     * recover for custom view
     *
     * @param save current custom view
     * @param container SparseArray<Parcelable>
     */
    public static <T> void recover(T save, SparseArray<Parcelable> container){
        if(container != null){
            ISaveViewStateHelper<T> viewSaveHelper = findViewSaveHelper(save);
            if(viewSaveHelper != null){
                viewSaveHelper.recover(save, container);
            }
        }
    }

	private static <T> ISaveInstanceStateHelper<T> findSaveHelper(T cl) {
		String clazz = cl.getClass().getName();
		ISaveInstanceStateHelper<T> saveInstanceStateHelper = helperCache.get(clazz);
		if(saveInstanceStateHelper == null){
			try {
				Class<?> findClass = Class.forName(clazz + HELPER_END);
				saveInstanceStateHelper = (ISaveInstanceStateHelper<T>)findClass.newInstance();
				helperCache.put(clazz,saveInstanceStateHelper);
			} catch (ClassNotFoundException e) {
				// ignore
				//e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return saveInstanceStateHelper;
	}

    private static <T> ISaveViewStateHelper<T> findViewSaveHelper(T cl) {
        String clazz = cl.getClass().getName();
        ISaveViewStateHelper<T> saveViewStateHelper = viewHelperCache.get(clazz);
        if(saveViewStateHelper == null){
            try {
                Class<?> findClass = Class.forName(clazz + HELPER_END);
                saveViewStateHelper = (ISaveViewStateHelper<T>)findClass.newInstance();
                viewHelperCache.put(clazz,saveViewStateHelper);
            } catch (ClassNotFoundException e) {
                // ignore
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return saveViewStateHelper;
    }

}
