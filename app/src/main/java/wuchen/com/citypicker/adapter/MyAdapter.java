package wuchen.com.citypicker.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import butterknife.ButterKnife;
import butterknife.InjectView;
import wuchen.com.citypicker.R;
import wuchen.com.citypicker.bean.CityBean;
import wuchen.com.citypicker.view.DragableGridlayout;

import static android.content.ContentValues.TAG;

/**
 * Created by 巫晨 on 2017/6/3.
 */

public class MyAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<CityBean> mCityBeens;
    private String mCity;

    public MyAdapter(Context context, ArrayList<CityBean> cityBeens) {
        mContext = context;
        mCityBeens = cityBeens;
    }

    public void setLocation(String city) {
        mCity = city;
        notifyItemChanged(0);
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= 0 && position <= 2) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.myitem, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);
            return new GridViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.bind(position);
        } else {
            GridViewHolder gridViewHolder = (GridViewHolder) holder;
            gridViewHolder.bind(position);
        }
    }

    @Override
    public int getItemCount() {
        if (mCityBeens != null) {
            return mCityBeens.size() + 3;
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.my_tv_headname)
        TextView mMyTvHeadname;
        @InjectView(R.id.my_tv_name)
        TextView mMyTvName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }

        public void bind(final int position) {
            int flag = -1;
            if (position == 3) {
                flag = 0;
            } else if (mCityBeens.get(position).getPinYIn().charAt(0) != mCityBeens.get(position - 1).getPinYIn().charAt(0)) {
                flag = 0;
            }
            mMyTvHeadname.setVisibility(flag == -1 ? View.GONE : View.VISIBLE);
            mMyTvHeadname.setText(Character.toString(mCityBeens.get(position).getPinYIn().charAt(0)));
            mMyTvName.setText(mCityBeens.get(position).getCityName());
            mMyTvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mCityBeens.get(position).getCityName());
                    }
                }
            });
            mMyTvHeadname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onHeadClick(mCityBeens.get(position).getPinYIn().charAt(0) + "");
                    }
                }
            });
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(String string);

        void onHeadClick(String string);
    }

    class GridViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.item_grid)
        DragableGridlayout mItemGrid;
        @InjectView(R.id.gridtv_headname)
        TextView mTextView;

        GridViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }

        public void bind(int position) {
            //绑定前面三个的gridView的布局
            mTextView.setVisibility(View.VISIBLE);
            if (position == 0) {
                mItemGrid.removeAllViews();
                mTextView.setText("定位城市");
                if (mCity == null || "".equals(mCity)) {
                    mItemGrid.addItem("未知");
                } else {
                    mItemGrid.addItem(mCity);
                }
                mItemGrid.setHasAnimation(false);
                mItemGrid.setOnItemClickListener(new DragableGridlayout.onItemClickListener() {
                    @Override
                    public void onItemClick(View parent, TextView child) {
                        if(mOnThreeHeadClickListener != null) {
                            mOnThreeHeadClickListener.onFirstClick(child.getText().toString());
                        }
                    }
                });
            } else if (position == 1) {
                mTextView.setText("最近访问城市");
                mItemGrid.removeAllViews();
                SharedPreferences history = mContext.getSharedPreferences("history", Context.MODE_PRIVATE);
                if (history == null) {
                    mItemGrid.setVisibility(View.INVISIBLE);
                } else {
                    HashSet<String> citys = (HashSet<String>) history.getStringSet("citys", null);
                    if (citys == null || citys.size() == 0) {
                        mItemGrid.setVisibility(View.INVISIBLE);
                    } else {
                        for (String city : citys) {
                            Log.d("tag", "最近访问城市: +++++++++++++");
                            mItemGrid.addItem(city);
                        }
                    }
                }
                mItemGrid.setHasAnimation(true);
                mItemGrid.setOnItemClickListener(new DragableGridlayout.onItemClickListener() {
                    @Override
                    public void onItemClick(View parent, TextView child) {
                        if(mOnThreeHeadClickListener != null) {
                            mOnThreeHeadClickListener.onSecondClick(child.getText().toString());
                        }
                    }
                });
            } else if (position == 2) {
                mItemGrid.removeAllViews();
                mItemGrid.setHasAnimation(false);
                mTextView.setText("热门城市");
                Log.d("tag", "热门城市: +++++++++++++");
                mItemGrid.addItem("泰州市");
                Log.d("tag", "热门城市: +++++台州++++++++");
                mItemGrid.addItem("蚌埠市");
                Log.d("tag", "热门城市: +++++++蚌埠++++++");
                mItemGrid.setOnItemClickListener(new DragableGridlayout.onItemClickListener() {
                    @Override
                    public void onItemClick(View parent, TextView child) {
                        if(mOnThreeHeadClickListener != null) {
                            mOnThreeHeadClickListener.onThirdclick(child.getText().toString());
                        }
                    }
                });
            }
        }
    }

    private OnThreeHeadClickListener mOnThreeHeadClickListener;

    public void setOnThreeHeadClickListener(OnThreeHeadClickListener onThreeHeadClickListener) {
        mOnThreeHeadClickListener = onThreeHeadClickListener;
    }

    public interface OnThreeHeadClickListener {
        void onFirstClick(String string);

        void onSecondClick(String string);

        void onThirdclick(String string);
    }

    public void addData(String str) {

        notifyDataSetChanged();

    }

}
