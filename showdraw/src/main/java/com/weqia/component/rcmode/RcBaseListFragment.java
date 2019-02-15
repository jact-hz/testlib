package com.weqia.component.rcmode;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.weqia.component.rcmode.adapter.RcFastAdapter;
import com.weqia.component.rcmode.recyclerView.LuRecyclerView;
import com.weqia.component.rcmode.recyclerView.LuRecyclerViewAdapter;
import com.weqia.component.rcmode.recyclerView.interfaces.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import cn.pinming.cadshow.library.R;


public abstract class RcBaseListFragment<T> extends RcBaseFt implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {
    protected LuRecyclerView rcListView = null;
    protected LinearLayout headerView;
    protected SwipeRefreshLayout mSwipeRefreshWidget;
    protected LinearLayoutManager layoutManager;
    protected RcFastAdapter<T> adapter;
    protected List<T> items = new ArrayList<>();
    private int pageSize = 15;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.rc_fragment_base_index, null);
            initBaseView(view);// 控件初始化
        }
        return view;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setAll(List<T> items) {
        this.items = items;
        adapter.setAll(items);
    }

    public void addAll(List<T> items) {
        if (items != null && items.size() > 0)
            this.items.addAll(items);
        adapter.addAll(items);
    }

    private void initBaseView(View view) {
        headerView = (LinearLayout) view.findViewById(R.id.headerView);
        rcListView = (LuRecyclerView) view.findViewById(R.id.rcListView);
        mSwipeRefreshWidget = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshWidget.setOnRefreshListener(this);
        mSwipeRefreshWidget.setColorSchemeResources(
                R.color.rc_color1, //
                R.color.rc_color2, //
                R.color.rc_color3, //
                R.color.rc_color4);
        layoutManager = new LinearLayoutManager(getActivity());
        rcListView.setLayoutManager(layoutManager);

        rcListView.setHasFixedSize(true);
        rcListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMore();
            }
        });
        //设置底部加载颜色
        rcListView.setFooterViewColor(R.color.rc_colorAccent, R.color.rc_dark, android.R.color.transparent);
        //设置底部加载文字提示
        rcListView.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧");

        initCustomView();
    }

    public void setAdapter(RcFastAdapter<T> adapter) {
        this.adapter = adapter;
        LuRecyclerViewAdapter luRecyclerViewAdapter = new LuRecyclerViewAdapter(this.adapter);
        rcListView.setAdapter(luRecyclerViewAdapter);
    }

    public List<T> getItems() {
        return items;
    }

    /**
     * 自定义自己需要的界面
     */
    public void initCustomView() {

    }

    @Override
    public void onRefresh() {
        loadComplete();
    }

    public void loadComplete() {
        if (mSwipeRefreshWidget != null)
            mSwipeRefreshWidget.setRefreshing(false);
        if (rcListView != null)
            rcListView.refreshComplete(getPageSize());
//        RecyclerViewStateUtils.setFooterViewState(rcListView, LoadingFooter.State.Normal);
//        GlobalUtil.loadComplete(plListView, getActivity(), canAdd());
    }

    public abstract void loadMore();
//    public abstract View emptyView();

    public boolean canAdd() {
        return true;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        final List<T> filteredModelList = filter(getItems(), newText);
        adapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    private List<T> filter(List<T> models, String query) {
        query = query.toLowerCase();

        final List<T> filteredModelList = new ArrayList<>();
        for (T model : models) {
            final String text = getFiterText(model).toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public void createSearchMenu(MenuItem searchItem) {
//        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        adapter.setFilter(getItems());
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true; // Return true to expand action view
                    }
                });
    }

    protected abstract String getFiterText(T data);
}


