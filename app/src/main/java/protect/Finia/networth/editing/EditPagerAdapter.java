package protect.Finia.networth.editing;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

import protect.Finia.R;

/**
 * A FragmentPagerAdapter that returns a fragment corresponding to
 * one of the tabs.
 *
 * @author Owner  Kevin Zhijun Wang
 * created on 2020/02/29
 */
public class EditPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_assets_text, R.string.tab_liabilities_tab};
    private final Context mContext;
    private ArrayList<Fragment> fragments;

    public EditPagerAdapter(Context context, FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        mContext = context;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 2;
    }
}