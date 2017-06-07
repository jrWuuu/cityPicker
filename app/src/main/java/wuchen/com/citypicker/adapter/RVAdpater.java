package wuchen.com.citypicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;

import wuchen.com.citypicker.R;
import wuchen.com.citypicker.bean.CityBean;

/**
 * Created by 巫晨 on 2017/6/3.
 */

public class RVAdpater extends RecyclerView.Adapter implements StickyRecyclerHeadersAdapter {

    private Context mContext;
    private ArrayList<CityBean> mCityBeens;

    public RVAdpater(Context context, ArrayList<CityBean> cityBeens) {
        mContext = context;
        mCityBeens = cityBeens;
        RequestQueue requestQueue = Volley.newRequestQueue();
        StringRequest stringRequest = new StringRequest();
        requestQueue.add(stringRequest);
    }

    public void setCityBeens(ArrayList<CityBean> cityBeens) {
        mCityBeens = cityBeens;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tv_item, parent, false);
        return new TvItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        TvItemViewHolder tvItemViewHolder = (TvItemViewHolder) holder;
        tvItemViewHolder.bind(position);
        tvItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnRVClickListener != null) {
                    mOnRVClickListener.onItemClick(mCityBeens.get(position).getCityName());
                }
            }
        });
    }

    @Override
    public long getHeaderId(int position) {
        char c = mCityBeens.get(position).getPinYIn().charAt(0);
        Log.d("asd", "getHeaderId: --------" + mCityBeens.get(position).getCityName() + "===" + c);
        return c;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.head_item, parent, false);
        return new HeadViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, final int position) {
        HeadViewHolder headViewHolder = (HeadViewHolder) holder;
        headViewHolder.bind(position);
        headViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnRVClickListener != null) {
                    mOnRVClickListener.onHeadClick(mCityBeens.get(position).getPinYIn().charAt(0) + "");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mCityBeens != null) {
            return mCityBeens.size();
        }
        return 0;
    }

    class TvItemViewHolder extends RecyclerView.ViewHolder {
        TextView mTvName;

        TvItemViewHolder(View view) {
            super(view);
            mTvName = (TextView) view.findViewById(R.id.tv_name);
        }

        void bind(int position) {
            mTvName.setText(mCityBeens.get(position).getCityName());
        }
    }

    class HeadViewHolder extends RecyclerView.ViewHolder {
        TextView mTvName;

        HeadViewHolder(View view) {
            super(view);
            mTvName = (TextView) view.findViewById(R.id.tv_headname);
        }

        void bind(int position) {
            char c = mCityBeens.get(position).getPinYIn().charAt(0);
            mTvName.setText(Character.toString(c));
        }
    }

    private OnRVClickListener mOnRVClickListener;

    public void setOnRVClickListener(OnRVClickListener onRVClickListener) {
        mOnRVClickListener = onRVClickListener;
    }

    public interface OnRVClickListener {
        void onHeadClick(String text);
        void onItemClick(String text);
    }

}
