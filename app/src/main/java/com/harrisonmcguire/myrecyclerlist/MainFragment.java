package com.harrisonmcguire.myrecyclerlist;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harrison on 6/12/2015.
 */
public class MainFragment extends Fragment {

    public static final String TAG = "MyRecyclerList";
    private List<ListItems> listItemsList = new ArrayList<ListItems>();

    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter adapter;

    private int counter = 0;
    private String count;
    private String jsonSubreddit;
    private String after_id;
    private static final String gaming = "gaming";
    private static final String pics = "pics";
    private static final String aww = "aww";
    private static final String funny = "funny";
    private static final String food = "food";
    private static final String subredditUrl = "http://www.reddit.com/r/";
    private static final String jsonEnd = "/.json";
    private static final String qCount = "?count=";
    private static final String after = "&after=";

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = layoutInflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        // uncomment the below decoration line for card view
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(Color.BLACK).build());

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        updateList(MainActivity.fragSubreddit);

        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                int lastFirstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
                loadMore(jsonSubreddit);
            }
        });

        Button gamingButton = (Button) rootView.findViewById(R.id.gaming_button);
        gamingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fragSubreddit = gaming;
                reloadFragment();
            }
        });

        Button picsButton = (Button) rootView.findViewById(R.id.pics_button);
        picsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fragSubreddit = pics;
                reloadFragment();
            }
        });

        return rootView;
    }


    public void updateList(String subreddit) {

        counter = 0;

        subreddit = subredditUrl + subreddit + jsonEnd;

        adapter = new MyRecyclerAdapter(getActivity(), listItemsList);
        mRecyclerView.setAdapter(adapter);

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        adapter.clearAdapter();

        showPD();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, subreddit, null, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, response.toString());
                hidePD();

                try {
                    JSONObject data = response.getJSONObject("data");
                    after_id = data.getString("after");
                    JSONArray children = data.getJSONArray("children");

                    for (int i = 0; i < children.length(); i++) {
                        JSONObject post = children.getJSONObject(i).getJSONObject("data");

                        ListItems item = new ListItems();

                        item.setTitle(post.getString("title"));
                        item.setThumbnail(post.getString("thumbnail"));
                        item.setUrl(post.getString("url"));
                        item.setSubreddit(post.getString("subreddit"));
                        item.setAuthor(post.getString("author"));

                        jsonSubreddit = post.getString("subreddit");

                        listItemsList.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error" + error.getMessage());
                hidePD();
            }
        });

        queue.add(jsonObjectRequest);
    }


    public void loadMore(String subreddit) {

        counter = counter + 25;
        count = String.valueOf(counter);
        subreddit = jsonSubreddit;

        subreddit = subredditUrl + subreddit + jsonEnd + qCount + after + after_id;

        adapter = new MyRecyclerAdapter(getActivity(), listItemsList);
        mRecyclerView.setAdapter(adapter);

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        showPD();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, subreddit, null, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, response.toString());
                hidePD();

                try {
                    JSONObject data = response.getJSONObject("data");
                    after_id = data.getString("after");
                    JSONArray children = data.getJSONArray("children");

                    for (int i = 0; i < children.length(); i++) {
                        JSONObject post = children.getJSONObject(i).getJSONObject("data");

                        ListItems item = new ListItems();

                        item.setTitle(post.getString("title"));
                        item.setThumbnail(post.getString("thumbnail"));
                        item.setUrl(post.getString("url"));
                        item.setSubreddit(post.getString("subreddit"));
                        item.setAuthor(post.getString("author"));

                        jsonSubreddit = post.getString("subreddit");

                        listItemsList.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error" + error.getMessage());
                hidePD();
            }
        });

        queue.add(jsonObjectRequest);
    }


    private void showPD() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    private void hidePD() {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

   public void reloadFragment() {
       Fragment newFragment = new MainFragment();
       FragmentTransaction transaction = getFragmentManager().beginTransaction();
       transaction.replace(R.id.fragment, newFragment);
       transaction.commit();
   }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePD();
    }

}
