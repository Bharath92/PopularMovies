package com.example.upadhyb1.popularmovies;

/*
 Copied this piece of code from http://stackoverflow.com/questions/10923034/endless-gridview
 */

import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;

public class EndlessScrollListener implements OnScrollListener {

    private GridView gridView;
    private boolean isLoading;
    private boolean hasMorePages;
    private int pageNumber=1;
    private RefreshList refreshList;
    private boolean isRefreshing;

    public EndlessScrollListener(GridView gridView,RefreshList refreshList) {
        this.gridView = gridView;
        this.isLoading = false;
        this.hasMorePages = true;
        this.refreshList=refreshList;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (gridView.getLastVisiblePosition() + 1 == totalItemCount && !isLoading) {
            isLoading = true;
            if (hasMorePages && !isRefreshing) {
                 isRefreshing=true;
                refreshList.onRefresh(pageNumber);
            }
        } else {
            isLoading = false;
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    public void noMorePages() {
        this.hasMorePages = false;
    }

    public void hasMorePages(){
        this.hasMorePages = true;
    }

    public void notifyMorePages(){
        isRefreshing=false;
        pageNumber=pageNumber+1;
    }

    public void setPageNumber(int num){
        pageNumber = num;
    }

    public interface RefreshList {

        public void onRefresh(int pageNumber);
    }
}