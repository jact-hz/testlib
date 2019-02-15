package com.weqia.component.rcmode.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weqia.component.rcmode.RcBaseViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by berwin on 2017/3/10.
 */

public abstract class RcBaseFastAdapter<T> extends RecyclerView.Adapter<RcBaseViewHolder> {

    private final Object mLock = new Object();
    private boolean mNotifyOnChange = true;
    private List<T> mObjects = new ArrayList<>();

    private int layoutResId;
    private Context context;

    public RcBaseFastAdapter(Context context, int layoutResId) {
        this(context, layoutResId, null);
    }

    public RcBaseFastAdapter(Context context, int layoutResId, List<T> datas) {
        this.context = context;
        this.layoutResId = layoutResId;
        this.mObjects = datas == null ? new ArrayList<T>() : new ArrayList<T>(datas);
    }

    public int getPos(T data) {
        return mObjects.indexOf(data);
    }

    public List<T> getmObjects() {
        return mObjects;
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }

    @Override
    public RcBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new RcBaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RcBaseViewHolder holder, int position) {
        T item = getItem(position);
        bindingData(holder, item);
    }

    @Override
    public int getItemCount() {
        if (mObjects != null) {
            return mObjects.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    /**
     * 应该使用这个获取item个数
     *
     * @return
     */
    public int getCount() {
        return mObjects.size();
    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    public void add(T object) {
        if (object != null) {
            synchronized (mLock) {
                mObjects.add(object);
            }
        }
        if (mNotifyOnChange) notifyItemInserted(getCount());
    }


    public void setAll(Collection<? extends T> collection) {
        clear();
        addAll(collection);
    }

    public void setAll(T[] items) {
        clear();
        addAll(items);
    }

    /**
     * Adds the specified Collection at the end of the array.
     *
     * @param collection The Collection to add at the end of the array.
     */
    public void addAll(Collection<? extends T> collection) {
        if (collection != null && collection.size() != 0) {
            synchronized (mLock) {
                mObjects.addAll(collection);
            }

            if (mObjects.size() == collection.size()) {
                notifyDataSetChanged();
            } else {
                int dataCount = collection == null ? 0 : collection.size();
                if (mNotifyOnChange)
                    notifyItemRangeInserted(getCount() - dataCount, dataCount);
            }
        } else {
            notifyDataSetChanged();
        }
    }

    /**
     * Adds the specified items at the end of the array.
     *
     * @param items The items to add at the end of the array.
     */
    public void addAll(T[] items) {
        if (items != null && items.length != 0) {
            synchronized (mLock) {
                Collections.addAll(mObjects, items);
            }

            if (mObjects.size() == items.length) {
                notifyDataSetChanged();
            } else {
                int dataCount = items == null ? 0 : items.length;
                if (mNotifyOnChange)
                    notifyItemRangeInserted(getCount() - dataCount, dataCount);
            }
        } else {
            notifyDataSetChanged();
        }
    }

    /**
     * 插入，不会触发任何事情
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void insert(T object, int index) {
        synchronized (mLock) {
            mObjects.add(index, object);
        }
        if (mNotifyOnChange) notifyItemInserted(index);
    }

    /**
     * 插入数组，不会触发任何事情
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void insertAll(T[] object, int index) {
        synchronized (mLock) {
            mObjects.addAll(index, Arrays.asList(object));
        }
        int dataCount = object == null ? 0 : object.length;
        if (mNotifyOnChange) notifyItemRangeInserted(index, dataCount);
    }

    /**
     * 插入数组，不会触发任何事情
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void insertAll(Collection<? extends T> object, int index) {
        synchronized (mLock) {
            mObjects.addAll(index, object);
        }
        int dataCount = object == null ? 0 : object.size();
        if (mNotifyOnChange) notifyItemRangeInserted(index, dataCount);
    }


    public void update(T object, int pos) {
        synchronized (mLock) {
            mObjects.set(pos, object);
        }
        if (mNotifyOnChange) notifyItemChanged(pos);
    }

    /**
     * 删除，不会触发任何事情
     *
     * @param object The object to remove.
     */
    public void remove(T object) {
        int position = mObjects.indexOf(object);
        synchronized (mLock) {
            if (mObjects.remove(object)) {
                if (mNotifyOnChange) notifyItemRemoved(position);
            }
        }
    }

    /**
     * 删除，不会触发任何事情
     *
     * @param position The position of the object to remove.
     */
    public void remove(int position) {
        synchronized (mLock) {
            mObjects.remove(position);
        }
        if (mNotifyOnChange) notifyItemRemoved(position);
    }


    /**
     * 触发清空
     * 与{@link #clear()}的不同仅在于这个使用notifyItemRangeRemoved.
     * 猜测这个方法与add伪并发执行的时候会造成"Scrapped or attached views may not be recycled"的Crash.
     * 所以建议使用{@link #clear()}
     */
    public void removeAll() {
        int count = mObjects.size();
        synchronized (mLock) {
            mObjects.clear();
        }
        if (mNotifyOnChange) notifyItemRangeRemoved(0, count);
    }

    /**
     * 触发清空
     */
    public void clear() {
        int count = mObjects.size();
        synchronized (mLock) {
            mObjects.clear();
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained
     *                   in this adapter.
     */
    public void sort(Comparator<? super T> comparator) {
        synchronized (mLock) {
            Collections.sort(mObjects, comparator);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public T getItem(int position) {
        if (mObjects != null) {
            return mObjects.get(position);
        }
        return null;
    }

    public abstract void bindingData(RcBaseViewHolder holder, T item);

}
