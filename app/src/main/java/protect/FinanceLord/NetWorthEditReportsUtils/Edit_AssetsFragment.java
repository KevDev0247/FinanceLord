package protect.FinanceLord.NetWorthEditReportsUtils;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import protect.FinanceLord.CalendarDialog;
import protect.FinanceLord.Database.AssetsTypeDao;
import protect.FinanceLord.Database.AssetsTypeQuery;
import protect.FinanceLord.Database.AssetsValue;
import protect.FinanceLord.Database.AssetsValueDao;
import protect.FinanceLord.Database.FinanceLordDatabase;
import protect.FinanceLord.R;
import protect.FinanceLord.NetWorthEditReportsUtils.FragmentsUtils.AssetsFragmentAdapter;
import protect.FinanceLord.NetWorthEditReportsUtils.FragmentsUtils.AssetsFragmentChildViewClickListener;
import protect.FinanceLord.NetWorthDataTerminal.DataProcessor_Assets;

public class Edit_AssetsFragment extends Fragment {

    String title;
    private Button btnCommit;
    private Button btnCalendar;
    private DataProcessor_Assets dataProcessor;

    ExpandableListView expandableListView;
    private AssetsFragmentAdapter adapter;

    public Edit_AssetsFragment(String title) {
        this.title = title;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View assetsView = inflater.inflate(R.layout.fragment_edit_assets, null);
        expandableListView = assetsView.findViewById(R.id.assets_list_view);

        this.btnCalendar = assetsView.findViewById(R.id.btnCalendar);
        this.btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                CalendarDialog calendarDialog = new CalendarDialog();
                calendarDialog.show(manager, "Calendar Dialog");
            }
        });

        this.btnCommit = assetsView.findViewById(R.id.btnCommit);
        this.btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        FinanceLordDatabase database = FinanceLordDatabase.getInstance(Edit_AssetsFragment.this.getContext());
                        AssetsValueDao assetsValueDao = database.assetsValueDao();
                        for(AssetsValue assetsValue: Edit_AssetsFragment.this.dataProcessor.getAllAssetsValues()) {
                            if(assetsValue.getAssetsPrimaryId() != 0) {
                                List<AssetsValue> assetsValues = assetsValueDao.queryAsset(assetsValue.getAssetsPrimaryId());
                                Log.d("Assets Value Check", " Print assetsValues status " + assetsValues.isEmpty() + " assets value is " + assetsValue.getAssetsValue());
                                if(!assetsValues.isEmpty()) {
                                    assetsValueDao.updateAssetValue(assetsValue);
                                } else {
                                    Log.w("Edit_AssetsFragment", "The assets not exists in the database? check if there is anything went wrong!!");
                                }
                            } else {
                                assetsValueDao.insertAssetValue(assetsValue);
                            }
                        }

                        Date startDate = DateUtils.firstSecondOfThisMinute();
                        Edit_AssetsFragment.this.dataProcessor.setAllAssetsValues(assetsValueDao.queryAssetsSinceDate(startDate.getTime()));

                        Log.d("Edit_AssetsFragment", "time is " + startDate);

                        dataProcessor.calculateParentAssets(assetsValueDao);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                        
                        Log.d("Edit_AssetsFragment", "Assets committed!");
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
                FinanceLordDatabase database = FinanceLordDatabase.getInstance(Edit_AssetsFragment.this.getContext());
                AssetsTypeDao assetsTypeDao = database.assetsTypeDao();
                AssetsValueDao assetsValueDao = database.assetsValueDao();
                List<AssetsTypeQuery> assetsTypeQueries = assetsTypeDao.queryGroupedAssetsType();

                Date startOfMinute = DateUtils.firstSecondOfThisMinute();
                Long milliseconds = startOfMinute.getTime();

                List<AssetsValue> assetsValues = assetsValueDao.queryAssetsSinceDate(milliseconds);
                Log.d("Edit_AssetsFragment", "Query all assets: " + assetsTypeQueries.toString());
                Log.d("Edit_AssetsFragment", "Query assets Values: " + assetsValues.toString());

                Edit_AssetsFragment.this.dataProcessor = new DataProcessor_Assets(assetsTypeQueries, assetsValues);
                adapter = new AssetsFragmentAdapter(Edit_AssetsFragment.this.getContext(), dataProcessor, 1,"Total Assets");
                final AssetsFragmentChildViewClickListener listener = new AssetsFragmentChildViewClickListener(dataProcessor.getSubSet(null, 0), dataProcessor, 0);
                Edit_AssetsFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        expandableListView.setAdapter(adapter);
                        expandableListView.setOnChildClickListener(listener);
                    }
                });
            }
        });
    }
}
