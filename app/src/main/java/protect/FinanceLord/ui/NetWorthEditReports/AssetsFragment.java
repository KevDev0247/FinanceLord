package protect.FinanceLord.ui.NetWorthEditReports;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import protect.FinanceLord.Database.AssetsTypeDao;
import protect.FinanceLord.Database.AssetsTypeQuery;
import protect.FinanceLord.Database.AssetsValue;
import protect.FinanceLord.Database.AssetsValueDao;
import protect.FinanceLord.Database.FinanceLordDatabase;
import protect.FinanceLord.NetWorthActivity;
import protect.FinanceLord.NetWorthCalculatorUtils.AssetsValueExtractor;
import protect.FinanceLord.NetWorthCalculatorUtils.AssetsValueInjector;
import protect.FinanceLord.NetWorthCalculatorUtils.NetWorthCalculator;
import protect.FinanceLord.R;
import protect.FinanceLord.AssetsFragmentUtils.AssetsFragmentAdapter;
import protect.FinanceLord.AssetsFragmentUtils.AssetsFragmentChildViewClickListener;
import protect.FinanceLord.AssetsFragmentUtils.AssetsFragmentDataProcessor;

public class AssetsFragment extends Fragment {
    String title;
    private Button btnCommit;
    private AssetsFragmentDataProcessor dataProcessor;

    public AssetsFragment(String title) {
        this.title = title;
    }

    ExpandableListView expandableListView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View assetsView = inflater.inflate(R.layout.fragment_assets, null);
        expandableListView = assetsView.findViewById(R.id.assets_list_view);
        this.btnCommit = assetsView.findViewById(R.id.btnCommit);
        this.btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        FinanceLordDatabase database = FinanceLordDatabase.getInstance(AssetsFragment.this.getContext());
                        AssetsValueDao dao = database.assetsValueDao();
                        Context context = getContext();
                        for(AssetsValue assetsValue: AssetsFragment.this.dataProcessor.getAllAssetsValues(context)) {
                            if(assetsValue.getAssetsPrimaryId() != 0) {
                                List<AssetsValue> assetsValues = dao.queryAsset(assetsValue.getAssetsPrimaryId());
                                Log.d("Assets Value Check", " Print assetsValues status " + assetsValues.isEmpty() + " assets value is " + assetsValue.getAssetsValue());
                                if(!assetsValues.isEmpty()) {
                                    dao.updateAssetValue(assetsValue);
                                } else {
                                    Log.w("AssetsFragment", "The assets not exists in the database? check if there is anything went wrong!!");
                                }
                            } else {
                                dao.insertAssetValue(assetsValue);
                            }
                        }

                        Date startDate = DateUtils.firstSecondOfThisMinute();
                        AssetsFragment.this.dataProcessor.setAssetsValues(dao.queryAssetsSinceDate(startDate.getTime()));

                        calculateParentAssets();

                        Log.d("AssetsFragment", "Assets committed!");
                    }
                });
            }
        });

        initAssetCategory();

        return assetsView;
    }

    private void initAssetCategory() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                FinanceLordDatabase database = FinanceLordDatabase.getInstance(AssetsFragment.this.getContext());
                AssetsTypeDao dao = database.assetsTypeDao();
                AssetsValueDao assetsValueDao = database.assetsValueDao();
                List<AssetsTypeQuery> assetsTypeQueries = dao.queryGroupedAssetsType();

                Date startOfMinute = DateUtils.firstSecondOfThisMinute();
                Long milliseconds = startOfMinute.getTime();
                List<AssetsValue> assetsValues = assetsValueDao.queryAssetsSinceDate(milliseconds);
                Log.d("AssetsFragment", "Query all assets: " + assetsTypeQueries.toString());
                Log.d("AssetsFragment", "Query assets Values: " + assetsValues.toString());

                AssetsFragment.this.dataProcessor = new AssetsFragmentDataProcessor(assetsTypeQueries, assetsValues);
                final AssetsFragmentAdapter adapter = new AssetsFragmentAdapter(AssetsFragment.this.getContext(), dataProcessor, 1,"Total Assets");
                final AssetsFragmentChildViewClickListener listener = new AssetsFragmentChildViewClickListener(dataProcessor.getSubSet(null, 0), dataProcessor, 0);
                AssetsFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        expandableListView.setAdapter(adapter);
                        expandableListView.setOnChildClickListener(listener);
                    }
                });
            }
        });
    }

    private void calculateParentAssets(){

        Date startOfMinute = DateUtils.firstSecondOfThisMinute();
        Long MilliSeconds = startOfMinute.getTime();
        List<Float> parentAssets = new ArrayList<>();
        AssetsValueInjector assetsValueInjector = new AssetsValueInjector(AssetsFragment.this.getContext(), MilliSeconds);
        AssetsValueExtractor assetsValueExtractor = new AssetsValueExtractor(AssetsFragment.this.getContext(), MilliSeconds);
        NetWorthCalculator netWorthCalculator = new NetWorthCalculator(assetsValueExtractor);


        float totalAssets = netWorthCalculator.calculateTotalAssets();
        float totalLiquidAssets = netWorthCalculator.calculateTotalLiquidAssets();
        float totalInvestedAssets = netWorthCalculator.calculateTotalInvestedAssets();
        float totalPersonalAssets = netWorthCalculator.calculateTotalPersonalAssets();
        float totalTaxableAccounts = netWorthCalculator.calculateTotalTaxableAccounts();
        float totalRetirementAccounts = netWorthCalculator.calculateTotalRetirementAccounts();
        float totalOwnershipInterests = netWorthCalculator.calculateTotalOwnershipInterests();

        parentAssets.add(totalAssets);
        parentAssets.add(totalLiquidAssets);
        parentAssets.add(totalInvestedAssets);
        parentAssets.add(totalPersonalAssets);
        parentAssets.add(totalTaxableAccounts);
        parentAssets.add(totalRetirementAccounts);
        parentAssets.add(totalOwnershipInterests);

        assetsValueInjector.insertParentAssetsValue(parentAssets);
    }
}
