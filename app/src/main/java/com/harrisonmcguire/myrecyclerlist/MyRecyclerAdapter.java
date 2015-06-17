package com.harrisonmcguire.myrecyclerlist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

/**
 * Created by Harrison on 6/11/2015.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<ListRowViewHolder> {

    private List<ListItems> listItemsList;
    private Context mContext;
    private ImageLoader mImageLoader;
    private int focusedItem = 0;


    public MyRecyclerAdapter(Context context, List<ListItems> listItemsList) {
        this.listItemsList = listItemsList;
        this.mContext = context;
    }

    @Override
    public ListRowViewHolder onCreateViewHolder(final ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);
        ListRowViewHolder holder = new ListRowViewHolder(v);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView redditUrl = (TextView) v.findViewById(R.id.url);
                String postUrl = redditUrl.getText().toString();
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra("url", postUrl);
                mContext.startActivity(intent);
            }
        });

        return holder;
    }


    @Override
    public void onBindViewHolder(final ListRowViewHolder listRowViewHolder, int position) {
        ListItems listItems = listItemsList.get(position);
        listRowViewHolder.itemView.setSelected(focusedItem == position);

        listRowViewHolder.getLayoutPosition();

        mImageLoader = MySingleton.getInstance(mContext).getImageLoader();

        listRowViewHolder.thumbnail.setImageUrl(listItems.getThumbnail(), mImageLoader);
        listRowViewHolder.thumbnail.setDefaultImageResId(R.drawable.reddit_placeholder);

        listRowViewHolder.title.setText(Html.fromHtml(listItems.getTitle()));
        listRowViewHolder.subreddit.setText(Html.fromHtml(listItems.getSubreddit()));
        listRowViewHolder.author.setText(Html.fromHtml(listItems.getAuthor()));
        listRowViewHolder.url.setText(Html.fromHtml(listItems.getUrl()));
    }

    public void clearAdapter () {
        listItemsList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (null != listItemsList ? listItemsList.size() : 0);
    }
}
