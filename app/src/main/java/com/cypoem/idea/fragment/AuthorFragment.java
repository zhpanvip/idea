package com.cypoem.idea.fragment;

import android.os.Bundle;
import com.cypoem.idea.R;
import com.cypoem.idea.adapter.CollectAdapter;
import com.cypoem.idea.module.bean.CollectBean;
import com.cypoem.idea.view.ListViewForScrollView;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by edianzu on 2017/5/23.
 *
 */

public class AuthorFragment extends BaseFragment {
    @BindView(R.id.lv_author)
    ListViewForScrollView mListView;

    private CollectAdapter mAdapter;

    public static AuthorFragment getFragment(Bundle bundle) {
        AuthorFragment authorFragment = new AuthorFragment();
        authorFragment.setArguments(bundle);
        return authorFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_author;
    }

    @Override
    protected void init() {
        initData();
    }

    private void initData() {
        List<CollectBean> mList = new ArrayList<>();
        CollectBean collectBean = new CollectBean();
        collectBean.setTime("2017-05-20");
        collectBean.setPicUrl("http://pic17.nipic.com/20111020/6337790_120550160000_2.jpg");
        collectBean.setArticlesNum(15);
        collectBean.setAuther("含箫剑");
        collectBean.setReadNum(565);
        collectBean.setPraiseNum(122);
        collectBean.setTitle("一只单身汪的日常");
        collectBean.setSubTitle("汪汪汪");
        mList.add(collectBean);
        mList.add(collectBean);
        mList.add(collectBean);
        mList.add(collectBean);
        mList.add(collectBean);
        mList.add(collectBean);
        mAdapter = new CollectAdapter(getContext(), R.layout.item_collect);
        mAdapter.setList(mList);
        mListView.setAdapter(mAdapter);
    }
}
