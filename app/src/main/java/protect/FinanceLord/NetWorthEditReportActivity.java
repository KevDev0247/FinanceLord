package protect.FinanceLord;

import android.app.SearchManager;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;

import protect.FinanceLord.NetWorthEditReportsUtils.Edit_AssetsFragment;
import protect.FinanceLord.NetWorthEditReportsUtils.Edit_LiabilitiesFragment;
import protect.FinanceLord.NetWorthEditReportsUtils.SectionsPagerAdapter;

public class NetWorthEditReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_worth_edit_reports);

        String search = getIntent().getStringExtra(SearchManager.QUERY);
        resetView(search);
    }

    private void resetView(String search){
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        final ViewPager viewPager = findViewById(R.id.view_pager);

        ArrayList<Fragment> fragments = new ArrayList<>();
        Edit_AssetsFragment assetsFragment = new Edit_AssetsFragment("Assets");
        Edit_LiabilitiesFragment liabilitiesFragment = new Edit_LiabilitiesFragment("Liabilities");
        fragments.add(assetsFragment);
        fragments.add(liabilitiesFragment);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), fragments);
        viewPager.setAdapter(sectionsPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        
    }
}