package protect.FinanceLord.NetWorthCalculatorUtils;

import android.content.Context;

import java.util.List;

import protect.FinanceLord.Database.AssetsTypeDao;
import protect.FinanceLord.Database.AssetsTypeQuery;
import protect.FinanceLord.Database.AssetsValue;
import protect.FinanceLord.Database.AssetsValueDao;
import protect.FinanceLord.Database.FinanceLordDatabase;

public class AssetsValueExtractor {

    Context context;
    Long date;
    FinanceLordDatabase database;
    AssetsValueDao assetsValueDao;
    AssetsTypeDao assetsTypeDao;
    List<AssetsValue> assetsValues;
    List<AssetsTypeQuery> assetsTypeQueries;

    public AssetsValueExtractor(final Context context, final Long date){
        this.context = context;
        this.date = date;

        database = FinanceLordDatabase.getInstance(context);
        assetsValueDao = database.assetsValueDao();
        assetsTypeDao = database.assetsTypeDao();
        assetsValues = assetsValueDao.queryAssetsBeforeDate(date);
        assetsTypeQueries = assetsTypeDao.queryGroupedAssetsType();
    }

    public List<AssetsTypeQuery> getAssetsTypeQueries() {
        return assetsTypeQueries;
    }

    public List<AssetsValue> getAssetsValues() {
        return assetsValues;
    }
}
